package inaugural.soliloquy.ui.test.readers.providers;

import inaugural.soliloquy.ui.readers.providers.FiniteLinearMovingColorProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.FiniteLinearMovingColorProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.FiniteLinearMovingColorProviderFactory;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.FiniteLinearMovingColorProviderDefinition.finiteLinearMovingColor;

@ExtendWith(MockitoExtension.class)
public class FiniteLinearMovingColorProviderDefinitionReaderTests {
    @Mock private FiniteLinearMovingColorProvider mockProvider;
    @Mock private FiniteLinearMovingColorProviderFactory mockFactory;

    private FiniteLinearMovingColorProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new FiniteLinearMovingColorProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteLinearMovingColorProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockFactory.make(any(), any(), any(), any(), any())).thenReturn(mockProvider);
        var vals = pairOf(randomInt(), randomColor());
        var isClockwise = randomBoolean();
        var contentRenderTimestamp = randomLong();
        var definition = finiteLinearMovingColor(listOf(isClockwise), vals);

        var provider = reader.read(definition, contentRenderTimestamp);

        assertSame(mockProvider, provider);
        var expectedRenderTimestamp = contentRenderTimestamp + vals.FIRST;
        verify(mockFactory, once()).make(any(),
                eq(mapOf(pairOf(expectedRenderTimestamp, vals.SECOND))), eq(listOf(isClockwise)),
                isNull(), isNull());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(finiteLinearMovingColor(null,
                        (soliloquy.specs.common.valueobjects.Pair<Integer, java.awt.Color>) null),
                randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(finiteLinearMovingColor(null,
                        (soliloquy.specs.common.valueobjects.Pair<Integer, java.awt.Color>[]) null),
                randomLong()));
    }
}
