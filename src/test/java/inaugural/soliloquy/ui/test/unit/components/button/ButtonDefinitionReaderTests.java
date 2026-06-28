package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinition;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinitionReader;
import inaugural.soliloquy.ui.components.textblock.TextBlockMethods;
import inaugural.soliloquy.ui.test.unit.components.ComponentDefinitionReaderTest;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.ImageAssetRenderable;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.AbstractImageAssetRenderableDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.assertMapContainsSubsetEquals;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;

@ExtendWith(MockitoExtension.class)
public class ButtonDefinitionReaderTests extends ComponentDefinitionReaderTest {
    private final UUID BUTTON_UUID = randomUUID();

    private final String TEST_PRESS_SOUND_ID = randomString();
    private final String TEST_MOUSE_OVER_SOUND_ID = randomString();
    private final String TEST_MOUSE_LEAVE_SOUND_ID = randomString();
    private final String TEST_RELEASE_SOUND_ID = randomString();

    private final String PARAGRAPH = randomString();
    private final float LINE_HEIGHT = randomFloat();
    private final float MAX_LINE_LENGTH = randomFloat();
    private final float GLYPH_PADDING = randomFloat();
    private final String FORMATTED_TEXT = randomString();
    private final int ITALIC_INDEX = randomInt();
    private final int BOLD_INDEX = randomInt();
    private final float TEXT_BLOCK_X_PADDING = randomFloat();
    private final float FIRST_PARAGRAPH_LENGTH = randomFloat();
    private final float TEXT_LINE_LENGTH_ROUNDING_ERROR = 1.0001f;
    private final float TEXT_BLOCK_HEIGHT = randomFloat();

    private final HorizontalAlignment TEXT_BLOCK_HORIZ_ALIGN_WITHIN_RECT =
            HorizontalAlignment.fromValue(randomIntInRange(1, 3));

    private final String ON_PRESS_ID = randomString();
    @SuppressWarnings("rawtypes") private final LookupAndEntitiesWithId<Consumer>
            MOCK_CONSUMER_AND_LOOKUP =
            generateMockLookupFunctionWithId(Consumer.class, ON_PRESS_ID);
    @SuppressWarnings("rawtypes") private final Consumer MOCK_ON_PRESS =
            MOCK_CONSUMER_AND_LOOKUP.entities.getFirst();
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> MOCK_GET_CONSUMER =
            MOCK_CONSUMER_AND_LOOKUP.lookup;

    private final String FONT_ID = randomString();
    private final LookupAndEntitiesWithId<Font> MOCK_FONT_AND_LOOKUP =
            generateMockLookupFunctionWithId(Font.class, FONT_ID);
    private final Font MOCK_FONT = MOCK_FONT_AND_LOOKUP.entities.getFirst();
    private final Function<String, Font> MOCK_GET_FONT = MOCK_FONT_AND_LOOKUP.lookup;

    private final float WIDTH_TO_HEIGHT_RATIO = randomFloat();

    @Mock private ProviderAtTime<FloatBox> mockAdjRectDimensProvider;

    @Mock private ProviderAtTime<FloatBox> mockRectUnadjDimensProvider;
    @Mock private AbstractProviderDefinition<FloatBox> mockRectDefaultUnadjDimensProviderDef;

    @Mock private TextBlockDefinitionReader mockTextBlockDefinitionReader;
    @Mock private TextMarkupParser mockMarkupParser;
    @Mock private TextMarkupParser.LineFormatting mockLineFormatting;
    @Mock private TextLineRenderer mockTextLineRenderer;
    @Mock private Supplier<Float> mockGetWidthToHeightRatio;

    @Mock private RectangleRenderable mockPlainRect;
    @Mock private RectangleRenderable mockRectDefault;
    @Mock private RectangleRenderable mockRectHover;
    @Mock private RectangleRenderable mockRectPressed;

