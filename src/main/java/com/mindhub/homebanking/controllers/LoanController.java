package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    ClientLoanRepository clientLoanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loan -> new LoanDTO(loan))
                .collect(Collectors.toList());
    }

    @PostMapping("/loans")
    @Transactional
    public ResponseEntity<Object> requestLoan(
            @RequestBody LoanApplicationDTO loanApplicationDTO,
            Authentication authentication) {

        // Verify that all parameters are provided and valid
        if (loanApplicationDTO.getLoanId() == null || loanApplicationDTO.getAmount() <= 0.0 ||
                loanApplicationDTO.getPayments() <= 0 || loanApplicationDTO.getToAccountNumber() == null) {
            return new ResponseEntity<>("Invalid parameters", HttpStatus.FORBIDDEN);
        }

        // Verify if the loan exists
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);
        if (loan == null) {
            return new ResponseEntity<>("Loan does not exist", HttpStatus.FORBIDDEN);
        }

        // Verify if the requested loan amount exceeds the maximum allowed
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Requested loan amount exceeds the maximum allowed", HttpStatus.FORBIDDEN);
        }

        // Verify if the requested number of payments is within the available payments options.
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("Invalid number of payments", HttpStatus.FORBIDDEN);
        }

        // Verify if the destination account exists
        if (!accountRepository.existsByNumber(loanApplicationDTO.getToAccountNumber())) {
            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
        }

        // Get authenticated client
        Client currentClient = clientRepository.findByEmail(authentication.getName());

        // Verify if the destination account belongs to the authenticated client
        if (!currentClient.getAccounts()
                .stream()
                .anyMatch(account -> account.getNumber().equals(loanApplicationDTO.getToAccountNumber()))) {
            return new ResponseEntity<>("Destination account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }

        // Create a loan application or request
        ClientLoan clientLoan = new ClientLoan(
                loanApplicationDTO.getPayments(),
                loanApplicationDTO.getAmount()
        );
        clientLoan.setClient(currentClient);
        clientLoan.setLoan(loan);

        clientLoanRepository.save(clientLoan);

        // Create a credit transaction for the destination account
        Account toAccount = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        String description = loan.getName() + " loan approved";
        double totalAmount = loanApplicationDTO.getAmount() + (loanApplicationDTO.getAmount() * 0.20); // Adding 20%
        Transaction transaction = new Transaction(TransactionType.CREDIT, totalAmount, description, LocalDateTime.now());
        transactionRepository.save(transaction);
        toAccount.addTransaction(transaction);
        accountRepository.save(toAccount);

        return new ResponseEntity<>("Loan application successful", HttpStatus.CREATED);
    }
}

