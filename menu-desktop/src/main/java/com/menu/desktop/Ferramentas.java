package com.menu.desktop;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

final class Ferramentas {

    static final Locale PT = new Locale("pt", "BR");

    record Funcionalidade(String nome, Supplier<JComponent> painel) {}

    private Ferramentas() {}

    static List<Funcionalidade> todas() {
        return List.of(
            new Funcionalidade("Agenda de contatos", Ferramentas::agenda),
            new Funcionalidade("Calcular IMC", () -> calc("Calcular IMC", v -> {
                double p = n(v[0]), a = n(v[1]);
                if (p <= 0 || a <= 0) return "Peso e altura devem ser positivos.";
                double i = p / (a * a);
                return "Seu IMC é: " + f(i) + "\n"
                        + (i < 18.5 ? "Abaixo do peso." : i < 25 ? "Peso normal." : i < 30 ? "Sobrepeso." : "Obesidade.");
            }, "Peso (kg)", "Altura (m)")),
            new Funcionalidade("Antecessor e sucessor", () -> calc("Antecessor e sucessor", v -> {
                long x = (long) n(v[0]);
                return "Número: " + x + "\nAntecessor: " + (x - 1) + "\nSucessor: " + (x + 1);
            }, "Número inteiro")),
            new Funcionalidade("Verificar par ou ímpar", () -> calc("Verificar par ou ímpar", v -> {
                long x = (long) n(v[0]);
                return "O número é " + (x % 2 == 0 ? "par." : "ímpar.");
            }, "Número inteiro")),
            new Funcionalidade("Verificar senha", () -> calc("Verificar senha", v ->
                v[0].equals("java123") ? "Acesso liberado!" : "Senha incorreta! Acesso negado.", "Senha")),
            new Funcionalidade("Lanchonete", Ferramentas::lanchonete),
            new Funcionalidade("Jogo de adivinhação", Ferramentas::jogo),
            new Funcionalidade("Tabuada de multiplicação", () -> calc("Tabuada de multiplicação",
                v -> tab((int) n(v[0])), "Número")),
            new Funcionalidade("Promoções com senha", Ferramentas::promo),
            new Funcionalidade("Calcular média do aluno", () -> calc("Calcular média do aluno", v -> {
                double a = n(v[0]), b = n(v[1]), c = n(v[2]);
                if (a < 0 || a > 10 || b < 0 || b > 10 || c < 0 || c > 10) return "As notas devem estar entre 0 e 10.";
                double m = (a + b + c) / 3;
                return "Média: " + f(m) + "\n"
                        + (m >= 7 ? "Aluno aprovado!" : m >= 5 ? "Aluno em recuperação." : "Aluno reprovado.");
            }, "Nota 1", "Nota 2", "Nota 3")),
            new Funcionalidade("Calculadora", () -> calc("Calculadora", v -> {
                double a = n(v[0]), b = n(v[1]);
                String op = v[2];
                if (op.startsWith("÷") && b == 0) return "Não é possível dividir por zero.";
                double r = op.startsWith("+") ? a + b : op.startsWith("−") ? a - b : op.startsWith("×") ? a * b : a / b;
                return "Resultado: " + r;
            }, "Primeiro número", "Segundo número",
               new String[]{"Operação", "+ Adição", "− Subtração", "× Multiplicação", "÷ Divisão"})),
            new Funcionalidade("Conversor de temperatura", () -> calc("Conversor de temperatura", v -> {
                double t = n(v[0]);
                switch (v[1]) {
                    case "Celsius → Fahrenheit": return "Fahrenheit: " + f(t * 9 / 5 + 32);
                    case "Fahrenheit → Celsius": return "Celsius: " + f((t - 32) * 5 / 9);
                    case "Celsius → Kelvin": return "Kelvin: " + f(t + 273.15);
                    default: return "Celsius: " + f(t - 273.15);
                }
            }, "Temperatura",
               new String[]{"Conversão", "Celsius → Fahrenheit", "Fahrenheit → Celsius",
                   "Celsius → Kelvin", "Kelvin → Celsius"})),
            new Funcionalidade("Calcular fatorial", () -> calc("Calcular fatorial", v -> {
                int x = (int) n(v[0]);
                if (x < 0 || x > 20) return "Digite um número entre 0 e 20.";
                BigInteger r = BigInteger.ONE;
                for (int i = 2; i <= x; i++) r = r.multiply(BigInteger.valueOf(i));
                return "Fatorial de " + x + ": " + r;
            }, "Número de 0 a 20")),
            new Funcionalidade("Verificar número primo", () -> calc("Verificar número primo", v -> {
                long x = (long) n(v[0]);
                boolean primo = x >= 2;
                for (long i = 2; i * i <= x; i++) {
                    if (x % i == 0) { primo = false; break; }
                }
                return x + (primo ? " é" : " não é") + " um número primo.";
            }, "Número inteiro")),
            new Funcionalidade("Sistema de votação", Ferramentas::votacao),
            new Funcionalidade("Caixa eletrônico", Ferramentas::caixa),
            new Funcionalidade("Contagem regressiva", () -> calc("Contagem regressiva", v -> {
                int x = (int) n(v[0]);
                if (x < 0) return "Digite um número positivo.";
                StringBuilder s = new StringBuilder();
                for (int i = x; i >= 0; i--) s.append(i).append('\n');
                return s.append("Contagem finalizada!").toString();
            }, "Número inicial")),
            new Funcionalidade("Calcular juros simples", () -> calc("Calcular juros simples", v -> {
                double c = n(v[0]), t = n(v[1]), m = n(v[2]);
                if (c < 0 || t < 0 || m < 0) return "Os valores não podem ser negativos.";
                double j = c * (t / 100) * m;
                return "Capital: R$ " + f(c) + "\nJuros: R$ " + f(j) + "\nMontante final: R$ " + f(c + j);
            }, "Capital inicial (R$)", "Taxa de juros mensal (%)", "Tempo em meses")),
            new Funcionalidade("Sorteio de números", () -> calc("Sorteio de números", v -> sorteio(v),
                "Número mínimo", "Número máximo")),
            new Funcionalidade("Cadastro de produtos", Ferramentas::produtos),
            new Funcionalidade("Converter metros", () -> calc("Converter metros", v -> {
                double m = n(v[0]);
                return "Centímetros: " + f(m * 100) + "\nMilímetros: " + f(m * 1000) + "\nQuilômetros: " + f(m / 1000);
            }, "Medida em metros")),
            new Funcionalidade("Área do retângulo", () -> calc("Área do retângulo", v -> {
                double b = n(v[0]), h = n(v[1]);
                if (b <= 0 || h <= 0) return "Valores devem ser positivos.";
                return "Área: " + f(b * h) + "\nPerímetro: " + f(2 * (b + h));
            }, "Base", "Altura")),
            new Funcionalidade("Área do círculo", () -> calc("Área do círculo", v -> {
                double r = n(v[0]);
                if (r <= 0) return "O raio deve ser positivo.";
                return "Área: " + f(Math.PI * r * r) + "\nCircunferência: " + f(2 * Math.PI * r);
            }, "Raio")),
            new Funcionalidade("Verificar idade", () -> calc("Verificar idade", v -> {
                int i = (int) n(v[0]);
                return i < 0 ? "Idade inválida." : i < 12 ? "Criança." : i < 18 ? "Adolescente."
                        : i < 60 ? "Adulto." : "Idoso.";
            }, "Idade")),
            new Funcionalidade("Contar vogais", () -> calc("Contar vogais", v -> {
                long c = v[0].toLowerCase(PT).chars().filter(ch -> "aeiouáéíóúâêôãõ".indexOf(ch) >= 0).count();
                return "Quantidade de vogais: " + c;
            }, "Palavra ou frase")),
            new Funcionalidade("Inverter palavra", () -> calc("Inverter palavra",
                v -> "Palavra invertida: " + new StringBuilder(v[0]).reverse(), "Palavra")),
            new Funcionalidade("Tabuada completa", () -> calc("Tabuada completa", v -> {
                int x = (int) n(v[0]);
                StringBuilder s = new StringBuilder(tab(x)).append("\n\nTabuada completa de 1 a 10:\n");
                for (int k = 1; k <= 10; k++) s.append("\nTabuada do ").append(k).append('\n').append(tab(k)).append('\n');
                return s.toString();
            }, "Número")),
            new Funcionalidade("Calcular desconto", () -> calc("Calcular desconto", v -> {
                double p = n(v[0]), d = n(v[1]);
                if (p < 0 || d < 0 || d > 100) return "Valores inválidos.";
                double desc = p * d / 100;
                return "Preço original: R$ " + f(p) + "\nDesconto: R$ " + f(desc) + "\nPreço final: R$ " + f(p - desc);
            }, "Preço do produto (R$)", "Desconto (%)")),
            new Funcionalidade("Verificar ano bissexto", () -> calc("Verificar ano bissexto", v -> {
                long a = (long) n(v[0]);
                if (a <= 0) return "Digite um ano positivo.";
                return (a % 400 == 0 || (a % 4 == 0 && a % 100 != 0)) ? "O ano é bissexto." : "O ano não é bissexto.";
            }, "Ano")),
            new Funcionalidade("Gerar número aleatório", () -> calc("Gerar número aleatório", v -> sorteio(v),
                "Menor número", "Maior número"))
        );
    }

