package com.bankapp.model;

public enum TransactionType {
    DEPOSIT ("Einzahlung", "+"),
    WITHDRAWAL ("Auszahlung", "-"),
    TRANSFER ("Überweisung", "⇄");

    private final String displayName;
    private final String symbol;

    TransactionType(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public static TransactionType fromString(String value) {
        for(TransactionType t : values()) {
            if(t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unbekannter Typ: " + value);
    }
}
