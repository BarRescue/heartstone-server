package app.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerDTO {
    @JsonIgnore
    private UUID id;
    @JsonProperty
    private String name;
    @JsonProperty
    private int gamesWon;

    public PlayerDTO(UUID id, String name, int gamesWon) {
        this.name = name;
        this.gamesWon = gamesWon;
        this.id = id;
    }
}
