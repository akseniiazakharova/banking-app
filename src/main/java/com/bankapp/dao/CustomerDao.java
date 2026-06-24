package com.bankapp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bankapp.db.DatabaseConnection;
import com.bankapp.model.Customer;

import javax.xml.crypto.Data;

public class CustomerDao {
    public List<Customer> findAll() throws SQLException {
        /** Alle Kunden, sortiert nach Nachnamen */
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, birth_date " + "FROM customers ORDER BY last_name, first_name";
        try(Statement stmt = DatabaseConnection.getInstance()
                                                .getConnection()
                                                .createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        }catch(SQLException e) {
            System.err.println("Fehler in FindAll: " + e.getMessage());
        }
        return list;
    }

    /** Kunden anhand der ID suchen. */
    public Optional<Customer> findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Fehler in FindById " + e.getMessage());
        }
        return Optional.empty();
    }

    /** Sucht nach einem Teil des Vor- oder Nachnamens. */
    public List<Customer> search(String term) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers " + "WHERE first_name ILIKE ? OR last_name ILIKE ? " +
                "ORDER BY last_name";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            String pattern = "%" + term + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        }catch (SQLException e) {
            System.err.println("Fehler in search " + e.getMessage());
        }
        return list;
    }

    public Optional<Customer> save(Customer c) {
        String sql = "INSERT INTO customers (first_name, last_name, email, birth_date) " +
                "VALUES (?, ?, ?, ?) RETURNING id ";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmail());
            ps.setDate(4, c.getBirthDay() != null ? Date.valueOf(c.getBirthDay()) : null);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    c.setId(rs.getInt("id"));
                    return Optional.of(c);
                }
            }
            } catch (SQLException e) {
            System.err.println("Fehler in save " + e.getMessage());
        }return Optional.empty();
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try(PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Fehler in delete " + e.getMessage());
            return false;
        }

    }











    /** ResultSet → Object Customer */
    private Customer mapRow(ResultSet rs) throws SQLException {
        Date db = rs.getDate("birth_date");
        return new Customer(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                db != null ? db.toLocalDate() : null);
    }
}
