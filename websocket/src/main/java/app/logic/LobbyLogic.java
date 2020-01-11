package app.logic;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LobbyLogic {
    private GameService gameService;

    @Autowired
    public LobbyLogic(GameService gameService) {
        this.gameService = gameService;
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
            List<Player> players = game.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
            players.forEach(Player::prepareForGame);

            this.updateGame(game);
            return true;
        }

        return false;
    }

    private Game getWaitingGame(Player player) {
        Optional<Game> game = this.gameService.getWaitingGame().stream().filter(g -> !g.containsPlayer(player)).findFirst();
        return game.orElseGet(() -> this.gameService.createOrUpdate(new Game(player)));
    }

    private void updateGame(Game game) {
        this.gameService.createOrUpdate(game);
    }
}
