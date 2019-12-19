package app.listeners;

import app.models.states.OnlinePlayersState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import javax.websocket.EncodeException;
import java.io.IOException;

@Service
public class OnSubscribeListener {
    private static Logger logger = LoggerFactory.getLogger(OnSubscribeListener.class);

    private SimpMessagingTemplate template;
    private SimpUserRegistry userRegistry;

    @Autowired
    public OnSubscribeListener(SimpMessagingTemplate template, SimpUserRegistry userRegistry) {
        this.template = template;
        this.userRegistry = userRegistry;
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        GenericMessage message = (GenericMessage) event.getMessage();
        String simpDestination = (String) message.getHeaders().get("simpDestination");
        ObjectMapper mapper = new ObjectMapper();

        if(simpDestination.equals("/topic/lobby")) {
            OnlinePlayersState onlinePlayersState = new OnlinePlayersState(userRegistry.getUserCount());
            try {
                template.convertAndSend("/topic/lobby", mapper.writeValueAsString(onlinePlayersState));
            } catch(IOException e) {
                logger.error("Error on converting object: {} with error {}", onlinePlayersState, e);
            }
        }
    }
}
