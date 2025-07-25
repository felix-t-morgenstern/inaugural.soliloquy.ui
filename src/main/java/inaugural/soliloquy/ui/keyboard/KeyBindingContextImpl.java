package inaugural.soliloquy.ui.keyboard;

import soliloquy.specs.ui.keyboard.KeyBinding;
import soliloquy.specs.ui.keyboard.KeyBindingContext;

import java.util.List;

import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class KeyBindingContextImpl implements KeyBindingContext {
    private final boolean BLOCKS_LOWER_BINDINGS;
    private final List<KeyBinding> BINDINGS;

    public KeyBindingContextImpl(boolean blocksLowerBindings, KeyBinding... bindings) {
        BLOCKS_LOWER_BINDINGS = blocksLowerBindings;
        BINDINGS = listOf(bindings);
    }

    @Override
    public List<KeyBinding> bindings() {
        return listOf(BINDINGS);
    }

    @Override
    public boolean blocksLowerBindings() {
        return BLOCKS_LOWER_BINDINGS;
    }
}
