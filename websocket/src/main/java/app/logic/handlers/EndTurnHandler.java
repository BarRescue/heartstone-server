package app.logic.handlers;

import app.entity.Game;
import app.entity.Player;
import app.logic.GameManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class EndTurnHandler extends DefaultHandler {
    public EndTurnHandler(SimpMessagingTemplate messagingTemplate, GameManager gameManager) {
        super(messagingTemplate, gameManager);
    }

    public void endTurn(Game game, Player player) {
        if(this.isNotPlayerTurn(game, player)) {
            return;
        }

        gameManager.resetPrivateMessage(game);
        gameManager.getBoard(game).handleNextTurn();

        for(Player p : gameManager.getBoard(game).getStateManager().getPlayers()) {
            this.sendResponse(p.getFullName(), gameManager.getGame(game));
        }
    }
}
