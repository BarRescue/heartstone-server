package app.models;

import app.models.enums.MonsterType;
import app.models.enums.Rarity;
import app.models.interfaces.Card;
import lombok.Getter;

@Getter
public class Monster implements Card {
    private Rarity rarity;
    private String name;
    private int health;
    private int damage;
    private int mana;
    private String path;

    public Monster(MonsterType monsterType) {
        this.rarity = monsterType.getRarity();
        this.name = monsterType.getName();
        this.health = monsterType.getHealth();
        this.damage = monsterType.getDamage();
        this.mana = monsterType.getMana();
        this.path = "/images/" + monsterType.getName() + ".jpg";
    }
}