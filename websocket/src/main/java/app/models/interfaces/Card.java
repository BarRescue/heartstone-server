package app.models.interfaces;

import app.models.enums.Rarity;

import java.util.UUID;

public interface Card {
    String getName();
    int getMana();
    int getHealth();
    int getDamage();
    boolean getHasAttacked();
    UUID getId();

    void setHealth(int health);
    void setHasAttacked(boolean status);
    boolean isDead();
    void attack(int damage);
}