    @Mock private ImageAssetRenderable mockImageAssetDefault;
    @Mock private ImageAssetRenderable mockImageAssetHover;
    @Mock private ImageAssetRenderable mockImageAssetPressed;

    @Mock private ProviderAtTime<Vertex> mockTextBlockUnadjRenderingLoc;
    @Mock private ProviderAtTime<Vertex> mockTextBlockAdjRenderingLoc;
    @Mock private AbstractProviderDefinition<Vertex> mockTextBlockUnadjCenterProviderDef;
    @Mock private ProviderAtTime<Vertex> mockTextBlockUnadjCenterProvider;
    @Mock private ProviderAtTime<Vertex> mockCenteredTextBlockUpperLeftProvider;

    @Mock private ProviderAtTime<FloatBox> mockImageAssetAdjDimensProvider;
    @Mock private AbstractProviderDefinition<FloatBox> mockImageAssetDefaultUnadjDimensProviderDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockImageAssetHoverUnadjDimensProviderDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockImageAssetPressedUnadjDimensProviderDef;
    @Mock private ProviderAtTime<FloatBox> mockImageAssetDefaultUnadjDimensProvider;
    @Mock private ProviderAtTime<FloatBox> mockImageAssetHoverUnadjDimensProvider;
    @Mock private ProviderAtTime<FloatBox> mockImageAssetPressedUnadjDimensProvider;

    private RectangleRenderableDefinition rectDefDefault;
    private RectangleRenderableDefinition rectDefHover;
    private RectangleRenderableDefinition rectDefPressed;
    private TextBlockDefinition textBlockDef;
    private ComponentDefinition readTextBlockDef;
    private AbstractImageAssetRenderableDefinition imageAssetDefDefault;
    private AbstractImageAssetRenderableDefinition imageAssetDefHover;
    private AbstractImageAssetRenderableDefinition imageAssetDefPressed;

