package inaugural.soliloquy.ui.test.readers.keyboard;

import inaugural.soliloquy.ui.readers.keyboard.KeyBindingDefinitionReader;
import inaugural.soliloquy.ui.test.readers.content.AbstractContentDefinitionTests;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.ui.keyboard.KeyBinding;

import java.util.function.Consumer;

import static inaugural.soliloquy.tools.random.Random.randomChar;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;

@ExtendWith(MockitoExtension.class)
public class KeyBindingDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final char CHAR = randomChar();

    @Mock KeyBinding mockBinding;
    @Mock TriFunction<Character[], Consumer<Long>, Consumer<Long>, KeyBinding> mockFactory;

    private KeyBindingDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        reader = new KeyBindingDefinitionReader(mockFactory, MOCK_GET_ACTION);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new KeyBindingDefinitionReader(mockFactory, null));
        assertThrows(IllegalArgumentException.class, () -> new KeyBindingDefinitionReader(null, MOCK_GET_ACTION));
    }

    @Test
    public void testRead() {
        when(mockFactory.apply(any(), any(), any())).thenReturn(mockBinding);
        var definition = binding(ON_PRESS_ID, ON_RELEASE_ID, CHAR);

        var binding = reader.read(definition);

        assertNotNull(binding);
    }
}
