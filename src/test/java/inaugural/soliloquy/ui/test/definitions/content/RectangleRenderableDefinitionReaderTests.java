package inaugural.soliloquy.ui.test.definitions.content;

import inaugural.soliloquy.ui.definitions.content.RectangleRenderableDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

@ExtendWith(MockitoExtension.class)
public class RectangleRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    @Mock private RectangleRenderable mockRenderable;
    @Mock private RectangleRenderableFactory mockFactory;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @SuppressWarnings("rawtypes") @Mock private StaticProvider mockNullProvider;

    private RectangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader =
                new RectangleRenderableDefinitionReader(mockFactory, MOCK_ACTIONS_AND_LOOKUP.lookup,
                        mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(null, MOCK_ACTIONS_AND_LOOKUP.lookup,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory, null,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory,
                        MOCK_ACTIONS_AND_LOOKUP.lookup, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory,
                        MOCK_ACTIONS_AND_LOOKUP.lookup, mockProviderDefinitionReader, null));
    }

    @Test
    public void testRead() {
        @SuppressWarnings("unchecked") var mockAreaProviderDefinition =
                (AbstractProviderDefinition<FloatBox>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var topLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var topRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureIdProviderDefinition =
                (AbstractProviderDefinition<Integer>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureWidthProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var textureHeightProviderDefinition =
                (AbstractProviderDefinition<Float>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var mockAreaProvider =
                (ProviderAtTime<FloatBox>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var topLeftColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var topRightColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var bottomLeftColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var bottomRightColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureIdProvider =
                (ProviderAtTime<Integer>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureWidthProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var textureHeightProvider =
                (ProviderAtTime<Float>) mock(ProviderAtTime.class);
        var mockStack = mock(RenderableStack.class);

        when(mockProviderDefinitionReader.read(mockAreaProviderDefinition)).thenReturn(
                mockAreaProvider);
        when(mockProviderDefinitionReader.read(topLeftColorDefinition)).thenReturn(topLeftColor);
        when(mockProviderDefinitionReader.read(topRightColorDefinition)).thenReturn(topRightColor);
        when(mockProviderDefinitionReader.read(bottomLeftColorDefinition)).thenReturn(
                bottomLeftColor);
        when(mockProviderDefinitionReader.read(bottomRightColorDefinition)).thenReturn(
                bottomRightColor);
        when(mockProviderDefinitionReader.read(textureIdProviderDefinition)).thenReturn(
                textureIdProvider);
        when(mockProviderDefinitionReader.read(textureWidthProviderDefinition)).thenReturn(
                textureWidthProvider);
        when(mockProviderDefinitionReader.read(textureHeightProviderDefinition)).thenReturn(
                textureHeightProvider);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = rectangle(mockAreaProviderDefinition, Z)
                .withColors(
                        topLeftColorDefinition,
                        topRightColorDefinition,
                        bottomLeftColorDefinition,
                        bottomRightColorDefinition)
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
        verify(mockProviderDefinitionReader, once()).read(mockAreaProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(topLeftColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(topRightColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(bottomLeftColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(bottomRightColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureIdProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureWidthProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(textureHeightProviderDefinition);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_PRESS_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_RELEASE_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_ACTIONS_AND_LOOKUP.lookup, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(topLeftColor), same(topRightColor),
                same(bottomLeftColor), same(bottomRightColor),
                same(textureIdProvider), same(textureWidthProvider), same(textureHeightProvider),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockStack));
    }

    @Test
    public void testReadWithMinimalArgs() {
        @SuppressWarnings("unchecked") var mockAreaProviderDefinition =
                (AbstractProviderDefinition<FloatBox>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var mockAreaProvider =
                (ProviderAtTime<FloatBox>) mock(ProviderAtTime.class);
        var mockStack = mock(RenderableStack.class);

        when(mockProviderDefinitionReader.read(mockAreaProviderDefinition)).thenReturn(
                mockAreaProvider);

        when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = rectangle(mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockStack, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(mockAreaProviderDefinition);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockNullProvider), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()), eq(mapOf()),
                isNull(), isNull(),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockStack));
    }
}
