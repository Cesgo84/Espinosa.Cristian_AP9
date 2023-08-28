package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    ClientRepository clientRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<String> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication) {
        // get current Client
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        // verify that currentClient don't have more than 3 accounts
        if (cardRepository.countByClientIdAndType(currentClient.getId(), cardType)>=3){
            return new ResponseEntity<>("you already reach the number of "+ cardType.toString() +"Card allowed (3).", HttpStatus.FORBIDDEN);
        }
        // create new Card and verify if the Card number is already in use in the database until found a free number
        Card newCard;
        do {
            newCard = new Card((currentClient.getFirstName()+" "+currentClient.getLastName()), cardType, cardColor, null, null, LocalDate.now(), LocalDate.now().plusYears(5));
        } while (cardRepository.existsByNumber(newCard.getNumber()));
        // add newCard to currentClient and save it in the Data Base
        currentClient.addCard(newCard);
        clientRepository.save(currentClient);
        cardRepository.save(newCard);
        return new ResponseEntity<>("Card created successfully", HttpStatus.CREATED);
    }

}