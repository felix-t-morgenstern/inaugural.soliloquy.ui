package inaugural.soliloquy.ui.test.readers.providers;

import inaugural.soliloquy.ui.readers.providers.FiniteSinusoidMovingProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.FiniteSinusoidMovingProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.FiniteSinusoidMovingProviderFactory;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

@ExtendWith(MockitoExtension.class)
public class FiniteSinusoidMovingProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private FiniteSinusoidMovingProvider mockProvider;
    @Mock private FiniteSinusoidMovingProviderFactory mockFactory;

    private FiniteSinusoidMovingProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new FiniteSinusoidMovingProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteSinusoidMovingProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        //noinspection unchecked
        when(mockFactory.make(any(), any(), any(), any(), any())).thenReturn(mockProvider);
        var sharpness = randomFloat();
        var vals = pairOf(randomInt(), randomFloat());
        var contentRenderTimestamp = randomLong();
        var definition = finiteSinusoidMoving(listOf(sharpness), vals);

        var provider = reader.read(definition, contentRenderTimestamp);

        assertSame(mockProvider, provider);
        var expectedRenderTimestamp = contentRenderTimestamp + vals.FIRST;
        verify(mockFactory, once()).make(any(),
                eq(mapOf(pairOf(expectedRenderTimestamp, vals.SECOND))), eq(listOf(sharpness)),
                isNull(), isNull());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(
                finiteSinusoidMoving(listOf(),
                        (soliloquy.specs.common.valueobjects.Pair<Integer, Object>) null),
                randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(
                finiteSinusoidMoving(listOf(),
                        (soliloquy.specs.common.valueobjects.Pair<Integer, Object>[]) null),
                randomLong()));
    }
}
