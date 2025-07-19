package inaugural.soliloquy.ui.test.definitions.content;

import inaugural.soliloquy.ui.definitions.content.TriangleRenderableDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @org.mockito.Mock private TriangleRenderable mockRenderable;
    @org.mockito.Mock private TriangleRenderableFactory mockFactory;
    @org.mockito.Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @SuppressWarnings("rawtypes") @org.mockito.Mock private StaticProvider mockNullProvider;

    private TriangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new TriangleRenderableDefinitionReader(mockFactory, MOCK_ACTIONS_AND_LOOKUP.lookup, mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(null, MOCK_ACTIONS_AND_LOOKUP.lookup, mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, null, mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_ACTIONS_AND_LOOKUP.lookup, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TriangleRenderableDefinitionReader(mockFactory, MOCK_ACTIONS_AND_LOOKUP.lookup, mockProviderDefinitionReader, null));
    }

    @Test
    public void testRead() {
        @SuppressWarnings({"unchecked"}) var vector1Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector1ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector2Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector2ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector3Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector3ColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureIdProviderDefinition =
                (AbstractProviderDefinition<Integer>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureWidthProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureHeightProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector1 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector1Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector2 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector2Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector3 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector3Color =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureIdProvider =
                (ProviderAtTime<Integer>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureWidthProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureHeightProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        var mockStack = mock(RenderableStack.class);

        when(mockProviderDefinitionReader.read(vector1Definition)).thenReturn(vector1);
        when(mockProviderDefinitionReader.read(vector1ColorDefinition)).thenReturn(vector1Color);
        when(mockProviderDefinitionReader.read(vector2Definition)).thenReturn(vector2);
        when(mockProviderDefinitionReader.read(vector2ColorDefinition)).thenReturn(vector2Color);
        when(mockProviderDefinitionReader.read(vector3Definition)).thenReturn(vector3);
        when(mockProviderDefinitionReader.read(vector3ColorDefinition)).thenReturn(vector3Color);
        when(mockProviderDefinitionReader.read(textureIdProviderDefinition)).thenReturn(
                textureIdProvider);
        when(mockProviderDefinitionReader.read(textureWidthProviderDefinition)).thenReturn(
                textureWidthProvider);
        when(mockProviderDefinitionReader.read(textureHeightProviderDefinition)).thenReturn(
                textureHeightProvider);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = triangle(vector1Definition, vector2Definition, vector3Definition, Z)
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
        verify(mockProviderDefinitionReader, once()).read(vector1Definition);
        verify(mockProviderDefinitionReader, once()).read(vector1ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(vector2Definition);
        verify(mockProviderDefinitionReader, once()).read(vector2ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(vector3Definition);
        verify(mockProviderDefinitionReader, once()).read(vector3ColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureIdProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureWidthProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureHeightProviderDefinition);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_PRESS_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_RELEASE_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(vector1), same(vector1Color),
                same(vector2), same(vector2Color),
                same(vector3), same(vector3Color),
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
        @SuppressWarnings({"unchecked"}) var vector1Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector2Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector3Definition =
                (AbstractProviderDefinition<Vertex>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings({"unchecked"}) var vector1 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector2 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        @SuppressWarnings({"unchecked"}) var vector3 =
                (ProviderAtTime<Vertex>) mock(ProviderAtTime.class);
        var mockStack = mock(RenderableStack.class);

        when(mockProviderDefinitionReader.read(vector1Definition)).thenReturn(vector1);
        when(mockProviderDefinitionReader.read(vector2Definition)).thenReturn(vector2);
        when(mockProviderDefinitionReader.read(vector3Definition)).thenReturn(vector3);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = triangle(vector1Definition, vector2Definition, vector3Definition, Z);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(vector1Definition);
        verify(mockProviderDefinitionReader, once()).read(vector2Definition);
        verify(mockProviderDefinitionReader, once()).read(vector3Definition);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(vector1), same(mockNullProvider),
                same(vector2), same(mockNullProvider),
                same(vector3), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                eq(Z),
                isNotNull(),
                same(mockStack));
    }
}
