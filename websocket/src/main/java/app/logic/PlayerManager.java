package app.logic;

import app.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
public class PlayerManager {
    @Getter
    private List<Player> players;

    private Player currentPlayer;

    @Setter
    private int roundNumber;

    public PlayerManager(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
    }

    public void getNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);

        if(currentPlayerIndex == players.size() - 1) {
            this.currentPlayer = players.get(0);
        } else {
            this.currentPlayer = players.get(1);
        }

        this.roundNumber++;

        
    }

    public Optional<Player> getPlayer(Player player) {
        return this.players.stream().filter(p -> p.getId() == player.getId()).findAny();
    }
}
