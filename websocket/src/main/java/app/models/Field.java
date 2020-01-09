package app.models;

import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Field {
    @JsonIgnore
    private static final int MAX_CARDS = 5;

    @JsonProperty
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public boolean hasMaxCards() {
        return this.cards.size() == MAX_CARDS;
    }

    public Card findCardById(UUID id) {
        return this.cards.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }
}
