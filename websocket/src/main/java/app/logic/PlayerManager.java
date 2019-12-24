package app.logic;

import app.entity.Player;
import app.models.Hand;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PlayerManager {
    @Getter
    private List<Player> players;

    private Player currentPlayer;

    public PlayerManager(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
    }

    public Hand getPlayerCards(Player player) {
        return players.stream()
                .filter(p -> p.getId() == player.getId())
                .findFirst()
                .get().getHand();
    }

    public Optional<Player> getPlayer(Player player) {
        return this.players.stream().filter(p -> p.getId() == player.getId()).findAny();
    }
}
