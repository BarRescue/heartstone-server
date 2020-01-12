package app.models;

import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Hand {
    @JsonIgnore
    private static final int MAX_CARDS = 10;

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

    public boolean hasMaxCards() {
        return this.cards.size() == MAX_CARDS;
    }

    public Card findCardById(UUID id) {
        if(id != null) {
            return this.cards.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        }

        return null;
    }
}