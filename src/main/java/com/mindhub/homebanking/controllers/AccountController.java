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
        // Get authenticated Client
        Client currentClient = clientRepository.findByEmail(authentication.getName());

        // get current client's Accounts
        return currentClient.getAccounts()
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
//
//package com.mindhub.homebanking.controllers;
//
//        import com.mindhub.homebanking.models.Account;
//        import com.mindhub.homebanking.models.Client;
//        import com.mindhub.homebanking.models.Transaction;
//        import com.mindhub.homebanking.models.TransactionType;
//        import com.mindhub.homebanking.repositories.AccountRepository;
//        import com.mindhub.homebanking.repositories.ClientRepository;
//        import com.mindhub.homebanking.repositories.TransactionRepository;
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.http.HttpStatus;
//        import org.springframework.http.ResponseEntity;
//        import org.springframework.security.core.Authentication;
//        import org.springframework.web.bind.annotation.*;
//
//        import java.time.LocalDateTime;
//        import java.util.Optional;
//
//@RestController
//@RequestMapping("/api")
//public class TransactionController {
//    @Autowired
//    private ClientRepository clientRepository;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @PostMapping("/transactions")
//    public ResponseEntity<Object> createTransaction(
//            @RequestParam String fromAccountNumber,
//            @RequestParam String toAccountNumber,
//            @RequestParam double amount,
//            @RequestParam String description,
//            Authentication authentication) {
//        // Get the authenticated client
//        Client currentClient = clientRepository.findByEmail(authentication.getName());
//
//        // Check if all parameters are provided
//        if (fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() || description.isEmpty()) {
//            return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
//        }
//
//        // Check if account numbers are the same
//        if (fromAccountNumber.equals(toAccountNumber)) {
//            return new ResponseEntity<>("Origin and destination accounts cannot be the same", HttpStatus.BAD_REQUEST);
//        }
//
//        // Check if accounts exist
//        Optional<Account> fromAccountOpt = accountRepository.findByNumber(fromAccountNumber);
//        Optional<Account> toAccountOpt = accountRepository.findByNumber(toAccountNumber);
//
//        if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
//            return new ResponseEntity<>("Invalid account number(s)", HttpStatus.BAD_REQUEST);
//        }
//
//        Account fromAccount = fromAccountOpt.get();
//        Account toAccount = toAccountOpt.get();
//
//        // Check if the fromAccount belongs to the authenticated client
//        if (!fromAccount.getClient().equals(currentClient)) {
//            return new ResponseEntity<>("Origin account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
//        }
//
//        // Check if the fromAccount has sufficient balance
//        if (fromAccount.getBalance() < amount) {
//            return new ResponseEntity<>("Insufficient balance in the origin account", HttpStatus.BAD_REQUEST);
//        }
//
//        // Create two transactions: DEBIT for fromAccount and CREDIT for toAccount
//        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, LocalDateTime.now(), -amount, description, fromAccount);
//        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, LocalDateTime.now(), amount, description, toAccount);
//
//        // Update account balances
//        fromAccount.setBalance(fromAccount.getBalance() - amount);
//        toAccount.setBalance(toAccount.getBalance() + amount);
//
//        // Save transactions and accounts
//        transactionRepository.save(debitTransaction);
//        transactionRepository.save(creditTransaction);
//        accountRepository.save(fromAccount);
//        accountRepository.save(toAccount);
//
//        return new ResponseEntity<>("Transaction completed successfully", HttpStatus.CREATED);
//    }
//}
//
