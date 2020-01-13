package Unit;

import app.entity.Game;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.Board;
import app.logic.GameLogic;
import app.logic.PlayerLogic;
import app.logic.StateManager;
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
class BoardAttackCardWithoutCardUT {
    private Board board;
    private Game game;

    private ObjectMapper mapper;
    private Player playerOne;
    private Player playerTwo;
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
        PlayerLogic playerLogic = new PlayerLogic(playerService);
        GameLogic gameLogic = new GameLogic(gameService, playerService);

        List<Player> players = new ArrayList<>();

        players.add(new Player(UUID.randomUUID(), "Rens Manders", 0));
        players.add(new Player(UUID.randomUUID(), "Piet Manders", 0));

        for(Player p : players) {
            p.prepareForGame();
        }

        this.game = createGameEntity(players);
        this.board = new Board(new StateManager(players), playerLogic, gameLogic);
        this.playerOne = this.board.getStateManager().getPlayers().get(0);
        this.playerTwo = this.board.getStateManager().getPlayers().get(1);

        this.playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));

        this.mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \"\"}";

        JsonNode node = reader.readTree(jsonString);

        this.action = new Action();
        action.setPayload(node);
    }

    @Test
    void attackEnemyWithoutCardsOnField() {
        this.board.handleAttack(action, this.game);

        assertEquals(27, playerTwo.getHp());
    }

    @Test
    void attackEnemyWithCardsOnField() {
        this.playerTwo.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));

        assertFalse(this.board.handleAttack(action, this.game));
    }

    @Test
    void attackEnemyKillEnemy() {
        this.playerTwo.setHp(1);

        this.board.handleAttack(action, this.game);

        assertTrue(playerTwo.isDead());
    }

    @Test
    void attackEnemyKillEnemyEndGame() {
        this.playerTwo.setHp(1);

        this.board.handleAttack(action, this.game);

        assertEquals(GameStatus.ENDED, this.game.getGameStatus());
    }

    @Test
    void attackTwiceWithCard() {
        this.board.handleAttack(action, this.game);

        // Card has Attacked is set to True
        assertFalse(this.board.handleAttack(action, this.game));
    }



    private Game createGameEntity(List<Player> players) {
        Game game = new Game(UUID.randomUUID());
        game.addPlayer(players.get(0));
        game.addPlayer(players.get(1));
        game.setGameStatus(GameStatus.STARTED);

        return game;
    }
}
