package inaugural.soliloquy.ui.test.definitions.providers;

import definitions.providers.FiniteLinearMovingProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.FiniteLinearMovingProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.FiniteLinearMovingProviderFactory;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.FiniteLinearMovingProviderDefinition.finiteLinearMoving;

@ExtendWith(MockitoExtension.class)
public class FiniteLinearMovingProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private FiniteLinearMovingProvider mockProvider;
    @Mock private FiniteLinearMovingProviderFactory mockFactory;

    private FiniteLinearMovingProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new FiniteLinearMovingProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteLinearMovingProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        //noinspection unchecked
        when(mockFactory.make(any(), any(), any(), any())).thenReturn(mockProvider);
        var vals = pairOf(randomInt(), randomFloat());
        var contentRenderTimestamp = randomLong();
        var definition = finiteLinearMoving(vals);

        var provider = reader.read(definition, contentRenderTimestamp);

        assertSame(mockProvider, provider);
        var expectedRenderTimestamp = contentRenderTimestamp + vals.FIRST;
        verify(mockFactory, once()).make(any(),
                eq(mapOf(pairOf(expectedRenderTimestamp, vals.SECOND))), isNull(), isNull());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(finiteLinearMoving(
                (soliloquy.specs.common.valueobjects.Pair<Integer, Object>) null), randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(finiteLinearMoving(
                (soliloquy.specs.common.valueobjects.Pair<Integer, Object>[]) null), randomLong()));
    }
}
