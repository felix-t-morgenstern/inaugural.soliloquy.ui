package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.renderables.RectangleRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

@ExtendWith(MockitoExtension.class)
public class RectangleRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final long TIMESTAMP = randomLong();

    @SuppressWarnings("unchecked")
    @Mock ProviderAtTime<Color> mockTopLeftColor = mock(ProviderAtTime.class);
    @SuppressWarnings("unchecked")
    @Mock ProviderAtTime<Color> mockTopRightColor = mock(ProviderAtTime.class);
    @SuppressWarnings("unchecked")
    @Mock ProviderAtTime<Color> mockBottomLeftColor = mock(ProviderAtTime.class);
    @SuppressWarnings("unchecked")
    @Mock ProviderAtTime<Color> mockBottomRightColor = mock(ProviderAtTime.class);

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
    public void testReadWithFullArgsAndProviderDefs() {
        @SuppressWarnings("unchecked") var topLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var topRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomLeftColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);
        @SuppressWarnings("unchecked") var bottomRightColorDefinition =
                (AbstractProviderDefinition<Color>) mock(AbstractProviderDefinition.class);

        when(mockProviderDefinitionReader.read(same(topLeftColorDefinition), anyLong())).thenReturn(
                mockTopLeftColor);
        when(mockProviderDefinitionReader.read(same(topRightColorDefinition),
                anyLong())).thenReturn(mockTopRightColor);
        when(mockProviderDefinitionReader.read(same(bottomLeftColorDefinition),
                anyLong())).thenReturn(mockBottomLeftColor);
        when(mockProviderDefinitionReader.read(same(bottomRightColorDefinition),
                anyLong())).thenReturn(mockBottomRightColor);

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
                same(mockTopLeftColor), same(mockTopRightColor),
                same(mockBottomLeftColor), same(mockBottomRightColor),
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
    public void testReadFromDimensProvider() {
        var definition = rectangle(mockAreaProvider, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockProviderDefinitionReader, never()).read(same(mockAreaProviderDefinition), anyLong());
        verify(mockRenderable, never()).setCapturesMouseEvents(anyBoolean());
        verify(mockFactory, once()).make(
                any(), any(),
                any(), any(),
                any(), any(), any(),
                any(), any(),
                isNull(), isNull(),
                same(mockAreaProvider),
                anyInt(),
                isNotNull(),
                any());
    }

    @Test
    public void testReadWithTexIdProvider() {
        var definition = rectangle(mockAreaProviderDefinition, Z)
                .withTexture(mockTextureIdProvider, randomFloat(), randomFloat());

        reader.read(mockComponent, definition, TIMESTAMP);

        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockNullProvider), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider),
                same(mockTextureIdProvider), any(), any(),
                eq(mapOf()), eq(mapOf()),
                isNull(), isNull(),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadWithTexDimensProviders() {
        @SuppressWarnings("unchecked") ProviderAtTime<Float> mockTexWidthProvider = mock(ProviderAtTime.class);
        @SuppressWarnings("unchecked") ProviderAtTime<Float> mockTexHeightProvider = mock(ProviderAtTime.class);

        var definition = rectangle(mockAreaProviderDefinition, Z)
                .withTexture(
                        mockTextureIdProvider,
                        mockTexWidthProvider,
                        mockTexHeightProvider
                )
                .withTexture(
                        mockTextureIdProvider,
                        randomFloat(),
                        randomFloat()
                );

        reader.read(mockComponent, definition, TIMESTAMP);

        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(mockNullProvider), same(mockNullProvider),
                same(mockNullProvider), same(mockNullProvider),
                same(mockTextureIdProvider),
                same(mockTexWidthProvider),
                same(mockTexHeightProvider),
                eq(mapOf()), eq(mapOf()),
                isNull(), isNull(),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent));
    }

    @Test
    public void testReadWithColorProviders() {
        var definition = rectangle(mockAreaProviderDefinition, Z)
                .withColors(
                        mockTopLeftColor,
                        mockTopRightColor,
                        mockBottomLeftColor,
                        mockBottomRightColor
                );

        reader.read(mockComponent, definition, TIMESTAMP);

        verify(mockFactory, once()).make(
                same(mockTopLeftColor), same(mockTopRightColor),
                same(mockBottomLeftColor), same(mockBottomRightColor),
                any(), any(), any(),
                anyMap(), anyMap(),
                isNull(), isNull(),
                any(),
                anyInt(),
                isNotNull(),
                any());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(null, rectangle(mockAreaProviderDefinition, randomInt()),
                        TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent,
                        rectangle((AbstractProviderDefinition<FloatBox>) null, randomInt()),
                        TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockComponent,
                        rectangle((ProviderAtTime<FloatBox>) null, randomInt()), TIMESTAMP));
    }
}
