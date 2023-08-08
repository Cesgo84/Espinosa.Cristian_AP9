package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.time.LocalDate;

public class AccountDTO {
    private Long id;
    private String number;
    private LocalDate date;
    private double balance;

    //account's owner
    private Client owner;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.date = account.getDate();
        this.balance = account.getBalance();
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

//    public void setNumber(String number) {
//        this.number = number;
//    }

    public LocalDate getDate() {
        return date;
    }

//    public void setDate(LocalDate date) {
//        this.date = date;
//    }

    public double getBalance() {
        return balance;
    }

//    public void setBalance(double balance) {
//        this.balance = balance;
//    }

    public Client getOwner() {
        return owner;
    }

//    public void setOwner(Client owner) {
//        this.owner = owner;
//    }

}
