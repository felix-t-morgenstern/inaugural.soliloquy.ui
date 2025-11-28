package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.renderables.TriangleRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TriangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;

@ExtendWith(MockitoExtension.class)
public class TriangleRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    @Mock private TriangleRenderable mockRenderable;
    @Mock private TriangleRenderableFactory mockFactory;

    @Mock private AbstractProviderDefinition<Vertex> mockVector1Definition;
    @Mock private AbstractProviderDefinition<Vertex> mockVector2Definition;
    @Mock private AbstractProviderDefinition<Vertex> mockVector3Definition;

    @Mock private ProviderAtTime<Vertex> mockVector1;
    @Mock private ProviderAtTime<Vertex> mockVector2;
    @Mock private ProviderAtTime<Vertex> mockVector3;
    @Mock private ProviderAtTime<Color> mockVector1Color;
    @Mock private ProviderAtTime<Color> mockVector2Color;
    @Mock private ProviderAtTime<Color> mockVector3Color;

    private TriangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(
                        mockProviderDefinitionReader.read(same(mockVector1Definition),
                                eq(TIMESTAMP)))
                .thenReturn(mockVector1);
        lenient().when(
                        mockProviderDefinitionReader.read(same(mockVector2Definition),
                                eq(TIMESTAMP)))
                .thenReturn(mockVector2);
        lenient().when(
                        mockProviderDefinitionReader.read(same(mockVector3Definition),
                                eq(TIMESTAMP)))
                .thenReturn(mockVector3);

        lenient().when(
                        mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(),
                                any(), any(), any(), any(), any(), anyInt(), any(), any()))
                .thenReturn(mockRenderable);

        reader = new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_CONSUMER,
                mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(null, MOCK_GET_CONSUMER,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, null,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_CONSUMER, null,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_CONSUMER,
                        mockProviderDefinitionReader, null));
    }

    @Test
    public void testReadFromDefsWithMaximalArgs() {
        @SuppressWarnings({"unchecked"}) var vector1ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector2ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector3ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);

        when(mockProviderDefinitionReader.read(same(vector1ColorDefinition), anyLong())).thenReturn(
                mockVector1Color);
        when(mockProviderDefinitionReader.read(same(vector2ColorDefinition), anyLong())).thenReturn(
                mockVector2Color);
        when(mockProviderDefinitionReader.read(same(vector3ColorDefinition), anyLong())).thenReturn(
                mockVector3Color);

        var definition = triangle(mockVector1Definition, mockVector2Definition,
                mockVector3Definition, Z)
                .withColors(
                        vector1ColorDefinition,
                        vector2ColorDefinition,
                        vector3ColorDefinition)
                .withTexture(
                        mockTextureIdProviderDefinition,
                        mockTextureWidthProviderDefinition,
                        mockTextureHeightProviderDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockRenderable, once()).setCapturesMouseEvents(true);
        verify(mockProviderDefinitionReader, once()).read(same(mockVector1Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(vector1ColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockVector2Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(vector2ColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockVector3Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(vector3ColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureIdProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureWidthProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureHeightProviderDefinition),
                eq(TIMESTAMP));
        verify(MOCK_GET_CONSUMER, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockVector1), same(mockVector1Color),
                same(mockVector2), same(mockVector2Color),
                same(mockVector3), same(mockVector3Color),
                same(mockTextureIdProvider), same(mockTextureWidthProvider), same(
                        mockTextureHeightProvider),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = triangle(mockVector1Definition, mockVector2Definition,
                mockVector3Definition, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, times(3)).read(any(), anyLong());
        verify(mockProviderDefinitionReader, once()).read(same(mockVector1Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockVector2Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockVector3Definition),
                eq(TIMESTAMP));
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockVector1), same(mockNullProvider),
                same(mockVector2), same(mockNullProvider),
                same(mockVector3), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadFromProviders() {
        var definition = triangle(mockVector1, mockVector2, mockVector3, Z)
                .withColors(
                        mockVector1Color,
                        mockVector2Color,
                        mockVector3Color);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, never()).read(same(mockVector1Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, never()).read(same(mockVector2Definition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, never()).read(same(mockVector3Definition),
                eq(TIMESTAMP));
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockVector1), same(mockVector1Color),
                same(mockVector2), same(mockVector2Color),
                same(mockVector3), same(mockVector3Color),
                same(mockNullProvider), same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null,
                triangle(mockVector1Definition, mockVector2Definition, mockVector3Definition, Z),
                TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockComponent,
                triangle(null, mockVector2Definition, mockVector3Definition, Z), TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockComponent,
                triangle(mockVector1Definition, null, mockVector3Definition, Z), TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockComponent,
                triangle(mockVector1Definition, mockVector2Definition, null, Z), TIMESTAMP));
    }
}
