package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.TextLineRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TextLineRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;

@ExtendWith(MockitoExtension.class)
public class TextLineRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final String FONT_ID = randomString();
    private final LookupAndEntitiesWithId<Font> MOCK_FONT_AND_LOOKUP =
            generateMockLookupFunctionWithId(Font.class, FONT_ID);
    private final Font MOCK_FONT = MOCK_FONT_AND_LOOKUP.entities.getFirst();
    private final Function<String, Font> MOCK_GET_FONT = MOCK_FONT_AND_LOOKUP.lookup;

    private final TextJustification JUSTIFICATION =
            TextJustification.fromValue(randomIntInRange(1, 3));
    private final float GLYPH_PADDING = randomFloat();
    private final int COLOR_INDEX = randomInt();
    private final int ITALIC_INDEX = randomInt();
    private final int BOLD_INDEX = randomInt();

    @Mock private AbstractProviderDefinition<String> mockTextDefinition;
    @Mock private AbstractProviderDefinition<Vertex> mockLocationDefinition;
    @Mock private AbstractProviderDefinition<Float> mockHeightDefinition;
    @Mock private AbstractProviderDefinition<Color> mockColorDefinition;
    @Mock private AbstractProviderDefinition<Float> mockDropShadowSizeDefinition;
    @Mock private AbstractProviderDefinition<Vertex> mockDropShadowOffsetDefinition;
    @Mock private AbstractProviderDefinition<Color> mockDropShadowColorDefinition;
    @Mock private ProviderAtTime<String> mockText;
    @Mock private ProviderAtTime<Vertex> mockLocation;
    @Mock private ProviderAtTime<Float> mockHeight;
    @Mock private ProviderAtTime<Color> mockColor;
    @Mock private ProviderAtTime<Float> mockDropShadowSize;
    @Mock private ProviderAtTime<Vertex> mockDropShadowOffset;
    @Mock private ProviderAtTime<Color> mockDropShadowColor;

    @Mock private TextLineRenderable mockRenderable;
    @Mock private TextLineRenderableFactory mockFactory;

    private TextLineRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockProviderDefinitionReader.read(mockTextDefinition)).thenReturn(mockText);
        lenient().when(mockProviderDefinitionReader.read(mockLocationDefinition))
                .thenReturn(mockLocation);
        lenient().when(mockProviderDefinitionReader.read(mockHeightDefinition))
                .thenReturn(mockHeight);

        lenient().when(mockFactory.make(
                any(),
                any(), any(), any(),
                any(),
                anyFloat(),
                any(), any(), any(),
                any(), any(),
                any(), any(), any(),
                anyInt(),
                any(),
                any()
        )).thenReturn(mockRenderable);

        reader = new TextLineRenderableDefinitionReader(mockFactory, MOCK_GET_FONT,
                mockProviderDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextLineRenderableDefinitionReader(null, MOCK_GET_FONT,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TextLineRenderableDefinitionReader(mockFactory, null,
                        mockProviderDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TextLineRenderableDefinitionReader(mockFactory, MOCK_GET_FONT, null,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new TextLineRenderableDefinitionReader(mockFactory, MOCK_GET_FONT,
                        mockProviderDefinitionReader, null));
    }

    @Test
    public void testRead() {
        when(mockProviderDefinitionReader.read(mockColorDefinition)).thenReturn(mockColor);
        when(mockProviderDefinitionReader.read(mockDropShadowSizeDefinition)).thenReturn(
                mockDropShadowSize);
        when(mockProviderDefinitionReader.read(mockDropShadowOffsetDefinition)).thenReturn(
                mockDropShadowOffset);
        when(mockProviderDefinitionReader.read(mockDropShadowColorDefinition)).thenReturn(
                mockDropShadowColor);

        @SuppressWarnings("unchecked") var definition =
                textLine(FONT_ID, mockTextDefinition, mockLocationDefinition, mockHeightDefinition,
                        JUSTIFICATION, GLYPH_PADDING, Z)
                        .withColors(pairOf(COLOR_INDEX, mockColorDefinition))
                        .withItalics(ITALIC_INDEX)
                        .withBold(BOLD_INDEX)
                        .withBorder(mockBorderThicknessDefinition, mockBorderColorDefinition)
                        .withDropShadow(
                                mockDropShadowSizeDefinition,
                                mockDropShadowOffsetDefinition,
                                mockDropShadowColorDefinition
                        );

        var renderable = reader.read(mockComponent, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockProviderDefinitionReader, once()).read(mockTextDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockLocationDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockHeightDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockBorderThicknessDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockBorderColorDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockDropShadowSizeDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockDropShadowOffsetDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockDropShadowColorDefinition);
        verify(mockFactory, once()).make(
                same(MOCK_FONT),
                same(mockText),
                same(mockLocation),
                same(mockHeight),
                eq(JUSTIFICATION),
                eq(GLYPH_PADDING),
                eq(mapOf(pairOf(COLOR_INDEX, mockColor))),
                eq(listOf(ITALIC_INDEX)),
                eq(listOf(BOLD_INDEX)),
                same(mockBorderThickness), same(mockBorderColor),
                same(mockDropShadowSize),
                same(mockDropShadowOffset),
                same(mockDropShadowColor),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition =
                textLine(FONT_ID, mockTextDefinition, mockLocationDefinition, mockHeightDefinition,
                        JUSTIFICATION, GLYPH_PADDING, Z);

        var renderable = reader.read(mockComponent, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockProviderDefinitionReader, once()).read(mockTextDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockLocationDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockHeightDefinition);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_FONT),
                same(mockText),
                same(mockLocation),
                same(mockHeight),
                eq(JUSTIFICATION),
                eq(GLYPH_PADDING),
                eq(mapOf()),
                eq(listOf()),
                eq(listOf()),
                same(mockNullProvider), same(mockNullProvider),
                same(mockNullProvider),
                same(mockNullProvider),
                same(mockNullProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
    }
}
