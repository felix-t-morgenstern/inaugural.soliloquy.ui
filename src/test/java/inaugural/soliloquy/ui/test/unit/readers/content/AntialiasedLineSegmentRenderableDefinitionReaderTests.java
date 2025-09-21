package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.renderables.AntialiasedLineSegmentRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.AntialiasedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.factories.AntialiasedLineSegmentRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.content.AntialiasedLineSegmentRenderableDefinition.antialiasedLine;

@ExtendWith(MockitoExtension.class)
public class AntialiasedLineSegmentRenderableDefinitionReaderTests
        extends AbstractContentDefinitionTests {
    private final long TIMESTAMP = randomLong();

    @Mock private AntialiasedLineSegmentRenderable mockRenderable;
    @Mock private AntialiasedLineSegmentRenderableFactory mockFactory;

    private AntialiasedLineSegmentRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new AntialiasedLineSegmentRenderableDefinitionReader(mockFactory,
                mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new AntialiasedLineSegmentRenderableDefinitionReader(null,
                        mockProviderDefinitionReader));
        assertThrows(IllegalArgumentException.class,
                () -> new AntialiasedLineSegmentRenderableDefinitionReader(mockFactory, null));
    }

    @Test
    public void testRead() {
        @SuppressWarnings("unchecked") var vertex1Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var vertex2Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var thicknessDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var colorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var thicknessGradientPercentDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var lengthGradientPercentDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);

        @SuppressWarnings("unchecked") var vertex1 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var vertex2 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var thickness =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var thicknessGradientPercent =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var lengthGradientPercent =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);

        when(mockProviderDefinitionReader.read(same(vertex1Definition), anyLong())).thenReturn(
                vertex1);
        when(mockProviderDefinitionReader.read(same(vertex2Definition), anyLong())).thenReturn(
                vertex2);
        when(mockProviderDefinitionReader.read(same(thicknessDefinition), anyLong())).thenReturn(
                thickness);
        when(mockProviderDefinitionReader.read(same(colorDefinition), anyLong())).thenReturn(color);
        when(mockProviderDefinitionReader.read(same(thicknessGradientPercentDefinition),
                anyLong())).thenReturn(thicknessGradientPercent);
        when(mockProviderDefinitionReader.read(same(lengthGradientPercentDefinition),
                anyLong())).thenReturn(lengthGradientPercent);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), anyInt(), any(), any()))
                .thenReturn(mockRenderable);

        var z = randomInt();

        var definition = antialiasedLine(vertex1Definition, vertex2Definition,
                thicknessDefinition, colorDefinition,
                thicknessGradientPercentDefinition, lengthGradientPercentDefinition, z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(same(vertex1Definition), eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(vertex2Definition), eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(thicknessDefinition), eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(colorDefinition), eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(thicknessGradientPercentDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(lengthGradientPercentDefinition),
                eq(TIMESTAMP));
        verify(mockFactory, once()).make(
                same(vertex1),
                same(vertex2),
                same(color),
                same(thickness),
                same(thicknessGradientPercent),
                same(lengthGradientPercent),
                eq(z),
                isNotNull(),
                same(mockComponent)
        );
    }
}
