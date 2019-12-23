package app.models.states;

import app.models.Deck;
import app.models.Hand;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PrivateGameState {
    String type = "private_game_state";
    Deck deck;
    Hand hand;

    public PrivateGameState(Deck deck, Hand hand) {
        this.deck = deck;
        this.hand = hand;
    }
}
