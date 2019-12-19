package app.entity;

import app.models.enums.MonsterType;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Setter
    private String fullName;

    @Transient
    @Setter @Getter
    private transient int hp;

    @Transient
    @Setter @Getter
    private transient int mana;

    @Transient
    @Getter
    private transient List<Card> cards = new ArrayList<>();

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

    public Player(UUID id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public void prepareForGame() {
        this.hp = 30;
        this.mana = 1;
    }

    public void giveCard(Card card) {
        this.cards.add(card);
    }

    public Card removeCard(MonsterType monsterType) {
        return cards.remove(cards.indexOf(cards.stream()
            .filter(card -> card.getName().equals(monsterType.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Player does not have " + monsterType.getName() + " to be removed."))
        ));
    }

    @JsonProperty
    public int amountOfCards() {
        return this.cards.size();
    }
}
