package app.models;

import app.models.interfaces.Card;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    @Getter
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        this.cards.add(card);
    }
}