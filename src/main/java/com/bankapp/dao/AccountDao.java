package com.bankapp.dao;

import com.bankapp.db.DatabaseConnection;
import com.bankapp.interfaces.Transferable;
import com.bankapp.model.Account;
import com.bankapp.model.CheckingAccount;
import com.bankapp.model.SavingsAccount;
import com.bankapp.model.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDao {
    public List<Account> findByCustomerId(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, iban " +
                "FROM accounts WHERE customer_id = ? ORDER BY id";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow (rs));
            }
        } catch (SQLException e) {
            System.err.println("Fehler in findByCustomerId " + e.getMessage());
        }
        return list;
    }

    /** Konto anhang der ID suchen */
    public Optional<Account> findById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fehler in findById " + e.getMessage());
        }return Optional.empty();
    }

    /** einen neunen Konto erstellen*/
    public Optional<Account> save(Account account) {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, iban) " +
                        "VALUES (?, ?, ?, ?) RETURNING id";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, account.getCustomerID());
            ps.setString(2, account.getAccountType());
            ps.setDouble(3, account.getBalance());
            ps.setString(4, account.getIban());

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) account.setId(rs.getInt("id"));
                return Optional.of(account);
            }
        } catch (Exception e) {
            System.err.println("Fehler in save " + e.getMessage());
        }return Optional.empty();
    }

    /** Kontostand in der Datenbank aktualisieren*/
    public boolean updateBalance(int accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Fehler in updateBalance " + e.getMessage());
            return false;
        }
    }


    /** ResultSet → Object Account(type) */
    private Account mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int customerId = rs.getInt("customer_id");
        double balance = rs.getDouble("balance");
        String iban = rs.getString("iban");
        String type = rs.getString("account_type");

        return switch (type) {
            case "CHECKING", "CHECKINGS" -> new CheckingAccount(id, customerId, balance, iban);
            case "SAVING", "SAVINGS" -> new SavingsAccount(id, customerId, balance, iban);
            default -> throw new IllegalStateException("Unbekannte Kontotyp " + type);
        };
    }
}
