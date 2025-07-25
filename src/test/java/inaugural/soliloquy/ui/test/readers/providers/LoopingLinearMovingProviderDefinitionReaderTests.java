package inaugural.soliloquy.ui.test.readers.providers;

import inaugural.soliloquy.ui.readers.providers.LoopingLinearMovingProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.LoopingLinearMovingProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.LoopingLinearMovingProviderFactory;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;

@ExtendWith(MockitoExtension.class)
public class LoopingLinearMovingProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private LoopingLinearMovingProvider mockProvider;
    @Mock private LoopingLinearMovingProviderFactory mockFactory;

    private LoopingLinearMovingProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new LoopingLinearMovingProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoopingLinearMovingProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        //noinspection unchecked
        when(mockFactory.make(any(), anyInt(), anyInt(), any(), any(), any())).thenReturn(
                mockProvider);
        var periodDuration = randomInt();
        var renderTimestampOffset = randomInt();
        var vals = pairOf(randomInt(), randomFloat());
        var contentRenderTimestamp = randomLong();
        var definition = loopingLinearMoving(periodDuration, renderTimestampOffset, vals);

        var provider = reader.read(definition, contentRenderTimestamp);

        var expectedPeriodModuloOffset =
                periodDuration - (int) (contentRenderTimestamp % (periodDuration));
        assertSame(mockProvider, provider);
        verify(mockFactory, once()).make(any(), eq(periodDuration), eq(expectedPeriodModuloOffset),
                eq(mapOf(vals)), any(), any());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
        assertThrows(IllegalArgumentException.class, () -> reader.read(
                loopingLinearMoving(randomInt(), randomInt(),
                        (soliloquy.specs.common.valueobjects.Pair<Integer, Object>[]) null),
                randomLong()));
    }
}
