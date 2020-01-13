package Unit;

import app.entity.Game;
import app.entity.Player;
import app.logic.LobbyLogic;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PlayerPrepareUT {
    private LobbyLogic lobbyLogic;

    private List<Player> players;

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

        Player playerOne = new Player(UUID.randomUUID(), "Rens Manders", 0);
        Player playerTwo = new Player(UUID.randomUUID(), "Piet Manders", 0);

        this.players = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
    }

    @Test
    void playerPrepareOnGameStart() {
        assertTrue(players.get(0).prepareForGame());
    }

    @Test
    void playerPrepareOnGameStartTwice() {
        players.get(0).prepareForGame();

        assertFalse(players.get(0).prepareForGame());
    }


}
