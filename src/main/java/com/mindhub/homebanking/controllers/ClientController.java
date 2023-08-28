package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> new ClientDTO(client))
                .collect(Collectors.toList());
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClientById(@PathVariable long id){
        return new ClientDTO(clientRepository.findById(id).get());
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String firstName,
            @RequestParam String lastName, @RequestParam String password){
        // verify that no field is left blank
        if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        // verify that the future client, has no other entry in the database, matching by email.
        if (clientRepository.findByEmail(email)!= null ){
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }
        // generete the new client, with encode password
        Client newClient = new Client(email, firstName, lastName, passwordEncoder.encode(password));
        // Create a new Account for the new client and verify that the id of the new account is not being used for an existing account in the database
        Account newAccount;
        do{
            newAccount = new Account(null, LocalDate.now(), 0.0);
        }while(accountRepository.existsByNumber(newAccount.getNumber()));
        // Add account to the client, save client and account in their according database
        newClient.addAccount(newAccount);
        clientRepository.save(newClient);
        accountRepository.save(newAccount);
        return new ResponseEntity<>("New client created successfully",HttpStatus.CREATED);
    }

    @GetMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        return new ClientDTO(clientRepository.findByEmail(authentication.getName()));
    }

}