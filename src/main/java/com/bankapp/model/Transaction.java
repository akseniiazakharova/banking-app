package com.bankapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int id;
    private int accountId;
    private int targetAccountId;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime createdAt;

    public Transaction(int id, int accountId, int targetAccountId, TransactionType type,
                       double amount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.targetAccountId = targetAccountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Transaction(int accountId, TransactionType type,
                       double amount, String description) {
        this(0, accountId, 0, type, amount, description, LocalDateTime.now());
    }

    //Getters
    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getTargetAccountId() {
        return targetAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTargetAccountId(int targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    //Datum in einem lesbaren Format für die Tabelle
    public String getFormattedDate() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return createdAt.format(fmt);
    }

    //Der Betrag wird mit Vorzeichen angezeigt
    public String getFormattedAmount() {
        String sign = (type == TransactionType.WITHDRAWAL) ? "- " : "+ ";
        return String.format("%s%.2f $", sign, amount);
    }
}
