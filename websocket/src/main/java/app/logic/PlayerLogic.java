package app.logic;

import app.entity.Player;
import app.jwt.TokenProvider;
import app.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerLogic {
    private PlayerService playerService;
    private TokenProvider tokenProvider;

    @Autowired
    public PlayerLogic(PlayerService playerService, TokenProvider tokenProvider) {
        this.playerService = playerService;
        this.tokenProvider = tokenProvider;
    }

    public Player CreateOrUpdate(Player player) {
        return this.playerService.createOrUpdate(player);
    }

    public Authentication getAuthOnToken(String JWT) {
        String token = this.tokenProvider.resolveToken("Bearer " + JWT);

        if(token != null && this.tokenProvider.validateToken(token)) {
            return this.tokenProvider.getAuthentication(token);
        }

        return null;
    }

    public int getGamesWon(UUID id) {
        return this.playerService.getGamesWonById(id);
    }
}
