package models;

import interfaces.IGame;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Game implements IGame {
    @Getter
    private List<Player> players;

    public Game() {
        this.players = new ArrayList<>();
    }

    public void registerPlayer(Player player) {
        this.players.add(player);
    }


}
