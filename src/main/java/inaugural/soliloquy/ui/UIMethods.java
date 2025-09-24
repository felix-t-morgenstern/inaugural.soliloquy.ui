package inaugural.soliloquy.ui;

import soliloquy.specs.ui.EventInputs;

public class UIMethods {
    public void onMouseOver(EventInputs eventInputs) {
        System.out.println("MOUSE OVER");
    }

    public void onMouseLeave(EventInputs eventInputs) {
        System.out.println("MOUSE LEAVE");
    }

    public void onMousePress(EventInputs eventInputs) {
        System.out.println("MOUSE PRESS");
    }

    public void onMouseRelease(EventInputs eventInputs) {
        System.out.println("MOUSE RELEASE");
    }
}
