package app.logic;

import app.entity.Player;
import app.models.interfaces.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
public class StateManager {
    @Getter
    private List<Player> players;

    private Player currentPlayer;

    public StateManager(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0);
    }

    public void getNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        this.currentPlayer.setRoundNumber(this.currentPlayer.getRoundNumber() + 1);

        resetPlayerCards(this.currentPlayer);

        if(currentPlayerIndex == players.size() - 1) {
            this.currentPlayer = players.get(0);
            this.addManaToPlayer(players.get(1));
        } else {
            this.currentPlayer = players.get(1);
            this.addManaToPlayer(players.get(0));
        }
    }

    Player getEnemyPlayer() {
        return this.players.stream().filter(p -> !p.getId().equals(this.currentPlayer.getId())).findFirst().orElse(null);
    }

    private void resetPlayerCards(Player player) {
        for(Card card : player.getField().getCards()) {
            card.setHasAttacked(false);
        }
    }

    private void addManaToPlayer(Player player) {
        player.setMana(player.getRoundNumber() >= 8 ? player.getMana() + 8 : player.getMana() + player.getRoundNumber());
    }

    public boolean isPlayerTurn(Player player) {
        return this.currentPlayer.getId().equals(player.getId());
    }
}
