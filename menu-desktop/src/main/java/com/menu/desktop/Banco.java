package com.menu.desktop;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Banco {

    private static final String URL = "jdbc:sqlite:menu.db";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static Connection con;
    private static long usuarioId;
    private static String usuarioNome = "";

    private Banco() {}

    public static void iniciar() throws SQLException {
        con = DriverManager.getConnection(URL);
        exec("PRAGMA foreign_keys = ON");
        exec("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, "
                + "email TEXT NOT NULL UNIQUE, senha_hash TEXT NOT NULL, saldo REAL NOT NULL DEFAULT 1000)");
        exec("CREATE TABLE IF NOT EXISTS contatos (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE, "
                + "nome TEXT NOT NULL, telefone TEXT, email TEXT)");
        exec("CREATE TABLE IF NOT EXISTS produtos (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE, "
                + "nome TEXT NOT NULL, preco REAL NOT NULL)");
        exec("CREATE TABLE IF NOT EXISTS votos (id INTEGER PRIMARY KEY AUTOINCREMENT, opcao TEXT NOT NULL)");
        exec("CREATE TABLE IF NOT EXISTS recuperacoes (usuario_id INTEGER NOT NULL REFERENCES usuarios(id) "
                + "ON DELETE CASCADE, codigo TEXT NOT NULL, expira_em INTEGER NOT NULL)");
    }

    public static String nomeUsuario() {
        return usuarioNome;
    }

    // ---------- CONTA ----------

    public static void cadastrar(String nome, String email, String senha) throws SQLException {
        if (nome.isBlank() || email.isBlank()) throw new IllegalArgumentException("Preencha todos os campos.");
        if (senha.length() < 6) throw new IllegalArgumentException("A senha precisa ter pelo menos 6 caracteres.");
        String mail = email.trim().toLowerCase();
        if (buscarId(mail) != null) throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        altera("INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)", nome.trim(), mail, hash(senha));
        login(mail, senha);
    }

    public static boolean login(String email, String senha) throws SQLException {
        List<Object[]> l = consulta("SELECT id, nome, senha_hash FROM usuarios WHERE email = ?",
                email.trim().toLowerCase());
        if (l.isEmpty() || !verificar(senha, (String) l.get(0)[2])) return false;
        usuarioId = ((Number) l.get(0)[0]).longValue();
        usuarioNome = (String) l.get(0)[1];
        return true;
    }

    /** Gera um código de 6 dígitos válido por 15 minutos. Retorna null se o e-mail não existir. */
    public static String gerarCodigo(String email) throws SQLException {
        Long id = buscarId(email.trim().toLowerCase());
        if (id == null) return null;
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        altera("DELETE FROM recuperacoes WHERE usuario_id = ?", id);
        altera("INSERT INTO recuperacoes (usuario_id, codigo, expira_em) VALUES (?, ?, ?)",
                id, codigo, System.currentTimeMillis() + 15 * 60_000L);
        return codigo;
    }

    public static void redefinirSenha(String email, String codigo, String novaSenha) throws SQLException {
        if (novaSenha.length() < 6) throw new IllegalArgumentException("A nova senha precisa ter pelo menos 6 caracteres.");
        Long id = buscarId(email.trim().toLowerCase());
        if (id == null || consulta("SELECT 1 FROM recuperacoes WHERE usuario_id = ? AND codigo = ? AND expira_em > ?",
                id, codigo.trim(), System.currentTimeMillis()).isEmpty()) {
            throw new IllegalArgumentException("Código inválido ou expirado.");
        }
        altera("UPDATE usuarios SET senha_hash = ? WHERE id = ?", hash(novaSenha), id);
        altera("DELETE FROM recuperacoes WHERE usuario_id = ?", id);
    }

    // ---------- AGENDA ----------

    public static List<Object[]> contatos(String filtro) throws SQLException {
        return consulta("SELECT id, nome, telefone, email FROM contatos WHERE usuario_id = ? "
                + "AND LOWER(nome) LIKE ? ORDER BY id", usuarioId, "%" + filtro.trim().toLowerCase() + "%");
    }

    public static void adicionarContato(String nome, String telefone, String email) throws SQLException {
        if (nome.isBlank()) throw new IllegalArgumentException("Informe o nome.");
        altera("INSERT INTO contatos (usuario_id, nome, telefone, email) VALUES (?, ?, ?, ?)",
                usuarioId, nome.trim(), telefone, email);
    }

    public static void removerContato(long id) throws SQLException {
        altera("DELETE FROM contatos WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    // ---------- PRODUTOS ----------

    public static List<Object[]> produtos() throws SQLException {
        return consulta("SELECT id, nome, preco FROM produtos WHERE usuario_id = ? ORDER BY id", usuarioId);
    }

    public static void adicionarProduto(String nome, double preco) throws SQLException {
        if (nome.isBlank()) throw new IllegalArgumentException("Informe o nome do produto.");
        if (preco < 0) throw new IllegalArgumentException("Preço inválido.");
        altera("INSERT INTO produtos (usuario_id, nome, preco) VALUES (?, ?, ?)", usuarioId, nome.trim(), preco);
    }

    public static void removerProduto(long id) throws SQLException {
        altera("DELETE FROM produtos WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    public static double totalProdutos() throws SQLException {
        return ((Number) consulta("SELECT COALESCE(SUM(preco), 0) FROM produtos WHERE usuario_id = ?",
                usuarioId).get(0)[0]).doubleValue();
    }

    // ---------- CAIXA ----------

    public static double saldo() throws SQLException {
        return ((Number) consulta("SELECT saldo FROM usuarios WHERE id = ?", usuarioId).get(0)[0]).doubleValue();
    }

    public static void depositar(double valor) throws SQLException {
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido.");
        altera("UPDATE usuarios SET saldo = saldo + ? WHERE id = ?", valor, usuarioId);
    }

    public static void sacar(double valor) throws SQLException {
        if (valor <= 0) throw new IllegalArgumentException("Valor inválido.");
        if (valor > saldo()) throw new IllegalArgumentException("Saldo insuficiente.");
        altera("UPDATE usuarios SET saldo = saldo - ? WHERE id = ?", valor, usuarioId);
    }

    // ---------- VOTAÇÃO ----------

    public static void votar(String opcao) throws SQLException {
        altera("INSERT INTO votos (opcao) VALUES (?)", opcao);
    }

    public static Map<String, Long> contagemVotos() throws SQLException {
        Map<String, Long> contagem = new LinkedHashMap<>();
        for (String k : List.of("A", "B", "C", "nulo")) contagem.put(k, 0L);
        for (Object[] l : consulta("SELECT opcao, COUNT(*) FROM votos GROUP BY opcao")) {
            contagem.put((String) l[0], ((Number) l[1]).longValue());
        }
        return contagem;
    }

    // ---------- INFRA ----------

    private static Long buscarId(String email) throws SQLException {
        List<Object[]> l = consulta("SELECT id FROM usuarios WHERE email = ?", email);
        return l.isEmpty() ? null : ((Number) l.get(0)[0]).longValue();
    }

    private static void exec(String sql) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }

    private static void altera(String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            ps.executeUpdate();
        }
    }

    private static List<Object[]> consulta(String sql, Object... args) throws SQLException {
        List<Object[]> linhas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) {
                int colunas = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] linha = new Object[colunas];
                    for (int c = 0; c < colunas; c++) linha[c] = rs.getObject(c + 1);
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    // Senhas: PBKDF2-HMAC-SHA256 com sal aleatório. Formato armazenado: "sal:hash" (Base64).

    private static String hash(String senha) {
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(derivar(senha, sal));
    }

    private static boolean verificar(String senha, String guardado) {
        String[] partes = guardado.split(":");
        byte[] calculado = derivar(senha, Base64.getDecoder().decode(partes[0]));
        return MessageDigest.isEqual(calculado, Base64.getDecoder().decode(partes[1]));
    }

    private static byte[] derivar(String senha, byte[] sal) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(senha.toCharArray(), sal, 120_000, 256))
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
