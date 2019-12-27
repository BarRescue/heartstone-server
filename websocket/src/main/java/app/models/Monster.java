package app.models;

import app.models.enums.MonsterType;
import app.models.enums.Rarity;
import app.models.interfaces.Card;
import lombok.Getter;
import org.springframework.core.io.Resource;

import javax.persistence.Convert;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

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
        this.path = monsterType.getName().toLowerCase() + ".png";

        String file = new File("resources/images/" + monsterType.getName().toLowerCase() + ".png").getPath();
    }
}