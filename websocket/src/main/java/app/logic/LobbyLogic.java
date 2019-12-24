package app.logic;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.service.GameService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class LobbyLogic {
    private GameService gameService;

    @Autowired
    public LobbyLogic(GameService gameService) {
        this.gameService = gameService;
    }

    private Game getWaitingGame(Player player) {
        Optional<Game> game = this.gameService.getWaitingGame().stream().filter(g -> !g.containsPlayer(player)).findFirst();
        return game.orElseGet(() -> this.gameService.createOrUpdate(new Game(player)));
    }

    public Game getStartedGame(Player player) {
        Optional<Game> game = this.gameService.getStartedGame().stream().filter(g -> g.containsPlayer(player)).findFirst();
        return game.orElse(null);
    }

    public Game joinOrCreateGame(Player player) {
        Game foundGame = this.getWaitingGame(player);

        if(!foundGame.containsPlayer(player)) {
            foundGame.addPlayer(player);
            this.updateGame(foundGame);

            return foundGame;
        }

        return foundGame;
    }

    public Optional<Game> findById(UUID id) {
        return this.gameService.findGameByID(id);
    }

    public boolean startGame(Game game) {
        if(game.getPlayers().size() == 2 && game.getGameStatus() == GameStatus.WAITING) {
            game.setGameStatus(GameStatus.STARTED);
            this.updateGame(game);
            return true;
        }

        return false;
    }

    private void updateGame(Game game) {
        this.gameService.createOrUpdate(game);
    }
}
