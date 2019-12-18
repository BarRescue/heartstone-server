package models;

import enums.MonsterType;
import enums.Rarity;
import interfaces.ICard;
import lombok.Getter;

public class Card implements ICard {
    @Getter
    private Rarity rarity;

    @Getter
    private String name;

    @Getter
    private int health;

    @Getter
    private int damage;

    @Getter
    private int mana;

    public Card(MonsterType monsterType) {
        this.rarity = monsterType.getRarity();
        this.name = monsterType.getName();
        this.health = monsterType.getHealth();
        this.damage = monsterType.getDamage();
        this.mana = monsterType.getMana();
    }
}
