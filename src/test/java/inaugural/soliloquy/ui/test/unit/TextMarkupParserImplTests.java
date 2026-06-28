package inaugural.soliloquy.ui.test.unit;

import inaugural.soliloquy.tools.timing.TimestampValidator;
import inaugural.soliloquy.ui.TextMarkupParserImpl;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.assets.FontStyleInfo;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Mock.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

@ExtendWith(MockitoExtension.class)
public class TextMarkupParserImplTests {
    private final String PRESET_COLOR_NAME_1 = randomString();
    private final String PRESET_COLOR_NAME_2 = randomString();
    private final Color PRESET_COLOR = randomColor();
    private final Color DEFAULT_COLOR = randomColor();
    private final float VERY_HIGH_MAX_LENGTH = randomFloatWithInclusiveFloor(1000000000f);
    // In theory, any value should work, but excessively large values are both totally silly in
    // practice, and also risk breaking tests by hitting Float.MAX_VALUE
    private final float GLYPH_WIDTH = randomFloatWithInclusiveCeiling(10f);
    private final float PADDING_BETWEEN_GLYPHS = randomFloatWithInclusiveCeiling(10f);
    private final float LINE_HEIGHT = randomFloatWithInclusiveCeiling(10f);
    private final long TIMESTAMP = randomLong();

    private final UUID CONTAINING_COMPONENT_UUID = randomUUID();
    private final LookupAndEntitiesWithUuid<Component> MOCK_COMPONENT_AND_LOOKUP =
            generateMockLookupFunctionWithUuid(Component.class, CONTAINING_COMPONENT_UUID);
    private final Component MOCK_CONTAINING_COMPONENT =
            MOCK_COMPONENT_AND_LOOKUP.entities.getFirst();
    private final Function<UUID, Component> MOCK_GET_COMPONENT = MOCK_COMPONENT_AND_LOOKUP.lookup;

    private final String MOCK_CUSTOM_COLOR_PROVIDER_DATA_KEY = randomString();
    private final Color CUSTOM_PROVIDED_COLOR = randomColor();
    private final ProviderAtTime<Color> MOCK_CUSTOM_COLOR_PROVIDER =
            generateMockStaticProvider(CUSTOM_PROVIDED_COLOR);
    private final Map<String, Object> MOCK_CONTAINING_COMPONENT_DATA = generateMockMap(pairOf(
            MOCK_CUSTOM_COLOR_PROVIDER_DATA_KEY,
            MOCK_CUSTOM_COLOR_PROVIDER
    ));

    @Mock private Font mockFont;
    @Mock private FontStyleInfo mockPlain;
    @Mock private FontStyleInfo mockItalic;
    @Mock private FontStyleInfo mockBold;
    @Mock private FontStyleInfo mockBoldItalic;

    @Mock private TextLineRenderer mockTextLineRenderer;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @Mock private Function<Color, ProviderAtTime<Color>> mockStaticProviderFactory;
    @Mock private TimestampValidator mockTimestampValidator;

    private List<Pair<Color, ProviderAtTime<Color>>> mockColorProvidersGenerated;

    private TextMarkupParser parser;

    @BeforeEach
    public void setUp() {
        lenient().when(mockTextLineRenderer.getGlyphWidth(anyChar(), any(), anyFloat()))
                .thenReturn(GLYPH_WIDTH);

        lenient().when(mockFont.plain()).thenReturn(mockPlain);
        lenient().when(mockFont.italic()).thenReturn(mockItalic);
        lenient().when(mockFont.bold()).thenReturn(mockBold);
        lenient().when(mockFont.boldItalic()).thenReturn(mockBoldItalic);

        lenient().when(MOCK_CONTAINING_COMPONENT.data()).thenReturn(MOCK_CONTAINING_COMPONENT_DATA);

        lenient().when(mockStaticProviderFactory.apply(any())).thenAnswer(invocation -> {
            Color color = invocation.getArgument(0);
            var provider = generateMockStaticProvider(color);
            mockColorProvidersGenerated.add(pairOf(color, provider));
            return provider;
        });

        mockColorProvidersGenerated = listOf();

        parser = new TextMarkupParserImpl(DEFAULT_COLOR,
                mapOf(setOf(PRESET_COLOR_NAME_1, PRESET_COLOR_NAME_2), PRESET_COLOR),
                mockTextLineRenderer, mockProviderDefinitionReader, mockStaticProviderFactory,
                mockTimestampValidator);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(null, mapOf(), mockTextLineRenderer,
                        mockProviderDefinitionReader, mockStaticProviderFactory,
                        mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, null, mockTextLineRenderer,
                        mockProviderDefinitionReader, mockStaticProviderFactory,
                        mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(null, PRESET_COLOR),
                        mockTextLineRenderer, mockProviderDefinitionReader,
                        mockStaticProviderFactory, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class, () -> new TextMarkupParserImpl(DEFAULT_COLOR,
                mapOf(setOf(null, PRESET_COLOR_NAME_2), PRESET_COLOR), mockTextLineRenderer,
                mockProviderDefinitionReader, mockStaticProviderFactory, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class, () -> new TextMarkupParserImpl(DEFAULT_COLOR,
                mapOf(setOf(PRESET_COLOR_NAME_1, null), PRESET_COLOR), mockTextLineRenderer,
                mockProviderDefinitionReader, mockStaticProviderFactory, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class, () -> new TextMarkupParserImpl(DEFAULT_COLOR,
                mapOf(setOf(PRESET_COLOR_NAME_1, PRESET_COLOR_NAME_2), null), mockTextLineRenderer,
                mockProviderDefinitionReader, mockStaticProviderFactory, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(), null,
                        mockProviderDefinitionReader, mockStaticProviderFactory,
                        mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(), mockTextLineRenderer, null,
                        mockStaticProviderFactory, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(), mockTextLineRenderer,
                        mockProviderDefinitionReader, null, mockTimestampValidator));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(), mockTextLineRenderer,
                        mockProviderDefinitionReader, mockStaticProviderFactory, null));
    }