    // ---------- CÁLCULOS SIMPLES ----------

    private static String sorteio(String[] v) {
        long a = (long) n(v[0]), b = (long) n(v[1]);
        if (a > b) return "O mínimo deve ser menor que o máximo.";
        return "Número sorteado: " + ThreadLocalRandom.current().nextLong(a, b + 1);
    }

    private static String tab(int x) {
        StringBuilder s = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            s.append(x).append(" x ").append(i).append(" = ").append(x * i);
            if (i < 10) s.append('\n');
        }
        return s.toString();
    }

    static double n(String s) {
        try {
            return Double.parseDouble(s.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número válido.");
        }
    }

    static String f(double x) {
        return String.format(PT, "%.2f", x);
    }

    // ---------- COMPONENTES COMUNS ----------

    /** Formulário genérico: campos de texto ou, quando for String[], uma lista de opções (o 1º item é o rótulo). */
    static JPanel calc(String titulo, Function<String[], String> fn, Object... campos) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        List<JComponent> entradas = new ArrayList<>();
        for (Object c : campos) {
            if (c instanceof String[] opcoes) {
                form.add(new JLabel(opcoes[0]));
                JComboBox<String> combo = new JComboBox<>(Arrays.copyOfRange(opcoes, 1, opcoes.length));
                entradas.add(combo);
                form.add(combo);
            } else {
                form.add(new JLabel((String) c));
                JTextField t = new JTextField();
                entradas.add(t);
                form.add(t);
            }
        }

        JTextArea out = saida();
        JButton calcular = new JButton("Calcular");
        calcular.addActionListener(e -> {
            try {
                String[] v = new String[entradas.size()];
                for (int i = 0; i < v.length; i++) {
                    JComponent c = entradas.get(i);
                    v[i] = c instanceof JComboBox<?> combo
                            ? (String) combo.getSelectedItem()
                            : ((JTextField) c).getText().trim();
                }
                out.setText(fn.apply(v));
            } catch (RuntimeException ex) {
                out.setText(ex.getMessage());
            }
        });

        JPanel topo = new JPanel(new BorderLayout(0, 8));
        topo.add(form, BorderLayout.NORTH);
        topo.add(calcular, BorderLayout.SOUTH);
        return painel(titulo, topo, new JScrollPane(out));
    }

    static JPanel painel(String titulo, JComponent topo, JComponent saida) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel t = new JLabel(titulo);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        p.add(t, BorderLayout.NORTH);
        JPanel meio = new JPanel(new BorderLayout(0, 12));
        meio.add(topo, BorderLayout.NORTH);
        meio.add(saida, BorderLayout.CENTER);
        p.add(meio, BorderLayout.CENTER);
        return p;
    }

    static JTextArea saida() {
        JTextArea t = new JTextArea(8, 30);
        t.setEditable(false);
        t.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        return t;
    }

    // ---------- FUNCIONALIDADES COM BANCO DE DADOS ----------

    static JPanel agenda() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Nome", "Telefone", "E-mail"}, 0);
        JTable tabela = new JTable(m);
        JTextField nome = new JTextField();
        JTextField tel = new JTextField();
        JTextField email = new JTextField();
        JTextField busca = new JTextField();

        Runnable carregar = () -> Ui.tentar(() -> {
            m.setRowCount(0);
            for (Object[] l : Banco.contatos(busca.getText())) m.addRow(l);
        });

        JButton adicionar = new JButton("Adicionar contato");
        adicionar.addActionListener(e -> Ui.tentar(() -> {
            Banco.adicionarContato(nome.getText(), tel.getText(), email.getText());
            nome.setText("");
            tel.setText("");
            email.setText("");
            carregar.run();
        }));

        JButton remover = new JButton("Remover selecionado");
        remover.addActionListener(e -> Ui.tentar(() -> {
            int linha = tabela.getSelectedRow();
            if (linha < 0) throw new IllegalArgumentException("Selecione um contato na tabela.");
            Banco.removerContato(((Number) m.getValueAt(tabela.convertRowIndexToModel(linha), 0)).longValue());
            carregar.run();
        }));

        JButton buscar = new JButton("Buscar");
        buscar.addActionListener(e -> carregar.run());
        busca.addActionListener(e -> carregar.run());

        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 8));
        campos.add(new JLabel("Nome"));
        campos.add(nome);
        campos.add(new JLabel("Telefone"));
        campos.add(tel);
        campos.add(new JLabel("E-mail"));
        campos.add(email);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 8, 8));
        botoes.add(adicionar);
        botoes.add(remover);

        JPanel buscaLinha = new JPanel(new BorderLayout(6, 0));
        buscaLinha.add(busca, BorderLayout.CENTER);
        buscaLinha.add(buscar, BorderLayout.EAST);

        JPanel topo = new JPanel(new GridLayout(0, 1, 8, 8));
        topo.add(campos);
        topo.add(botoes);
        topo.add(buscaLinha);

        carregar.run();
        return painel("Agenda de contatos", topo, new JScrollPane(tabela));
    }

    static JPanel produtos() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Produto", "Preço (R$)"}, 0);
        JTable tabela = new JTable(m);
        JTextField nome = new JTextField();
        JTextField preco = new JTextField();

        Runnable carregar = () -> Ui.tentar(() -> {
            m.setRowCount(0);
            for (Object[] l : Banco.produtos()) {
                m.addRow(new Object[]{l[0], l[1], f(((Number) l[2]).doubleValue())});
            }
        });

        JButton adicionar = new JButton("Adicionar");
        adicionar.addActionListener(e -> Ui.tentar(() -> {
            Banco.adicionarProduto(nome.getText(), n(preco.getText()));
            nome.setText("");
            preco.setText("");
            carregar.run();
        }));

        JButton remover = new JButton("Remover selecionado");
        remover.addActionListener(e -> Ui.tentar(() -> {
            int linha = tabela.getSelectedRow();
            if (linha < 0) throw new IllegalArgumentException("Selecione um produto na tabela.");
            Banco.removerProduto(((Number) m.getValueAt(tabela.convertRowIndexToModel(linha), 0)).longValue());
            carregar.run();
        }));

        JButton total = new JButton("Ver valor total");
        total.addActionListener(e -> Ui.tentar(() ->
                Ui.aviso("Valor total: R$ " + f(Banco.totalProdutos()))));

        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 8));
        campos.add(new JLabel("Nome do produto"));
        campos.add(nome);
        campos.add(new JLabel("Preço (R$)"));
        campos.add(preco);

        JPanel botoes = new JPanel(new GridLayout(1, 3, 8, 8));
        botoes.add(adicionar);
        botoes.add(remover);
        botoes.add(total);

        JPanel topo = new JPanel(new GridLayout(0, 1, 8, 8));
        topo.add(campos);
        topo.add(botoes);

        carregar.run();
        return painel("Cadastro de produtos", topo, new JScrollPane(tabela));
    }

    static JPanel caixa() {
        JLabel saldo = new JLabel(" ");
        saldo.setFont(saldo.getFont().deriveFont(Font.BOLD, 20f));
        JTextField valor = new JTextField();
        JTextArea out = saida();

        Runnable atualizar = () -> Ui.tentar(() -> saldo.setText("Saldo atual: R$ " + f(Banco.saldo())));

        JButton depositar = new JButton("Depositar");
        depositar.addActionListener(e -> Ui.tentar(() -> {
            Banco.depositar(n(valor.getText()));
            valor.setText("");
            atualizar.run();
            out.setText("Depósito realizado!");
        }));

        JButton sacar = new JButton("Sacar");
        sacar.addActionListener(e -> Ui.tentar(() -> {
            Banco.sacar(n(valor.getText()));
            valor.setText("");
            atualizar.run();
            out.setText("Saque realizado!");
        }));

        JPanel botoes = new JPanel(new GridLayout(1, 2, 8, 8));
        botoes.add(depositar);
        botoes.add(sacar);

        JPanel topo = new JPanel(new GridLayout(0, 1, 8, 8));
        topo.add(saldo);
        topo.add(new JLabel("Valor (R$)"));
        topo.add(valor);
        topo.add(botoes);

        atualizar.run();
        return painel("Caixa eletrônico", topo, new JScrollPane(out));
    }

    static JPanel votacao() {
        JTextArea out = saida();
        JPanel topo = new JPanel(new GridLayout(0, 2, 8, 8));

        String[][] opcoes = {{"A", "Candidato A"}, {"B", "Candidato B"}, {"C", "Candidato C"}, {"nulo", "Voto nulo"}};
        for (String[] op : opcoes) {
            JButton b = new JButton(op[1]);
            b.addActionListener(e -> Ui.tentar(() -> {
                Banco.votar(op[0]);
                out.setText(op[0].equals("nulo") ? "Voto nulo registrado." : "Voto registrado para " + op[0] + ".");
            }));
            topo.add(b);
        }

        JButton resultado = new JButton("Ver resultado");
        resultado.addActionListener(e -> Ui.tentar(() -> {
            Map<String, Long> c = Banco.contagemVotos();
            long a = c.get("A"), b = c.get("B"), cc = c.get("C"), nulo = c.get("nulo");
            long total = a + b + cc + nulo;
            String r;
            if (total == 0) {
                r = "Nenhum voto foi registrado.";
            } else {
                long max = Math.max(a, Math.max(b, cc));
                int lideres = (a == max ? 1 : 0) + (b == max ? 1 : 0) + (cc == max ? 1 : 0);
                r = lideres > 1 ? "Houve empate entre os candidatos."
                        : "Candidato " + (a == max ? "A" : b == max ? "B" : "C") + " recebeu mais votos.";
            }
            out.setText("RESULTADO\nCandidato A: " + a + "\nCandidato B: " + b + "\nCandidato C: " + cc
                    + "\nVotos nulos: " + nulo + "\nTotal de votos: " + total + "\n" + r);
        }));
        topo.add(resultado);

        return painel("Sistema de votação", topo, new JScrollPane(out));
    }

    // ---------- FUNCIONALIDADES SOMENTE NA INTERFACE ----------

    static JPanel lanchonete() {
        JTextArea out = saida();
        double[] total = {0};
        JPanel topo = new JPanel(new GridLayout(0, 2, 8, 8));
        Object[][] itens = {{"X-Burger", 15.0}, {"Pizza", 30.0}, {"Batata frita", 10.0}, {"Refrigerante", 6.0}};
        for (Object[] it : itens) {
            String nome = (String) it[0];
            double preco = (Double) it[1];
            JButton b = new JButton(nome + " – R$ " + f(preco));
            b.addActionListener(e -> {
                total[0] += preco;
                out.setText(nome + " adicionado! Total parcial: R$ " + f(total[0]));
            });
            topo.add(b);
        }
        JButton fim = new JButton("Finalizar pedido");
        fim.addActionListener(e -> {
            out.setText("Finalizando pedido...\nTotal do pedido: R$ " + f(total[0]));
            total[0] = 0;
        });
        topo.add(fim);
        return painel("Lanchonete", topo, new JScrollPane(out));
    }

    static JPanel jogo() {
        int[] segredo = {ThreadLocalRandom.current().nextInt(1, 11)};
        int[] tentativas = {0};
        JTextField palpite = new JTextField();
        JTextArea out = saida();

        Runnable chutar = () -> Ui.tentar(() -> {
            int p = (int) n(palpite.getText());
            tentativas[0]++;
            if (p < segredo[0]) {
                out.setText("Tente um número maior!");
            } else if (p > segredo[0]) {
                out.setText("Tente um número menor!");
            } else {
                out.setText("Parabéns! Você acertou!\nTentativas: " + tentativas[0] + "\nUma nova rodada começou.");
                segredo[0] = ThreadLocalRandom.current().nextInt(1, 11);
                tentativas[0] = 0;
            }
            palpite.setText("");
        });
        JButton palpitar = new JButton("Palpitar");
        palpitar.addActionListener(e -> chutar.run());
        palpite.addActionListener(e -> chutar.run());

        JPanel topo = new JPanel(new GridLayout(0, 1, 8, 8));
        topo.add(new JLabel("Adivinhe um número de 1 a 10!"));
        topo.add(palpite);
        topo.add(palpitar);
        return painel("Jogo de adivinhação", topo, new JScrollPane(out));
    }

    static JPanel promo() {
        JTextArea out = saida();
        double[] carrinho = {0};
        JPasswordField senha = new JPasswordField();
        JButton entrar = new JButton("Entrar");
        JPanel ofertas = new JPanel(new GridLayout(0, 1, 8, 8));
        ofertas.setVisible(false);

        entrar.addActionListener(e -> {
            if (new String(senha.getPassword()).equals("PROMO10")) {
                out.setText("Senha correta! Você ganhou 10% de desconto!");
                senha.setEnabled(false);
                entrar.setEnabled(false);
                ofertas.setVisible(true);
            } else {
                out.setText("Senha incorreta.\nPromoção não liberada.");
            }
        });

        Object[][] itens = {{"CAMISA", 35.0}, {"CALÇA", 150.0}, {"MEIA (PAR)", 12.0}};
        for (Object[] it : itens) {
            String nome = (String) it[0];
            double preco = (Double) it[1];
            JButton b = new JButton(nome + " – R$ " + f(preco));
            b.addActionListener(e -> {
                carrinho[0] += preco;
                out.setText(nome + " ADICIONADA!");
            });
            ofertas.add(b);
        }
        JButton fim = new JButton("Finalizar compra");
        fim.addActionListener(e -> {
            double s = carrinho[0], d = s * 0.10;
            out.setText("Subtotal: R$ " + f(s) + "\nDesconto de 10%: R$ " + f(d)
                    + "\nTOTAL COM DESCONTO: R$ " + f(s - d) + "\nObrigado pela preferência!");
            carrinho[0] = 0;
        });
        ofertas.add(fim);

        JPanel topo = new JPanel(new GridLayout(0, 1, 8, 8));
        topo.add(new JLabel("Senha promocional"));
        topo.add(senha);
        topo.add(entrar);
        topo.add(ofertas);
        return painel("Promoções com senha", topo, new JScrollPane(out));
    }
}
