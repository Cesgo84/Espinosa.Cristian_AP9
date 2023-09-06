package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String number;
    private LocalDate date;
    private double balance;

    //account's owner (upstream : señala al nivel superior, a quien pertenece)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Owner_Id")
    private Client owner;

    //transaction's Set (downstream : señala al nivel inferior, a quienes estan bajo su pertenencia)
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<Transaction> transactions = new HashSet<>();

    //constructors
    public Account() {
    }

    public Account(String number, LocalDate date, double balance) {
        this.number = generateAccountNumber(number);
        this.date = date;
        this.balance = balance;
    }

    //getters & setters
    //self
    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreationDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    //upstream
    //@JsonIgnore alternativa para evitar recursividad pero no es practico para el manejo de datos
    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    //downstream
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    //methods
    public void addTransaction(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }

    private String generateAccountNumber(String number) {
        if (number == null) { //notas al corrector: esto está asi para poder preservar las cuentas VIN00X hardcodeadas a melba
            Random random = new Random();
            int randomNumber = random.nextInt(99999999) + 1;
            return "VIN-" + String.format("%08d", randomNumber);
        } else
            return number;
    }
}