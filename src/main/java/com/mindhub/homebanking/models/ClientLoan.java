package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ClientLoan {
    //attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private double amount;
    private Integer payments;

    //loan requesting client
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Client_id")
    private Client client;

    //loan type details
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Loan_id")
    private Loan loan;

    //constructor
    public ClientLoan() {

    }

    public ClientLoan(Integer payments, double amount) {
        this.payments = payments;
        this.amount = amount;
    }

    //getter & setter
    //self
    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    //upstream
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}