package app.logic.handlers;

import app.entity.Game;
import app.entity.Player;
import app.logic.GameManager;
import app.models.payloads.Action;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

public class AttackHandler extends DefaultHandler {

    public AttackHandler(SimpMessagingTemplate messagingTemplate, GameManager gameManager) {
        super(messagingTemplate, gameManager);
    }

    public void attack(Game game, Player player, Action action) {
        Optional<Player> gamePlayer = gameManager.getBoard(game).getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(this.isNotPlayerTurn(game, player)) {
            return;
        }

        if(gamePlayer.isPresent()) {
            if(!gameManager.getBoard(game).handleAttack(action, game)) {
                this.sendResponse(player.getFullName(), this.getPrivateMessage(game));
                return;
            }

            this.sendResponse(gamePlayer.get().getFullName(), getPrivateMessage(game));

            for (Player p : gameManager.getBoard(game).getStateManager().getPlayers()) {
                this.sendResponse(p.getFullName(), gameManager.getGame(game));
            }
        }
    }

}
