package com.bankapp.model;

public class SavingsAccount extends Account{
    private double interestRate;

    public SavingsAccount(int id, int customerId, double balance, String iban) {
        super(id, customerId, balance, iban);
        this.interestRate = 0.02;
    }

    public SavingsAccount(int customerId, double balance, String iban) {
        super(customerId, balance, iban);
        this.interestRate = 0.02;
    }

    @Override
    public String getAccountType() {
        return "SAVING";
    }

    @Override
    public String getDisplayName() {
        return "Sparkonto";
    }

    public double applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
        return interest;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double rate) {
        this.interestRate = rate;
    }




}
