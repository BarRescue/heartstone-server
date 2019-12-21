package app.controllers;

import app.jwt.TokenProvider;
import app.models.payloads.Action;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
import org.springframework.messaging.simp.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Controller
public class LobbySocketController{
    private Logger logger = LoggerFactory.getLogger(LobbySocketController.class);

    private TokenProvider tokenProvider;
    private SimpMessagingTemplate message;
    private SimpUserRegistry userRegistry;


    @Autowired
    public LobbySocketController(SimpMessagingTemplate messagingTemplate, TokenProvider tokenProvider, SimpUserRegistry simpUserRegistry) {
        this.message = messagingTemplate;
        this.tokenProvider = tokenProvider;
        this.userRegistry = simpUserRegistry;
    }

    @MessageMapping("/lobby")
    public void open(@Payload String actionType, SimpMessageHeaderAccessor headerAccessor) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String token = this.tokenProvider.resolveToken("Bearer " + headerAccessor.getSessionAttributes().get("JWT").toString());

            if(token != null && this.tokenProvider.validateToken(token)) {
                Authentication auth = this.tokenProvider.getAuthentication(token);

                Action action = objectMapper.readValue(actionType, Action.class);
                String name = action.getActionType();
            }
        } catch(IOException e) {
            message.convertAndSendToUser(headerAccessor.getUser().getName(), "/topic/search", "Cannot convert given string an JSON object!");
            logger.error("Cannot convert given string as JSON object: {}", actionType);
        }
    }
}
