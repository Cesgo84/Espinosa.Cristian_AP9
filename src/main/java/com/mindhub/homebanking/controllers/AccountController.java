package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(client -> new AccountDTO(client))
                .collect(Collectors.toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getAccountByOwner(@PathVariable long id){
         return new AccountDTO(accountRepository.findById(id).get());
    }

    @GetMapping("/clients/current/accounts")
    @ResponseBody
    public List<AccountDTO> getCurrentClientAccounts(Authentication authentication) {
        // get current client's Accounts
        return clientRepository.findByEmail(authentication.getName()).getAccounts()
                .stream()
                .map(account -> new AccountDTO(account))
                .collect(Collectors.toList());
    }

   @PostMapping("/clients/current/accounts")
        public ResponseEntity<Object> createAccount(Authentication authentication) {
        // create new Account and verify if the account number is already in use in the database until found a free number
       Account newAccount;
       do{
           newAccount = new Account(null, LocalDate.now(), 0.0);
       }while(accountRepository.existsByNumber(newAccount.getNumber()));
        // get current Client
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        //verify that currentClient don't have more than 3 accounts
        if (currentClient.getAccounts().size()>=3){
           return new ResponseEntity<>("you already reach the number of accounts allowed (3).",HttpStatus.FORBIDDEN);
        }
        // Add newAccount to currentClient and save it in the Data Base
        currentClient.addAccount(newAccount);
        accountRepository.save(newAccount);
        return new ResponseEntity<>("Account created successfully",HttpStatus.CREATED);
   }

}
