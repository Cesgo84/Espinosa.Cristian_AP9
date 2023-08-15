package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ClientDTO {
    //attributes
    private Long id;
    private String email;
    private String firstName;
    private String lastName;

    //acoountsDTO's set
    private Set<AccountDTO> accounts = new HashSet<>();

    //clientLoanDTO's set
    private Set<ClientLoanDTO> loans = new HashSet<>();

    //cardDTO's set
    private Set<CardDTO> cards = new HashSet<>();

    //constructor
    public ClientDTO(Client client) {
        this.id = client.getId();
        this.email = client.getEmail();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();

        // getting an accountDTO for each account in accounts's set
        this.accounts = client.getAccounts()
                                    .stream()
                                    .map(account -> new AccountDTO(account))
                                    .collect(Collectors.toSet());

        this.loans = client.getClientLoans()
                                    .stream()
                                    .map(loan -> new ClientLoanDTO(loan))
                                    .collect(Collectors.toSet());

        this.cards = client.getCards()
                                    .stream()
                                    .map(card -> new CardDTO(card))
                                    .collect(Collectors.toSet());

    }

    //getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public Set<ClientLoanDTO> getLoans() {
        return loans;
    }

    public Set<CardDTO> getCards() {
        return cards;
    }

}
