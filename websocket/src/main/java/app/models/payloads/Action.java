package app.models.payloads;

import app.models.Monster;
import app.models.enums.MonsterType;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Action {
    private static ObjectMapper mapper = new ObjectMapper();

    @Getter
    @JsonProperty("actionType")
    private String actionType;

    @Getter
    @JsonProperty
    private JsonNode payload;

    @JsonIgnore
    public List<Card> getCardsFromPayload() {
        List<Card> cards = new ArrayList<>();

        payload.get("cards").forEach(jsonNode -> {
            cards.add(new Monster(MonsterType.valueOf(jsonNode.textValue())));
        });

        return cards;
    }
}
