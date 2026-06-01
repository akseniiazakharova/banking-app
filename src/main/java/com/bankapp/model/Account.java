package com.bankapp.model;

import java.time.LocalDateTime;

public abstract class Account {
    private int id;
    private int customerID;
    private double balance;
    private String iban;
    private LocalDateTime createdAt;

    public Account(int id, int customerID, double balance, String iban) {
        this.id = id;
        this.customerID = customerID;
        this.balance = balance;
        this.iban = iban;
        this.createdAt = LocalDateTime.now();
    }

    public Account(int customerID, double balance, String iban) {
        this(0, customerID, balance, iban);
    }

    public abstract String getAccountType();

    public abstract String getDisplayName();

    public void deposit(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Einzahlungsbetrag muss positiv sein: " + amount);
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException(
                    "Abhebungsbetrag muss positiv sein: " + amount);
        }
        if(amount > this.balance) {
            throw new IllegalArgumentException(
                    "Unzureichendes Guthaben. Verfügbar: " + this.balance
            );
        }
        this.balance -= amount;
    }

    //Getters und Setters
    public int getId() {return id;}
    public int getCustomerID() {return customerID;}
    public double getBalance() {return balance;}
    public String getIban() {return iban;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setId(int id) {
        this.id = id;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCreatedAt(LocalDateTime dt) {
        this.createdAt = dt;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %.2f $", getDisplayName(), iban, balance);
    }
}
