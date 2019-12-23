package app.listeners;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.logic.GameLogic;
import app.logic.LobbyLogic;
import app.logic.PlayerLogic;
import app.models.User;
import app.models.states.GameWonState;
import app.models.states.OnlinePlayersState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnSubscribeListener {
    private static Logger logger = LoggerFactory.getLogger(OnSubscribeListener.class);

    private SimpMessagingTemplate template;
    private Set<String> socketUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private LobbyLogic lobbyLogic;
    private PlayerLogic playerLogic;
    private GameLogic gameLogic;

    @Autowired
    public OnSubscribeListener(SimpMessagingTemplate template, LobbyLogic lobbyLogic, PlayerLogic playerLogic, GameLogic gameLogic) {
        this.template = template;
        this.lobbyLogic = lobbyLogic;
        this.playerLogic = playerLogic;
        this.gameLogic = gameLogic;
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();
        String simpDestination = (String) message.getHeaders().get("simpDestination");

        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        ObjectMapper mapper = new ObjectMapper();

        // Add user to session
        socketUsers.add(sha.getSessionId());

        if(simpDestination.equals("/topic/lobby")) {
            try {
                template.convertAndSend("/topic/lobby", mapper.writeValueAsString(new OnlinePlayersState(socketUsers.size())));
            } catch(IOException e) {
                logger.error("Error on converting object: {} with error {}", socketUsers, e);
            }
        }
    }

    /*@EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        ObjectMapper mapper = new ObjectMapper();

        if("/topic/game".equals(simpDestination)) {
            Authentication auth = this.playerLogic.getAuthOnToken(sha.getSessionAttributes().get("JWT").toString());

            if(auth != null) {
                User user = (User) auth.getPrincipal();
                Game startedGame = this.lobbyLogic.getStartedGame(this.playerLogic.CreateOrUpdate(new Player(user.getId(), user.getUsername(), this.playerLogic.getGamesWon(user.getId()))));

                if (startedGame != null) {
                    Optional<GamePlayer> wonPlayer = startedGame.getPlayers().stream().filter(p -> p.getPlayer().getId() != user.getId()).findFirst();

                    if (wonPlayer.isPresent()) {
                        this.gameLogic.endGame(wonPlayer.get(), startedGame);
                        template.convertAndSendToUser(wonPlayer.get().getPlayer().getFullName(), "/topic/game", new GameWonState());
                    }
                }
            }
        }
    }*/

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        socketUsers.remove(sha.getSessionId());
        ObjectMapper mapper = new ObjectMapper();

        try {
            template.convertAndSend("/topic/lobby", mapper.writeValueAsString(new OnlinePlayersState(socketUsers.size())));
        } catch(IOException e) {
            logger.error("Error on converting object: {} with error {}", socketUsers, e);
        }
    }
}