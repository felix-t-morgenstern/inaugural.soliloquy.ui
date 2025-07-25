package inaugural.soliloquy.ui.test.readers.providers;

import inaugural.soliloquy.ui.readers.providers.LoopingLinearMovingColorProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.LoopingLinearMovingColorProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.LoopingLinearMovingColorProviderFactory;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingColorProviderDefinition.loopingColor;

@ExtendWith(MockitoExtension.class)
public class LoopingLinearMovingColorProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private LoopingLinearMovingColorProvider mockProvider;
    @Mock private LoopingLinearMovingColorProviderFactory mockFactory;

    private LoopingLinearMovingColorProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new LoopingLinearMovingColorProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoopingLinearMovingColorProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockFactory.make(any(), any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(
                mockProvider);
        var periodDuration = randomInt();
        var renderTimestampOffset = randomInt();
        var isClockwise = randomBoolean();
        var vals = pairOf(randomInt(), randomColor());
        var contentRenderTimestamp = randomLong();
        var definition =
                loopingColor(periodDuration, renderTimestampOffset, listOf(isClockwise), vals);

        var provider = reader.read(definition, contentRenderTimestamp);

        var expectedPeriodModuloOffset =
                periodDuration - (int) (contentRenderTimestamp % (periodDuration));
        assertSame(mockProvider, provider);
        verify(mockFactory, once()).make(any(), eq(mapOf(vals)), eq(listOf(isClockwise)),
                eq(periodDuration), eq(expectedPeriodModuloOffset), isNull(), isNull());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(
                loopingColor(randomInt(), randomInt(), listOf(),
                        (soliloquy.specs.common.valueobjects.Pair<Integer, Color>[]) null),
                randomLong()));
    }
}
