package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.*;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.ui.definitions.content.*;

import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContentDefinitionReaderTests {
    private final long TIMESTAMP = randomLong();

    @Mock private RasterizedLineSegmentRenderableDefinition mockRasterizedLineDefinition;
    @Mock private AntialiasedLineSegmentRenderableDefinition mockAntialiasedLineDefinition;
    @Mock private RectangleRenderableDefinition mockRectangleDefinition;
    @Mock private TriangleRenderableDefinition mockTriangleDefinition;
    @Mock private SpriteRenderableDefinition mockSpriteDefinition;
    @Mock private ImageAssetSetRenderableDefinition mockImageAssetSetDefinition;
    @Mock private FiniteAnimationRenderableDefinition mockFiniteAnimationDefinition;
    @Mock private TextLineRenderableDefinition mockTextLineDefinition;
    @Mock private ComponentDefinition mockComponentDefinition;

    @Mock private RasterizedLineSegmentRenderableDefinitionReader mockRasterizedLineReader;
    @Mock private AntialiasedLineSegmentRenderableDefinitionReader mockAntialiasedLineReader;
    @Mock private RectangleRenderableDefinitionReader mockRectangleReader;
    @Mock private TriangleRenderableDefinitionReader mockTriangleReader;
    @Mock private SpriteRenderableDefinitionReader mockSpriteReader;
    @Mock private ImageAssetSetRenderableDefinitionReader mockImageAssetSetReader;
    @Mock private FiniteAnimationRenderableDefinitionReader mockFiniteAnimationReader;
    @Mock private TextLineRenderableDefinitionReader mockTextLineReader;
    @Mock private TriFunction<ComponentDefinition, Component, Long, Component> mockComponentReader;

    @Mock private RasterizedLineSegmentRenderable mockRasterizedLineRenderable;
    @Mock private AntialiasedLineSegmentRenderable mockAntialiasedLineRenderable;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private TriangleRenderable mockTriangleRenderable;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private ImageAssetSetRenderable mockImageAssetSetRenderable;
    @Mock private FiniteAnimationRenderable mockFiniteAnimationRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockComponent;

    @Mock private Component mockContainingComponent;

    private ContentDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockRasterizedLineReader.read(any(), any())).thenReturn(mockRasterizedLineRenderable);
        lenient().when(mockAntialiasedLineReader.read(any(), any())).thenReturn(mockAntialiasedLineRenderable);
        lenient().when(mockRectangleReader.read(any(), any())).thenReturn(mockRectangleRenderable);
        lenient().when(mockTriangleReader.read(any(), any())).thenReturn(mockTriangleRenderable);
        lenient().when(mockSpriteReader.read(any(), any())).thenReturn(mockSpriteRenderable);
        lenient().when(mockImageAssetSetReader.read(any(), any())).thenReturn(mockImageAssetSetRenderable);
        lenient().when(mockFiniteAnimationReader.read(any(), any(), anyLong())).thenReturn(mockFiniteAnimationRenderable);
        lenient().when(mockTextLineReader.read(any(), any())).thenReturn(mockTextLineRenderable);
        lenient().when(mockComponentReader.apply(any(), any(), anyLong())).thenReturn(mockComponent);

        reader = new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        );
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                null,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                null,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                null,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                null,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                null,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                null,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                null,
                mockTextLineReader,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                null,
                mockComponentReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new ContentDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                null
        ));
    }

    @Test
    public void testReadRasterizedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockRasterizedLineDefinition, TIMESTAMP);

        assertSame(mockRasterizedLineRenderable, output);
        verify(mockRasterizedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockRasterizedLineDefinition)
        );
    }

    @Test
    public void testReadAntialiasedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockAntialiasedLineDefinition, TIMESTAMP);

        assertSame(mockAntialiasedLineRenderable, output);
        verify(mockAntialiasedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockAntialiasedLineDefinition)
        );
    }

    @Test
    public void testReadRectangleDefinition() {
        var output = reader.read(mockContainingComponent, mockRectangleDefinition, TIMESTAMP);

        assertSame(mockRectangleRenderable, output);
        verify(mockRectangleReader, once()).read(
                same(mockContainingComponent),
                same(mockRectangleDefinition)
        );
    }

    @Test
    public void testReadTriangleDefinition() {
        var output = reader.read(mockContainingComponent, mockTriangleDefinition, TIMESTAMP);

        assertSame(mockTriangleRenderable, output);
        verify(mockTriangleReader, once()).read(
                same(mockContainingComponent),
                same(mockTriangleDefinition)
        );
    }

    @Test
    public void testReadSpriteDefinition() {
        var output = reader.read(mockContainingComponent, mockSpriteDefinition, TIMESTAMP);

        assertSame(mockSpriteRenderable, output);
        verify(mockSpriteReader, once()).read(
                same(mockContainingComponent),
                same(mockSpriteDefinition)
        );
    }

    @Test
    public void testReadImageAssetSetDefinition() {
        var output = reader.read(mockContainingComponent, mockImageAssetSetDefinition, TIMESTAMP);

        assertSame(mockImageAssetSetRenderable, output);
        verify(mockImageAssetSetReader, once()).read(
                same(mockContainingComponent),
                same(mockImageAssetSetDefinition)
        );
    }

    @Test
    public void testReadFiniteAnimationDefinition() {
        var output = reader.read(mockContainingComponent, mockFiniteAnimationDefinition, TIMESTAMP);

        assertSame(mockFiniteAnimationRenderable, output);
        verify(mockFiniteAnimationReader, once()).read(
                same(mockContainingComponent),
                same(mockFiniteAnimationDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadTextLineDefinition() {
        var output = reader.read(mockContainingComponent, mockTextLineDefinition, TIMESTAMP);

        assertSame(mockTextLineRenderable, output);
        verify(mockTextLineReader, once()).read(
                same(mockContainingComponent),
                same(mockTextLineDefinition)
        );
    }

    @Test
    public void testReadComponentDefinition() {
        var output = reader.read(mockContainingComponent, mockComponentDefinition, TIMESTAMP);

        assertSame(mockComponent, output);
        verify(mockComponentReader, once()).apply(
                same(mockComponentDefinition),
                same(mockContainingComponent),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, mockComponentDefinition, TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockContainingComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockContainingComponent, mock(AbstractContentDefinition.class), TIMESTAMP));
    }
}
