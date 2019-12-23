package app.logic;

import app.entity.Player;
import app.models.Hand;
import app.models.interfaces.Card;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
