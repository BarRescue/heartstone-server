package app.controllers;

import app.jwt.TokenProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import javax.websocket.EncodeException;
import javax.websocket.Session;

@Controller
public class WebsocketController {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebsocketController.applicationContext = applicationContext;
    }

    @MessageMapping("/lobby")
    @SendTo("/topic/lobby")
    public String open(@Payload String name, SimpMessageHeaderAccessor headerAccessor) {
        //TokenProvider tokenProvider = applicationContext.getBean(TokenProvider.class);
        return "testttt";
    }
}