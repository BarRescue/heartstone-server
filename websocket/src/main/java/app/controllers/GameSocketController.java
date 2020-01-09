package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.*;
import app.models.User;
import app.models.payloads.Action;
import app.models.responses.DefaultResponse;
import app.models.states.GameState;
import app.models.states.PrivateGameState;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;
import javassist.NotFoundException;

@Controller
public class GameSocketController {
    private Logger logger = LoggerFactory.getLogger(GameSocketController.class);
    private static final String LOBBY_SUBSCRIBE_PATH = "/topic/game";
    private ObjectMapper objectMapper = new ObjectMapper();

    private SimpMessagingTemplate message;
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;

    private static HashMap<UUID, GameState> games = new HashMap<>();

    @Autowired
    public GameSocketController(SimpMessagingTemplate simpMessagingTemplate, PlayerLogic playerLogic, GameLogic gameLogic) {
        this.message = simpMessagingTemplate;
        this.playerLogic = playerLogic;
        this.gameLogic = gameLogic;
    }

    @MessageMapping("/game/{id}")
    public void open(@Payload Action action, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id) throws NotFoundException {
        String token = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("JWT").toString();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

        Authentication auth = this.playerLogic.getAuthOnToken(token);
        Player player = this.getPlayerByAuth(auth, foundGame);

        if(player != null && foundGame.getGameStatus() == GameStatus.STARTED) {
            switch (action.getActionType()) {
                case "game_init":
                    this.prepareGame(foundGame, player);
                    break;
                case "take_card":
                    this.handleTakeCard(foundGame, player);
                    break;
                case "end_turn":
                    this.handleEndTurn(foundGame, player);
                    break;
                case "play_card":
                    this.handlePlayCard(action, foundGame, player);
                    break;
                case "attack":
                    this.handleAttack(action, foundGame, player);
                    break;
                default:
                    throw new NotFoundException("No action defined that is valid.");
            }
        }
    }

    private void prepareGame(Game foundGame, Player player) {
        if(!games.containsKey(foundGame.getId())) {
            List<Player> players = foundGame.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
            players.forEach(Player::prepareForGame);
            Collections.shuffle(players);

            games.put(foundGame.getId(), new GameState(new Board(new StateManager(players), "Welcome to the game!", playerLogic, gameLogic)));
        }

        Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(gamePlayer.isPresent()) {
            this.sendResponse(gamePlayer.get().getFullName(), games.get(foundGame.getId()));
            this.sendResponse(gamePlayer.get().getFullName(), getPrivateGameState(gamePlayer.get()));
        }
    }

    private void handleTakeCard(Game foundGame, Player player) {
        Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(this.isNotPlayerTurn(foundGame, player)) {
            return;
        }

        if(gamePlayer.isPresent()) {
            if(!games.get(foundGame.getId()).getBoard().handleTakeCard()) {
                this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(foundGame));
                return;
            }

            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(foundGame));
            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateGameState(gamePlayer.get()));

            for(Player p : games.get(foundGame.getId()).getBoard().getStateManager().getPlayers()) {
                this.sendResponse(p.getFullName(), games.get(foundGame.getId()));
            }
        }
    }

    private void handleEndTurn(Game foundGame, Player player) {

        if(this.isNotPlayerTurn(foundGame, player)) {
            return;
        }

        games.get(foundGame.getId()).getBoard().getStateManager().getNextPlayer();

        for(Player p : games.get(foundGame.getId()).getBoard().getStateManager().getPlayers()) {
            this.sendResponse(p.getFullName(), games.get(foundGame.getId()));
        }
    }

    private void handlePlayCard(Action action, Game foundGame, Player player) {
        Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(this.isNotPlayerTurn(foundGame, player)) {
            return;
        }

        if(gamePlayer.isPresent()) {
            if(!games.get(foundGame.getId()).getBoard().handlePlayCard(action)) {
                this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(foundGame));
                return;
            }

            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateMessage(foundGame));
            this.sendResponse(gamePlayer.get().getFullName(), this.getPrivateGameState(gamePlayer.get()));

            for(Player p : games.get(foundGame.getId()).getBoard().getStateManager().getPlayers()) {
                this.sendResponse(p.getFullName(), games.get(foundGame.getId()));
            }
        }
    }

    private void handleAttack(Action action, Game foundGame, Player player) {
        Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getStateManager().getPlayers().stream().filter(p -> p.getId().equals(player.getId())).findFirst();

        if(this.isNotPlayerTurn(foundGame, player)) {
            return;
        }

        if(gamePlayer.isPresent()) {
            if(!games.get(foundGame.getId()).getBoard().handleAttack(action, foundGame)) {
                this.sendResponse(player.getFullName(), this.getPrivateMessage(foundGame));
                return;
            }

            this.sendResponse(gamePlayer.get().getFullName(), getPrivateMessage(foundGame));

            for (Player p : games.get(foundGame.getId()).getBoard().getStateManager().getPlayers()) {
                this.sendResponse(p.getFullName(), games.get(foundGame.getId()));
            }
        }
    }

    private Player getPlayerByAuth(Authentication auth, Game game) throws NotFoundException {
        if(auth != null) {
            User user = (User) auth.getPrincipal();
            Optional<Player> player = this.playerLogic.findById(user.getId());

            if(player.isEmpty() || !game.containsPlayer(player.get())) {
                logger.error("couldn't find Player with id or player isn't in game");
                throw new NotFoundException("couldn't find Player with id or player isn't in game");
            }

            return player.get();
        }

        return null;
    }

    private boolean isNotPlayerTurn(Game foundGame, Player player) {
        if (!games.get(foundGame.getId()).getBoard().getStateManager().isPlayerTurn(player)) {
            this.sendResponse(player.getFullName(), new DefaultResponse("It's not your turn"));
            return true;
        }

        return false;
    }

    private void sendResponse(String receiver, Object o) {
        try {
            message.convertAndSendToUser(receiver, LOBBY_SUBSCRIBE_PATH, objectMapper.writeValueAsString(o));
        } catch(JsonProcessingException e) {
            logger.error("Error sending to user {} with error {}", receiver, e);
        }
    }

    private DefaultResponse getPrivateMessage(Game foundGame) {
        return new DefaultResponse(games.get(foundGame.getId()).getBoard().getPrivateMessage());
    }

    private PrivateGameState getPrivateGameState(Player player) {
        return new PrivateGameState(player.getDeck(), player.getHand());
    }
}