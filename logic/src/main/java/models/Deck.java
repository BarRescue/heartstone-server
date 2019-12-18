package models;

import interfaces.ICard;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    @Getter
    private List<ICard> cards = new ArrayList<>();

    public void addCard(ICard card) {
        this.cards.add(card);
    }
}
