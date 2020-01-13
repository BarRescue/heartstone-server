package Unit;

import app.entity.Game;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.*;
import app.models.Monster;
import app.models.enums.MonsterType;
import app.models.payloads.Action;
import app.models.states.GameState;
import app.repository.GameRepository;
import app.repository.PlayerRepository;
import app.service.GameService;
import app.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameManagerUT {
    private Game game;
    private Game gameTwo;
    private GameManager gameManager;

    @InjectMocks
    private PlayerService playerService;
    @InjectMocks
    private GameService gameService;

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        PlayerLogic playerLogic = new PlayerLogic(playerService);
        GameLogic gameLogic = new GameLogic(gameService, playerService);

        List<Player> players = new ArrayList<>();

        players.add(new Player(UUID.randomUUID(), "Rens Manders", 0));
        players.add(new Player(UUID.randomUUID(), "Piet Manders", 0));

        for(Player p : players) {
            p.prepareForGame();
        }

        this.game = createGameEntity(players, UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
        this.gameTwo = createGameEntity(players, UUID.fromString("123e4567-e89b-42d3-a456-556642440001"));

        this.gameManager = new GameManager();
        this.gameManager.addGame(this.game, players, playerLogic, gameLogic);
        this.gameManager.addGame(this.gameTwo, players, playerLogic, gameLogic);
    }

    @Test
    void resetPrivateMessage() {
        this.gameManager.getBoard(this.game).setPrivateMessage("Unit test");
        this.gameManager.resetPrivateMessage(this.game);
        assertEquals("", this.gameManager.getBoard(this.game).getPrivateMessage());
    }

    @Test
    void gameExistsOnGame() {
        assertNotNull(this.gameManager.getGame(this.game));
    }

    @Test
    void gameNotExistsOnGame() {
        assertNull(this.gameManager.getGame(new Game(UUID.randomUUID())));
    }

    private Game createGameEntity(List<Player> players, UUID id) {
        Game game = new Game(id);
        game.addPlayer(players.get(0));
        game.addPlayer(players.get(1));
        game.setGameStatus(GameStatus.STARTED);

        return game;
    }
}
