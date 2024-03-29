package app.entity;

import app.models.Deck;
import app.models.Field;
import app.models.Hand;
import app.models.enums.MonsterType;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;


import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "player")
@Getter @Setter
public class Player implements Serializable {
    @JsonIgnore
    private static final int AMOUNT_OF_CARDS_IN_HAND = 10;

    @Id
    @Type(type = "uuid-char")
    @Setter
    private UUID id;

    @NotBlank
    @Setter
    private String fullName;

    @JsonProperty
    @Setter
    private int gamesWon;

    @JsonProperty
    @Setter
    @Transient
    private transient int amountOfCardsInHand;

    @Transient
    @Setter @Getter
    private transient int hp;

    @Transient
    @Setter @Getter
    private transient int mana;

    @JsonIgnore
    @Transient
    @Getter
    private transient Deck deck;

    @JsonIgnore
    @Transient
    @Getter
    private transient Hand hand;

    @Transient
    @Setter
    @Getter
    private transient Field field;

    @JsonIgnore
    @Transient
    @Setter
    @Getter
    private transient int roundNumber;

    @OneToMany(
        fetch = FetchType.EAGER,
        mappedBy = "player",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonBackReference
    @Getter
    private Set<GamePlayer> games = new HashSet<>();

    public Player() {}

    public Player(UUID id, String fullName, int gamesWon) {
        this.id = id;
        this.fullName = fullName;
        this.gamesWon = gamesWon;
    }

    public boolean prepareForGame() {
        if(deck == null && hand == null && field == null) {
            this.hp = 30;
            this.mana = 1;
            this.hand = new Hand();
            this.deck = new Deck();
            this.field = new Field();

            this.deck.prepare();

            this.amountOfCardsInHand = this.hand.amountOfCards();

            return true;
        }

        return false;
    }

    public boolean hasEnoughMana(int mana) {
        return this.mana >= mana;
    }

    public boolean isDead() {
        return this.hp <= 0;
    }
}
