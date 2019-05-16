package nl.jochemkuijpers.app.window;

public class ApplicationEvent {
    public enum Type {
        LEFT, RIGHT, UP, DOWN, EXIT, PREV_SCENE, NEXT_SCENE;
    }

    private final Type type;

    public ApplicationEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
