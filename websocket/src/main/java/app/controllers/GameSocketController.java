package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.jwt.TokenProvider;
import app.logic.*;
import app.models.User;
import app.models.payloads.Action;
import app.models.states.GameState;
import app.models.states.PrivateGameState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
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

import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
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
    public void open(@Payload String actionType, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Action action = objectMapper.readValue(actionType, Action.class);

        if(action.getActionType().equals("game_init")) {
            Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));
            String token = headerAccessor.getSessionAttributes().get("JWT").toString();

            if (token != null && this.tokenProvider.validateToken(token)) {
                Authentication auth = this.playerLogic.getAuthOnToken(token);

                if (auth != null) {
                    User user = (User) auth.getPrincipal();
                    Optional<Player> player = this.playerLogic.findById(user.getId());

                    if(player.isEmpty()) throw new NotFoundException("couldn't find Player with id.");

                    if(!foundGame.containsPlayer(player.get())) throw new NotFoundException("Player is not in game");

                    if(!games.containsKey(foundGame.getId())) {
                        List<Player> players = foundGame.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
                        players.forEach(Player::prepareForGame);
                        Collections.shuffle(players);

                        games.put(foundGame.getId(), new GameState(new Board(new PlayerManager(players), "Welcome to the game!")));
                    }

                    Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers().stream().filter(p -> p.getId().equals(player.get().getId())).findFirst();

                    if(gamePlayer.isPresent()) {
                        message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(games.get(foundGame.getId())));
                        message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new PrivateGameState(gamePlayer.get().getDeck(), gamePlayer.get().getHand())));
                    }
                }
            }
        }
    }
}
