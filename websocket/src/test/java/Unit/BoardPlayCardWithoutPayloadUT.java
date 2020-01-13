package Unit;
import app.entity.Player;
import app.logic.Board;
import app.logic.GameLogic;
import app.logic.PlayerLogic;
import app.logic.StateManager;
import app.models.payloads.Action;
import app.repository.GameRepository;
import app.repository.PlayerRepository;
import app.service.GameService;
import app.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
class BoardPlayCardWithoutPayloadUT {
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;
    private Board board;
    private ObjectMapper mapper;

    private Player playerOne;
    private Action action;

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
        this.playerLogic = new PlayerLogic(playerService);
        this.gameLogic = new GameLogic(gameService, playerService);

        List<Player> players = new ArrayList<>();

        players.add(new Player(UUID.randomUUID(), "Rens Manders", 0));
        players.add(new Player(UUID.randomUUID(), "Piet Manders", 0));

        for(Player p : players) {
            p.prepareForGame();
        }

        this.board = new Board(new StateManager(players), playerLogic, gameLogic);
        this.mapper = new ObjectMapper();
        this.playerOne = this.board.getStateManager().getPlayers().get(0);

        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \"\"}";
        JsonNode node = reader.readTree(jsonString);

        action = new Action();
        action.setPayload(node);
    }

    @Test
    void playCardWithoutPayload() {
        this.playerOne.setMana(999);
        this.board.handleTakeCard();

        assertFalse(this.board.handlePlayCard(action));
    }
}
