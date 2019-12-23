package app.models.states;

import app.logic.Board;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GameState {
    String type = "game_state";
    Board board;

    public GameState(Board board) {
        this.board = board;
    }
}
