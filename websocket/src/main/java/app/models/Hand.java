package app.models;

import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hand {
    @Getter
    @Setter
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void playCard(Card card) {
        this.cards.remove(card);
    }

    @JsonProperty
    public int amountOfCards() {
        return this.cards.size();
    }
}