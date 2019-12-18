package app.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
public class OnSubscribeListener {

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

        if(simpDestination.equals("/topic/lobby")) {
            template.convertAndSend("/topic/lobby", userRegistry.getUserCount());
        }
    }
}
