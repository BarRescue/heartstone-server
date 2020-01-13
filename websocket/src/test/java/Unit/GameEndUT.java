package Unit;

import app.entity.Game;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.*;
import app.models.states.GameState;
import app.repository.GameRepository;
import app.repository.PlayerRepository;
import app.service.GameService;
import app.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameEndUT {
    private LobbyLogic lobbyLogic;
    private GameLogic gameLogic;
    private PlayerLogic playerLogic;

    private Board board;
    private Game game;

    @InjectMocks
    private PlayerService playerService;
    @InjectMocks
    private GameService gameService;

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        this.lobbyLogic = new LobbyLogic(gameService);
        this.gameLogic = new GameLogic(gameService, playerService);
        this.playerLogic = new PlayerLogic(playerService);

        List<Player> players = new ArrayList<>();

        players.add(new Player(UUID.randomUUID(), "Rens Manders", 0));
        players.add(new Player(UUID.randomUUID(), "Piet Manders", 0));

        for(Player p : players) {
            p.prepareForGame();
        }

        this.game = createGameEntity(players);
        this.board = new Board(new StateManager(players), playerLogic, gameLogic);

        lobbyLogic.startGame(game);
    }

    @Test
    void endGameWonPlayerDoesNotHaveHp() {
        this.board.getStateManager().getCurrentPlayer().setHp(0);
        gameLogic.endGame(this.board.getStateManager().getCurrentPlayer(), game);

        assertEquals(GameStatus.STARTED, game.getGameStatus());
    }

    @Test
    void endGameWonPlayerHasHp() {
        this.board.getStateManager().getPlayers().get(1).setHp(0);
        gameLogic.endGame(this.board.getStateManager().getCurrentPlayer(), game);

        assertEquals(GameStatus.ENDED, game.getGameStatus());
    }

    @Test
    void endGameIfPlayerIsDead() {
        this.board.getStateManager().getCurrentPlayer().setHp(0);

        gameLogic.endGame(this.board.getStateManager().getCurrentPlayer(), game);

        assertEquals(GameStatus.STARTED, game.getGameStatus());
    }

    @Test
    void endGameIsPlayerIsNotDead() {
        this.board.getStateManager().getPlayers().get(1).setHp(0);

        gameLogic.endGame(this.board.getStateManager().getCurrentPlayer(), game);

        assertEquals(GameStatus.ENDED, game.getGameStatus());
    }

    private Game createGameEntity(List<Player> players) {
        Game game = new Game(UUID.randomUUID());
        game.addPlayer(players.get(0));
        game.addPlayer(players.get(1));
        game.setGameStatus(GameStatus.STARTED);

        return game;
    }
}
