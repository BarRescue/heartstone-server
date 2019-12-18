package models;

import interfaces.ICard;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<ICard> cards = new ArrayList<>();

    public void addCard(ICard card) {
        this.cards.add(card);
    }

    public void removeCard(ICard card) {
        this.cards.remove(card);
    }
}