    @Test
    public void testFormatSingleLineNull() {
        var formatting = parser.formatSingleLine(null, null, TIMESTAMP);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals("", formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatSingleLineEmpty() {
        var formatting = parser.formatSingleLine(null, null, TIMESTAMP);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals("", formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatPlainString() {
        var rawText = randomString();

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(rawText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatItalicization() {
        var rawText = "plain *italic* plain";

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain italic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(6, 12), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatBoldface() {
        var rawText = "plain **bold** plain";

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain bold plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 10), formatting.boldIndices());
    }

    @Test
    public void testFormatBoldItalic() {
        var rawText = "plain ***bolditalic*** plain";

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain bolditalic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(6, 16), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 16), formatting.boldIndices());
    }

    @Test
    public void testFormatInterspersedBoldAndItalic() {
        var rawText = "plain **bold *both** italic* plain";

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain bold both italic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(11, 22), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 15), formatting.boldIndices());
    }

    @Test
    public void testColorFromPreset() {
        var rawText = String.format("plain [color=%s]color1[/color] [color=%s]color2[/color] plain",
                PRESET_COLOR_NAME_1, PRESET_COLOR_NAME_2);

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain color1 color2 plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        mockColorProvidersGenerated.get(0).SECOND,
                        6,
                        mockColorProvidersGenerated.get(1).SECOND,
                        12,
                        mockColorProvidersGenerated.get(0).SECOND,
                        13,
                        mockColorProvidersGenerated.get(2).SECOND,
                        19,
                        mockColorProvidersGenerated.get(0).SECOND
                ),
                formatting.colorIndices()
        );
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testAddColorPreset() {
        var customColorName = randomString();
        var customColor = randomColor();
        var rawText = String.format("plain [color=%s]color[/color] plain",
                customColorName.toUpperCase());

        parser.addColorPreset(customColorName, customColor);
        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        mockColorProvidersGenerated.get(0).SECOND,
                        6,
                        mockColorProvidersGenerated.get(1).SECOND,
                        11,
                        mockColorProvidersGenerated.get(0).SECOND
                ),
                formatting.colorIndices()
        );
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testAddColorPresetWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.addColorPreset(null, randomColor()));
        assertThrows(IllegalArgumentException.class,
                () -> parser.addColorPreset("", randomColor()));
        assertThrows(IllegalArgumentException.class,
                () -> parser.addColorPreset(randomString(), null));
    }

    @Test
    public void testColorFromRGB() {
        var rawText = String.format("plain [color=%d,%d,%d]color[/color] plain",
                PRESET_COLOR.getRed(), PRESET_COLOR.getGreen(), PRESET_COLOR.getBlue());

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        mockColorProvidersGenerated.get(0).SECOND,
                        6,
                        mockColorProvidersGenerated.get(1).SECOND,
                        11,
                        mockColorProvidersGenerated.get(0).SECOND
                ),
                formatting.colorIndices()
        );
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testColorFromRGBA() {
        var rawText = String.format("plain [color=%d,%d,%d,%d]color[/color] plain",
                PRESET_COLOR.getRed(), PRESET_COLOR.getGreen(), PRESET_COLOR.getBlue(),
                PRESET_COLOR.getAlpha());

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        mockColorProvidersGenerated.get(0).SECOND,
                        6,
                        mockColorProvidersGenerated.get(1).SECOND,
                        11,
                        mockColorProvidersGenerated.get(0).SECOND
                ),
                formatting.colorIndices()
        );
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testColorInvalidVal() {
        var invalidColor = "invalidColor";
        var rawText = String.format("plain [color=%s]color[/color] plain", invalidColor);

        assertThrows(IllegalArgumentException.class,
                () -> parser.formatSingleLine(rawText, null, TIMESTAMP),
                "TextMarkupParserImpl.formatSingleLine: invalid color val at index 6 (\"" +
                        invalidColor + "\")");
    }

    @Test
    public void testEscapeCharacter() {
        var rawText = "\\*text\\[!";

        var formatting = parser.formatSingleLine(rawText, null, TIMESTAMP);

        assertEquals("*text[!", formatting.text());
    }

    @Test
    public void testFormatMultilineNull() {
        var formatting = parser.formatMultiline(
                null,
                mockFont,
                randomFloat(),
                randomFloat(),
                randomFloat(),
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("", formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertNotNull(formatting[0].italicIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertNotNull(formatting[0].boldIndices());
        assertTrue(formatting[0].boldIndices().isEmpty());
    }

    @Test
    public void testFormatMultilineEmpty() {
        var formatting = parser.formatMultiline(
                "",
                mockFont,
                randomFloat(),
                randomFloat(),
                randomFloat(),
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("", formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertNotNull(formatting[0].italicIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertNotNull(formatting[0].boldIndices());
        assertTrue(formatting[0].boldIndices().isEmpty());
    }

    @Test
    public void testFormatMultilineFormatsSingleLine() {
        var rawText = randomString();

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals(rawText, formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertNotNull(formatting[0].italicIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertNotNull(formatting[0].boldIndices());
        assertTrue(formatting[0].boldIndices().isEmpty());
    }

    @Test
    public void testFormatMultilineMultipleLinesPlainText() {
        var rawText = "thereAreNoSpacesInThisString";
        var lineCharLength = rawText.length() - randomIntInRange(1, (rawText.length() / 2) - 1);
        // (The small multiplicand at the end is to avoid rounding errors)
        var maxLength = (lineCharLength * GLYPH_WIDTH) +
                ((lineCharLength - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT)) * (1.0001f);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                maxLength,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(2, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals(rawText.substring(0, lineCharLength), formatting[0].text());
        assertEquals(rawText.substring(lineCharLength), formatting[1].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertNotNull(formatting[0].italicIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertNotNull(formatting[0].boldIndices());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertNotNull(formatting[1].colorIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[1].colorIndices());
        assertNotNull(formatting[1].italicIndices());
        assertTrue(formatting[1].italicIndices().isEmpty());
        assertNotNull(formatting[1].boldIndices());
        assertTrue(formatting[1].boldIndices().isEmpty());
    }

    @Test
    public void testProperStylesUsedForGlyphLength() {
        var rawText = "p*i***b*bi";

        parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH,
                null,
                TIMESTAMP
        );

        var inOrder = Mockito.inOrder(mockTextLineRenderer);
        inOrder.verify(mockTextLineRenderer).getGlyphWidth('p', mockPlain, LINE_HEIGHT);
        inOrder.verify(mockTextLineRenderer).getGlyphWidth('i', mockItalic, LINE_HEIGHT);
        inOrder.verify(mockTextLineRenderer).getGlyphWidth('b', mockBold, LINE_HEIGHT);
        inOrder.verify(mockTextLineRenderer).getGlyphWidth('b', mockBoldItalic, LINE_HEIGHT);
        inOrder.verify(mockTextLineRenderer).getGlyphWidth('i', mockBoldItalic, LINE_HEIGHT);
    }

    @Test
    public void testLineBreaksAtSpaceWhenPossible() {
        when(mockTextLineRenderer.textLineLength(anyString(), any(), anyFloat(), anyList(),
                anyList(), anyFloat()))
                .thenAnswer(invocation -> {
                    String newLineText = invocation.getArgument(0);
                    return (newLineText.length() * GLYPH_WIDTH) +
                            ((newLineText.length() - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT));
                });
        var rawText = "wordNumber1 wordNumber2 wordNumber3 wordNumber4";
        // It places the line break in the middle of wordNumber2
        var lineCharLength = 20;
        // (The small multiplicand at the end is to avoid rounding errors)
        var maxLength = (lineCharLength * GLYPH_WIDTH) +
                ((lineCharLength - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT)) * (1.0001f);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                maxLength,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        for (var i = 0; i < 4; i++) {
            assertNotNull(formatting[i].colorIndices());
            assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                    formatting[1].colorIndices());
            assertNotNull(formatting[i].italicIndices());
            assertTrue(formatting[i].italicIndices().isEmpty());
            assertNotNull(formatting[i].boldIndices());
            assertTrue(formatting[i].boldIndices().isEmpty());
        }
    }

    // You can verify whether the results look good to you by grabbing the results in debug mode
    // and pasting the text input into any basic text editor. These tests are basically treating
    // the font as if it's monospace, so you should be able to see all the lines going as close
    // to 50 characters in length without going over.
    @Test
    public void testLargeParagraph() {
        var lorem =
                "A spectre is haunting Europe – the spectre of communism. All the powers of old " +
                        "Europe have entered into a holy alliance to exorcise this spectre: Pope " +
                        "and Tsar, Metternich and Guizot, French Radicals and German police-spies" +
                        ". Where is the party in opposition that has not been decried as " +
                        "communistic by its opponents in power? Where is the opposition that has " +
                        "not hurled back the branding reproach of communism, against the more " +
                        "advanced opposition parties, as well as against its reactionary " +
                        "adversaries?";

        when(mockTextLineRenderer.textLineLength(anyString(), any(), anyFloat(), anyList(),
                anyList(), anyFloat()))
                .thenAnswer(invocation -> {
                    String newLineText = invocation.getArgument(0);
                    return (newLineText.length() * GLYPH_WIDTH) +
                            ((newLineText.length() - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT));
                });
        // It places the line break in the middle of wordNumber2
        var lineCharLength = 50;
        // (The small multiplicand at the end is to avoid rounding errors)
        var maxLength = (lineCharLength * GLYPH_WIDTH) +
                ((lineCharLength - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT)) * (1.0001f);

        var formatting = parser.formatMultiline(
                lorem,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                maxLength,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(11, formatting.length);
        Arrays.stream(formatting).forEach(f -> assertTrue(f.text().length() <= lineCharLength));
    }

    @Test
    public void testFormattingCarriesOverToNewLine() {
        when(mockTextLineRenderer.textLineLength(anyString(), any(), anyFloat(), anyList(),
                anyList(), anyFloat()))
                .thenAnswer(invocation -> {
                    String newLineText = invocation.getArgument(0);
                    return (newLineText.length() * GLYPH_WIDTH) +
                            ((newLineText.length() - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT));
                });
        var rawText = String.format(
                "wordNumber1 wo***[color=%s]rdNumber2 wordNumber***[/color]3 wordNumber4",
                PRESET_COLOR_NAME_1);
        // It places the line break in the middle of wordNumber2
        var lineCharLength = 20;
        // (The small multiplicand at the end is to avoid rounding errors)
        var maxLength = (lineCharLength * GLYPH_WIDTH) +
                ((lineCharLength - 1) * (PADDING_BETWEEN_GLYPHS * LINE_HEIGHT)) * (1.0001f);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                maxLength,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(0).SECOND,
                2,
                mockColorProvidersGenerated.get(1).SECOND
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(1).SECOND,
                10,
                mockColorProvidersGenerated.get(0).SECOND
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[3].colorIndices());
        assertTrue(formatting[3].italicIndices().isEmpty());
        assertTrue(formatting[3].boldIndices().isEmpty());
    }

    @Test
    public void testCarriageReturn() {
        var rawText = "line1\nline2";

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH,
                null,
                TIMESTAMP
        );

        assertEquals(2, formatting.length);
        assertEquals("line1", formatting[0].text());
        assertEquals("line2", formatting[1].text());
    }

    @Test
    public void testFormattingPreservedAcrossLinesWhenUsingCarriageReturn() {
        var rawText = String.format(
                "wordNumber1\nwo***[color=%s]rdNumber2\nwordNumber***[/color]3\nwordNumber4",
                PRESET_COLOR_NAME_1);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(0).SECOND,
                2,
                mockColorProvidersGenerated.get(1).SECOND
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(1).SECOND,
                10,
                mockColorProvidersGenerated.get(0).SECOND
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[3].colorIndices());
        assertTrue(formatting[3].italicIndices().isEmpty());
        assertTrue(formatting[3].boldIndices().isEmpty());
    }

    @Test
    public void testFormattingPreservedAcrossLinesWhenUsingLineBreaksInTextBlock() {
        var rawText = String.format(
                """
                wordNumber1 extraWord
                wo***[color=%s]rdNumber2
                wordNumber***[/color]3
                wordNumber4""",
                PRESET_COLOR_NAME_1);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH,
                null,
                TIMESTAMP
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1 extraWord", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(0).SECOND,
                2,
                mockColorProvidersGenerated.get(1).SECOND
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                mockColorProvidersGenerated.get(1).SECOND,
                10,
                mockColorProvidersGenerated.get(0).SECOND
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(mapOf(0, mockColorProvidersGenerated.getFirst().SECOND),
                formatting[3].colorIndices());
        assertTrue(formatting[3].italicIndices().isEmpty());
        assertTrue(formatting[3].boldIndices().isEmpty());
    }
}
