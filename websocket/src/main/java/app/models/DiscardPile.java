package app.models;

import app.entity.Player;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class DiscardPile {
    @JsonProperty
    HashMap<Player, Card> cards = new HashMap<>();

    public void addCard(Player player, Card card) {
        if(player != null && card != null) {
            this.cards.put(player, card);
        }
    }

    @JsonProperty
    public int amountOfCards() {
        return this.cards.size();
    }
}
