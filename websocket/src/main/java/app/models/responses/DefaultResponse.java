package app.models.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DefaultResponse {
    String type = "response";
    String message;

    public DefaultResponse(String message) {
        this.message = message;
    }
}
