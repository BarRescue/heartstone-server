package Unit;

import app.entity.Game;
import app.entity.Player;
import app.logic.LobbyLogic;
import app.logic.PlayerLogic;
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
class PlayerStartGameUT {
    private LobbyLogic lobbyLogic;

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

        Player playerOne = new Player(UUID.randomUUID(), "Rens Manders", 1);
        Player playerTwo = new Player(UUID.randomUUID(), "Piet Manders", 0);

        // Player
        lenient().when(playerRepository.findById(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).thenReturn(Optional.of(playerOne));
        lenient().when(playerRepository.findById(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))).thenReturn(Optional.of(playerTwo));
        lenient().when(playerRepository.save(Mockito.any(Player.class))).thenAnswer(i -> i.getArguments()[0]);

        Game game = new Game(UUID.randomUUID());

        // Game
        lenient().when(gameRepository.save(Mockito.any(Game.class))).thenReturn(game);
    }

    @Test
    void startGameOnePlayer() {
        Optional<Player> playerOne = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));

        Game game = lobbyLogic.joinOrCreateGame(playerOne.get());

        assertFalse(lobbyLogic.startGame(game));
    }

    @Test
    void startGameTwoPlayers() {
        Optional<Player> playerOne = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
        Optional<Player> playerTwo = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"));


        Game game = lobbyLogic.joinOrCreateGame(playerOne.get());
        game = lobbyLogic.joinOrCreateGame(playerTwo.get());

        assertTrue(lobbyLogic.startGame(game));
    }
}
