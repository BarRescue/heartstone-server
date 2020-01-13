package app.logic.handlers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.logic.GameLogic;
import app.logic.GameManager;
import app.logic.PlayerLogic;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class PrepareGameHandler extends DefaultHandler {

    public PrepareGameHandler(SimpMessagingTemplate messagingTemplate, GameManager gameManager) {
        super(messagingTemplate, gameManager);
    }

    public void prepare(Game game, Player player, PlayerLogic playerLogic, GameLogic gameLogic) {
        if(!gameManager.gameExists(game)) {
            List<Player> players = game.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
            players.forEach(Player::prepareForGame);
            Collections.shuffle(players);

            gameManager.addGame(game, players, playerLogic, gameLogic);
        }

        Optional<Player> gamePlayer = gameManager.getBoard(game).getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(gamePlayer.isPresent()) {
            this.sendResponse(gamePlayer.get().getFullName(), gameManager.getGame(game));
            this.sendResponse(gamePlayer.get().getFullName(), getPrivateGameState(gamePlayer.get()));
        }
    }

}
