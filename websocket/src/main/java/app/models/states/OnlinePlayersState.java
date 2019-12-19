package app.models.states;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OnlinePlayersState {
    String type = "online_player_state";
    Integer onlinePlayers;

    public OnlinePlayersState(Integer onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }
}
