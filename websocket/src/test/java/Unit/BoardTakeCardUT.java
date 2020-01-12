package Unit;

import app.entity.Player;
import app.logic.Board;
import app.logic.GameLogic;
import app.logic.PlayerLogic;
import app.logic.StateManager;
import app.models.interfaces.Card;
import app.repository.GameRepository;
import app.repository.PlayerRepository;
import app.service.GameService;
import app.service.PlayerService;
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
class BoardTakeCardUT {
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;
    private Board board;

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
        this.playerLogic = new PlayerLogic(playerService);
        this.gameLogic = new GameLogic(gameService, playerService);

        List<Player> players = new ArrayList<>();

        players.add(new Player(UUID.randomUUID(), "Rens Manders", 0));
        players.add(new Player(UUID.randomUUID(), "Piet Manders", 0));

        for(Player p : players) {
            p.prepareForGame();
        }

        this.board = new Board(new StateManager(players), playerLogic, gameLogic);
    }

    @Test
    void takeCardWithCards() {
        assertTrue(this.board.handleTakeCard());
    }

    @Test
    void takeCardWithoutCards() {
        for(int i = 0; i <= 30; i++) {
            this.board.getStateManager().getCurrentPlayer().getDeck().takeCard();
        }

        assertFalse(this.board.handleTakeCard());
    }

    @Test
    void takeCardWithMaxCardsInhand() {
        for(int i = 0; i <= 10; i++) {
            this.board.handleTakeCard();
        }

        assertFalse(this.board.handleTakeCard());
    }
}
