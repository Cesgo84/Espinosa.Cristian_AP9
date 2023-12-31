package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {
    private Long id;
    private String number;
    private LocalDate date;
    private double balance;

    //transactionDTO's set
    private Set<TransactionDTO> transactions = new HashSet<>();

    //account's owner
    private Client owner;

    //constructor
    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.date = account.getCreationDate();
        this.balance = account.getBalance();

        // getting a transactionDTO for each transaction in transactions's set
        this.transactions = account.getTransactions()
                                    .stream()
                                    .map(transaction -> new TransactionDTO(transaction))
                                    .collect(Collectors.toSet());
    }

    //getters
    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getCreationDate() {
        return date;
    }

    public double getBalance() {
        return balance;
    }

    //downstream
    public Set<TransactionDTO> getTransactions() {
        return transactions;
    }

//    public Client getOwner() {
//        return owner;
//    }

}
