package inaugural.soliloquy.ui.test.unit.readers.keyboard;

import inaugural.soliloquy.ui.readers.keyboard.KeyBindingDefinitionReader;
import inaugural.soliloquy.ui.test.unit.readers.content.AbstractContentDefinitionTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static inaugural.soliloquy.tools.collections.Collections.arrayChars;
import static inaugural.soliloquy.tools.random.Random.randomChar;
import static org.junit.jupiter.api.Assertions.*;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;

@ExtendWith(MockitoExtension.class)
public class KeyBindingDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final char KEY = randomChar();

    private KeyBindingDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        reader = new KeyBindingDefinitionReader(MOCK_GET_ACTION);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new KeyBindingDefinitionReader(null));
    }

    @Test
    public void testRead() {
        var definition = binding(ON_PRESS_ID, ON_RELEASE_ID, KEY);

        var binding = reader.read(definition);

        assertNotNull(binding);
        assertArrayEquals(arrayChars(KEY), binding.BOUND_KEYS);
        assertSame(MOCK_ON_PRESS, binding.ON_PRESS);
        assertSame(MOCK_ON_RELEASE, binding.ON_RELEASE);
    }
}
