package com.menu.desktop;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel painel = new JPanel(cartas);

    public LoginFrame() {
        super("Menu Utilidades");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        painel.add(telaLogin(), "login");
        painel.add(telaCadastro(), "cadastro");
        painel.add(telaRecuperar(), "recuperar");
        add(painel, BorderLayout.CENTER);
        setSize(440, 620);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void abrirMenu() {
        new MenuFrame().setVisible(true);
        dispose();
    }

    private JPanel telaLogin() {
        JTextField email = Ui.campo();
        JPasswordField senha = Ui.senha();
        JButton entrar = Ui.botao("Entrar");

        Runnable acao = () -> Ui.tentar(() -> {
            if (Banco.login(email.getText(), new String(senha.getPassword()))) abrirMenu();
            else Ui.aviso("E-mail ou senha incorretos.");
        });
        entrar.addActionListener(e -> acao.run());
        senha.addActionListener(e -> acao.run());

        return cartao("Bem-vindo de volta", "Entre com seu e-mail e senha",
                Ui.rotulo("E-mail"), email,
                Ui.rotulo("Senha"), senha,
                Box.createVerticalStrut(18), entrar,
                Box.createVerticalStrut(12), linksDe(
                        Ui.link("Esqueceu a senha?", () -> cartas.show(painel, "recuperar")),
                        Ui.link("Criar conta", () -> cartas.show(painel, "cadastro"))));
    }

    private JPanel telaCadastro() {
        JTextField nome = Ui.campo();
        JTextField email = Ui.campo();
        JPasswordField senha = Ui.senha();
        JButton criar = Ui.botao("Criar conta");
        criar.addActionListener(e -> Ui.tentar(() -> {
            Banco.cadastrar(nome.getText(), email.getText(), new String(senha.getPassword()));
            abrirMenu();
        }));

        return cartao("Criar conta", "Leva menos de um minuto",
                Ui.rotulo("Nome"), nome,
                Ui.rotulo("E-mail"), email,
                Ui.rotulo("Senha (mín. 6 caracteres)"), senha,
                Box.createVerticalStrut(18), criar,
                Box.createVerticalStrut(12), linksDe(
                        Ui.link("Já tenho conta", () -> cartas.show(painel, "login"))));
    }

    private JPanel telaRecuperar() {
        JTextField email = Ui.campo();
        JTextField codigo = Ui.campo();
        JPasswordField nova = Ui.senha();

        JButton gerar = Ui.botao("Gerar código");
        gerar.addActionListener(e -> Ui.tentar(() -> {
            String c = Banco.gerarCodigo(email.getText());
            Ui.aviso(c == null
                    ? "Se o e-mail estiver cadastrado, um código foi gerado."
                    : "Código de recuperação: " + c
                    + "\n\nEm uma versão com envio de e-mail, este código chegaria na sua caixa de entrada. "
                    + "Ele vale por 15 minutos.");
        }));

        JButton redefinir = Ui.botao("Redefinir senha");
        redefinir.addActionListener(e -> Ui.tentar(() -> {
            Banco.redefinirSenha(email.getText(), codigo.getText(), new String(nova.getPassword()));
            Ui.aviso("Senha redefinida! Faça login com a nova senha.");
            cartas.show(painel, "login");
        }));

        return cartao("Recuperar senha", "Gere um código e defina uma nova senha",
                Ui.rotulo("E-mail"), email,
                Box.createVerticalStrut(8), gerar,
                Box.createVerticalStrut(12), Ui.rotulo("Código recebido"), codigo,
                Ui.rotulo("Nova senha (mín. 6 caracteres)"), nova,
                Box.createVerticalStrut(18), redefinir,
                Box.createVerticalStrut(12), linksDe(
                        Ui.link("Voltar ao login", () -> cartas.show(painel, "login"))));
    }

    private static JPanel cartao(String titulo, String subtitulo, Component... itens) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 22f));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = new JLabel(subtitulo);
        s.setForeground(Color.GRAY);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.add(t);
        c.add(Box.createVerticalStrut(4));
        c.add(s);
        c.add(Box.createVerticalStrut(20));
        for (Component i : itens) c.add(i);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(Ui.AZUL);
        fundo.add(c);
        return fundo;
    }

    private static JPanel linksDe(JComponent... botoes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JComponent b : botoes) p.add(b);
        return p;
    }
}
