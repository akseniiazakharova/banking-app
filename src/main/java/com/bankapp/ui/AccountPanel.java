package com.bankapp.ui;

import com.bankapp.dao.AccountDao;
import com.bankapp.dao.TransactionDao;
import com.bankapp.model.*;
import com.bankapp.util.CurrencyFormatter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Rechter Bereich: Konten des ausgewählten Kunden, Transaktionen und Verlauf.
 */
public class AccountPanel extends JPanel {

    private final AccountDao     accountDao;
    private final TransactionDao transactionDao;

    private Customer currentCustomer;


    private JLabel              labelHeader;
    private JLabel              labelTotal;
    private DefaultListModel<Account> accountModel;
    private JList<Account>      accountList;
    private JButton             btnDeposit, btnWithdraw, btnTransfer;
    private DefaultTableModel   tableModel;
    private JTable              transTable;

    public AccountPanel(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao     = accountDao;
        this.transactionDao = transactionDao;
        buildUI();
        clear();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ── ÜBERSCHRIFT ───────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        labelHeader = new JLabel("Kein Kunde ausgewählt");
        labelHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        labelTotal  = new JLabel("");
        labelTotal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelTotal.setForeground(new Color(70, 130, 180));
        headerPanel.add(labelHeader, BorderLayout.WEST);
        headerPanel.add(labelTotal,  BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // ── KONTOLISTE ────────────────────────────────────────
        accountModel = new DefaultListModel<>();
        accountList  = new JList<>(accountModel);
        accountList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.setFixedCellHeight(28);

        accountList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadTransactions();
        });

        JScrollPane accountScroll = new JScrollPane(accountList);
        accountScroll.setPreferredSize(new Dimension(0, 120));
        accountScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Konten"));

