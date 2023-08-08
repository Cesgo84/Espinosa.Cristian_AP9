package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ClientDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<AccountDTO> accounts = new HashSet<>();
    public ClientDTO(Client client) {
        this.id = client.getId();
        this.email = client.getEmail();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.accounts = client.getAccounts()
                                    .stream()
                                    .map(account -> new AccountDTO(account))
                                    .collect(Collectors.toSet());
    }

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

}
