package app.logic.handlers;

import app.entity.Game;
import app.entity.Player;
import app.logic.GameManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

public class TakeCardHandler extends DefaultHandler {

    public TakeCardHandler(SimpMessagingTemplate messagingTemplate, GameManager gameManager) {
        super(messagingTemplate, gameManager);
    }

    public void takeCard(Game game, Player player) {
        Optional<Player> gamePlayer = gameManager.getBoard(game).getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(this.isNotPlayerTurn(game, player)) {
            return;
        }

        if(gamePlayer.isPresent()) {
            if(!gameManager.getBoard(game).handleTakeCard()) {
                this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(game));
                return;
            }

            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(game));
            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateGameState(gamePlayer.get()));

            for(Player p : gameManager.getBoard(game).getStateManager().getPlayers()) {
                this.sendResponse(p.getFullName(), gameManager.getGame(game));
            }
        }
    }
}
