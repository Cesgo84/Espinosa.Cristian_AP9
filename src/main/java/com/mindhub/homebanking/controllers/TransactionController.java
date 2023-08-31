package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/transactions")
    @Transactional
    public ResponseEntity<Object> createTransaction(
            @RequestParam String fromAccountNumber,
            @RequestParam String toAccountNumber,
            @RequestParam double amount,
            @RequestParam String description,
            Authentication authentication) {

        // Verify that all parameters are provided
        if (fromAccountNumber.isBlank() || toAccountNumber.isBlank() || amount == 0 || description.isBlank()) {
            return new ResponseEntity<>("Missing parameters", HttpStatus.FORBIDDEN);
        }

        // Get authenticated client
        Client currentClient = clientRepository.findByEmail(authentication.getName());

        // Verify if accounts exists
        if (!accountRepository.existsByNumber(fromAccountNumber)) {
            return new ResponseEntity<>("Account of origin doesn't exist", HttpStatus.FORBIDDEN);
        }

        if (!accountRepository.existsByNumber(toAccountNumber)) {
            return new ResponseEntity<>("Account of destination doesn't exist", HttpStatus.FORBIDDEN);
        }

        // Verify source account belongs to the authenticated client
        boolean fromAccountExists = currentClient
                .getAccounts()
                .stream()
                .anyMatch(account ->
                        account.getNumber().equals(fromAccountNumber));
        if (!fromAccountExists) {
            return new ResponseEntity<>("Origin account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        // Verify that the two account numbers are different
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("accounts must be differents", HttpStatus.FORBIDDEN);
        }

        // Get Origin account
        Account fromAccount = accountRepository.findByNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByNumber(toAccountNumber);

        // Verify that origin Account has enough balance
        if (fromAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient funds in source account", HttpStatus.FORBIDDEN);
        }

        // Create debit transaction for Origin account
        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, amount, description, LocalDateTime.now());
        debitTransaction.setAccount(fromAccount);
        fromAccount.addTransaction(debitTransaction);
        fromAccount.setBalance(fromAccount.getBalance() - amount);

        // Create credit transaction for destination account
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        creditTransaction.setAccount(toAccount);
        toAccount.addTransaction(creditTransaction);
        toAccount.setBalance(toAccount.getBalance() + amount);

        // Save transactions and update accounts in the database
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return new ResponseEntity<>("Transaction successful",HttpStatus.CREATED);
    }
}
