package inaugural.soliloquy.ui.test.integration.display;

import soliloquy.specs.ui.EventInputs;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.Constants.COMPONENT_DIMENS;

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

    public void printComponentDimens(EventInputs e) {
        System.out.println("Dimens = " + getFromData(e.component, COMPONENT_DIMENS));
    }
}
