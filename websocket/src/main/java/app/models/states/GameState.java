package app.models.states;

import app.logic.Board;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class GameState {
    String type = "game_state";
    Board board;

    public GameState(Board board) {
        this.board = board;
    }
}
