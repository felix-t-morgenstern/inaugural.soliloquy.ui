package inaugural.soliloquy.ui.test.keyboard;

import inaugural.soliloquy.tools.timing.TimestampValidator;
import inaugural.soliloquy.ui.keyboard.KeyBindingImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.keyboard.KeyBinding;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomChar;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KeyBindingImplTests {
    private final Long TIMESTAMP = randomLong();
    private final Character[] CHARACTERS = arrayOf(randomChar(), randomChar(), randomChar());

    @Mock private Component mockComponent;
    @Mock private Action<Object> mockOnPress;
    @Mock private Action<Object> mockOnRelease;
    @Mock private TimestampValidator mockTimestampValidator;

    private KeyBinding keyBinding;

    @BeforeEach
    public void setUp() {
        keyBinding =
                new KeyBindingImpl(mockComponent, CHARACTERS, mockOnPress, mockOnRelease, mockTimestampValidator);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new KeyBindingImpl(null, CHARACTERS, null, null, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new KeyBindingImpl(mockComponent, null, null, null, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new KeyBindingImpl(mockComponent, arrayOf(), null, null, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class, () -> new KeyBindingImpl(mockComponent, arrayOf(
                (Character) null), null, null, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new KeyBindingImpl(mockComponent, CHARACTERS, null, null, null));
    }

    @Test
    public void testBoundCharacters() {
        var boundCharacters = keyBinding.boundCharacters();

        assertNotNull(boundCharacters);
        assertNotSame(keyBinding.boundCharacters(), boundCharacters);
        assertEquals(CHARACTERS.length, boundCharacters.size());
        for (var c : CHARACTERS) {
            assertTrue(boundCharacters.contains(c));
        }
    }

    @Test
    public void testOnPress() {
        keyBinding.press(TIMESTAMP);

        verify(mockTimestampValidator, once()).validateTimestamp(TIMESTAMP);
        verify(mockOnPress, once()).run(same(mockComponent), eq(TIMESTAMP));
    }

    @Test
    public void testOnRelease() {
        keyBinding.release(TIMESTAMP);

        verify(mockTimestampValidator, once()).validateTimestamp(TIMESTAMP);
        verify(mockOnRelease, once()).run(same(mockComponent), eq(TIMESTAMP));
    }
}
