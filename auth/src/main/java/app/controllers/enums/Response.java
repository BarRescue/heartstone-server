package app.controllers.enums;

public enum Response {
    INVALID_PARAMS("Invalid parameters"),
    UNEXPECTED_ERROR("A unexpected error occurred. Try again later"),
    WRONG_CREDENTIALS("Wrong credentials"),
    USER_ALREADY_EXISTS("A user with this email already exists");

    private final String text;

    Response(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
