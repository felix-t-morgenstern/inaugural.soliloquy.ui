package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.renderables.RectangleRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

@ExtendWith(MockitoExtension.class)
public class RectangleRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final long TIMESTAMP = randomLong();

    @Mock private RectangleRenderable mockRenderable;
    @Mock private RectangleRenderableFactory mockFactory;

    private RectangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockFactory.make(any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        reader =
                new RectangleRenderableDefinitionReader(mockFactory, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(null, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory, null,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory,
                        MOCK_GET_ACTION, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, null));
    }

    @Test
    public void testRead() {
        @SuppressWarnings("unchecked") var topLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var topRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var topLeftColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var topRightColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var bottomLeftColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") var bottomRightColor =
                (ProviderAtTime<Color>) mock(ProviderAtTime.class);

        when(mockProviderDefinitionReader.read(same(topLeftColorDefinition), anyLong())).thenReturn(
                topLeftColor);
        when(mockProviderDefinitionReader.read(same(topRightColorDefinition),
                anyLong())).thenReturn(topRightColor);
        when(mockProviderDefinitionReader.read(same(bottomLeftColorDefinition),
                anyLong())).thenReturn(bottomLeftColor);
        when(mockProviderDefinitionReader.read(same(bottomRightColorDefinition),
                anyLong())).thenReturn(bottomRightColor);

        var definition = rectangle(mockAreaProviderDefinition, Z)
                .withColors(
                        topLeftColorDefinition,
                        topRightColorDefinition,
                        bottomLeftColorDefinition,
                        bottomRightColorDefinition)
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
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(topLeftColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(topRightColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(bottomLeftColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(bottomRightColorDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureIdProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureWidthProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockTextureHeightProviderDefinition),
                eq(TIMESTAMP));
        verify(mockRenderable, once()).setCapturesMouseEvents(true);
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(topLeftColor), same(topRightColor),
                same(bottomLeftColor), same(bottomRightColor),
                same(mockTextureIdProvider), same(mockTextureWidthProvider), same(
                        mockTextureHeightProvider),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = rectangle(mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        verify(mockRenderable, never()).setCapturesMouseEvents(anyBoolean());
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
                same(mockComponent));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(null, rectangle(mockAreaProviderDefinition, randomInt()),
                        TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent, rectangle(null, randomInt()), TIMESTAMP));
    }
}
