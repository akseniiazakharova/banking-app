package com.bankapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDay;
    private LocalDateTime createdAt;

    private List<Account> accounts = new ArrayList<>();

    public Customer(int id, String firstName, String lastName, String email, LocalDate birthDay) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDay = birthDay;
    }

    public Customer(String firstName, String lastName, String email, LocalDate birthDay) {
        this(0, firstName, lastName, email, birthDay);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public String  getFullName() {
        return firstName + " " + lastName;
    }

    public double getTotalBalance() {
        return accounts.stream().mapToDouble(Account::getBalance).sum();
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime dt) {
        this.createdAt = dt;
    }

    @Override
    public String toString() {
        return getFullName();
    }


}
