package app.logic;

import app.entity.Player;
import app.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerLogic {
    private PlayerService playerService;

    @Autowired
    public PlayerLogic(PlayerService playerService) {
        this.playerService = playerService;
    }

    public Player CreateOrUpdate(Player player) {
        return this.playerService.createOrUpdate(player);
    }
}
