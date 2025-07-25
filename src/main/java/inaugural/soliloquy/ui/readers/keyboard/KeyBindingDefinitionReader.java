package inaugural.soliloquy.ui.readers.keyboard;

import inaugural.soliloquy.tools.Check;
import org.apache.commons.lang3.function.TriFunction;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;
import soliloquy.specs.ui.keyboard.KeyBinding;

import java.util.function.Consumer;
import java.util.function.Function;

public class KeyBindingDefinitionReader {
    private final TriFunction<Character[], Consumer<Long>, Consumer<Long>, KeyBinding> FACTORY;
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;

    public KeyBindingDefinitionReader(
            TriFunction<Character[], Consumer<Long>, Consumer<Long>, KeyBinding> factory,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction) {
        FACTORY = Check.ifNull(factory, "factory");
        GET_ACTION = Check.ifNull(getAction, "getAction");
    }

    public KeyBinding read(KeyBindingDefinition definition) {
        @SuppressWarnings("unchecked") Action<Long> onPress =
                definition.PRESS_ACTION_ID == null ? null :
                        GET_ACTION.apply(definition.PRESS_ACTION_ID);
        @SuppressWarnings("unchecked") Action<Long> onRelease =
                definition.RELEASE_ACTION_ID == null ? null :
                        GET_ACTION.apply(definition.RELEASE_ACTION_ID);

        //noinspection DataFlowIssue
        return FACTORY.apply(definition.CHARS, onPress::run, onRelease::run);
    }
}
