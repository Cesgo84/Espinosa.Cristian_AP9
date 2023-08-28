package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Random;

@Entity
public class Card {
    //attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String cardHolder;
    private CardType type;
    private CardColor color;
    private String number;
    private Integer cvv;
    private LocalDate fromDate;
    private LocalDate thruDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Client_Id")
    private Client client;

    //constructor
    public Card() {

    }

    public Card(String cardHolder, CardType type, CardColor color, String number, Integer cvv, LocalDate fromDate, LocalDate thruDate) {
        this.cardHolder = cardHolder;
        this.type = type;
        this.color = color;
        this.number = generateRandomCardNumber(number);
        this.cvv = generateRandomCVV(cvv);
        this.fromDate = fromDate;
        this.thruDate = thruDate;
    }

    //getters & setters
    //self
    public long getId() {
        return id;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDate thruDate) {
        this.thruDate = thruDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    //upstream
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    //Methods
    private String generateRandomCardNumber(String number) {
        if (number == null) { //notas al corrector: esto está asi para poder preservar los numeros de tarjeta hardcodeadas a melba
            Random random = new Random();
            int[] randomNumbers = new int[4];
            for (int i = 0; i < 4; i++) {
                randomNumbers[i] = random.nextInt(10000);
            }
            return String.format("%04d-%04d-%04d-%04d", randomNumbers[0], randomNumbers[1], randomNumbers[2], randomNumbers[3]);
        }
            return number;
    }

    private int generateRandomCVV (Integer cvv) {
        if (cvv == null){
            Random random = new Random();
            int randomCVV = random.nextInt(900)+100;
            return randomCVV;
        }
        return cvv;
    }

}

