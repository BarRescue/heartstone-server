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
class BoardAttackCardUT {
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


    }

    @Test
    void attackEnemyCardWithMoreDamage() {
        Player playerOne = this.board.getStateManager().getPlayers().get(0);
        Player playerTwo = this.board.getStateManager().getPlayers().get(1);

        playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));
        playerTwo.getField().addCard(new Monster(MonsterType.BLOODFEN_RAPTOR));

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \""+ playerTwo.getField().getCards().get(0).getId().toString() +"\"}";

        try {
            JsonNode node = reader.readTree(jsonString);

            Action action = new Action();
            action.setPayload(node);

            this.board.handleAttack(action, this.game);

            assertEquals(29, playerTwo.getHp());

        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void attackEnemyCardWithMoreHealth() {
        Player playerOne = this.board.getStateManager().getPlayers().get(0);
        Player playerTwo = this.board.getStateManager().getPlayers().get(1);

        playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));
        playerTwo.getField().addCard(new Monster(MonsterType.ARCHMAGE));

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \""+ playerTwo.getField().getCards().get(0).getId().toString() +"\"}";

        try {
            JsonNode node = reader.readTree(jsonString);

            Action action = new Action();
            action.setPayload(node);

            this.board.handleAttack(action, this.game);

            assertEquals(25, playerOne.getHp());

        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void attackEnemyWithCardsOnField() {
        Player playerOne = this.board.getStateManager().getPlayers().get(0);
        Player playerTwo = this.board.getStateManager().getPlayers().get(1);

        playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));
        playerTwo.getField().addCard(new Monster(MonsterType.ARCHMAGE));

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \"\"}";

        try {
            JsonNode node = reader.readTree(jsonString);

            Action action = new Action();
            action.setPayload(node);

            assertFalse(this.board.handleAttack(action, this.game));

        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void attackEnemyWithoutCardsOnField() {
        Player playerOne = this.board.getStateManager().getPlayers().get(0);
        Player playerTwo = this.board.getStateManager().getPlayers().get(1);

        playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \"\"}";

        try {
            JsonNode node = reader.readTree(jsonString);

            Action action = new Action();
            action.setPayload(node);

            this.board.handleAttack(action, this.game);

            assertEquals(27, playerTwo.getHp());

        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void attackEnemyKillEnemy() {
        Player playerOne = this.board.getStateManager().getPlayers().get(0);
        Player playerTwo = this.board.getStateManager().getPlayers().get(1);

        playerOne.getField().addCard(new Monster(MonsterType.ACIDIC_SWAMP_OOZE));
        playerTwo.setHp(1);

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        String jsonString = "{\"card\" : \""+ playerOne.getField().getCards().get(0).getId().toString() +"\", \"enemyCard\" : \"\"}";

        try {
            JsonNode node = reader.readTree(jsonString);

            Action action = new Action();
            action.setPayload(node);

            this.board.handleAttack(action, this.game);

            assertEquals(GameStatus.ENDED, this.game.getGameStatus());
            assertEquals(1, playerOne.getGamesWon());

        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }



    private Game createGameEntity(List<Player> players) {
        Game game = new Game(UUID.randomUUID());
        game.addPlayer(players.get(0));
        game.addPlayer(players.get(1));
        game.setGameStatus(GameStatus.STARTED);

        return game;
    }
}
