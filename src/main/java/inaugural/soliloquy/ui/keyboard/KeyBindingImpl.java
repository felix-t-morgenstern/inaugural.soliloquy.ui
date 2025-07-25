package inaugural.soliloquy.ui.keyboard;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.timing.TimestampValidator;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.keyboard.KeyBinding;

import java.util.List;

import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class KeyBindingImpl implements KeyBinding {
    private final Component COMPONENT;
    private final List<Character> BOUND_CHARACTERS;
    private final Action<Object> ON_PRESS;
    private final Action<Object> ON_RELEASE;
    private final TimestampValidator TIMESTAMP_VALIDATOR;

    public KeyBindingImpl(Component component, Character[] chars, Action<Object> onPress, Action<Object> onRelease,
                          TimestampValidator timestampValidator) {
        COMPONENT = Check.ifNull(component, "component");
        Check.ifNull(chars, "chars");
        if (chars.length == 0) {
            throw new IllegalArgumentException(
                    "KeyBindingImpl: at least one char must be provided");
        }
        BOUND_CHARACTERS = listOf();
        for (var c : chars) {
            BOUND_CHARACTERS.add(Check.ifNull(c, "char within chars"));
        }
        ON_PRESS = onPress;
        ON_RELEASE = onRelease;
        TIMESTAMP_VALIDATOR = Check.ifNull(timestampValidator, "timestampValidator");
    }

    // TODO: Ensure that this is a clone
    @Override
    public List<Character> boundCharacters() {
        return listOf(BOUND_CHARACTERS);
    }

    @Override
    public void press(long timestamp) throws IllegalArgumentException {
        runAction(timestamp, ON_PRESS);
    }

    @Override
    public void release(long timestamp) throws IllegalArgumentException {
        runAction(timestamp, ON_RELEASE);
    }

    private void runAction(long timestamp, Action<Object> action) {
        TIMESTAMP_VALIDATOR.validateTimestamp(timestamp);
        if (action != null) {
            action.run(COMPONENT, timestamp);
        }
    }
}
