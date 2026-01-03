package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.unit.components.ComponentDefinitionTest;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.ui.components.ComponentMethods.LAST_TIMESTAMP;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.HEIGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextBlockDefinitionReaderTests extends ComponentDefinitionTest {
    private final float LINE_HEIGHT = randomFloat();
    private final float MAX_LINE_LENGTH = randomFloat();
    private final float GLYPH_PADDING = randomFloat();
    private final float LINE_SPACING = randomFloat();
    private final float PARAGRAPH_SPACING = randomFloat();
    private final HorizontalAlignment ALIGNMENT =
            HorizontalAlignment.fromValue(randomIntInRange(1, 3));
    private final String PARAGRAPH_1 = randomString();
    private final String PARAGRAPH_2 = randomString();
    private final String LINE_TEXT_1 = randomString();
    private final String LINE_TEXT_2 = randomString();
    private final String LINE_TEXT_3 = randomString();
    private final String LINE_TEXT_4 = randomString();
    private final int Z = randomInt();
    private final long TIMESTAMP = randomLong();

    private final static String HEIGHT_DATA_KEY = "HEIGHT";
    private final static String LAST_TIMESTAMP_DATA_KEY = "LAST_TIMESTAMP";

    private final static String TEXT_RENDERING_LOC_METHOD = "TextBlock_provideTextRenderingLoc";

    @Mock private TextMarkupParser mockParser;
    @Mock private TextMarkupParser.LineFormatting mockLine1;
    @Mock private TextMarkupParser.LineFormatting mockLine2;
    @Mock private TextMarkupParser.LineFormatting mockLine3;
    @Mock private TextMarkupParser.LineFormatting mockLine4;

    @Mock private AbstractProviderDefinition<Vertex> mockUpperLeftDefinition;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;

    private TextBlockDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(
                        mockParser.formatMultiline(eq(PARAGRAPH_1), any(), anyFloat(), anyFloat(),
                                anyFloat()))
                .thenReturn(arrayOf(mockLine1, mockLine2));
        lenient().when(
                        mockParser.formatMultiline(eq(PARAGRAPH_2), any(), anyFloat(), anyFloat(),
                                anyFloat()))
                .thenReturn(arrayOf(mockLine3, mockLine4));
        lenient().when(mockLine1.text()).thenReturn(LINE_TEXT_1);
        lenient().when(mockLine2.text()).thenReturn(LINE_TEXT_2);
        lenient().when(mockLine3.text()).thenReturn(LINE_TEXT_3);
        lenient().when(mockLine4.text()).thenReturn(LINE_TEXT_4);

        reader = new TextBlockDefinitionReader(mockParser, MOCK_GET_FONT,
                mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new TextBlockDefinitionReader(null, MOCK_GET_FONT,
                        mockProviderDefinitionReader));
        assertThrows(IllegalArgumentException.class,
                () -> new TextBlockDefinitionReader(mockParser, null,
                        mockProviderDefinitionReader));
        assertThrows(IllegalArgumentException.class,
                () -> new TextBlockDefinitionReader(mockParser, MOCK_GET_FONT, null));
    }

    @Test
    public void testReadFromDefs() {
        @SuppressWarnings("rawtypes") var providersRead = Collections.<ProviderAtTime>listOf();
        when(mockProviderDefinitionReader.read(any(), anyLong())).thenAnswer(_ -> {
            var provider = mock(ProviderAtTime.class);
            providersRead.add(provider);
            return provider;
        });

        var definition = textBlock(
                FONT_ID,
                LINE_HEIGHT,
                MAX_LINE_LENGTH,
                mockUpperLeftDefinition,
                GLYPH_PADDING,
                LINE_SPACING,
                PARAGRAPH_SPACING,
                ALIGNMENT,
                listOf(PARAGRAPH_1, PARAGRAPH_2),
                Z
        );
        float expectedHeight =
                LINE_HEIGHT + LINE_SPACING + LINE_HEIGHT + PARAGRAPH_SPACING + LINE_HEIGHT +
                        LINE_SPACING + LINE_HEIGHT;

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(3, output.data.size());
        assertEquals(mapOf(
                WIDTH,
                definition.MAX_LINE_LENGTH,
                HEIGHT,
                expectedHeight,
                LAST_TIMESTAMP,
                TIMESTAMP - 1
        ), output.data);
        assertEquals(expectedHeight, output.data.get(HEIGHT_DATA_KEY));
        assertEquals(TIMESTAMP - 1, output.data.get(LAST_TIMESTAMP_DATA_KEY));
        assertEquals(4, output.CONTENT.size());
        assertEquals(5, providersRead.size());
        var lines = listOf(LINE_TEXT_1, LINE_TEXT_2, LINE_TEXT_3, LINE_TEXT_4);
        for (var i = 1; i < providersRead.size(); i++) {
            final int index = i;
            var content = output.CONTENT.stream()
                    .filter(c -> c instanceof TextLineRenderableDefinition d &&
                            d.LOCATION_PROVIDER == providersRead.get(index)).findFirst();
            assertTrue(content.isPresent());
            var textLine = (TextLineRenderableDefinition) content.get();
            assertSame(MOCK_FONT, textLine.FONT);
            assertEquals(lines.get(i - 1), extractStaticVal(textLine.TEXT_PROVIDER));
            assertEquals(LINE_HEIGHT, extractStaticVal(textLine.HEIGHT_PROVIDER));
            assertEquals(ALIGNMENT, textLine.ALIGNMENT);
            assertEquals(GLYPH_PADDING, textLine.GLYPH_PADDING);
            assertEquals(0, textLine.Z);
        }

        verify(mockProviderDefinitionReader, times(5)).read(any(), anyLong());
        verify(mockParser, times(2))
                .formatMultiline(anyString(), any(), anyFloat(), anyFloat(),
                        anyFloat());

        var inOrder = inOrder(MOCK_GET_FONT, mockProviderDefinitionReader, mockParser,
                mockProviderDefinitionReader);
        inOrder.verify(mockProviderDefinitionReader, once())
                .read(mockUpperLeftDefinition, TIMESTAMP);
        inOrder.verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        inOrder.verify(mockParser, once())
                .formatMultiline(eq(PARAGRAPH_1), same(MOCK_FONT), eq(GLYPH_PADDING),
                        eq(LINE_HEIGHT), eq(MAX_LINE_LENGTH));
        inOrder.verify(mockParser, once())
                .formatMultiline(eq(PARAGRAPH_2), same(MOCK_FONT), eq(GLYPH_PADDING),
                        eq(LINE_HEIGHT), eq(MAX_LINE_LENGTH));
        var upperLeftProvider = providersRead.getFirst();
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                        TEXT_RENDERING_LOC_METHOD,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                output.UUID,
                                TextBlock_blockUpperLeftProvider,
                                upperLeftProvider,
                                TextBlock_topOffset, 0f))), eq(TIMESTAMP));
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                        TEXT_RENDERING_LOC_METHOD,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                output.UUID,
                                TextBlock_blockUpperLeftProvider,
                                upperLeftProvider,
                                TextBlock_topOffset, LINE_HEIGHT + LINE_SPACING))), eq(TIMESTAMP));
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                        TEXT_RENDERING_LOC_METHOD,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                output.UUID,
                                TextBlock_blockUpperLeftProvider,
                                upperLeftProvider,
                                TextBlock_topOffset,
                                LINE_HEIGHT + LINE_SPACING + LINE_HEIGHT + PARAGRAPH_SPACING))),
                eq(TIMESTAMP));
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                        TEXT_RENDERING_LOC_METHOD,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                output.UUID,
                                TextBlock_blockUpperLeftProvider,
                                upperLeftProvider,
                                TextBlock_topOffset,
                                LINE_HEIGHT + LINE_SPACING + LINE_HEIGHT + PARAGRAPH_SPACING +
                                        LINE_HEIGHT + LINE_SPACING))), eq(TIMESTAMP));

    }
}
