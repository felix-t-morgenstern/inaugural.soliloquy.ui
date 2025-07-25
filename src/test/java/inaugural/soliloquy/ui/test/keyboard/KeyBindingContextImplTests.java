package inaugural.soliloquy.ui.test.keyboard;

import inaugural.soliloquy.ui.keyboard.KeyBindingContextImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.ui.keyboard.KeyBinding;
import soliloquy.specs.ui.keyboard.KeyBindingContext;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.random.Random.randomBoolean;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class KeyBindingContextImplTests {
    private final boolean BLOCKS_LOWER_BINDINGS = randomBoolean();

    @Mock private KeyBinding mockBinding;

    private KeyBindingContext keyBindingContext;

    @BeforeEach
    public void setUp() {
        keyBindingContext = new KeyBindingContextImpl(BLOCKS_LOWER_BINDINGS, mockBinding);
    }

    @Test
    public void testBindings() {
        assertNotNull(keyBindingContext.bindings());
        assertEquals(listOf(mockBinding), keyBindingContext.bindings());
        assertNotSame(keyBindingContext.bindings(), keyBindingContext.bindings());
    }

    @Test
    public void testBlocksAllLowerBindings() {
        assertEquals(BLOCKS_LOWER_BINDINGS, keyBindingContext.blocksLowerBindings());
    }
}
