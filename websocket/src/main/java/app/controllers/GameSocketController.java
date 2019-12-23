package app.controllers;

import app.entity.GamePlayer;
import app.entity.Player;
import app.jwt.TokenProvider;
import app.logic.*;
import app.models.states.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class GameSocketController {
    private Logger logger = LoggerFactory.getLogger(GameSocketController.class);

    private TokenProvider tokenProvider;
    private SimpMessagingTemplate message;
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;

    private static HashMap<UUID, GameState> games = new HashMap<>();

    @Autowired
    public GameSocketController(TokenProvider tokenProvider, SimpMessagingTemplate simpMessagingTemplate, PlayerLogic playerLogic, GameLogic gameLogic) {
        this.tokenProvider = tokenProvider;
        this.message = simpMessagingTemplate;
        this.playerLogic = playerLogic;
        this.gameLogic = gameLogic;
    }

    @MessageMapping("/game/{id}")
    public void open(@Payload String actionType, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id) {
        //List<Player> players = foundGame.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
        //games.put(foundGame.getId(), new GameState(new Board(new PlayerManager(players), "Welcome to the game!")));
    }
}