        // ── SCHALTFLÄCHEN FÜR TRANSAKTIONEN ───────────────────
        JPanel opPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));

        btnDeposit  = new JButton("💰 Einzahlen");
        btnWithdraw = new JButton("💸 Abheben");
        btnTransfer = new JButton("⇄ Überweisen");

        styleButton(btnDeposit,  new Color(46, 139, 87));   // зелёный
        styleButton(btnWithdraw, new Color(178, 34, 34));   // красный
        styleButton(btnTransfer, new Color(70, 130, 180));  // синий

        btnDeposit.addActionListener(e  -> doDeposit());
        btnWithdraw.addActionListener(e -> doWithdraw());
        btnTransfer.addActionListener(e -> doTransfer());

        opPanel.add(btnDeposit);
        opPanel.add(btnWithdraw);
        opPanel.add(btnTransfer);

        // ── TRANSAKTIONSTABELLE ───────────────────────────────
        String[] cols = {"Datum", "Typ", "Betrag", "Beschreibung"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            // isCellEditable = false → ячейки нельзя редактировать
        };

        transTable = new JTable(tableModel);
        transTable.setRowHeight(22);
        transTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        transTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        transTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        transTable.setFillsViewportHeight(true);

        JScrollPane transScroll = new JScrollPane(transTable);
        transScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Letzte Transaktionen"));

        // ── ZENTRALER BEREICH ─────────────────────────────────
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(accountScroll, BorderLayout.NORTH);
        centerPanel.add(opPanel,       BorderLayout.CENTER);
        centerPanel.add(transScroll,   BorderLayout.SOUTH);

        // ── AUFBAU DER BENUTZEROBERFLÄCHE ─────────────────────
        add(headerPanel,  BorderLayout.NORTH);
        add(centerPanel,  BorderLayout.CENTER);
    }

    /** Zeigt die Daten des ausgewählten Kunden an. */
    public void showCustomer(Customer customer) {
        this.currentCustomer = customer;
        labelHeader.setText(customer.getFullName());

        // Konten aus der Datenbank laden
        List<Account> accounts = accountDao.findByCustomerId(customer.getId());
        customer.getAccounts().clear();
        accountModel.clear();

        accounts.forEach(a -> {
            customer.addAccount(a);
            accountModel.addElement(a);
        });

        // Gesamtsaldo aktualisieren
        labelTotal.setText("Gesamt: " +
                CurrencyFormatter.format(customer.getTotalBalance()));

        // Erstes Konto automatisch auswählen
        if (!accountModel.isEmpty()) {
            accountList.setSelectedIndex(0);
        }

        updateButtonState(true);
    }

    /** Leert die Ansicht (kein Kunde ausgewählt). */
    public void clear() {
        currentCustomer = null;
        labelHeader.setText("Kein Kunde ausgewählt");
        labelTotal.setText("");
        accountModel.clear();
        tableModel.setRowCount(0);
        updateButtonState(false);
    }

    /** Lädt die Transaktionen des ausgewählten Kontos. */
    private void loadTransactions() {
        tableModel.setRowCount(0);
        Account selected = accountList.getSelectedValue();
        if (selected == null) return;

        List<Transaction> txList =
                transactionDao.findByAccountId(selected.getId(), 20);

        for (Transaction tx : txList) {
            tableModel.addRow(new Object[]{
                    tx.getFormattedDate(),
                    tx.getType().getDisplayName(),
                    tx.getFormattedAmount(),
                    tx.getDescription()
            });
        }
    }

    // ── TRANSAKTIONEN ─────────────────────────────────────

    private void doDeposit() {
        Account acc = getSelectedAccount();
        if (acc == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Einzahlungsbetrag (€):", "Einzahlung",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;

        try {
            double amount = parseAmount(input);
            acc.deposit(amount);
            accountDao.updateBalance(acc.getId(), acc.getBalance());

            Transaction tx = new Transaction(acc.getId(),
                    TransactionType.DEPOSIT, amount, "Bareinzahlung");
            transactionDao.save(tx);

            refreshAfterOperation();
            showSuccess(String.format("Eingezahlt: %s",
                    CurrencyFormatter.format(amount)));

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void doWithdraw() {
        Account acc = getSelectedAccount();
        if (acc == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Abhebungsbetrag (€):", "Auszahlung",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;

        try {
            double amount = parseAmount(input);
            acc.withdraw(amount);
            // withdraw() в Account/CheckingAccount уже проверяет баланс

            accountDao.updateBalance(acc.getId(), acc.getBalance());

            Transaction tx = new Transaction(acc.getId(),
                    TransactionType.WITHDRAWAL, amount, "Barauszahlung");
            transactionDao.save(tx);

            refreshAfterOperation();
            showSuccess(String.format("Abgehoben: %s",
                    CurrencyFormatter.format(amount)));

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void doTransfer() {
        Account from = getSelectedAccount();
        if (from == null) return;

        if (!(from instanceof CheckingAccount)) {
            showError("Überweisungen nur vom Girokonto möglich.");
            return;
        }

        // Dialog zur Auswahl des Zielkontos
        String targetIban = JOptionPane.showInputDialog(this,
                "Ziel-IBAN:", "Überweisung", JOptionPane.QUESTION_MESSAGE);
        if (targetIban == null || targetIban.isBlank()) return;

        String amountStr = JOptionPane.showInputDialog(this,
                "Betrag (€):", "Überweisung", JOptionPane.QUESTION_MESSAGE);
        if (amountStr == null) return;

        try {
            double amount = parseAmount(amountStr);

            // Zielkonto anhand der IBAN suchen
            // Zur Vereinfachung wird ein einfaches SELECT auf der Datenbank ausgeführt.
            List<Account> allAcc = accountDao.findByCustomerId(0);
            // В учебном проекте упрощаем — передаём как описание
            ((CheckingAccount) from).transfer(from, 0); // заглушка

            // In einem realen Projekt würde hier eine Suche anhand der IBAN erfolgen.
            showError("Überweisung nach " + targetIban +
                    "\nFunktion in Entwicklung.");

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ── HILFSMETHODEN ─────────────────────────────────────

    private Account getSelectedAccount() {
        Account acc = accountList.getSelectedValue();
        if (acc == null) {
            showError("Bitte zuerst ein Konto auswählen.");
        }
        return acc;
    }

    private double parseAmount(String input) {
        try {
            return Double.parseDouble(
                    input.replace(",", ".").replace("€", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Ungültiger Betrag: " + input);
        }
    }

    private void refreshAfterOperation() {
        if (currentCustomer != null) showCustomer(currentCustomer);
    }

    private void updateButtonState(boolean enabled) {
        btnDeposit.setEnabled(enabled);
        btnWithdraw.setEnabled(enabled);
        btnTransfer.setEnabled(enabled);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Erfolg", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}