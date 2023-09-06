package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    ClientRepository clientRepository;

    @GetMapping("/cards")
    public List<CardDTO> getCards() {
        return cardRepository.findAll()
                .stream()
                .map(client -> new CardDTO(client))
                .collect(Collectors.toList());
    }

    @GetMapping("/cards/{id}")
    public CardDTO getCardById(@PathVariable long id) {
        return new CardDTO(cardRepository.findById(id).get());
    }

    @GetMapping("/clients/current/cards")
    @ResponseBody
    public List<CardDTO> getCurrentClientCards(Authentication authentication) {
        // get authenticated client
        Client currentClient = clientRepository.findByEmail(authentication.getName());

        // get current client's cards
        return currentClient.getCards()
                .stream()
                .map(card -> new CardDTO(card))
                .collect(Collectors.toList());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication) {
        // get current Client
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        //Si solo puede tener una tarjeta de cada una (type/color) comprobar la cantidad pierde sentido, solo hya que verificar que no exista ya una tarjeta con esos parametros (lo que me hace pensar que se esta malinterpretando la consigna)
        // verify that currentClient don't have more than 3 accounts
//        if (cardRepository.countByClientIdAndType(currentClient.getId(), cardType)>=3 || ){
//                return new ResponseEntity<>("you already reach the number of "+ cardType.toString() +"Card allowed (3).", HttpStatus.FORBIDDEN);
//        }
        // verify that doesn´t exist another card like the one requested.
        boolean cardExists = currentClient
                .getCards()
                .stream()
                .anyMatch(card ->
                        card.getType() == cardType && card.getColor() == cardColor);
        if (cardExists) {
            return new ResponseEntity<>("Already exist a "+ cardColor + " " + cardType + " card", HttpStatus.FORBIDDEN);
        }
        // create new Card and verify if the Card number is already in use in the database until found a free number
        Card newCard;
        do {
            newCard = new Card((currentClient.getFirstName()+" "+currentClient.getLastName()), cardType, cardColor, null, null, LocalDate.now(), LocalDate.now().plusYears(5));
        } while (cardRepository.existsByNumber(newCard.getNumber()));
        // add newCard to currentClient and save it in the Data Base
        currentClient.addCard(newCard);
        cardRepository.save(newCard);
        return new ResponseEntity<>("Card created successfully", HttpStatus.CREATED);
    }

}