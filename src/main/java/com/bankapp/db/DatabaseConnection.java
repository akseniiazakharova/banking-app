package com.bankapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bankapp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    // Erstellung der einzigen Instanz innerhalb der Klasse als statisches Feld.
    private static DatabaseConnection instance;
    private Connection connection;

    // Der Konstruktor wird vor externem Zugriff geschützt.
    private DatabaseConnection() throws SQLException {
        // Die Verbindung zur Datenbank wird initialisiert.
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Datenbankverbindung hergestellt");
    }

    // Es wird ein globaler Zugriffspunkt für andere Klassen bereitgestellt.
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        // Falls das Objekt noch nicht erstellt wurde, wird es erzeugt.
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        // Das bereits vorhandene Objekt wird zurückgegeben.
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Schließen: " + e.getMessage());
        }


    }
}
