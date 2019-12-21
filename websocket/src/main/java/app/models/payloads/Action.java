package app.models.payloads;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class Action {
    @Getter
    @JsonProperty("actionType")
    private String actionType;
}
