package app.models.interfaces;

import app.models.enums.Rarity;

public interface Card {
    String getName();
    int getMana();
    int getHealth();
    int getDamage();
    Rarity getRarity();
    String getPath();
}