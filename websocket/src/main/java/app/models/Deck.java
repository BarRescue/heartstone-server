package app.models;

import app.models.enums.MonsterType;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.*;

public class Deck {
    private Random random = new Random();

    @Getter
    @Setter
    private List<Card> cards = new ArrayList<>();

    public Card takeCard(int index) {
        if(!this.cards.isEmpty()) {
            Card monster = this.cards.get(index);
            this.cards.remove(index);

            return monster;
        }

        return null;
    }

    public void prepare() {
        List<MonsterType> monsterCards = List.of(MonsterType.values());
        final int monsterCardsSize = monsterCards.size();

        for(int i = 0; i < 30; i++) {
            this.cards.add(new Monster(monsterCards.get(random.nextInt(monsterCardsSize))));
        }
    }

    @JsonProperty
    public int amountOfCards() {
        return this.cards.size();
    }
}