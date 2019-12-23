package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.jwt.TokenProvider;
import app.logic.*;
import app.models.User;
import app.models.interfaces.Card;
import app.models.payloads.Action;
import app.models.states.GameState;
import app.models.states.PrivateGameState;
import app.models.states.SearchingState;
import app.service.GameService;
import app.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class LobbySocketController{
    private Logger logger = LoggerFactory.getLogger(LobbySocketController.class);

    private TokenProvider tokenProvider;
    private SimpMessagingTemplate message;
    private PlayerLogic playerLogic;
    private LobbyLogic lobbyLogic;

    private static HashMap<UUID, GameState> games = new HashMap<>();

    @Autowired
    public LobbySocketController(SimpMessagingTemplate messagingTemplate, TokenProvider tokenProvider, PlayerLogic playerLogic, LobbyLogic lobbyLogic) {
        this.message = messagingTemplate;
        this.tokenProvider = tokenProvider;
        this.playerLogic = playerLogic;
        this.lobbyLogic = lobbyLogic;
    }

    @MessageMapping("/lobby")
    public void open(@Payload String actionType, SimpMessageHeaderAccessor headerAccessor) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Authentication auth = getAuthOnToken(headerAccessor.getSessionAttributes().get("JWT").toString());

            if(auth != null) {
                // Get action from Payload
                Action action = objectMapper.readValue(actionType, Action.class);

                // Get user from principal
                User user = (User) auth.getPrincipal();

                // Create or get player
                Player player = this.playerLogic.CreateOrUpdate(new Player(user.getId(), user.getUsername()));

                if(action.getActionType().equals("search_game")) {
                    this.joinOrStartGame(player);
                }
            }
        } catch(IOException e) {
            message.convertAndSendToUser(headerAccessor.getUser().getName(), "/topic/search", "Cannot convert given string an JSON object!");
            logger.error("Cannot convert given string as JSON object: {}", actionType);
        }
    }

    private void joinOrStartGame(Player player) {
        Game foundGame = this.lobbyLogic.joinOrCreateGame(player);
        ObjectMapper objectMapper = new ObjectMapper();

        if(foundGame.getPlayers().size() == 1) {
            try {
                message.convertAndSendToUser(player.getFullName(), "/topic/search", objectMapper.writeValueAsString(new SearchingState()));
            } catch(IOException e) {
                logger.error("Could not send message to user {} with error {}", player.getFullName(), e);
                throw new IllegalArgumentException("Could not send message to user");
            }
        }

        if(foundGame.getPlayers().size() == 2) {
            if(!this.lobbyLogic.startGame(foundGame)) {
                throw new IllegalArgumentException("Game could not be started");
            }

            List<Player> players = foundGame.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
            games.put(foundGame.getId(), new GameState(new Board(new PlayerManager(players), "Welcome to the game!")));

            for(Player p : players) {
                try {
                    message.convertAndSendToUser(p.getFullName(), "/topic/search", objectMapper.writeValueAsString(games.get(foundGame.getId())));
                    message.convertAndSendToUser(p.getFullName(), "/topic/search", objectMapper.writeValueAsString(new PrivateGameState(p.getDeck(), p.getHand())));
                } catch(IOException e) {
                    logger.error("Could not send message to user {} with error {}", p.getFullName(), e);
                    throw new IllegalArgumentException("Could not send message to user");
                }
            }
        }
    }

    private Authentication getAuthOnToken(String jwt) {
        String token = this.tokenProvider.resolveToken("Bearer " + jwt);

        if(token != null && this.tokenProvider.validateToken(token)) {
            return this.tokenProvider.getAuthentication(token);
        }

        return null;
    }
}
