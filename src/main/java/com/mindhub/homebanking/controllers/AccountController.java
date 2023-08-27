package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
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
import java.util.Optional;
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
    public AccountDTO getAccountById(@PathVariable long id){
         return new AccountDTO(accountRepository.findById(id).get());
    }

   @PostMapping("/clients/current/accounts")
        public ResponseEntity<String> createAccount(Authentication authentication) {
        // create new Account
//        Account newAccount = new Account(null, LocalDate.now(), 0.0);
        // verify if the account number is already in use in the database
       Account newAccount;
       do{
           newAccount = new Account(null, LocalDate.now(), 0.0);
       }while(accountRepository.existsByNumber(newAccount.getNumber()));
//        if (accountRepository.existsByNumber(newAccount.getNumber())) {
//           return new ResponseEntity<>("Account number already in use",HttpStatus.FORBIDDEN);
//        }
        // get current Client
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        //verify that currentClient don't have more than 3 accounts
        if (currentClient.getAccounts().size()>=3){ // I've used ">=" to trigger 403 even if there are more than 3 accounts (e.g. 4 hardcoded accounts)
           return new ResponseEntity<>("you already reach the number of accounts allowed (3).",HttpStatus.FORBIDDEN);
        }
        // Add newAccount to currentClient and save it in the Data Base
        currentClient.addAccount(newAccount);
        clientRepository.save(currentClient);
        accountRepository.save(newAccount);
        return new ResponseEntity<>("Account created successfully",HttpStatus.CREATED);
   }

}

//            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
//        }
//        if (accountRepository.existsByNumber(number)){
//            return new ResponseEntity<>("Number already in use", HttpStatus.FORBIDDEN);
//        }
////        accountRepository.save(new Account(number, firstName, lastName, passwordEncoder.encode(password)));
////        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

