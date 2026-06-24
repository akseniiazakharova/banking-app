package com.bankapp.ui;

import com.bankapp.dao.CustomerDao;
import com.bankapp.model.Customer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Linker Bereich: Kundenliste und Steuerschaltflächen.
 */
public class CustomerPanel extends JPanel {
    private final CustomerDao customerDao;
    private final AccountPanel accountPanel;

    private DefaultListModel<Customer> listModel;
    private JList<Customer> customerList;
    private JTextField searchField;

    public CustomerPanel(CustomerDao customerDao, AccountPanel accountPanel) {
        this.customerDao = customerDao;
        this.accountPanel = accountPanel;
        buildUI();
        loadCustomers();
    }

    private void buildUI() {
        // Einrichtung des Containers und des Fensters (buildUI)
        // Es wird ein BorderLayout (Nord–Mitte–Süd) mit einem Abstand von 5 Pixeln zwischen
        // den Komponenten festgelegt.
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Kunden", TitledBorder.LEFT, TitledBorder.TOP));

        // ── SUCHE ─────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(3, 0));
        searchField = new JTextField();
        searchField.setToolTipText("Nach Name suchen...");
        JButton btnSearch = new JButton("\uD83D\uDD0D");
        btnSearch.setMargin(new Insets(1, 4, 1, 4));
        searchPanel.add(new JLabel("Suche: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Suche per Schaltfläche oder Eingabetaste.
        btnSearch.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        // ── KUNDENLISTE ───────────────────────────────────────
        listModel = new DefaultListModel<>();
        customerList = new JList<>(listModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Bei Auswahl eines Kunden wird die rechte Ansicht aktualisiert.
        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Customer selected = customerList.getSelectedValue();
                if (selected != null) {
                    accountPanel.showCustomer(selected);
                }
            }
        });

        // Doppelklick → Dialog mit Details
        customerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showCustomerDetails();
                }
            }
        });

        // Scroll-Container für die Kundenliste
        JScrollPane scrollPane = new JScrollPane(customerList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        // ── SCHALTFLÄCHEN ─────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 4, 0));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JButton btnAdd = new JButton("+ Neu");
        JButton btnDelete = new JButton("Löschen");
        btnAdd.setBackground(new Color(70, 130, 180));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);
        btnAdd.setFont(new Font("Sans Serif", Font.PLAIN, 13));
        btnAdd.setMargin(new Insets(2, 2, 2, 2));

        btnDelete.setBackground(new Color(205, 92, 92));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(false);
        btnDelete.setFont(new Font("Sans Serif", Font.PLAIN, 13));
        btnDelete.setMargin(new Insets(2,2,2,2));

        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

        // ── AUFBAU DER BENUTZEROBERFLÄCHE ─────────────────────
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void loadCustomers() {
        try {
            List<Customer> customers = customerDao.findAll();
            listModel.clear();

            for (Customer c : customers) {
                listModel.addElement(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Kunden aus der Datenbank!",
                    "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSearch() {
        try {
            String term = searchField.getText().trim();
            List<Customer> results = term.isEmpty()
                    ? customerDao.findAll()
                    : customerDao.search(term);
            listModel.clear();
            for (Customer c : results) {
                listModel.addElement(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Kunden aus der Datenbank!",
                    "Datenbankfehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showAddDialog() {
        JTextField tfFirst = new JTextField();
        JTextField tfLast = new JTextField();
        JTextField tfEmail = new JTextField();
        JTextField tfBirth = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Vorname: ")); form.add(tfFirst);
        form.add(new JLabel("Nachname: ")); form.add(tfLast);
        form.add(new JLabel("Email: ")); form.add(tfEmail);
        form.add(new JLabel("Geb.-Datum (YYYY-MM-DD): ")); form.add(tfBirth);

        int result = JOptionPane.showConfirmDialog(this,
                form,
                "Neuen Kunden anlegen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer(
                        tfFirst.getText().trim(),
                        tfLast.getText().trim(),
                        tfEmail.getText().trim(),
                        LocalDate.parse(tfBirth.getText().trim())
                );
                customerDao.save(c).ifPresentOrElse(
                        saved -> {
                            loadCustomers();
                            JOptionPane.showMessageDialog(this,
                                    "Kunde angelegt " + saved.getFullName(),
                                    "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                        },
                        () -> JOptionPane.showMessageDialog(this,
                                "Fehler beim Anlegen!",
                                "Fehler", JOptionPane.ERROR_MESSAGE)
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ungültige Eingabe: " + e.getMessage(),
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        Customer selected = customerList.getSelectedValue();
        if(selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Bitte einen Kunden auswählen",
                    "Hinweis", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] options = {"Ja", "Nein"};
        int confirm = JOptionPane.showOptionDialog(this,
                "Kunden wirklich löschen?\n" + selected.getFullName(),
                "Bestätigung", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);
        if(confirm == 0) {
            if(customerDao.delete(selected.getId())) {
                loadCustomers();
                accountPanel.clear();
            }
        }

    }

    private void showCustomerDetails() {
        Customer c = customerList.getSelectedValue();
        if(c == null) return;
        JOptionPane.showMessageDialog(this,
                String.format("Name: %s\nE-mail: %s\nGeboren %s\n", c.getFullName(), c.getEmail(), c.getBirthDay()),
                "Kunden details", JOptionPane.INFORMATION_MESSAGE);
    }

}


