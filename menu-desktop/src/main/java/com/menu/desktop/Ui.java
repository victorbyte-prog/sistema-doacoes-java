package com.menu.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

final class Ui {

    static final Color AZUL = new Color(0x3b5bdb);

    @FunctionalInterface
    interface Acao {
        void executar() throws Exception;
    }

    private Ui() {}

    /** Executa uma ação e mostra a mensagem de erro, se houver. */
    static void tentar(Acao acao) {
        try {
            acao.executar();
        } catch (Exception e) {
            aviso(e.getMessage() == null ? "Ocorreu um erro." : e.getMessage());
        }
    }

    static void aviso(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    static JTextField campo() {
        JTextField t = new JTextField(22);
        estilizar(t);
        return t;
    }

    static JPasswordField senha() {
        JPasswordField t = new JPasswordField(22);
        estilizar(t);
        return t;
    }

    private static void estilizar(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    static JButton botao(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return b;
    }

    static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    static JButton link(String texto, Runnable acao) {
        JButton b = new JButton(texto);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setForeground(AZUL);
        b.addActionListener(e -> acao.run());
        return b;
    }
}
