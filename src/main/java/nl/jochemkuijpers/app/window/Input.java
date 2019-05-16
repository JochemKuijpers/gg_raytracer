package nl.jochemkuijpers.app.window;

public class Input {
    public enum Type {
        LEFT, RIGHT, UP, DOWN
    }

    private final Type type;

    public Input(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
