package app.models.payloads;

import app.models.Monster;
import app.models.enums.ActionType;
import app.models.enums.MonsterType;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Getter
@Setter
public class Action {
    Logger logger = LoggerFactory.getLogger(Action.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String ENEMY_CARD = "enemyCard";
    private static final String PLAYER_CARD = "card";

    @Getter
    @JsonProperty("actionType")
    private ActionType actionType;

    @Getter
    @JsonProperty
    private JsonNode payload;

    @JsonIgnore
    public UUID getCardsFromPayload() {
        UUID id = null;

        try {
            id = UUID.fromString(payload.get(PLAYER_CARD).textValue());
        } catch(IllegalArgumentException e) {
            logger.error("Card {} not found with error: {}", payload.get(PLAYER_CARD).textValue(), e);
        }

        return id;
    }

    @JsonIgnore
    public UUID getEnemyCardsFromPayload() {
        UUID id = null;

        try {
            if(payload.get(ENEMY_CARD).asBoolean()) {
                id = UUID.fromString(payload.get(ENEMY_CARD).textValue());
            }
        } catch(IllegalArgumentException e) {
            logger.error("Card {} not found with error: {}", payload.get(ENEMY_CARD).textValue(), e);
        }

        return id;
    }
}
