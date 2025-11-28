package inaugural.soliloquy.ui.readers.keyboard;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.input.keyboard.KeyBinding;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;

import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static soliloquy.specs.io.input.keyboard.KeyBinding.keyBinding;

public class KeyBindingDefinitionReader {
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> GET_CONSUMER;

    public KeyBindingDefinitionReader(
            @SuppressWarnings("rawtypes") Function<String, Consumer> getConsumer
    ) {
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
    }

    public KeyBinding read(KeyBindingDefinition definition) {
        var onPress = defaultIfNull(definition.PRESS_CONSUMER_ID, null, GET_CONSUMER);
        var onRelease = defaultIfNull(definition.RELEASE_CONSUMER_ID, null, GET_CONSUMER);

        //noinspection unchecked
        return keyBinding(definition.KEY_CODEPOINTS, onPress, onRelease);
    }
}
