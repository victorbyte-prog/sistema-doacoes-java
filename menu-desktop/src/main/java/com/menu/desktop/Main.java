package com.menu.desktop;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // usa o visual padrão
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Banco.iniciar();
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Não foi possível abrir o banco de dados: " + e.getMessage());
            }
        });
    }
}
