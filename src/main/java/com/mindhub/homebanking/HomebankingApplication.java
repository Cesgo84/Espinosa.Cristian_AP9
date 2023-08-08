package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository) {
		return (args) -> {
			//anidado como en la documentación
//			clientRepository.save(new Client("melba@mindhub.com","Melba","Morel"));
//			clientRepository.save(new Client("espinosa@hotmail.com","Cristian","Espinosa"));
			//no anidado como en el workshop
			Client client1 = new Client("melba@mindhub.com","Melba","Morel");
			clientRepository.save(client1);
			Client client2 = new Client("espinosa@hotmail.com", "Cris", "Tian");
			clientRepository.save(client2);

//			anidado no funciona, da error, no reconoce el objeto client
//			accountRepository.save(client1.addAccount(new Account("VIN001", LocalDate.now(),5000)));
//			accountRepository.save(new Account("VIN002", LocalDate.now().plusDays(1),7500));
			Account account1 = new Account("VIN001", LocalDate.now(),5000);
			client1.addAccount(account1);
			accountRepository.save(account1);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1),7500);
			client1.addAccount(account2);
			accountRepository.save(account2);

		};

	}
}
	