package app.logic;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.service.GameService;
import app.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameLogic {
    private GameService gameService;
    private PlayerService playerService;

    @Autowired
    public GameLogic(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    public void endGame(GamePlayer player, Game game) {
        Player actualPlayer = player.getPlayer();

        // Update Game
        game.setGameStatus(GameStatus.ENDED);
        this.gameService.createOrUpdate(game);

        // Update Player
        actualPlayer.setGamesWon(actualPlayer.getGamesWon() + 1);
        this.playerService.createOrUpdate(actualPlayer);
    }
}
