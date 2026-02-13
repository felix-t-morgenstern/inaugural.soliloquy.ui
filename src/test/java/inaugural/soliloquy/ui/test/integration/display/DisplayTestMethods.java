package inaugural.soliloquy.ui.test.integration.display;

import soliloquy.specs.ui.EventInputs;

public class DisplayTestMethods {
    public void onMouseOver(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE OVER");
    }

    public void onMouseLeave(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE LEAVE");
    }
}
