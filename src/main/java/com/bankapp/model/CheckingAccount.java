package com.bankapp.model;

import com.bankapp.interfaces.Transferable;

// Girokonto (oder laufendes Konto). Dies ist ein gewöhnliches Bankkonto, mit dem man Zahlungen tätigen,
// Geld abheben und Überweisungen durchführen kann.
public class CheckingAccount extends Account implements Transferable {
    private double overdraftLimit = 0.0;

    public CheckingAccount(int id, int customerId, double balance, String iban) {
        super(id, customerId, balance, iban);
    }
    public CheckingAccount(int customerId, double balance, String iban) {
        super(customerId, balance, iban);
    }

    @Override
    public String getAccountType() {return "SAVING";}

    @Override
    public String getDisplayName() {return "Girokonto";}

    @Override
    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Betrag muss positiv sein");
        }
        if(amount > getBalance() + overdraftLimit) {
            throw new IllegalArgumentException("Kreditlimit überschritten. Verfügbar: " + (getBalance() + overdraftLimit));
        }
        setBalance(getBalance() - amount);
    }

    //Getters und Setters
    public void setOverdraftLimit(double limit) {
        this.overdraftLimit = limit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void transfer(Account target, double amount) {
        //Von unserem Konto abbuchen.
        this.withdraw(amount);
        //Auf das Zielkonto überweisen.
        target.deposit(amount);
    }



}
