package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
//import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository) {
		return (args) -> {
			//anidado como en la documentación
			//clients
//			clientRepository.save(new Client("melba@mindhub.com","Melba","Morel"));
//			clientRepository.save(new Client("espinosa@hotmail.com","Cristian","Espinosa"));
			//no anidado como en el workshop
			Client client1 = new Client("melba@mindhub.com","Melba","Morel");
			clientRepository.save(client1);
			Client client2 = new Client("espinosa@hotmail.com", "Cris", "Tian");
			clientRepository.save(client2);

//			anidado no funciona, da error, no reconoce el objeto client

			//accounts
//			accountRepository.save(client1.addAccount(new Account("VIN001", LocalDate.now(),5000)));
//			accountRepository.save(new Account("VIN002", LocalDate.now().plusDays(1),7500));

			Account account1 = new Account("VIN001", LocalDate.now(),5000);
			client1.addAccount(account1);
			accountRepository.save(account1);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1),7500);
			client1.addAccount(account2);
			accountRepository.save(account2);
			Account account3 = new Account("VIN003", LocalDate.now().minusDays(2),6000);
			client2.addAccount(account3);
			accountRepository.save(account3);

			//transactions
			Transaction transaction1 = new Transaction(TransactionType.DEBIT, 1000, "this is only a test part1", LocalDate.now());
			account1.addTransaction(transaction1);
			transactionRepository.save(transaction1);
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 500, "this is only a test part2", LocalDate.now());
			account1.addTransaction(transaction2);
			transactionRepository.save(transaction2);
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, 1000, "this is only a test part1", LocalDate.now());
			account3.addTransaction(transaction3);
			transactionRepository.save(transaction3);
			Transaction transaction4 = new Transaction(TransactionType.CREDIT, 500, "this is only a test part2", LocalDate.now());
			account3.addTransaction(transaction4);
			transactionRepository.save(transaction4);

			//loans
			Loan loan1 = new Loan("Hipotecario", 500000, List.of(12,24,36,48,60));
			loanRepository.save(loan1);
			Loan loan2 = new Loan("Personal", 100000, List.of(6,12,24));
			loanRepository.save(loan2);
			Loan loan3 = new Loan("Automotriz", 300000, List.of(6,12,24,36));
			loanRepository.save(loan3);

			//loans requests (clientLoans)
			//ClientLoan clientLoan1 = new ClientLoan()

		};
	}
}
	