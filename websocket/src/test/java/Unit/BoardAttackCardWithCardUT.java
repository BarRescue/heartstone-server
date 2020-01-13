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
class BoardAttackCardWithCardUT {
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
        this.playerTwo.getField().addCard(new Monster(MonsterType.ARCHMAGE));

        this.mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \""+ playerTwo.getField().getCards().get(0).getId().toString() +"\"}";

        JsonNode node = reader.readTree(jsonString);

        this.action = new Action();
        action.setPayload(node);
    }

    @Test
    void attackEnemyCardDeflectDamageToEnemy() {
        this.playerTwo.getField().getCards().get(0).setHealth(2);

        this.board.handleAttack(action, this.game);

        assertEquals(29, playerTwo.getHp());
    }

    @Test
    void attackEnemyCardDeflectDamageToPlayer() {
        this.board.handleAttack(action, this.game);

        assertEquals(25, playerOne.getHp());
    }

    @Test
    void attackEnemyDeflectDamageKillsPlayer() {
        this.playerOne.setHp(1);

        this.board.handleAttack(action, this.game);

        assertTrue(this.playerOne.isDead());
    }

    @Test
    void attackEnemyDeflectDamageKillsEnemy() {
        this.playerTwo.setHp(1);
        this.playerTwo.getField().getCards().get(0).setHealth(1);

        this.board.handleAttack(action, this.game);

        assertTrue(this.playerTwo.isDead());
    }

    @Test
    void attackEnemyDeflectDamageKillsPlayerEndGame() {
        this.playerOne.setHp(1);

        this.board.handleAttack(action, this.game);

        assertEquals(GameStatus.ENDED, this.game.getGameStatus());
    }

    @Test
    void attackEnemyDeflectDamageKillsEnemyEndGame() {
        this.playerTwo.setHp(1);
        this.playerTwo.getField().getCards().get(0).setHealth(1);

        this.board.handleAttack(action, this.game);

        assertEquals(GameStatus.ENDED, this.game.getGameStatus());
    }

    @Test
    void increaseGamesWonEnemyPlayer() {
        this.playerOne.setHp(1);

        this.board.handleAttack(action, this.game);

        assertEquals(1, playerTwo.getGamesWon());
    }

    @Test
    void increaseGamesWonPlayer() {
        this.playerTwo.setHp(1);
        this.playerTwo.getField().getCards().get(0).setHealth(1);

        this.board.handleAttack(action, this.game);

        assertEquals(1, playerOne.getGamesWon());
    }

    @Test
    void attackEnemyCardDecreaseEnemyCardHealth() {
        this.board.handleAttack(action, this.game);

        assertEquals(4, this.playerTwo.getField().getCards().get(0).getHealth());
    }

    @Test
    void attackEnemyCardDecreasePlayerCardHealth() {
        this.playerOne.getField().getCards().get(0).setHealth(10);

        this.board.handleAttack(action, this.game);

        assertEquals(3, this.playerOne.getField().getCards().get(0).getHealth());
    }

    @Test
    void killEnemyCardCheckDiscardPile() {
        this.playerTwo.getField().getCards().get(0).setHealth(1);

        this.board.handleAttack(action, this.game);

        assertEquals(1, this.board.getDiscardPile().amountOfCards());
    }

    @Test
    void killPlayerCardCheckDiscardPile() {
        this.board.handleAttack(action, this.game);

        assertEquals(1, this.board.getDiscardPile().amountOfCards());
    }

    private Game createGameEntity(List<Player> players) {
        Game game = new Game(UUID.randomUUID());
        game.addPlayer(players.get(0));
        game.addPlayer(players.get(1));
        game.setGameStatus(GameStatus.STARTED);

        return game;
    }
}
