package com.bankapp.ui;

import com.bankapp.dao.AccountDao;
import com.bankapp.dao.CustomerDao;
import com.bankapp.dao.TransactionDao;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CustomerDao customerDao = new CustomerDao();
    private final AccountDao accountDao = new AccountDao();
    private final TransactionDao transactionDao = new TransactionDao();

    private CustomerPanel customerPanel;
    private AccountPanel accountPanel;

    public MainFrame() {
        initWindow();
        buildUi();
        setVisible(true);
    }

    private void initWindow() {
        setTitle("\uD83C\uDFE6 BankApp — Kontoverwaltung");
        setSize(900, 600);
        setMaximumSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // Das systemeigene Look-and-Feel wird verwendet.
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception ignored) {}
    }

    private void buildUi() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Datei");
        JMenu menuHelp = new JMenu("Hilfe");

        JMenuItem miExit = new JMenuItem("Beenden");
        JMenuItem miAbout = new JMenuItem("Über Bankapp");

        miExit.addActionListener(e -> {
            // Die Datenbankverbindung wird vor dem Beenden geschlossen.
            try {com.bankapp.db.DatabaseConnection.getInstance().close();}
            catch (Exception ex) {
                System.err.println("Die Datenbank konnte beim Beenden nicht geschlossen werden: " + ex.getMessage());
            }
                System.exit(0);
            });

        miAbout.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "BankApp v1.0\nLernprojekt\n" +
                                "Java OOP + Swing + PostgreSQL",
                        "Über BankApp", JOptionPane.INFORMATION_MESSAGE));

        menuFile.add(miExit);
        menuHelp.add(miAbout);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        // ── HAUPTBEREICH ─────────────────────────────────────
        // Das JSplitPane teilt das Fenster in zwei Bereiche mit einer verschiebbaren Trennlinie.
        accountPanel = new AccountPanel(accountDao, transactionDao);
        customerPanel = new CustomerPanel(customerDao, accountPanel);

        JSplitPane split = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        customerPanel,
        accountPanel
        );
        split.setDividerLocation(250);
        split.setDividerSize(5);
        split.setContinuousLayout(true);

        // ── STATUSLEISTE ─────────────────────────────────────
        JLabel statusBar = new JLabel("Bereit");
        statusBar.setPreferredSize(new Dimension(0, 22));
        statusBar.setFont(new Font("Sans Serif", Font.PLAIN, 11));
        statusBar.setForeground(Color.GRAY);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        add(split, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }


}
