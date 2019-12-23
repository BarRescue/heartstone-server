package app.logic;

import app.entity.Player;
import app.models.DiscardPile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

public class Board {
    @JsonUnwrapped
    @Getter
    private PlayerManager playerManager;

    @JsonIgnore
    private ActionManager actionManager;

    @Getter
    private DiscardPile discardPile = new DiscardPile();

    @Getter
    @Setter
    private String message;

    @JsonIgnore
    @Getter
    @Setter
    private String privateMessage;

    public Board(PlayerManager playerManager, String message) {
        this.playerManager = playerManager;
        this.actionManager = new ActionManager(this);
        this.message = message;

        prepareCards();
    }

    private void prepareCards() {
        for(Player player : this.playerManager.getPlayers()) {
            player.prepareForGame();
        }
    }
}
