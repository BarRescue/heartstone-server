package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.entity.enums.GameStatus;
import app.logic.*;
import app.logic.handlers.*;
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
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;

    private SimpMessagingTemplate messagingTemplate;
    private GameManager gameManager;

    @Autowired
    public GameSocketController(SimpMessagingTemplate messagingTemplate, PlayerLogic playerLogic, GameLogic gameLogic, GameManager gameManager) {
        this.playerLogic = playerLogic;
        this.gameLogic = gameLogic;
        this.gameManager = gameManager;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/{id}")
    public void open(@Payload Action action, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id) throws NotFoundException {
        String token = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("JWT").toString();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

        Authentication auth = this.playerLogic.getAuthOnToken(token);
        Player player = this.getPlayerByAuth(auth, foundGame);

        if(player != null && foundGame.getGameStatus() == GameStatus.STARTED) {
            switch (action.getActionType()) {
                case GAME_INIT:
                    new PrepareGameHandler(messagingTemplate, gameManager).prepare(foundGame, player, playerLogic, gameLogic);
                    break;
                case TAKE_CARD:
                    new TakeCardHandler(messagingTemplate, gameManager).takeCard(foundGame, player);
                    break;
                case END_TURN:
                    new EndTurnHandler(messagingTemplate, gameManager).endTurn(foundGame, player);
                    break;
                case PLAY_CARD:
                    new PlayCardHandler(messagingTemplate, gameManager).playCard(foundGame, player, action);
                    break;
                case ATTACK:
                    new AttackHandler(messagingTemplate, gameManager).attack(foundGame, player, action);
                    break;
                default:
                    throw new NotFoundException("No action defined that is valid.");
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
}