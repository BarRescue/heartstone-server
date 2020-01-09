package app.models.states;

import app.entity.Player;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameWonState {
    String type = "game_won_state";
    Player wonPlayer;

    public GameWonState(Player player) {
        this.wonPlayer = player;
    }
}
