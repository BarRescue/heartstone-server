package app.models.DTO;

import app.helpers.UrlHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Getter
public class PlayerHighscoreDTO {
    @JsonIgnore
    private UUID id;

    @JsonProperty
    private String name;

    @JsonProperty
    private int gamesWon;

    @JsonProperty
    private String playerUrl;

    public PlayerHighscoreDTO(UUID id, String name, int gamesWon, String baseUrl) {
        this.id = id;
        this.name = name;
        this.gamesWon = gamesWon;

        this.playerUrl = baseUrl + "/player/" + this.id;
    }
}
