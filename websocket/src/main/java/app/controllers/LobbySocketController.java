package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.jwt.TokenProvider;
import app.logic.*;
import app.models.User;
import app.models.enums.ActionType;
import app.models.payloads.Action;
import app.models.states.GameStartState;
import app.models.states.SearchingState;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LobbySocketController{
    private Logger logger = LoggerFactory.getLogger(LobbySocketController.class);

    private TokenProvider tokenProvider;
    private SimpMessagingTemplate message;
    private PlayerLogic playerLogic;
    private LobbyLogic lobbyLogic;

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
            Authentication auth = this.playerLogic.getAuthOnToken(headerAccessor.getSessionAttributes().get("JWT").toString());

            if(auth != null) {
                // Get action from Payload
                Action action = objectMapper.readValue(actionType, Action.class);

                // Get user from principal
                User user = (User) auth.getPrincipal();

                // Create or get player
                Player player = this.playerLogic.createOrUpdate(new Player(user.getId(), user.getUsername(), this.playerLogic.getGamesWon(user.getId())));

                if(action.getActionType().equals(ActionType.SEARCH_GAME)) {
                    this.joinOrStartGame(player);
                }
            }
        } catch(IOException | NotFoundException e) {
            message.convertAndSendToUser(headerAccessor.getUser().getName(), "/topic/search", "Cannot convert given string an JSON object!");
            logger.error("Cannot convert given string as JSON object: {}", actionType);
        }
    }

    private void joinOrStartGame(Player player) throws NotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game foundGame = this.lobbyLogic.joinOrCreateGame(player);

        if (foundGame.getPlayers().size() == 1) {
            try {
                message.convertAndSendToUser(player.getFullName(), "/topic/search", objectMapper.writeValueAsString(new SearchingState()));
            } catch (IOException e) {
                logger.error("Could not send message to user {} with error {}", player.getFullName(), e);
                throw new IllegalArgumentException("Could not send message to user");
            }
        }

        if (foundGame.getPlayers().size() == 2) {
            Game gameToStart = this.lobbyLogic.findById(foundGame.getId()).orElseThrow(() -> new NotFoundException("Game not found"));

            if (!this.lobbyLogic.startGame(gameToStart)) {
                throw new IllegalArgumentException("Game could not be started");
            }

            List<Player> players = gameToStart.getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());

            for (Player p : players) {
                try {
                    message.convertAndSendToUser(p.getFullName(), "/topic/search", objectMapper.writeValueAsString(new GameStartState(gameToStart.getId())));
                } catch (IOException e) {
                    logger.error("Could not send message to user {} with error {}", p.getFullName(), e);
                    throw new IllegalArgumentException("Could not send message to user");
                }
            }
        }
    }
}
