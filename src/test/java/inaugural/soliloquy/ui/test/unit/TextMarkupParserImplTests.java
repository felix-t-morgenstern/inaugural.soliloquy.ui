package inaugural.soliloquy.ui.test.unit;

import inaugural.soliloquy.ui.TextMarkupParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.assets.FontStyleInfo;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextMarkupParserImplTests {
    private final String PRESET_COLOR_NAME = randomString();
    private final Color PRESET_COLOR = randomColor();
    private final Color DEFAULT_COLOR = randomColor();
    private final Map<Integer, Color> DEFAULT_COLOR_INDICES = mapOf(0, DEFAULT_COLOR);
    private final float VERY_HIGH_MAX_LENGTH = randomFloatWithInclusiveFloor(1000000000f);
    // In theory, any value should work, but excessively large values are both totally silly in
    // practice, and also risk breaking tests by hitting Float.MAX_VALUE
    private final float GLYPH_WIDTH = randomFloatWithInclusiveCeiling(10f);
    private final float PADDING_BETWEEN_GLYPHS = randomFloatWithInclusiveCeiling(10f);
    private final float LINE_HEIGHT = randomFloatWithInclusiveCeiling(10f);

    @Mock private Font mockFont;
    @Mock private FontStyleInfo mockPlain;
    @Mock private FontStyleInfo mockItalic;
    @Mock private FontStyleInfo mockBold;
    @Mock private FontStyleInfo mockBoldItalic;

    @Mock private TextLineRenderer mockTextLineRenderer;

    private TextMarkupParser parser;

    @BeforeEach
    public void setUp() {
        lenient().when(mockTextLineRenderer.getGlyphWidth(anyChar(), any(), anyFloat()))
                .thenReturn(GLYPH_WIDTH);

        lenient().when(mockFont.plain()).thenReturn(mockPlain);
        lenient().when(mockFont.italic()).thenReturn(mockItalic);
        lenient().when(mockFont.bold()).thenReturn(mockBold);
        lenient().when(mockFont.boldItalic()).thenReturn(mockBoldItalic);

        parser = new TextMarkupParserImpl(DEFAULT_COLOR, mapOf(PRESET_COLOR_NAME, PRESET_COLOR),
                mockTextLineRenderer);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(null, mapOf(PRESET_COLOR_NAME, PRESET_COLOR),
                        mockTextLineRenderer));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR, null, mockTextLineRenderer));
        assertThrows(IllegalArgumentException.class,
                () -> new TextMarkupParserImpl(DEFAULT_COLOR,
                        mapOf(PRESET_COLOR_NAME, PRESET_COLOR), null));
    }

    @Test
    public void testFormatSingleLineNull() {
        var formatting = parser.formatSingleLine(null);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals("", formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatSingleLineEmpty() {
        var formatting = parser.formatSingleLine(null);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals("", formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatPlainString() {
        var rawText = randomString();

        var formatting = parser.formatSingleLine(rawText);

        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(rawText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatItalicization() {
        var rawText = "plain *italic* plain";

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain italic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(6, 12), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertTrue(formatting.boldIndices().isEmpty());
    }

    @Test
    public void testFormatBoldface() {
        var rawText = "plain **bold** plain";

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain bold plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertTrue(formatting.italicIndices().isEmpty());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 10), formatting.boldIndices());
    }

    @Test
    public void testFormatBoldItalic() {
        var rawText = "plain ***bolditalic*** plain";

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain bolditalic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(6, 16), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 16), formatting.boldIndices());
    }

    @Test
    public void testFormatInterspersedBoldAndItalic() {
        var rawText = "plain **bold *both** italic* plain";

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain bold both italic plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting.colorIndices());
        assertNotNull(formatting.italicIndices());
        assertEquals(listOf(11, 22), formatting.italicIndices());
        assertNotNull(formatting.boldIndices());
        assertEquals(listOf(6, 15), formatting.boldIndices());
    }

    @Test
    public void testColorFromPreset() {
        var rawText = String.format("plain [color=%s]color[/color] plain", PRESET_COLOR_NAME);

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        DEFAULT_COLOR,
                        6,
                        PRESET_COLOR,
                        11,
                        DEFAULT_COLOR
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
        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        DEFAULT_COLOR,
                        6,
                        customColor,
                        11,
                        DEFAULT_COLOR
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

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        DEFAULT_COLOR,
                        6,
                        new Color(PRESET_COLOR.getRed(), PRESET_COLOR.getGreen(),
                                PRESET_COLOR.getBlue()),
                        11,
                        DEFAULT_COLOR
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

        var formatting = parser.formatSingleLine(rawText);

        var expectedText = "plain color plain";
        assertNotNull(formatting);
        assertNotNull(formatting.text());
        assertEquals(expectedText, formatting.text());
        assertNotNull(formatting.colorIndices());
        assertEquals(
                mapOf(
                        0,
                        DEFAULT_COLOR,
                        6,
                        PRESET_COLOR,
                        11,
                        DEFAULT_COLOR
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

        assertThrows(IllegalArgumentException.class, () -> parser.formatSingleLine(rawText),
                "TextMarkupParserImpl.formatSingleLine: invalid color val at index 6 (\"" +
                        invalidColor + "\")");
    }

    @Test
    public void testEscapeCharacter() {
        var rawText = "\\*text\\[!";

        var formatting = parser.formatSingleLine(rawText);

        assertEquals("*text[!", formatting.text());
    }

    @Test
    public void testFormatMultilineNull() {
        var formatting = parser.formatMultiline(
                null,
                mockFont,
                randomFloat(),
                randomFloat(),
                randomFloat()
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("", formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
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
                randomFloat()
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("", formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
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
                VERY_HIGH_MAX_LENGTH
        );

        assertNotNull(formatting);
        assertEquals(1, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals(rawText, formatting[0].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
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
        System.out.println("maxLength = " + maxLength);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                maxLength
        );

        assertNotNull(formatting);
        assertEquals(2, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals(rawText.substring(0, lineCharLength), formatting[0].text());
        assertEquals(rawText.substring(lineCharLength), formatting[1].text());
        assertNotNull(formatting[0].colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
        assertNotNull(formatting[0].italicIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertNotNull(formatting[0].boldIndices());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertNotNull(formatting[1].colorIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[1].colorIndices());
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
                VERY_HIGH_MAX_LENGTH
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
                maxLength
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
            assertEquals(DEFAULT_COLOR_INDICES, formatting[i].colorIndices());
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
                maxLength
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
                PRESET_COLOR_NAME);
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
                maxLength
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                DEFAULT_COLOR,
                2,
                PRESET_COLOR
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                PRESET_COLOR,
                10,
                DEFAULT_COLOR
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[3].colorIndices());
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
                VERY_HIGH_MAX_LENGTH
        );

        assertEquals(2, formatting.length);
        assertEquals("line1", formatting[0].text());
        assertEquals("line2", formatting[1].text());
    }

    @Test
    public void testFormattingPreservedAcrossLinesWhenUsingCarriageReturn() {
        var rawText = String.format(
                "wordNumber1\nwo***[color=%s]rdNumber2\nwordNumber***[/color]3\nwordNumber4",
                PRESET_COLOR_NAME);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                DEFAULT_COLOR,
                2,
                PRESET_COLOR
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                PRESET_COLOR,
                10,
                DEFAULT_COLOR
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[3].colorIndices());
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
                PRESET_COLOR_NAME);

        var formatting = parser.formatMultiline(
                rawText,
                mockFont,
                PADDING_BETWEEN_GLYPHS,
                LINE_HEIGHT,
                VERY_HIGH_MAX_LENGTH
        );

        assertNotNull(formatting);
        assertEquals(4, formatting.length);
        assertNotNull(formatting[0].text());
        assertEquals("wordNumber1 extraWord", formatting[0].text());
        assertEquals("wordNumber2", formatting[1].text());
        assertEquals("wordNumber3", formatting[2].text());
        assertEquals("wordNumber4", formatting[3].text());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[0].colorIndices());
        assertTrue(formatting[0].italicIndices().isEmpty());
        assertTrue(formatting[0].boldIndices().isEmpty());
        assertEquals(mapOf(
                0,
                DEFAULT_COLOR,
                2,
                PRESET_COLOR
        ), formatting[1].colorIndices());
        assertEquals(listOf(2), formatting[1].italicIndices());
        assertEquals(listOf(2), formatting[1].boldIndices());
        assertEquals(mapOf(
                0,
                PRESET_COLOR,
                10,
                DEFAULT_COLOR
        ), formatting[2].colorIndices());
        assertEquals(listOf(0, 10), formatting[2].italicIndices());
        assertEquals(listOf(0, 10), formatting[2].boldIndices());
        assertEquals(DEFAULT_COLOR_INDICES, formatting[3].colorIndices());
        assertTrue(formatting[3].italicIndices().isEmpty());
        assertTrue(formatting[3].boldIndices().isEmpty());
    }
}
