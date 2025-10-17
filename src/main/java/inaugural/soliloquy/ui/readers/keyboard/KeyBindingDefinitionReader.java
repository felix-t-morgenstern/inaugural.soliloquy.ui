package inaugural.soliloquy.ui.readers.keyboard;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.input.keyboard.KeyBinding;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;

import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static soliloquy.specs.io.input.keyboard.KeyBinding.keyBinding;

public class KeyBindingDefinitionReader {
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;

    public KeyBindingDefinitionReader(
            @SuppressWarnings("rawtypes") Function<String, Action> getAction
    ) {
        GET_ACTION = Check.ifNull(getAction, "getAction");
    }

    public KeyBinding read(KeyBindingDefinition definition) {
        var onPress = defaultIfNull(definition.PRESS_ACTION_ID, null, GET_ACTION);
        var onRelease = defaultIfNull(definition.RELEASE_ACTION_ID, null, GET_ACTION);

        //noinspection unchecked
        return keyBinding(definition.KEY_CODEPOINTS, onPress, onRelease);
    }
}
