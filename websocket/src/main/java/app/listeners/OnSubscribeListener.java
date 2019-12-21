package app.listeners;

import app.models.states.OnlinePlayersState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnSubscribeListener {
    private static Logger logger = LoggerFactory.getLogger(OnSubscribeListener.class);

    private SimpMessagingTemplate template;
    private Set<String> socketUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Autowired
    public OnSubscribeListener(SimpMessagingTemplate template) {
        this.template = template;
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

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        ObjectMapper mapper = new ObjectMapper();

        // Remove user from session
        socketUsers.remove(sha.getSessionId());

        try {
            template.convertAndSend("/topic/lobby", mapper.writeValueAsString(new OnlinePlayersState(socketUsers.size())));
        } catch(IOException e) {
            logger.error("Error on converting object: {} with error {}", socketUsers, e);
        }
    }
}