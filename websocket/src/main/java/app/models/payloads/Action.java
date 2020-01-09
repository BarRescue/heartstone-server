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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Action {
    Logger logger = LoggerFactory.getLogger(Action.class);
    private static ObjectMapper mapper = new ObjectMapper();

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
            id = UUID.fromString(payload.get("card").textValue());
        } catch(IllegalArgumentException e) {
            logger.error("Card {} not found with error: {}", payload.get("card").textValue(), e);
        }

        return id;
    }

    @JsonIgnore
    public UUID getEnemyCardsFromPayload() {
        UUID id = null;

        try {
            id = UUID.fromString(payload.get("enemyCard").textValue());
        } catch(IllegalArgumentException e) {
            logger.error("Card {} not found with error: {}", payload.get("card").textValue(), e);
        }

        return id;
    }
}
