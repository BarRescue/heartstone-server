package app.models;

import app.models.enums.MonsterType;
import app.models.enums.Rarity;
import app.models.interfaces.Card;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import javax.persistence.Convert;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@Setter
public class Monster implements Card {
    private UUID id;
    private Rarity rarity;
    private String name;
    private int health;
    private int damage;
    private int mana;
    private String path;
    private boolean hasAttacked = false;

    public Monster(MonsterType monsterType) {
        this.id = UUID.randomUUID();
        this.rarity = monsterType.getRarity();
        this.name = monsterType.getName();
        this.health = monsterType.getHealth();
        this.damage = monsterType.getDamage();
        this.mana = monsterType.getMana();
        this.path = monsterType.getName().toLowerCase() + ".jpg";

        new File("resources/images/" + monsterType.getName() + ".jpg");
    }

    @Override
    public boolean isDead() {
        return this.health <= 0;
    }

    @Override
    public void setHasAttacked(boolean status) {
        this.hasAttacked = status;
    }

    @Override
    public boolean getHasAttacked() {
        return this.hasAttacked;
    }
}