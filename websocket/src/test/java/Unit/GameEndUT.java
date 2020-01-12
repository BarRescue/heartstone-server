package Unit;

import app.entity.Game;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.GameLogic;
import app.logic.LobbyLogic;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameEndUT {
    private LobbyLogic lobbyLogic;
    private GameLogic gameLogic;

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

        Player playerOne = new Player(UUID.randomUUID(), "Rens Manders", 0);
        Player playerTwo = new Player(UUID.randomUUID(), "Piet Manders", 0);

        // Player
        lenient().when(playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).thenReturn(Optional.of(playerOne));
        lenient().when(playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))).thenReturn(Optional.of(playerTwo));
        lenient().when(playerService.createOrUpdate(Mockito.any(Player.class))).thenAnswer(i -> i.getArguments()[0]);

        Game game = new Game(UUID.randomUUID());

        // Game
        lenient().when(gameService.createOrUpdate(Mockito.any(Game.class))).thenReturn(game);
    }

    @Test
    void endGameWonPlayerDoesNotHaveHp() {
        Optional<Player> playerOne = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
        Optional<Player> playerTwo = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"));

        Game game = lobbyLogic.joinOrCreateGame(playerOne.get());
        game = lobbyLogic.joinOrCreateGame(playerTwo.get());

        lobbyLogic.startGame(game);

        playerOne.get().setHp(0);
        gameLogic.endGame(playerOne.get(), game);

        assertEquals(GameStatus.STARTED, game.getGameStatus());
    }

    @Test
    void endGameWonPlayerHasHp() {
        Optional<Player> playerOne = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
        Optional<Player> playerTwo = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"));

        Game game = lobbyLogic.joinOrCreateGame(playerOne.get());
        game = lobbyLogic.joinOrCreateGame(playerTwo.get());

        lobbyLogic.startGame(game);

        playerTwo.get().setHp(0);
        gameLogic.endGame(playerOne.get(), game);

        assertEquals(GameStatus.ENDED, game.getGameStatus());
    }
}
