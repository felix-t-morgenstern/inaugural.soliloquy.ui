package inaugural.soliloquy.ui.test.definitions.content;

import inaugural.soliloquy.ui.definitions.content.RasterizedLineSegmentRenderableDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.RasterizedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RasterizedLineSegmentRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomShort;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.content.RasterizedLineSegmentRenderableDefinition.rasterizedLineSegment;

@ExtendWith(MockitoExtension.class)
public class RasterizedLineSegmentRenderableDefinitionReaderTests {
    private final short STIPPLE_PATTERN = randomShort();
    private final short STIPPLE_FACTOR = randomShort();
    private final short DEFAULT_STIPPLE_PATTERN = randomShort();
    private final short DEFAULT_STIPPLE_FACTOR = randomShort();
    private final int Z = randomInt();

    @Mock private RasterizedLineSegmentRenderable mockRenderable;
    @Mock private RasterizedLineSegmentRenderableFactory mockFactory;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @Mock private RenderableStack mockStack;

    private RasterizedLineSegmentRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new RasterizedLineSegmentRenderableDefinitionReader(mockFactory,
                mockProviderDefinitionReader, DEFAULT_STIPPLE_PATTERN, DEFAULT_STIPPLE_FACTOR);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new RasterizedLineSegmentRenderableDefinitionReader(null,
                        mockProviderDefinitionReader, DEFAULT_STIPPLE_PATTERN,
                        DEFAULT_STIPPLE_FACTOR));
        assertThrows(IllegalArgumentException.class,
                () -> new RasterizedLineSegmentRenderableDefinitionReader(mockFactory, null,
                        DEFAULT_STIPPLE_PATTERN, DEFAULT_STIPPLE_FACTOR));
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

        @SuppressWarnings("unchecked") var vertex1 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var vertex2 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var thickness =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);

        when(mockProviderDefinitionReader.read(vertex1Definition)).thenReturn(vertex1);
        when(mockProviderDefinitionReader.read(vertex2Definition)).thenReturn(vertex2);
        when(mockProviderDefinitionReader.read(thicknessDefinition)).thenReturn(thickness);
        when(mockProviderDefinitionReader.read(colorDefinition)).thenReturn(color);

        when(mockFactory.make(any(), any(), any(), anyShort(), anyShort(), any(), anyInt(), any(),
                any())).thenReturn(mockRenderable);

        var definition =
                rasterizedLineSegment(vertex1Definition, vertex2Definition, thicknessDefinition,
                        colorDefinition, Z)
                        .withStipple(STIPPLE_PATTERN, STIPPLE_FACTOR);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(vertex1Definition);
        verify(mockProviderDefinitionReader, once()).read(vertex2Definition);
        verify(mockProviderDefinitionReader, once()).read(thicknessDefinition);
        verify(mockProviderDefinitionReader, once()).read(colorDefinition);
        verify(mockFactory, once()).make(
                same(vertex1), same(vertex2),
                same(thickness), eq(STIPPLE_PATTERN), eq(STIPPLE_FACTOR),
                same(color),
                eq(Z),
                isNotNull(),
                same(mockStack)
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        @SuppressWarnings("unchecked") var vertex1Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var vertex2Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var thicknessDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var colorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);

        @SuppressWarnings("unchecked") var vertex1 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var vertex2 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var thickness =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);

        when(mockProviderDefinitionReader.read(vertex1Definition)).thenReturn(vertex1);
        when(mockProviderDefinitionReader.read(vertex2Definition)).thenReturn(vertex2);
        when(mockProviderDefinitionReader.read(thicknessDefinition)).thenReturn(thickness);
        when(mockProviderDefinitionReader.read(colorDefinition)).thenReturn(color);

        when(mockFactory.make(any(), any(), any(), anyShort(), anyShort(), any(), anyInt(), any(),
                any())).thenReturn(mockRenderable);

        var definition =
                rasterizedLineSegment(vertex1Definition, vertex2Definition, thicknessDefinition,
                        colorDefinition, Z);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(vertex1Definition);
        verify(mockProviderDefinitionReader, once()).read(vertex2Definition);
        verify(mockProviderDefinitionReader, once()).read(thicknessDefinition);
        verify(mockProviderDefinitionReader, once()).read(colorDefinition);
        verify(mockFactory, once()).make(
                same(vertex1), same(vertex2),
                same(thickness), eq(DEFAULT_STIPPLE_PATTERN), eq(DEFAULT_STIPPLE_FACTOR),
                same(color),
                eq(Z),
                isNotNull(),
                same(mockStack)
        );
    }
}
