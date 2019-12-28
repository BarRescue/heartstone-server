package app.controllers;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.jwt.TokenProvider;
import app.logic.*;
import app.models.User;
import app.models.payloads.Action;
import app.models.responses.DefaultResponse;
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
    public void open(@Payload Action action, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id) throws IOException, NotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        String token = headerAccessor.getSessionAttributes().get("JWT").toString();

        switch(action.getActionType()) {
            case "game_init":
                this.prepareGame(id, token);
                break;
            case "take_card":
                this.handleTakeCard(id, token);
                break;
            case "end_turn":
                this.handleEndTurn(id, token);
                break;
            case "play_card":
                this.handlePlayCard(action, id, token);
                break;
        }
    }

    private void prepareGame(String id, String token) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

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

    private void handleTakeCard(String id, String token) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

        Authentication auth = this.playerLogic.getAuthOnToken(token);

        if(auth != null) {
            User user = (User) auth.getPrincipal();
            Optional<Player> player = this.playerLogic.findById(user.getId());

            if(player.isEmpty()) throw new NotFoundException("couldn't find Player with id.");

            if(!foundGame.containsPlayer(player.get())) throw new NotFoundException("Player is not in game");

            if (!games.get(foundGame.getId()).getBoard().getPlayerManager().getCurrentPlayer().getId().equals(player.get().getId())) {
                message.convertAndSendToUser(player.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new DefaultResponse("It's not your turn")));
                return;
            }

            games.get(foundGame.getId()).getBoard().handleTakeCard();

            Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers().stream().filter(p -> p.getId().equals(player.get().getId())).findFirst();

            if(gamePlayer.isPresent()) {
                message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new DefaultResponse(games.get(foundGame.getId()).getBoard().getPrivateMessage())));
                message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new PrivateGameState(gamePlayer.get().getDeck(), gamePlayer.get().getHand())));
            }

            for(Player p : games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers()) {
                message.convertAndSendToUser(p.getFullName(), "/topic/game", objectMapper.writeValueAsString(games.get(foundGame.getId())));
            }
        }
    }

    private void handleEndTurn(String id, String token) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

        Authentication auth = this.playerLogic.getAuthOnToken(token);

        if(auth != null) {
            User user = (User) auth.getPrincipal();
            Optional<Player> player = this.playerLogic.findById(user.getId());

            if (player.isEmpty()) throw new NotFoundException("couldn't find Player with id.");

            if (!foundGame.containsPlayer(player.get())) throw new NotFoundException("Player is not in game");

            if (!games.get(foundGame.getId()).getBoard().getPlayerManager().getCurrentPlayer().getId().equals(player.get().getId())) {
                message.convertAndSendToUser(player.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new DefaultResponse("It's not your turn")));
                return;
            }

            games.get(foundGame.getId()).getBoard().getPlayerManager().getNextPlayer();

            for(Player p : games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers()) {
                message.convertAndSendToUser(p.getFullName(), "/topic/game", objectMapper.writeValueAsString(games.get(foundGame.getId())));
            }
        }
    }

    private void handlePlayCard(Action action, String id, String token) throws NotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game foundGame = this.gameLogic.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Game not found"));

        Authentication auth = this.playerLogic.getAuthOnToken(token);

        if(auth != null) {
            User user = (User) auth.getPrincipal();
            Optional<Player> player = this.playerLogic.findById(user.getId());

            if (player.isEmpty()) throw new NotFoundException("couldn't find Player with id.");

            if (!foundGame.containsPlayer(player.get())) throw new NotFoundException("Player is not in game");

            if (!games.get(foundGame.getId()).getBoard().getPlayerManager().getCurrentPlayer().getId().equals(player.get().getId())) {
                message.convertAndSendToUser(player.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new DefaultResponse("It's not your turn")));
                return;
            }

            games.get(foundGame.getId()).getBoard().handlePlayCard(action);

            Optional<Player> gamePlayer = games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers().stream().filter(p -> p.getId().equals(player.get().getId())).findFirst();

            if(gamePlayer.isPresent()) {
                message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new DefaultResponse(games.get(foundGame.getId()).getBoard().getPrivateMessage())));
                message.convertAndSendToUser(gamePlayer.get().getFullName(), "/topic/game", objectMapper.writeValueAsString(new PrivateGameState(gamePlayer.get().getDeck(), gamePlayer.get().getHand())));
            }

            for(Player p : games.get(foundGame.getId()).getBoard().getPlayerManager().getPlayers()) {
                message.convertAndSendToUser(p.getFullName(), "/topic/game", objectMapper.writeValueAsString(games.get(foundGame.getId())));
            }
        }
    }
}