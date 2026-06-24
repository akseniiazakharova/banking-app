package com.bankapp.interfaces;

import com.bankapp.model.Account;

public interface Transferable {
    void transfer(Account target, double amount);
}
