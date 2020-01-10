package app.logic.handlers;

import app.entity.Game;
import app.entity.Player;
import app.logic.GameManager;
import app.models.responses.DefaultResponse;
import app.models.states.PrivateGameState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

abstract class DefaultHandler {
    private static final String LOBBY_SUBSCRIBE_PATH = "/topic/game";

    private Logger logger = LoggerFactory.getLogger(DefaultHandler.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    SimpMessagingTemplate message;

    @Getter
    GameManager gameManager;

    DefaultHandler(SimpMessagingTemplate message, GameManager gameManager) {
        this.message = message;
        this.gameManager = gameManager;
    }

    DefaultHandler() {}

    boolean isNotPlayerTurn(Game foundGame, Player player) {
        if (!gameManager.getBoard(foundGame).getStateManager().isPlayerTurn(player)) {
            this.sendResponse(player.getFullName(), new DefaultResponse("It's not your turn"));
            return true;
        }

        return false;
    }

    void sendResponse(String receiver, Object o) {
        try {
            message.convertAndSendToUser(receiver, LOBBY_SUBSCRIBE_PATH, objectMapper.writeValueAsString(o));
        } catch(JsonProcessingException e) {
            logger.error("Error sending to user {} with error {}", receiver, e);
        }
    }

    DefaultResponse getPrivateMessage(Game foundGame) {
        return new DefaultResponse(gameManager.getBoard(foundGame).getPrivateMessage());
    }

    PrivateGameState getPrivateGameState(Player player) {
        return new PrivateGameState(player.getDeck(), player.getHand());
    }
}
