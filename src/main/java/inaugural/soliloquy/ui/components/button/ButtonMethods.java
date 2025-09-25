package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.Check;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.input.mouse.MouseEventHandler;
import soliloquy.specs.ui.EventInputs;

import java.util.function.Consumer;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;

public class ButtonMethods {
    final String PRESS_STATE = "isPressed";
    final String HOVER_STATE = "isHovering";
    final String PRESS_SOUND_ID = "pressSoundId";
    final String RELEASE_SOUND_ID = "releaseSoundId";
    final String PRESS_ACTION = "pressAction";

    private final Consumer<String> PLAY_SOUND;
    private final TriConsumer<Integer, MouseEventHandler.EventType, Runnable> SUBSCRIBE;

    public ButtonMethods(Consumer<String> playSound,
                         TriConsumer<Integer, MouseEventHandler.EventType, Runnable> subscribe) {
        PLAY_SOUND = Check.ifNull(playSound, "playSound");
        SUBSCRIBE = Check.ifNull(subscribe, "subscribe");
    }

    public void pressButton(EventInputs e) {
        var data = e.component.data();
        data.put(PRESS_STATE, true);
        PLAY_SOUND.accept((String) data.get(PRESS_SOUND_ID));
        SUBSCRIBE.accept(LEFT_MOUSE_BUTTON, MouseEventHandler.EventType.RELEASE, () -> {
            if ((Boolean) data.get(HOVER_STATE)) {
                @SuppressWarnings("rawtypes") var pressAction = (Action) data.get(PRESS_ACTION);
                //noinspection unchecked
                pressAction.accept(null);
                PLAY_SOUND.accept((String) data.get(RELEASE_SOUND_ID));
            }
        });
    }

    public void mouseOverButton(EventInputs e) {
        e.component.data().put(HOVER_STATE, true);
    }

    public void mouseLeaveButton(EventInputs e) {
        e.component.data().put(HOVER_STATE, false);
    }
}