    private ButtonDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockGetWidthToHeightRatio.get()).thenReturn(WIDTH_TO_HEIGHT_RATIO);

        lenient().when(mockProviderDefReader.read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                                Button_rectDimensWithAdj,
                                mapOf(
                                        COMPONENT_UUID,
                                        BUTTON_UUID
                                )
                        )),
                anyLong()
        )).thenReturn(mockAdjRectDimensProvider);

        lenient().when(
                        mockProviderDefReader.read(same(mockRectDefaultUnadjDimensProviderDef),
                                anyLong()))
                .thenReturn(mockRectUnadjDimensProvider);

        rectDefDefault = rectangle();
        rectDefHover = rectangle();
        rectDefPressed = rectangle();
        lenient().when(mockRenderableDefReader.read(any(), any(), anyLong())).thenReturn(mockPlainRect);
        lenient().when(mockRenderableDefReader.read(any(), same(rectDefDefault), anyLong()))
                .thenReturn(mockRectDefault);
        lenient().when(mockRenderableDefReader.read(any(), same(rectDefHover), anyLong()))
                .thenReturn(mockRectHover);
        lenient().when(mockRenderableDefReader.read(any(), same(rectDefPressed), anyLong()))
                .thenReturn(mockRectPressed);

        lenient().when(mockProviderDefReader.read(argThat(
                                new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                        Button_provideUnadjRectDimensFromTextBlock,
                                        mapOf(
                                                COMPONENT_UUID,
                                                BUTTON_UUID,
                                                Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft,
                                                mockTextBlockUnadjRenderingLoc,
                                                Button_provideUnadjRectDimensFromTextBlock_textBlockDimens,
                                                vertexOf(MAX_LINE_LENGTH, TEXT_BLOCK_HEIGHT),
                                                Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz,
                                                TEXT_BLOCK_X_PADDING,
                                                Button_provideUnadjRectDimensFromTextBlock_textPaddingVert,
                                                TEXT_BLOCK_X_PADDING / WIDTH_TO_HEIGHT_RATIO
                                        )
                                )),
                        anyLong()))
                .thenReturn(mockRectUnadjDimensProvider);

        lenient().when(mockLineFormatting.text()).thenReturn(FORMATTED_TEXT);
        lenient().when(mockLineFormatting.italicIndices()).thenReturn(listOf(ITALIC_INDEX));
        lenient().when(mockLineFormatting.boldIndices()).thenReturn(listOf(BOLD_INDEX));

        lenient().when(mockMarkupParser.formatSingleLine(any(), any(), anyLong()))
                .thenReturn(mockLineFormatting);

        textBlockDef = textBlock(FONT_ID, LINE_HEIGHT, MAX_LINE_LENGTH, PARAGRAPH)
                .withGlyphPadding(GLYPH_PADDING);

        lenient().when(mockTextBlockDefinitionReader.read(any(), anyLong()))
                .thenReturn(readTextBlockDef);

        lenient().when(mockProviderDefReader.read(argThat(
                                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                                        Button_provideUnadjTextBlockLocFromRect,
                                        mapOf(
                                                COMPONENT_UUID,
                                                BUTTON_UUID,
                                                Button_provideUnadjTextBlockLocFromRect_horizontalAlignment,
                                                TEXT_BLOCK_HORIZ_ALIGN_WITHIN_RECT,
                                                Button_provideUnadjTextBlockLocFromRect_paddingHoriz,
                                                TEXT_BLOCK_X_PADDING,
                                                Button_provideUnadjTextBlockLocFromRect_textBlockHeight,
                                                TEXT_BLOCK_HEIGHT,
                                                Button_provideUnadjTextBlockLocFromRect_lineLength,
                                                FIRST_PARAGRAPH_LENGTH * TEXT_LINE_LENGTH_ROUNDING_ERROR
                                        )
                                )),
                        anyLong()))
                .thenReturn(mockTextBlockUnadjRenderingLoc);

        lenient().when(mockProviderDefReader.read(argThat(
                                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                                        Button_provideCenteredUnadjTextBlockLocFromRect,
                                        mapOf(
                                                Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider,
                                                mockTextBlockUnadjCenterProvider,
                                                Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens,
                                                vertexOf(
                                                        FIRST_PARAGRAPH_LENGTH * TEXT_LINE_LENGTH_ROUNDING_ERROR,
                                                        TEXT_BLOCK_HEIGHT
                                                )
                                        )
                                )
                        ),
                        anyLong()
                ))
                .thenReturn(mockCenteredTextBlockUpperLeftProvider);

        lenient().when(mockProviderDefReader.read(argThat(
                                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                                        Button_textBlockLocWithAdj,
                                        mapOf(
                                                COMPONENT_UUID,
                                                BUTTON_UUID
                                        )
                                )),
                        anyLong()))
                .thenReturn(mockTextBlockAdjRenderingLoc);

        lenient().when(
                        mockProviderDefReader.read(same(mockTextBlockUnadjCenterProviderDef),
                                anyLong()))
                .thenReturn(mockTextBlockUnadjCenterProvider);

        imageAssetDefDefault = sprite(null, mockImageAssetDefaultUnadjDimensProviderDef);
        lenient().when(mockProviderDefReader.read(same(mockImageAssetDefaultUnadjDimensProviderDef),
                anyLong())).thenReturn(mockImageAssetDefaultUnadjDimensProvider);
        lenient().when(
                        mockRenderableDefReader.read(isNull(), same(imageAssetDefDefault),
                                anyLong()))
                .thenReturn(mockImageAssetDefault);

        imageAssetDefHover = sprite(null, mockImageAssetHoverUnadjDimensProviderDef);
        lenient().when(mockProviderDefReader.read(same(mockImageAssetHoverUnadjDimensProviderDef),
                anyLong())).thenReturn(mockImageAssetHoverUnadjDimensProvider);
        lenient().when(mockRenderableDefReader.read(isNull(), same(imageAssetDefHover), anyLong()))
                .thenReturn(mockImageAssetHover);

        imageAssetDefPressed = sprite(null, mockImageAssetPressedUnadjDimensProviderDef);
        lenient().when(mockProviderDefReader.read(same(mockImageAssetPressedUnadjDimensProviderDef),
                anyLong())).thenReturn(mockImageAssetPressedUnadjDimensProvider);
        lenient().when(
                        mockRenderableDefReader.read(isNull(), same(imageAssetDefPressed),
                                anyLong()))
                .thenReturn(mockImageAssetPressed);

        lenient().when(mockProviderDefReader.read(argThat(
                                new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                        Button_imageAssetDimensWithAdj,
                                        mapOf(
                                                COMPONENT_UUID,
                                                BUTTON_UUID
                                        )
                                )),
                        anyLong()))
                .thenReturn(mockImageAssetAdjDimensProvider);

        lenient().when(
                mockTextLineRenderer.textLineLength(anyString(), any(), anyFloat(), anyList(),
                        anyList(), anyFloat())).thenReturn(FIRST_PARAGRAPH_LENGTH);

        readTextBlockDef = component(randomInt())
                .withData(mapOf(
                        TextBlockMethods.TEXT_BLOCK_HEIGHT,
                        TEXT_BLOCK_HEIGHT,
                        COMPONENT_ORIGIN_PROVIDER,
                        mockTextBlockUnadjRenderingLoc
                ));

        lenient().when(mockTextBlockDefinitionReader.read(any(), anyLong()))
                .thenReturn(readTextBlockDef);

        reader = new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer,
                MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetWidthToHeightRatio);
    }

    @Test
    public void testConstructorWithInvalidParams() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(null, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer,
                        MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, null,
                        mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer,
                        MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        null, mockMarkupParser, mockTextLineRenderer, MOCK_GET_CONSUMER,
                        MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, null, mockTextLineRenderer,
                        MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, mockMarkupParser, null, MOCK_GET_CONSUMER,
                        MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer, null,
                        MOCK_GET_FONT, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer,
                        MOCK_GET_CONSUMER, null, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockRenderableDefReader,
                        mockTextBlockDefinitionReader, mockMarkupParser, mockTextLineRenderer,
                        MOCK_GET_CONSUMER, MOCK_GET_FONT, null));
    }

    @Test
    public void testReadFromDefsWithDimensFromRect() {
        rectDefDefault.dimensProviderDef = mockRectDefaultUnadjDimensProviderDef;

        var buttonDef = button(Z, BUTTON_UUID)
                .withRectDefault(rectDefDefault)
                .withRectHover(rectDefHover)
                .withRectPressed(rectDefPressed)
                .withTextBlockDef(textBlockDef)
                .withTextBlockPadding(TEXT_BLOCK_X_PADDING)
                .rectDefinesTextDimens(TEXT_BLOCK_HORIZ_ALIGN_WITHIN_RECT)
                .withImageAsset(imageAssetDefDefault)
                .withImageAssetHover(imageAssetDefHover)
                .withImageAssetPressed(imageAssetDefPressed)
                .onPress(ON_PRESS_ID)
                .withMouseOverSound(TEST_MOUSE_OVER_SOUND_ID)
                .withMouseLeaveSound(TEST_MOUSE_LEAVE_SOUND_ID)
                .withPressSound(TEST_PRESS_SOUND_ID)
                .withReleaseSound(TEST_RELEASE_SOUND_ID);

        var componentDef = reader.read(buttonDef, TIMESTAMP);

        var expectedReadTextBlockDefData = mapOf(
                TextBlockMethods.TEXT_BLOCK_HEIGHT,
                TEXT_BLOCK_HEIGHT,
                COMPONENT_ORIGIN_PROVIDER,
                mockTextBlockAdjRenderingLoc
        );

        var expectedData = Collections.<String, Object>mapOf(
                PRESS_CONSUMER,
                MOCK_ON_PRESS,
                PRESS_SOUND_ID,
                TEST_PRESS_SOUND_ID,
                MOUSE_OVER_SOUND_ID,
                TEST_MOUSE_OVER_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID,
                TEST_MOUSE_LEAVE_SOUND_ID,
                RELEASE_SOUND_ID,
                TEST_RELEASE_SOUND_ID,
                RECT_UNADJ_DIMENS_PROVIDER,
                mockRectUnadjDimensProvider,
                TEXT_BLOCK_UNADJ_LOC_PROVIDER,
                mockTextBlockUnadjRenderingLoc
        );

        assertMapContainsSubsetEquals(expectedData, componentDef.data);
        assertEquals(expectedData.size() + 3, componentDef.data.size());
        setOf(
                RENDERABLE_OPTIONS_DEFAULT,
                RENDERABLE_OPTIONS_HOVER,
                RENDERABLE_OPTIONS_PRESSED
        ).forEach(k -> assertTrue(componentDef.data.containsKey(k)));

        Options optionsDefault = getFromData(componentDef.data, RENDERABLE_OPTIONS_DEFAULT);
        assertSame(mockRectDefault, optionsDefault.rect);
        assertSame(mockImageAssetDefault, optionsDefault.imageAsset);
        assertSame(mockImageAssetDefaultUnadjDimensProvider, optionsDefault.unadjImageAssetDimens);

        Options optionsHover = getFromData(componentDef.data, RENDERABLE_OPTIONS_HOVER);
        assertSame(mockRectHover, optionsHover.rect);
        assertSame(mockImageAssetHover, optionsHover.imageAsset);
        assertSame(mockImageAssetHoverUnadjDimensProvider, optionsHover.unadjImageAssetDimens);

        Options optionsPressed = getFromData(componentDef.data, RENDERABLE_OPTIONS_PRESSED);
        assertSame(mockRectPressed, optionsPressed.rect);
        assertSame(mockImageAssetPressed, optionsPressed.imageAsset);
        assertSame(mockImageAssetPressedUnadjDimensProvider, optionsPressed.unadjImageAssetDimens);

        assertEquals(setOf(mockRectDefault, mockImageAssetDefault), componentDef.PREREAD_CONTENT);

        assertUniversalRectPropsAndVerifyRead(rectDefDefault);
        assertUniversalRectPropsAndVerifyRead(rectDefHover);
        assertUniversalRectPropsAndVerifyRead(rectDefPressed);

        assertEquals(TEXT_BLOCK_Z, textBlockDef.z);
        assertEquals(TEXT_BLOCK_X_PADDING / WIDTH_TO_HEIGHT_RATIO, buttonDef.textBlockYPadding);

        assertEquals(expectedReadTextBlockDefData, readTextBlockDef.data);

        assertImageAssetDefPrepped(imageAssetDefDefault);
        assertImageAssetDefPrepped(imageAssetDefHover);
        assertImageAssetDefPrepped(imageAssetDefPressed);

        verify(MOCK_GET_CONSUMER, once()).apply(ON_PRESS_ID);
        verify(mockProviderDefReader, once())
                .read(same(mockRectDefaultUnadjDimensProviderDef), eq(TIMESTAMP));
        verify(mockProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<>(
                                Button_rectDimensWithAdj,
                                mapOf(
                                        COMPONENT_UUID,
                                        BUTTON_UUID
                                )
                        )),
                eq(TIMESTAMP)
        );
        verify(mockGetWidthToHeightRatio, once()).get();
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockTextLineRenderer, once()).textLineLength(
                eq(FORMATTED_TEXT),
                same(MOCK_FONT),
                eq(GLYPH_PADDING),
                eq(listOf(ITALIC_INDEX)),
                eq(listOf(BOLD_INDEX)),
                eq(LINE_HEIGHT)
        );
        verify(mockTextBlockDefinitionReader, once()).read(same(textBlockDef), eq(TIMESTAMP));
        verify(mockProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<>(
                                Button_provideUnadjTextBlockLocFromRect,
                                mapOf(
                                        COMPONENT_UUID,
                                        BUTTON_UUID,
                                        Button_provideUnadjTextBlockLocFromRect_horizontalAlignment,
                                        TEXT_BLOCK_HORIZ_ALIGN_WITHIN_RECT,
                                        Button_provideUnadjTextBlockLocFromRect_paddingHoriz,
                                        TEXT_BLOCK_X_PADDING,
                                        Button_provideUnadjTextBlockLocFromRect_textBlockHeight,
                                        TEXT_BLOCK_HEIGHT,
                                        Button_provideUnadjTextBlockLocFromRect_lineLength,
                                        FIRST_PARAGRAPH_LENGTH * TEXT_LINE_LENGTH_ROUNDING_ERROR
                                )
                        )),
                eq(TIMESTAMP)
        );
        verify(mockProviderDefReader, once()).read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_textBlockLocWithAdj,
                                mapOf(
                                        COMPONENT_UUID,
                                        BUTTON_UUID
                                )
                        )),
                eq(TIMESTAMP));
        verify(mockTextBlockDefinitionReader, once()).read(same(textBlockDef), eq(TIMESTAMP));
    }

    @Test
    public void testReadFromProvidersWithDimensFromRect() {
        rectDefDefault.dimensProvider = mockRectUnadjDimensProvider;

        var buttonDef = button(Z, BUTTON_UUID)
                .withRectDefault(rectDefDefault)
                .withRectHover(rectDefHover)
                .withRectPressed(rectDefPressed)
                .withTextBlockDef(textBlockDef)
                .withTextBlockPadding(TEXT_BLOCK_X_PADDING)
                .rectDefinesTextDimens(TEXT_BLOCK_HORIZ_ALIGN_WITHIN_RECT)
                .withImageAsset(imageAssetDefDefault)
                .withImageAssetHover(imageAssetDefHover)
                .withImageAssetPressed(imageAssetDefPressed)
                .onPress(ON_PRESS_ID)
                .withMouseOverSound(TEST_MOUSE_OVER_SOUND_ID)
                .withMouseLeaveSound(TEST_MOUSE_LEAVE_SOUND_ID)
                .withPressSound(TEST_PRESS_SOUND_ID)
                .withReleaseSound(TEST_RELEASE_SOUND_ID);

        reader.read(buttonDef, TIMESTAMP);

        verify(mockProviderDefReader, never())
                .read(same(mockRectDefaultUnadjDimensProviderDef), eq(TIMESTAMP));
    }

    @Test
    public void testReadFromDefsWithDimensFromTextBlock() {
        rectDefDefault.dimensProviderDef = mockRectDefaultUnadjDimensProviderDef;

        var buttonDef = button(Z, BUTTON_UUID)
                .withRectDefault(rectDefDefault)
                .withRectHover(rectDefHover)
                .withRectPressed(rectDefPressed)
                .withTextBlockDef(textBlockDef)
                .withTextBlockPadding(TEXT_BLOCK_X_PADDING)
                .textBlockDefinesRectDimens()
                .withTextBlockCenterProviderDef(mockTextBlockUnadjCenterProviderDef)
                .withImageAsset(imageAssetDefDefault)
                .withImageAssetHover(imageAssetDefHover)
                .withImageAssetPressed(imageAssetDefPressed)
                .onPress(ON_PRESS_ID)
                .withMouseOverSound(TEST_MOUSE_OVER_SOUND_ID)
                .withMouseLeaveSound(TEST_MOUSE_LEAVE_SOUND_ID)
                .withPressSound(TEST_PRESS_SOUND_ID)
                .withReleaseSound(TEST_RELEASE_SOUND_ID);

        var componentDef = reader.read(buttonDef, TIMESTAMP);

        assertSame(mockTextBlockUnadjRenderingLoc, getFromData(componentDef.data, TEXT_BLOCK_UNADJ_LOC_PROVIDER));
        assertSame(mockRectUnadjDimensProvider, getFromData(componentDef.data, RECT_UNADJ_DIMENS_PROVIDER));

        // TextBlock center provider should be ignored when max length is not dynamic
        verify(mockProviderDefReader, never()).read(same(mockTextBlockUnadjCenterProviderDef),
                anyLong());
        verify(mockProviderDefReader, once()).read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromTextBlock,
                                mapOf(
                                        COMPONENT_UUID,
                                        BUTTON_UUID,
                                        Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft,
                                        mockTextBlockUnadjRenderingLoc,
                                        Button_provideUnadjRectDimensFromTextBlock_textBlockDimens,
                                        vertexOf(MAX_LINE_LENGTH, TEXT_BLOCK_HEIGHT),
                                        Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz,
                                        TEXT_BLOCK_X_PADDING,
                                        Button_provideUnadjRectDimensFromTextBlock_textPaddingVert,
                                        TEXT_BLOCK_X_PADDING / WIDTH_TO_HEIGHT_RATIO
                                )
                        )),
                eq(TIMESTAMP));
    }

    @Test
    public void testMakePlainRectAroundTextBlockWithTextBlockCenterProviderDef() {
        textBlockDef.maxLineLength = -1f;
        var buttonDef = button(Z, BUTTON_UUID)
                .withTextBlockDef(textBlockDef)
                .withTextBlockPadding(TEXT_BLOCK_X_PADDING)
                .withTextBlockCenterProviderDef(mockTextBlockUnadjCenterProviderDef);

        var componentDef = reader.read(buttonDef, TIMESTAMP);

        verify(mockProviderDefReader, once())
                .read(same(mockTextBlockUnadjCenterProviderDef), eq(TIMESTAMP));
        verify(mockProviderDefReader, once()).read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                                Button_provideCenteredUnadjTextBlockLocFromRect,
                                mapOf(
                                        Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider,
                                        mockTextBlockUnadjCenterProvider,
                                        Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens,
                                        vertexOf(
                                                FIRST_PARAGRAPH_LENGTH * TEXT_LINE_LENGTH_ROUNDING_ERROR,
                                                TEXT_BLOCK_HEIGHT
                                        )
                                )
                        )
                ),
                eq(TIMESTAMP)
        );
        var plainRectDefCaptor = ArgumentCaptor.forClass(RectangleRenderableDefinition.class);
        verify(mockRenderableDefReader, once())
                .read(any(), plainRectDefCaptor.capture(), anyLong());
        var plainRectDef = plainRectDefCaptor.getValue();
        assertEquals(RECT_Z, plainRectDef.z);
        assertSame(mockAdjRectDimensProvider, plainRectDef.dimensProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, Button_pressMouse), plainRectDef.onPressIds);
        assertEquals(Button_mouseOver, plainRectDef.onMouseOverId);
        assertEquals(Button_mouseLeave, plainRectDef.onMouseLeaveId);
        assertTrue(componentDef.PREREAD_CONTENT.contains(mockPlainRect));
    }

    private void assertImageAssetDefPrepped(AbstractImageAssetRenderableDefinition def) {
        assertEquals(IMAGE_ASSET_Z, def.z);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, Button_pressMouse), def.onPressIds);
        assertEquals(Button_mouseOver, def.onMouseOverId);
        assertEquals(Button_mouseLeave, def.onMouseLeaveId);
    }

    private void assertUniversalRectPropsAndVerifyRead(RectangleRenderableDefinition rectDef) {
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, Button_pressMouse), rectDef.onPressIds);
        assertEquals(Button_mouseOver, rectDef.onMouseOverId);
        assertEquals(Button_mouseLeave, rectDef.onMouseLeaveId);
        assertEquals(RECT_Z, rectDef.z);
        assertSame(mockAdjRectDimensProvider, rectDef.dimensProvider);

        verify(mockRenderableDefReader, once()).read(isNull(), same(rectDef), eq(TIMESTAMP));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
    }
}
