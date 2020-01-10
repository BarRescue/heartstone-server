package app.logic;

import app.entity.Game;
import app.entity.Player;
import app.models.states.GameState;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GameManager {
    private static Map<UUID, GameState> games = new HashMap<>();

    public Board getBoard(Game game) {
        return games.get(game.getId()).getBoard();
    }

    public GameState getGame(Game game) {
        return games.get(game.getId());
    }

    public boolean gameExists(Game game) {
        return games.containsKey(game.getId());
    }

    public void addGame(Game game, List<Player> players, PlayerLogic playerLogic, GameLogic gameLogic) {
        games.put(game.getId(), new GameState(new Board(new StateManager(players), playerLogic, gameLogic)));
    }

    public void resetPrivateMessage(Game game) {
        if(games.containsKey(game.getId())) {
            games.get(game.getId()).getBoard().setPrivateMessage("");
        }
    }
}
