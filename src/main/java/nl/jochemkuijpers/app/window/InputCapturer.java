package nl.jochemkuijpers.app.window;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Deque;

public class InputCapturer implements KeyListener {
    private final Deque<ApplicationEvent> applicationEvents;

    public InputCapturer(Deque<ApplicationEvent> applicationEvents) {
        this.applicationEvents = applicationEvents;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ApplicationEvent event = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                event = new ApplicationEvent(ApplicationEvent.Type.EXIT);
                break;
            case KeyEvent.VK_LEFT:
                event = new ApplicationEvent(ApplicationEvent.Type.LEFT);
                break;
            case KeyEvent.VK_UP:
                event = new ApplicationEvent(ApplicationEvent.Type.UP);
                break;
            case KeyEvent.VK_DOWN:
                event = new ApplicationEvent(ApplicationEvent.Type.DOWN);
                break;
            case KeyEvent.VK_RIGHT:
                event = new ApplicationEvent(ApplicationEvent.Type.RIGHT);
                break;
            case KeyEvent.VK_PAGE_DOWN:
                event = new ApplicationEvent(ApplicationEvent.Type.PREV_SCENE);
                break;
            case KeyEvent.VK_PAGE_UP:
                event = new ApplicationEvent(ApplicationEvent.Type.NEXT_SCENE);
                break;
        }
        if (event != null) {
            synchronized (applicationEvents) {
                applicationEvents.offer(event);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
