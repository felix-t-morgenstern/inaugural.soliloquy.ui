package inaugural.soliloquy.ui.test.integration.display;

import soliloquy.specs.ui.EventInputs;

public class DisplayTestMethods {
    public void onMouseOver(EventInputs e) {
        System.out.println("MOUSE OVER");
    }

    public void onMouseLeave(EventInputs e) {
        System.out.println("MOUSE LEAVE");
    }

    public void onMousePress(EventInputs e) {
        System.out.println("MOUSE PRESS");
    }

    public void onMouseRelease(EventInputs e) {
        System.out.println("MOUSE RELEASE");
    }
}
