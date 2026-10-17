package com.menu.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class MenuFrame extends JFrame {

    private final JPanel conteudo = new JPanel(new BorderLayout());

    public MenuFrame() {
        super("Menu Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Menu Principal");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.add(new JLabel("Olá, " + Banco.nomeUsuario()));
        direita.add(sair);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        topo.add(titulo, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);

        List<Ferramentas.Funcionalidade> itens = Ferramentas.todas();
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (int i = 0; i < itens.size(); i++) modelo.addElement((i + 1) + ". " + itens.get(i).nome());

        JList<String> lista = new JList<>(modelo);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lista.getSelectedIndex() >= 0) {
                mostrar(itens.get(lista.getSelectedIndex()).painel().get());
            }
        });

        JScrollPane navegacao = new JScrollPane(lista);
        navegacao.setPreferredSize(new Dimension(230, 0));

        add(topo, BorderLayout.NORTH);
        add(navegacao, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        setSize(980, 640);
        setLocationRelativeTo(null);
        lista.setSelectedIndex(0);
    }

    private void mostrar(JComponent painel) {
        conteudo.removeAll();
        conteudo.add(painel, BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }
}
