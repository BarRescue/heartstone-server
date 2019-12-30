package app.models.interfaces;

import app.models.enums.Rarity;

import java.net.URI;

public interface Card {
    String getName();
    int getMana();
    int getHealth();
    int getDamage();
    Rarity getRarity();
    String getPath();
    boolean getHasAttacked();

    void setHealth(int health);
    void setHasAttacked(boolean status);
    boolean isDead();
}