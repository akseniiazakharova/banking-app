package com.bankapp.dao;

import com.bankapp.db.DatabaseConnection;
import com.bankapp.model.Transaction;
import com.bankapp.model.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    /** Lezte N Transactions eines Kontos*/
    public List<Transaction> findByAccountId(int accountId, int limit) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions " + "WHERE account_id = ? " + "ORDER BY created_at DESC LIMIT ?";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("Fehler in findByAccountId " + e.getMessage());
        }
        return list;
    }

    /** Transaktion speichern */
    public boolean save(Transaction t) {
        String sql = "INSERT INTO transactions " +
                "(account_id, target_account, type, amount, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getInstance()
                .getConnection()
                .prepareStatement(sql)) {
            ps.setInt(1, t.getAccountId());

            if (t.getTargetAccountId() > 0)
                ps.setInt(2, t.getTargetAccountId());
            else
                ps.setNull(2, Types.INTEGER);

            ps.setString(3, t.getType().name());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Fehler in save: " + e.getMessage());
            return false;
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("account_id"),
                rs.getInt("target_account"),
                TransactionType.fromString((rs.getString("type"))),
                rs.getDouble("amount"),
                rs.getString("description"),
                ts != null ? ts.toLocalDateTime() : null
        );

    }
}
