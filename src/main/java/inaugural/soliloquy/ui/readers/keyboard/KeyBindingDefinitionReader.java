package inaugural.soliloquy.ui.readers.keyboard;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.input.keyboard.entities.KeyBinding;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;
import soliloquy.specs.ui.definitions.keyboard.KeyEventInfo;

import java.util.function.Function;

import static soliloquy.specs.io.input.keyboard.entities.KeyBinding.keyBinding;

public class KeyBindingDefinitionReader {
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;

    public KeyBindingDefinitionReader(
            @SuppressWarnings("rawtypes") Function<String, Action> getAction
    ) {
        GET_ACTION = Check.ifNull(getAction, "getAction");
    }

    public KeyBinding read(KeyBindingDefinition definition) {
        @SuppressWarnings("unchecked") Action<KeyEventInfo> onPress =
                definition.PRESS_ACTION_ID == null ? null :
                        GET_ACTION.apply(definition.PRESS_ACTION_ID);
        @SuppressWarnings("unchecked") Action<KeyEventInfo> onRelease =
                definition.RELEASE_ACTION_ID == null ? null :
                        GET_ACTION.apply(definition.RELEASE_ACTION_ID);

        //noinspection DataFlowIssue
        return keyBinding(definition.CHARS, onPress, onRelease);
    }
}
