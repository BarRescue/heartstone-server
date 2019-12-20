package app.logic;

import app.entity.Player;
import lombok.Getter;

import java.util.List;

@Getter
public class PlayerManager {
    private List<Player> players;
    private Player currentPlayer;

    public PlayerManager(List<Player> players) {
        this.players = players;
    }


}
