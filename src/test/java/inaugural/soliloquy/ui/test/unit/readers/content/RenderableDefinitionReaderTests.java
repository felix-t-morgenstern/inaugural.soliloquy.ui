package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.renderables.*;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.*;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;

@ExtendWith(MockitoExtension.class)
public class RenderableDefinitionReaderTests {
    private final long TIMESTAMP = randomLong();

    @Mock private RasterizedLineSegmentRenderableDefinition mockRasterizedLineDefinition;
    @Mock private AntialiasedLineSegmentRenderableDefinition mockAntialiasedLineDefinition;
    @Mock private RectangleRenderableDefinition mockRectangleDefinition;
    @Mock private TriangleRenderableDefinition mockTriangleDefinition;
    @Mock private SpriteRenderableDefinition mockSpriteDefinition;
    @Mock private ImageAssetSetRenderableDefinition mockImageAssetSetDefinition;
    @Mock private FiniteAnimationRenderableDefinition mockFiniteAnimationDefinition;
    @Mock private TextLineRenderableDefinition mockTextLineDefinition;

    @Mock private RasterizedLineSegmentRenderableDefinitionReader mockRasterizedLineReader;
    @Mock private AntialiasedLineSegmentRenderableDefinitionReader mockAntialiasedLineReader;
    @Mock private RectangleRenderableDefinitionReader mockRectangleReader;
    @Mock private TriangleRenderableDefinitionReader mockTriangleReader;
    @Mock private SpriteRenderableDefinitionReader mockSpriteReader;
    @Mock private ImageAssetSetRenderableDefinitionReader mockImageAssetSetReader;
    @Mock private FiniteAnimationRenderableDefinitionReader mockFiniteAnimationReader;
    @Mock private TextLineRenderableDefinitionReader mockTextLineReader;
    @Mock private ComponentFactory mockComponentFactory;

    @Mock private RasterizedLineSegmentRenderable mockRasterizedLineRenderable;
    @Mock private AntialiasedLineSegmentRenderable mockAntialiasedLineRenderable;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private TriangleRenderable mockTriangleRenderable;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private ImageAssetSetRenderable mockImageAssetSetRenderable;
    @Mock private FiniteAnimationRenderable mockFiniteAnimationRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockComponent;

    @Mock private AbstractProviderDefinition<FloatBox> mockComponentDimensDefinition;
    @Mock private ProviderDefinitionReader mockProviderReader;
    @Mock private ProviderAtTime<FloatBox> mockComponentDimens;

    @Mock private Component mockContainingComponent;

    private RenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockRasterizedLineReader.read(any(), any(), anyLong()))
                .thenReturn(mockRasterizedLineRenderable);
        lenient().when(mockAntialiasedLineReader.read(any(), any(), anyLong()))
                .thenReturn(mockAntialiasedLineRenderable);
        lenient().when(mockRectangleReader.read(any(), any(), anyLong())).thenReturn(mockRectangleRenderable);
        lenient().when(mockTriangleReader.read(any(), any(), anyLong())).thenReturn(mockTriangleRenderable);
        lenient().when(mockSpriteReader.read(any(), any(), anyLong())).thenReturn(mockSpriteRenderable);
        lenient().when(mockImageAssetSetReader.read(any(), any(), anyLong()))
                .thenReturn(mockImageAssetSetRenderable);
        lenient().when(mockFiniteAnimationReader.read(any(), any(), anyLong()))
                .thenReturn(mockFiniteAnimationRenderable);
        lenient().when(mockTextLineReader.read(any(), any(), anyLong())).thenReturn(mockTextLineRenderable);

        lenient().when(mockComponentFactory.make(any(), anyInt(), any(), any(), any()))
                .thenReturn(mockComponent);

        lenient().when(mockProviderReader.read(same(mockComponentDimensDefinition), anyLong()))
                .thenReturn(mockComponentDimens);

        reader = new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        );
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                null,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                null,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                null,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                null,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                null,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                null,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                null,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                null,
                mockComponentFactory,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                null,
                mockProviderReader
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                null
        ));
    }

    @Test
    public void testReadRasterizedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockRasterizedLineDefinition, TIMESTAMP);

        assertSame(mockRasterizedLineRenderable, output);
        verify(mockRasterizedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockRasterizedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadAntialiasedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockAntialiasedLineDefinition, TIMESTAMP);

        assertSame(mockAntialiasedLineRenderable, output);
        verify(mockAntialiasedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockAntialiasedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadRectangleDefinition() {
        var output = reader.read(mockContainingComponent, mockRectangleDefinition, TIMESTAMP);

        assertSame(mockRectangleRenderable, output);
        verify(mockRectangleReader, once()).read(
                same(mockContainingComponent),
                same(mockRectangleDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadTriangleDefinition() {
        var output = reader.read(mockContainingComponent, mockTriangleDefinition, TIMESTAMP);

        assertSame(mockTriangleRenderable, output);
        verify(mockTriangleReader, once()).read(
                same(mockContainingComponent),
                same(mockTriangleDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadSpriteDefinition() {
        var output = reader.read(mockContainingComponent, mockSpriteDefinition, TIMESTAMP);

        assertSame(mockSpriteRenderable, output);
        verify(mockSpriteReader, once()).read(
                same(mockContainingComponent),
                same(mockSpriteDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadImageAssetSetDefinition() {
        var output = reader.read(mockContainingComponent, mockImageAssetSetDefinition, TIMESTAMP);

        assertSame(mockImageAssetSetRenderable, output);
        verify(mockImageAssetSetReader, once()).read(
                same(mockContainingComponent),
                same(mockImageAssetSetDefinition),
                eq(TIMESTAMP)
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
                same(mockTextLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadComponentDefinition() {
        var z = randomInt();
        var key = randomString();
        var val = randomInt();
        var data = Collections.<String, Object>mapOf(key, val);
        var componentDefinition = component(
                z,
                mockComponentDimensDefinition,
                mockRasterizedLineDefinition
        ).withData(data);

        var output = reader.read(mockContainingComponent, componentDefinition, TIMESTAMP);

        assertSame(mockComponent, output);
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                eq(z),
                same(mockComponentDimens),
                same(mockContainingComponent),
                eq(data)
        );
        verify(mockRasterizedLineReader, once()).read(
                same((Component) output),
                same(mockRasterizedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(null, mock(ComponentDefinition.class), TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockContainingComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockContainingComponent, mock(AbstractContentDefinition.class),
                        TIMESTAMP));
    }
}
