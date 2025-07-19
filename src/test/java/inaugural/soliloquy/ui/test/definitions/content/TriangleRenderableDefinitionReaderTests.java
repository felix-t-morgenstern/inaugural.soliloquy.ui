package inaugural.soliloquy.ui.test.definitions.content;

import inaugural.soliloquy.ui.definitions.content.TriangleRenderableDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TriangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
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
    @SuppressWarnings("rawtypes") @Mock private StaticProvider mockNullProvider;

    @Mock private AbstractProviderDefinition<Vertex> mockVector1Definition;
    @Mock private AbstractProviderDefinition<Vertex> mockVector2Definition;
    @Mock private AbstractProviderDefinition<Vertex> mockVector3Definition;
    @Mock private ProviderAtTime<Vertex> mockVector1;
    @Mock private ProviderAtTime<Vertex> mockVector2;
    @Mock private ProviderAtTime<Vertex> mockVector3;

    private TriangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockProviderDefinitionReader.read(mockVector1Definition)).thenReturn(
                mockVector1);
        lenient().when(mockProviderDefinitionReader.read(mockVector2Definition)).thenReturn(
                mockVector2);
        lenient().when(mockProviderDefinitionReader.read(mockVector3Definition)).thenReturn(
                mockVector3);

        lenient().when(
                        mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(),
                                any(), any(), any(), any(), any(), anyInt(), any(), any()))
                .thenReturn(mockRenderable);

        reader = new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_ACTION,
                mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(null, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, null,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_ACTION, null,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, null));
    }

    @Test
    public void testRead() {
        @SuppressWarnings({"unchecked"}) var vector1ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector2ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector3ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureIdProviderDefinition =
                (AbstractProviderDefinition<Integer>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureWidthProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureHeightProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector1Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector2Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector3Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureIdProvider =
                (ProviderAtTime<Integer>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureWidthProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureHeightProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        var mockStack = mock(RenderableStack.class);

        when(mockProviderDefinitionReader.read(mockVector1Definition)).thenReturn(mockVector1);
        when(mockProviderDefinitionReader.read(vector1ColorDefinition)).thenReturn(vector1Color);
        when(mockProviderDefinitionReader.read(mockVector2Definition)).thenReturn(mockVector2);
        when(mockProviderDefinitionReader.read(vector2ColorDefinition)).thenReturn(vector2Color);
        when(mockProviderDefinitionReader.read(mockVector3Definition)).thenReturn(mockVector3);
        when(mockProviderDefinitionReader.read(vector3ColorDefinition)).thenReturn(vector3Color);
        when(mockProviderDefinitionReader.read(textureIdProviderDefinition)).thenReturn(
                textureIdProvider);
        when(mockProviderDefinitionReader.read(textureWidthProviderDefinition)).thenReturn(
                textureWidthProvider);
        when(mockProviderDefinitionReader.read(textureHeightProviderDefinition)).thenReturn(
                textureHeightProvider);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = triangle(mockVector1Definition, mockVector2Definition,
                mockVector3Definition, Z)
                .withColors(
                        vector1ColorDefinition,
                        vector2ColorDefinition,
                        vector3ColorDefinition)
                .withTexture(
                        textureIdProviderDefinition,
                        textureWidthProviderDefinition,
                        textureHeightProviderDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(mockVector1Definition);
        verify(mockProviderDefinitionReader, once()).read(vector1ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockVector2Definition);
        verify(mockProviderDefinitionReader, once()).read(vector2ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockVector3Definition);
        verify(mockProviderDefinitionReader, once()).read(vector3ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureIdProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureWidthProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureHeightProviderDefinition);
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockVector1), same(vector1Color),
                same(mockVector2), same(vector2Color),
                same(mockVector3), same(vector3Color),
                same(textureIdProvider), same(textureWidthProvider), same(textureHeightProvider),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                eq(Z),
                isNotNull(),
                same(mockStack));
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = triangle(mockVector1Definition, mockVector2Definition,
                mockVector3Definition, Z);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(mockVector1Definition);
        verify(mockProviderDefinitionReader, once()).read(mockVector2Definition);
        verify(mockProviderDefinitionReader, once()).read(mockVector3Definition);
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
                same(mockStack));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null,
                triangle(mockVector1Definition, mockVector2Definition, mockVector3Definition, Z)));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockStack, null));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockStack,
                triangle(null, mockVector2Definition, mockVector3Definition, Z)));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockStack,
                triangle(mockVector1Definition, null, mockVector3Definition, Z)));
        assertThrows(IllegalArgumentException.class, () -> reader.read(mockStack,
                triangle(mockVector1Definition, mockVector2Definition, null, Z)));
    }
}
