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
import java.util.UUID;

@Component
public class GameLogic {
    private GameService gameService;
    private PlayerService playerService;

    @Autowired
    public GameLogic(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    public void endGame(Player wonPlayer, Game game) {
        // Update Game
        game.setGameStatus(GameStatus.ENDED);
        this.gameService.createOrUpdate(game);

        // Update Player
        wonPlayer.setGamesWon(wonPlayer.getGamesWon() + 1);
        this.playerService.createOrUpdate(wonPlayer);
    }

    public Optional<Game> findById(UUID id) {
        return this.gameService.findGameByID(id);
    }
}
