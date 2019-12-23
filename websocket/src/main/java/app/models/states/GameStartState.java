package app.models.states;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameStartState {
    String type = "game_started_state";
    UUID gameId;

    public GameStartState(UUID gameId) {
        this.gameId = gameId;
    }
}
