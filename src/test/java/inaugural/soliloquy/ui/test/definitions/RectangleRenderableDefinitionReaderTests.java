package inaugural.soliloquy.ui.test.definitions;

import inaugural.soliloquy.ui.definitions.RectangleRenderableDefinitionReader;
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
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

@ExtendWith(MockitoExtension.class)
public class RectangleRenderableDefinitionReaderTests {
    @Mock private RectangleRenderable mockRenderable;
    @Mock private RectangleRenderableFactory mockFactory;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;

    private RectangleRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new RectangleRenderableDefinitionReader(mockFactory, mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(mockFactory, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RectangleRenderableDefinitionReader(null, mockProviderDefinitionReader));
    }

    @Test
    public void testRead() {
        @SuppressWarnings("unchecked") var mockAreaProviderDefinition =
                (AbstractProviderDefinition<FloatBox>) mock(AbstractProviderDefinition.class);
        var z = randomInt();
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
        var onPress = mapOf(pairOf(randomInt(), randomString()));
        var onRelease = mapOf(pairOf(randomInt(), randomString()));
        var onMouseOver = randomString();
        var onMouseLeave = randomString();

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

        when(mockFactory.make(any(), any(), any(), any(), any(), anyFloat(), anyFloat(), any(),
                any(), any(), any(), any(), anyInt(), any(), any())).thenReturn(mockRenderable);

        var definition = rectangle(mockAreaProviderDefinition, z)
                .withColors(
                        bottomRightColorDefinition,
                        bottomRightColorDefinition,
                        bottomRightColorDefinition,
                        bottomRightColorDefinition)
                .withTexture(
                        textureIdProviderDefinition,
                        textureWidthProviderDefinition,
                        textureHeightProviderDefinition)
                .onPress(onPress)
                .onRelease(onRelease)
                .onMouseOver(onMouseOver)
                .onMouseLeave(onMouseLeave);

        var renderable = reader.read(definition);

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
        verify(mockFactory, once()).make(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor, textureIdProvider, )
    }
}
