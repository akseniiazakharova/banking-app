package com.bankapp;

import com.bankapp.db.DatabaseConnection;
import com.bankapp.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Hauptmethode der Anwendung BankApp.
 */
public class Main {

    public static void main(String[] args) {

        // 1. Die Verbindung zur Datenbank wird geprüft.
        System.out.println("=== BankApp wird gestartet ===");
        try {
            DatabaseConnection.getInstance();
        } catch (Exception e) {
            System.err.println("FEHLER: Keine Datenbankverbindung!");
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println("Lösung:");
            System.err.println("  1. brew services start postgresql@16");
            System.err.println("  2. USER in DatabaseConnection.java prüfen (whoami)");
            System.err.println("  3. Datenbank 'bankapp' muss existieren");
            System.exit(1);
        }

        // 2. GUI im Event Dispatch Thread starten
        SwingUtilities.invokeLater(MainFrame::new);
    }
}