package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.EventInputs;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;
import soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ButtonDefinitionReaderTests {
    private static final String PRESS_MOUSE_METHOD = "pressMouse_Button";
    private static final String MOUSE_OVER_METHOD = "mouseOver_Button";
    private static final String MOUSE_LEAVE_METHOD = "mouseLeave_Button";
    private static final String PRESS_KEY_METHOD = "pressKey_Button";
    private static final String RELEASE_KEY_METHOD = "releaseKey_Button";

    private static final String TEXT_LOC_FROM_RECT_DIMENS_METHOD =
            "provideTextRenderingLocFromRect_Button";
    private static final String RECT_DIMENS_FROM_TEXT_LOC_METHOD =
            "provideRectDimensFromText_Button";
    private static final String TEX_WIDTH_FROM_RECT_DIMENS_METHOD = "provideTexTileWidth_Button";
    private static final String TEX_HEIGHT_FROM_RECT_DIMENS_METHOD = "provideTexTileHeight_Button";

    private final static String PRESS_ACTION_DATA_KEY = "pressAction";
    private final static String PRESS_SOUND_ID_DATA_KEY = "pressSoundId";
    private final static String MOUSE_OVER_SOUND_ID_DATA_KEY = "mouseOverSoundId";
    private final static String MOUSE_LEAVE_SOUND_ID_DATA_KEY = "mouseLeaveSoundId";
    private final static String RELEASE_SOUND_ID_DATA_KEY = "releaseSoundId";

    private final static String DEFAULT_RENDERABLE_OPTIONS_DATA_KEY = "defaultRenderableOptions";
    private final static String HOVER_RENDERABLE_OPTIONS_DATA_KEY = "hoverRenderableOptions";
    private final static String PRESSED_RENDERABLE_OPTIONS_DATA_KEY = "pressedRenderableOptions";

    private final static String provideTextRenderingLocFromRect_Button_textJustification =
            "provideTextRenderingLocFromRect_Button_textJustification";
    private final static String provideTextRenderingLocFromRect_Button_rectDimensProvider =
            "provideTextRenderingLocFromRect_Button_rectDimensProvider";
    private final static String provideTextRenderingLocFromRect_Button_paddingHoriz =
            "provideTextRenderingLocFromRect_Button_paddingHoriz";
    private final static String provideTextRenderingLocFromRect_Button_textHeight =
            "provideTextRenderingLocFromRect_Button_textHeight";

    private static final int RECT_Z = 0;
    private static final int SPRITE_Z = 1;
    private static final int TEXT_Z = 2;

    private final String PRESS_ACTION_ID = randomString();
    @SuppressWarnings("rawtypes") private final LookupAndEntitiesWithId<Action>
            MOCK_ACTION_AND_LOOKUP =
            generateMockLookupFunctionWithId(Action.class, PRESS_ACTION_ID);
    @SuppressWarnings("unchecked") private final Action<EventInputs> MOCK_PRESS_ACTION =
            MOCK_ACTION_AND_LOOKUP.entities.getFirst();
    @SuppressWarnings("rawtypes") private final Function<String, Action> MOCK_GET_ACTION =
            MOCK_ACTION_AND_LOOKUP.lookup;

    private final float WIDTH_TO_HEIGHT_RATIO = randomFloat();

    private final String FONT_ID = randomString();
    private final LookupAndEntitiesWithId<Font> MOCK_FONT_AND_LOOKUP =
            generateMockLookupFunctionWithId(Font.class, FONT_ID);
    private final Font MOCK_FONT = MOCK_FONT_AND_LOOKUP.entities.getFirst();
    private final Function<String, Font> MOCK_GET_FONT = MOCK_FONT_AND_LOOKUP.lookup;

    private final int Z = randomInt();
    private final int KEY = randomInt();
    private final int KEY_BINDING_PRIORITY = randomInt();
    private final String SPRITE_ID_DEFAULT = randomString();
    private final String SPRITE_ID_HOVER = randomString();
    private final String SPRITE_ID_PRESSED = randomString();
    private final String TEXT = randomString();
    private final TextJustification TEXT_JUSTIFICATION =
            TextJustification.fromValue(randomIntInRange(1, 3));
    private final float TEXT_LINE_LENGTH_DEFAULT = randomFloat();
    private final float TEXT_LINE_LENGTH_HOVER = randomFloat();
    private final float TEXT_LINE_LENGTH_PRESSED = randomFloat();
    private final float TEXT_HEIGHT = randomFloat();
    private final float TEXT_GLYPH_PADDING = randomFloat();
    private final float TEXT_PADDING_VERT = randomFloat();
    private final float TEXT_PADDING_HORIZ = TEXT_PADDING_VERT / WIDTH_TO_HEIGHT_RATIO;

    private final int COLOR_INDEX_DEFAULT = randomInt();
    private final int ITALIC_INDEX_DEFAULT = randomInt();
    private final int BOLD_INDEX_DEFAULT = randomInt();
    private final int COLOR_INDEX_HOVER = randomInt();
    private final int ITALIC_INDEX_HOVER = randomInt();
    private final int BOLD_INDEX_HOVER = randomInt();
    private final int COLOR_INDEX_PRESSED = randomInt();
    private final int ITALIC_INDEX_PRESSED = randomInt();
    private final int BOLD_INDEX_PRESSED = randomInt();

    private final String PRESS_SOUND_ID = randomString();
    private final String MOUSE_OVER_SOUND_ID = randomString();
    private final String MOUSE_LEAVE_SOUND_ID = randomString();
    private final String RELEASE_SOUND_ID = randomString();

    private final long TIMESTAMP = randomLong();

    @Mock private AbstractProviderDefinition<FloatBox> mockRectDimensProviderDef;

    @Mock private AbstractProviderDefinition<Color> mockBgColorTopLeftDefaultDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorTopRightDefaultDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomLeftDefaultDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomRightDefaultDef;

    @Mock private AbstractProviderDefinition<Color> mockBgColorTopLeftHoverDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorTopRightHoverDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomLeftHoverDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomRightHoverDef;

    @Mock private AbstractProviderDefinition<Color> mockBgColorTopLeftPressedDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorTopRightPressedDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomLeftPressedDef;
    @Mock private AbstractProviderDefinition<Color> mockBgColorBottomRightPressedDef;

    @Mock private AbstractProviderDefinition<Integer> mockTexProviderDefaultDef;
    @Mock private AbstractProviderDefinition<Integer> mockTexProviderHoverDef;
    @Mock private AbstractProviderDefinition<Integer> mockTexProviderPressedDef;

    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteDimensDefaultDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteDimensHoverDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteDimensPressedDef;

    @Mock private ShiftDefinition mockSpriteShiftDefaultDef;
    @Mock private ShiftDefinition mockSpriteShiftHoverDef;
    @Mock private ShiftDefinition mockSpriteShiftPressedDef;

    @Mock private AbstractProviderDefinition<Vertex> mockTextRenderingLocDef;

    @Mock private AbstractProviderDefinition<Color> mockTextColorDefaultDef;
    @Mock private AbstractProviderDefinition<Color> mockTextColorHoverDef;
    @Mock private AbstractProviderDefinition<Color> mockTextColorPressedDef;

    @Mock private ProviderAtTime<FloatBox> mockRectDimensProvider;

    @Mock private ProviderAtTime<Float> mockTexTileWidthProvider;
    @Mock private ProviderAtTime<Float> mockTexTileHeightProvider;

    @Mock private ProviderAtTime<Color> mockBgColorTopLeftDefault;
    @Mock private ProviderAtTime<Color> mockBgColorTopRightDefault;
    @Mock private ProviderAtTime<Color> mockBgColorBottomLeftDefault;
    @Mock private ProviderAtTime<Color> mockBgColorBottomRightDefault;

    @Mock private ProviderAtTime<Color> mockBgColorTopLeftHover;
    @Mock private ProviderAtTime<Color> mockBgColorTopRightHover;
    @Mock private ProviderAtTime<Color> mockBgColorBottomLeftHover;
    @Mock private ProviderAtTime<Color> mockBgColorBottomRightHover;

    @Mock private ProviderAtTime<Color> mockBgColorTopLeftPressed;
    @Mock private ProviderAtTime<Color> mockBgColorTopRightPressed;
    @Mock private ProviderAtTime<Color> mockBgColorBottomLeftPressed;
    @Mock private ProviderAtTime<Color> mockBgColorBottomRightPressed;

    @Mock private ProviderAtTime<Integer> mockTexProviderDefault;
    @Mock private ProviderAtTime<Integer> mockTexProviderHover;
    @Mock private ProviderAtTime<Integer> mockTexProviderPressed;

    @Mock private ProviderAtTime<FloatBox> mockSpriteDimensDefault;
    @Mock private ProviderAtTime<FloatBox> mockSpriteDimensHover;
    @Mock private ProviderAtTime<FloatBox> mockSpriteDimensPressed;

    @Mock private ColorShift mockSpriteShiftDefault;
    @Mock private ColorShift mockSpriteShiftHover;
    @Mock private ColorShift mockSpriteShiftPressed;

    @Mock private ProviderAtTime<Vertex> mockTextRenderingLoc;

    @Mock private ProviderAtTime<Color> mockTextColorDefault;
    @Mock private ProviderAtTime<Color> mockTextColorHover;
    @Mock private ProviderAtTime<Color> mockTextColorPressed;


    @Mock private ProviderAtTime<FloatBox> mockRectDimensFuncProviderDefault;
    @Mock private ProviderAtTime<FloatBox> mockRectDimensFuncProviderHover;
    @Mock private ProviderAtTime<FloatBox> mockRectDimensFuncProviderPressed;

    @Mock private ProviderDefinitionReader mockProviderDefReader;
    @Mock private ShiftDefinitionReader mockShiftReader;
    @SuppressWarnings("rawtypes") @Mock private ProviderAtTime mockNullProvider;
    @Mock private Supplier<Long> mockGetTimestamp;
    @Mock private TextLineRenderer mockTextLineRenderer;
    @Mock private Function<String, Integer> mockGetTexId;
    @Mock private Supplier<Float> mockGetWidthToHeightRatio;

    private Map<Integer, AbstractProviderDefinition<Color>> textColorsDefaultDefs;
    private Map<Integer, AbstractProviderDefinition<Color>> textColorsHoverDefs;
    private Map<Integer, AbstractProviderDefinition<Color>> textColorsPressedDefs;
    private Map<Integer, ProviderAtTime<Color>> textColorsDefault;
    private Map<Integer, ProviderAtTime<Color>> textColorsHover;
    private Map<Integer, ProviderAtTime<Color>> textColorsPressed;

    private ButtonDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockProviderDefReader.read(same(mockRectDimensProviderDef), anyLong()))
                .thenReturn(mockRectDimensProvider);

        lenient().when(mockProviderDefReader.read(same(mockBgColorTopLeftDefaultDef), anyLong()))
                .thenReturn(mockBgColorTopLeftDefault);
        lenient().when(mockProviderDefReader.read(same(mockBgColorTopRightDefaultDef), anyLong()))
                .thenReturn(mockBgColorTopRightDefault);
        lenient().when(mockProviderDefReader.read(same(mockBgColorBottomLeftDefaultDef), anyLong()))
                .thenReturn(mockBgColorBottomLeftDefault);
        lenient().when(
                        mockProviderDefReader.read(same(mockBgColorBottomRightDefaultDef),
                                anyLong()))
                .thenReturn(mockBgColorBottomRightDefault);

        lenient().when(mockProviderDefReader.read(same(mockBgColorTopLeftHoverDef), anyLong()))
                .thenReturn(mockBgColorTopLeftHover);
        lenient().when(mockProviderDefReader.read(same(mockBgColorTopRightHoverDef), anyLong()))
                .thenReturn(mockBgColorTopRightHover);
        lenient().when(mockProviderDefReader.read(same(mockBgColorBottomLeftHoverDef), anyLong()))
                .thenReturn(mockBgColorBottomLeftHover);
        lenient().when(mockProviderDefReader.read(same(mockBgColorBottomRightHoverDef), anyLong()))
                .thenReturn(mockBgColorBottomRightHover);

        lenient().when(mockProviderDefReader.read(same(mockBgColorTopLeftPressedDef), anyLong()))
                .thenReturn(mockBgColorTopLeftPressed);
        lenient().when(mockProviderDefReader.read(same(mockBgColorTopRightPressedDef), anyLong()))
                .thenReturn(mockBgColorTopRightPressed);
        lenient().when(mockProviderDefReader.read(same(mockBgColorBottomLeftPressedDef), anyLong()))
                .thenReturn(mockBgColorBottomLeftPressed);
        lenient().when(
                        mockProviderDefReader.read(same(mockBgColorBottomRightPressedDef),
                                anyLong()))
                .thenReturn(mockBgColorBottomRightPressed);

        lenient().when(mockProviderDefReader.read(same(mockTexProviderDefaultDef), anyLong()))
                .thenReturn(mockTexProviderDefault);
        lenient().when(mockProviderDefReader.read(same(mockTexProviderHoverDef), anyLong()))
                .thenReturn(mockTexProviderHover);
        lenient().when(mockProviderDefReader.read(same(mockTexProviderPressedDef), anyLong()))
                .thenReturn(mockTexProviderPressed);

        lenient().when(mockProviderDefReader.read(same(mockTextRenderingLocDef), anyLong()))
                .thenReturn(mockTextRenderingLoc);

        lenient().when(mockProviderDefReader.read(same(mockTextColorDefaultDef), anyLong()))
                .thenReturn(mockTextColorDefault);
        lenient().when(mockProviderDefReader.read(same(mockTextColorHoverDef), anyLong()))
                .thenReturn(mockTextColorHover);
        lenient().when(mockProviderDefReader.read(same(mockTextColorPressedDef), anyLong()))
                .thenReturn(mockTextColorPressed);

        lenient().when(mockProviderDefReader.read(same(mockSpriteDimensDefaultDef), anyLong()))
                .thenReturn(mockSpriteDimensDefault);
        lenient().when(mockProviderDefReader.read(same(mockSpriteDimensHoverDef), anyLong()))
                .thenReturn(mockSpriteDimensHover);
        lenient().when(mockProviderDefReader.read(same(mockSpriteDimensPressedDef), anyLong()))
                .thenReturn(mockSpriteDimensPressed);

        lenient().when(mockShiftReader.read(same(mockSpriteShiftDefaultDef), anyLong()))
                .thenReturn(mockSpriteShiftDefault);
        lenient().when(mockShiftReader.read(same(mockSpriteShiftHoverDef), anyLong()))
                .thenReturn(mockSpriteShiftHover);
        lenient().when(mockShiftReader.read(same(mockSpriteShiftPressedDef), anyLong()))
                .thenReturn(mockSpriteShiftPressed);

        lenient().when(mockGetTimestamp.get()).thenReturn(TIMESTAMP);

        lenient().when(mockTextLineRenderer.textLineLength(
                anyString(),
                any(),
                anyFloat(),
                eq(listOf(ITALIC_INDEX_DEFAULT)),
                any(),
                anyFloat())
        ).thenReturn(TEXT_LINE_LENGTH_DEFAULT);
        lenient().when(mockTextLineRenderer.textLineLength(
                anyString(),
                any(),
                anyFloat(),
                eq(listOf(ITALIC_INDEX_HOVER)),
                any(),
                anyFloat())
        ).thenReturn(TEXT_LINE_LENGTH_HOVER);
        lenient().when(mockTextLineRenderer.textLineLength(
                anyString(),
                any(),
                anyFloat(),
                eq(listOf(ITALIC_INDEX_PRESSED)),
                any(),
                anyFloat())
        ).thenReturn(TEXT_LINE_LENGTH_PRESSED);

        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(RECT_DIMENS_FROM_TEXT_LOC_METHOD,
                                mapOf(
                                        provideRectDimensFromText_Button_textRenderingLocProvider,
                                        mockTextRenderingLoc,
                                        provideRectDimensFromText_Button_lineLength,
                                        TEXT_LINE_LENGTH_DEFAULT,
                                        provideRectDimensFromText_Button_textHeight,
                                        TEXT_HEIGHT,
                                        provideRectDimensFromText_Button_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        provideRectDimensFromText_Button_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensFuncProviderDefault);
        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(RECT_DIMENS_FROM_TEXT_LOC_METHOD,
                                mapOf(
                                        provideRectDimensFromText_Button_textRenderingLocProvider,
                                        mockTextRenderingLoc,
                                        provideRectDimensFromText_Button_lineLength,
                                        TEXT_LINE_LENGTH_HOVER,
                                        provideRectDimensFromText_Button_textHeight,
                                        TEXT_HEIGHT,
                                        provideRectDimensFromText_Button_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        provideRectDimensFromText_Button_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensFuncProviderHover);
        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(RECT_DIMENS_FROM_TEXT_LOC_METHOD,
                                mapOf(
                                        provideRectDimensFromText_Button_textRenderingLocProvider,
                                        mockTextRenderingLoc,
                                        provideRectDimensFromText_Button_lineLength,
                                        TEXT_LINE_LENGTH_PRESSED,
                                        provideRectDimensFromText_Button_textHeight,
                                        TEXT_HEIGHT,
                                        provideRectDimensFromText_Button_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        provideRectDimensFromText_Button_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensFuncProviderPressed);

        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(TEX_WIDTH_FROM_RECT_DIMENS_METHOD,
                                mapOf(
                                        provideTexTileDimens_Button_rectDimensProvider,
                                        mockRectDimensProvider
                                ))), anyLong())).
                thenReturn(mockTexTileWidthProvider);
        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(TEX_HEIGHT_FROM_RECT_DIMENS_METHOD,
                                mapOf(
                                        provideTexTileDimens_Button_rectDimensProvider,
                                        mockRectDimensProvider
                                ))), anyLong())).
                thenReturn(mockTexTileHeightProvider);

        lenient().when(mockGetWidthToHeightRatio.get()).thenReturn(WIDTH_TO_HEIGHT_RATIO);

        textColorsDefaultDefs = mapOf(COLOR_INDEX_DEFAULT, mockTextColorDefaultDef);
        textColorsHoverDefs = mapOf(COLOR_INDEX_HOVER, mockTextColorHoverDef);
        textColorsPressedDefs = mapOf(COLOR_INDEX_PRESSED, mockTextColorPressedDef);
        textColorsDefault = mapOf(COLOR_INDEX_DEFAULT, mockTextColorDefault);
        textColorsHover = mapOf(COLOR_INDEX_HOVER, mockTextColorHover);
        textColorsPressed = mapOf(COLOR_INDEX_PRESSED, mockTextColorPressed);

        reader =
                new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader, mockNullProvider,
                        mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION, MOCK_GET_FONT,
                        mockGetTexId, mockGetWidthToHeightRatio);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(null, mockShiftReader, mockNullProvider,
                        mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION, MOCK_GET_FONT,
                        mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, null, mockNullProvider,
                        mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION, MOCK_GET_FONT,
                        mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader, null,
                        mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION, MOCK_GET_FONT,
                        mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, null, mockTextLineRenderer, MOCK_GET_ACTION,
                        MOCK_GET_FONT, mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockGetTimestamp, null, MOCK_GET_ACTION, MOCK_GET_FONT,
                        mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockGetTimestamp, mockTextLineRenderer, null,
                        MOCK_GET_FONT, mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION,
                        null, mockGetTexId, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION,
                        MOCK_GET_FONT, null, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockGetTimestamp, mockTextLineRenderer, MOCK_GET_ACTION,
                        MOCK_GET_FONT, mockGetTexId, null));
    }

    @Test
    public void testReadFromRectDimensAndDefsWithMaximalArgs() {
        //noinspection unchecked
        when(mockProviderDefReader.read(
                argThat(new FunctionalProviderDefMatcher(TEXT_LOC_FROM_RECT_DIMENS_METHOD, mapOf(
                        provideTextRenderingLocFromRect_Button_textJustification,
                        TEXT_JUSTIFICATION,
                        provideTextRenderingLocFromRect_Button_rectDimensProvider,
                        mockRectDimensProvider,
                        provideTextRenderingLocFromRect_Button_paddingHoriz,
                        TEXT_PADDING_HORIZ,
                        provideTextRenderingLocFromRect_Button_textHeight,
                        TEXT_HEIGHT
                ))), anyLong()))
                .thenReturn(mockTextRenderingLoc);

        var def = buttonDefinitionFromRectDimensAndDefsWithMaximalArgs();

        var output = reader.read(def);

        assertNotNull(output);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_ACTION_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_ACTION_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertSame(mockRectDimensProvider, rectDef.DIMENS_PROVIDER);
        assertSame(mockBgColorTopLeftDefault, rectDef.topLeftColorProvider);
        assertSame(mockBgColorTopRightDefault, rectDef.topRightColorProvider);
        assertSame(mockBgColorBottomLeftDefault, rectDef.bottomLeftColorProvider);
        assertSame(mockBgColorBottomRightDefault, rectDef.bottomRightColorProvider);
        assertSame(mockTexProviderDefault, rectDef.textureIdProvider);
        assertSame(mockTexTileWidthProvider, rectDef.textureTileWidthProvider);
        assertSame(mockTexTileHeightProvider, rectDef.textureTileHeightProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        assertSpriteHasFullDef(output);

        assertTextHasFullDef(output);

        var expectedDefaultOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensProvider,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteDimensDefault,
                mockSpriteShiftDefault,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedHoverOptions = new ButtonMethods.RenderableOptions(
                null,
                mockBgColorTopLeftHover,
                mockBgColorTopRightHover,
                mockBgColorBottomLeftHover,
                mockBgColorBottomRightHover,
                mockTexProviderHover,
                SPRITE_ID_HOVER,
                mockSpriteDimensHover,
                mockSpriteShiftHover,
                textColorsHover,
                listInts(ITALIC_INDEX_HOVER),
                listInts(BOLD_INDEX_HOVER)
        );
        var expectedPressedOptions = new ButtonMethods.RenderableOptions(
                null,
                mockBgColorTopLeftPressed,
                mockBgColorTopRightPressed,
                mockBgColorBottomLeftPressed,
                mockBgColorBottomRightPressed,
                mockTexProviderPressed,
                SPRITE_ID_PRESSED,
                mockSpriteDimensPressed,
                mockSpriteShiftPressed,
                textColorsPressed,
                listInts(ITALIC_INDEX_PRESSED),
                listInts(BOLD_INDEX_PRESSED)
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION_DATA_KEY,
                MOCK_PRESS_ACTION,
                PRESS_SOUND_ID_DATA_KEY,
                PRESS_SOUND_ID,
                MOUSE_OVER_SOUND_ID_DATA_KEY,
                MOUSE_OVER_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID_DATA_KEY,
                MOUSE_LEAVE_SOUND_ID,
                RELEASE_SOUND_ID_DATA_KEY,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedDefaultOptions,
                expectedHoverOptions,
                expectedPressedOptions,
                output.data
        );

        verify(mockGetTimestamp, once()).get();
        verify(MOCK_GET_ACTION, once()).apply(PRESS_ACTION_ID);
        verify(mockProviderDefReader, once()).read(mockRectDimensProviderDef, TIMESTAMP);
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockGetWidthToHeightRatio, once()).get();
        verify(mockTextLineRenderer, times(3)).textLineLength(
                anyString(),
                any(),
                anyFloat(),
                anyList(),
                anyList(),
                anyFloat()
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_DEFAULT),
                listOf(BOLD_INDEX_DEFAULT),
                TEXT_HEIGHT
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_HOVER),
                listOf(BOLD_INDEX_HOVER),
                TEXT_HEIGHT
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_PRESSED),
                listOf(BOLD_INDEX_PRESSED),
                TEXT_HEIGHT
        );
    }

    @Test
    public void testReadFromTextAndDefsWithMaximalArgs() {
        when(mockProviderDefReader.read(mockTextRenderingLocDef, TIMESTAMP))
                .thenReturn(mockTextRenderingLoc);

        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(TEX_WIDTH_FROM_RECT_DIMENS_METHOD,
                                mapOf(
                                        provideTexTileDimens_Button_rectDimensProvider,
                                        mockRectDimensFuncProviderDefault
                                ))), anyLong())).
                thenReturn(mockTexTileWidthProvider);
        //noinspection unchecked
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher(TEX_HEIGHT_FROM_RECT_DIMENS_METHOD,
                                mapOf(
                                        provideTexTileDimens_Button_rectDimensProvider,
                                        mockRectDimensFuncProviderDefault
                                ))), anyLong())).
                thenReturn(mockTexTileHeightProvider);

        var def = buttonDefinitionFromTextAndDefsWithMaximalArgs();

        var output = reader.read(def);

        assertNotNull(output);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_ACTION_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_ACTION_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertSame(mockRectDimensFuncProviderDefault, rectDef.DIMENS_PROVIDER);
        assertSame(mockBgColorTopLeftDefault, rectDef.topLeftColorProvider);
        assertSame(mockBgColorTopRightDefault, rectDef.topRightColorProvider);
        assertSame(mockBgColorBottomLeftDefault, rectDef.bottomLeftColorProvider);
        assertSame(mockBgColorBottomRightDefault, rectDef.bottomRightColorProvider);
        assertSame(mockTexProviderDefault, rectDef.textureIdProvider);
        assertSame(mockTexTileWidthProvider, rectDef.textureTileWidthProvider);
        assertSame(mockTexTileHeightProvider, rectDef.textureTileHeightProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        assertSpriteHasFullDef(output);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atTextLineZ =
                output.CONTENT.stream().filter(d -> d.Z == TEXT_Z).findFirst().get();
        assertInstanceOf(TextLineRenderableDefinition.class, atTextLineZ);
        var textLineDef = (TextLineRenderableDefinition) atTextLineZ;
        assertEquals(FONT_ID, textLineDef.FONT_ID);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.TEXT_PROVIDER);
        assertEquals(TEXT, extractStaticVal(textLineDef.TEXT_PROVIDER));
        assertSame(mockTextRenderingLoc, textLineDef.LOCATION_PROVIDER);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(TextJustification.CENTER, textLineDef.JUSTIFICATION);
        assertEquals(TEXT_GLYPH_PADDING, textLineDef.GLYPH_PADDING);
        assertEquals(textColorsDefault, textLineDef.colorProviderIndices);
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertEquals(listOf(ITALIC_INDEX_DEFAULT), textLineDef.italicIndices);
        assertEquals(listOf(BOLD_INDEX_DEFAULT), textLineDef.boldIndices);

        var expectedDefaultOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensFuncProviderDefault,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteDimensDefault,
                mockSpriteShiftDefault,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedHoverOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensFuncProviderHover,
                mockBgColorTopLeftHover,
                mockBgColorTopRightHover,
                mockBgColorBottomLeftHover,
                mockBgColorBottomRightHover,
                mockTexProviderHover,
                SPRITE_ID_HOVER,
                mockSpriteDimensHover,
                mockSpriteShiftHover,
                textColorsHover,
                listInts(ITALIC_INDEX_HOVER),
                listInts(BOLD_INDEX_HOVER)
        );
        var expectedPressedOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensFuncProviderPressed,
                mockBgColorTopLeftPressed,
                mockBgColorTopRightPressed,
                mockBgColorBottomLeftPressed,
                mockBgColorBottomRightPressed,
                mockTexProviderPressed,
                SPRITE_ID_PRESSED,
                mockSpriteDimensPressed,
                mockSpriteShiftPressed,
                textColorsPressed,
                listInts(ITALIC_INDEX_PRESSED),
                listInts(BOLD_INDEX_PRESSED)
        );

        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION_DATA_KEY,
                MOCK_PRESS_ACTION,
                PRESS_SOUND_ID_DATA_KEY,
                PRESS_SOUND_ID,
                MOUSE_OVER_SOUND_ID_DATA_KEY,
                MOUSE_OVER_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID_DATA_KEY,
                MOUSE_LEAVE_SOUND_ID,
                RELEASE_SOUND_ID_DATA_KEY,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedDefaultOptions,
                expectedHoverOptions,
                expectedPressedOptions,
                output.data
        );

        verify(mockGetTimestamp, once()).get();
        verify(MOCK_GET_ACTION, once()).apply(PRESS_ACTION_ID);
        verify(mockProviderDefReader, once()).read(mockTexProviderDefaultDef, TIMESTAMP);
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockGetWidthToHeightRatio, once()).get();
        verify(mockTextLineRenderer, times(3)).textLineLength(
                anyString(),
                any(),
                anyFloat(),
                anyList(),
                anyList(),
                anyFloat()
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_DEFAULT),
                listOf(BOLD_INDEX_DEFAULT),
                TEXT_HEIGHT
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_HOVER),
                listOf(BOLD_INDEX_HOVER),
                TEXT_HEIGHT
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_PRESSED),
                listOf(BOLD_INDEX_PRESSED),
                TEXT_HEIGHT
        );
    }

    @Test
    public void testReadFromRectDimensAndMaximalArgsForDefaultOnly() {
        //noinspection unchecked
        when(mockProviderDefReader.read(
                argThat(new FunctionalProviderDefMatcher(TEXT_LOC_FROM_RECT_DIMENS_METHOD, mapOf(
                        provideTextRenderingLocFromRect_Button_textJustification,
                        TEXT_JUSTIFICATION,
                        provideTextRenderingLocFromRect_Button_rectDimensProvider,
                        mockRectDimensProvider,
                        provideTextRenderingLocFromRect_Button_paddingHoriz,
                        TEXT_PADDING_HORIZ,
                        provideTextRenderingLocFromRect_Button_textHeight,
                        TEXT_HEIGHT
                ))), anyLong()))
                .thenReturn(mockTextRenderingLoc);

        var definition = withMaximalDefaultArgs(withText(definitionFromRectDimens()));

        var output = reader.read(definition);

        assertNotNull(output);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_ACTION_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_ACTION_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertSame(mockRectDimensProvider, rectDef.DIMENS_PROVIDER);
        assertSame(mockBgColorTopLeftDefault, rectDef.topLeftColorProvider);
        assertSame(mockBgColorTopRightDefault, rectDef.topRightColorProvider);
        assertSame(mockBgColorBottomLeftDefault, rectDef.bottomLeftColorProvider);
        assertSame(mockBgColorBottomRightDefault, rectDef.bottomRightColorProvider);
        assertSame(mockTexProviderDefault, rectDef.textureIdProvider);
        assertSame(mockTexTileWidthProvider, rectDef.textureTileWidthProvider);
        assertSame(mockTexTileHeightProvider, rectDef.textureTileHeightProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        assertSpriteHasFullDef(output, true);

        assertTextHasFullDef(output);

        var expectedOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensProvider,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteDimensDefault,
                mockSpriteShiftDefault,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION_DATA_KEY,
                MOCK_PRESS_ACTION,
                PRESS_SOUND_ID_DATA_KEY,
                PRESS_SOUND_ID,
                MOUSE_OVER_SOUND_ID_DATA_KEY,
                MOUSE_OVER_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID_DATA_KEY,
                MOUSE_LEAVE_SOUND_ID,
                RELEASE_SOUND_ID_DATA_KEY,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                new ButtonMethods.RenderableOptions(),
                new ButtonMethods.RenderableOptions(),
                output.data
        );

        verify(mockGetTimestamp, once()).get();
        verify(MOCK_GET_ACTION, once()).apply(PRESS_ACTION_ID);
        verify(mockProviderDefReader, once()).read(mockRectDimensProviderDef, TIMESTAMP);
        verify(MOCK_GET_FONT, once()).apply(FONT_ID);
        verify(mockGetWidthToHeightRatio, once()).get();
        verify(mockTextLineRenderer, once()).textLineLength(
                anyString(),
                any(),
                anyFloat(),
                anyList(),
                anyList(),
                anyFloat()
        );
        verify(mockTextLineRenderer, once()).textLineLength(
                TEXT,
                MOCK_FONT,
                TEXT_GLYPH_PADDING,
                listOf(ITALIC_INDEX_DEFAULT),
                listOf(BOLD_INDEX_DEFAULT),
                TEXT_HEIGHT
        );
    }

    @Test
    public void testReadSpriteOnly() {
        var definition = button(SPRITE_ID_DEFAULT, mockSpriteDimensDefaultDef, Z);

        var output = reader.read(definition);

        assertNotNull(output);
        assertEquals(1, output.CONTENT.size());
        assertSpriteHasBasicDef(output);
    }

    private SpriteRenderableDefinition assertSpriteHasBasicDef(ComponentDefinition output) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") var atSpriteZ =
                output.CONTENT.stream().filter(d -> d.Z == SPRITE_Z).findFirst().get();
        assertInstanceOf(SpriteRenderableDefinition.class, atSpriteZ);
        var spriteDef = (SpriteRenderableDefinition) atSpriteZ;
        assertEquals(SPRITE_ID_DEFAULT, spriteDef.SPRITE_ID);
        assertSame(mockSpriteDimensDefaultDef, spriteDef.DIMENSIONS_PROVIDER_DEF);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), spriteDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, spriteDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, spriteDef.onMouseLeaveId);
        return spriteDef;
    }

    private void assertSpriteHasFullDef(ComponentDefinition output, boolean justDefault) {
        var spriteDef = assertSpriteHasBasicDef(output);
        assertArrayEquals(arrayOf(mockSpriteShiftDefault), spriteDef.colorShifts);
        verify(mockShiftReader, once()).read(mockSpriteShiftDefaultDef, TIMESTAMP);
        verify(mockShiftReader, times(justDefault ? 1 : 3)).read(any(), anyLong());
        if (!justDefault) {
            verify(mockShiftReader, once()).read(mockSpriteShiftHoverDef, TIMESTAMP);
            verify(mockShiftReader, once()).read(mockSpriteShiftPressedDef, TIMESTAMP);
        }
    }

    private void assertSpriteHasFullDef(ComponentDefinition output) {
        assertSpriteHasFullDef(output, false);
    }

    private void assertTextHasFullDef(ComponentDefinition output) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") var atTextLineZ =
                output.CONTENT.stream().filter(d -> d.Z == TEXT_Z).findFirst().get();
        assertInstanceOf(TextLineRenderableDefinition.class, atTextLineZ);
        var textLineDef = (TextLineRenderableDefinition) atTextLineZ;
        assertEquals(FONT_ID, textLineDef.FONT_ID);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.TEXT_PROVIDER);
        assertEquals(TEXT, extractStaticVal(textLineDef.TEXT_PROVIDER));
        assertSame(mockTextRenderingLoc, textLineDef.LOCATION_PROVIDER);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(TEXT_JUSTIFICATION, textLineDef.JUSTIFICATION);
        assertEquals(TEXT_GLYPH_PADDING, textLineDef.GLYPH_PADDING);
        assertEquals(textColorsDefault, textLineDef.colorProviderIndices);
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertEquals(listOf(ITALIC_INDEX_DEFAULT), textLineDef.italicIndices);
        assertEquals(listOf(BOLD_INDEX_DEFAULT), textLineDef.boldIndices);
    }

    @Test
    public void testReadFromRectDimensWithMinimalArgs() {
        var definition = definitionFromRectDimens();

        var output = reader.read(definition);

        assertNotNull(output);
        assertEquals(1, output.CONTENT.size());
        assertEquals(0, output.bindings.length);
        //noinspection OptionalGetWithoutIsPresent
        var content = output.CONTENT.stream().findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, content);
        var rectDef = (RectangleRenderableDefinition) content;
        assertSame(mockRectDimensProvider, rectDef.DIMENS_PROVIDER);
        assertSame(mockNullProvider, rectDef.topLeftColorProvider);
        assertSame(mockNullProvider, rectDef.topRightColorProvider);
        assertSame(mockNullProvider, rectDef.bottomLeftColorProvider);
        assertSame(mockNullProvider, rectDef.bottomRightColorProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        @SuppressWarnings("unchecked") var expectedOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                null,
                null,
                null,
                null,
                null,
                null

        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION_DATA_KEY,
                null,
                PRESS_SOUND_ID_DATA_KEY,
                null,
                MOUSE_OVER_SOUND_ID_DATA_KEY,
                null,
                MOUSE_LEAVE_SOUND_ID_DATA_KEY,
                null,
                RELEASE_SOUND_ID_DATA_KEY,
                null
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                new ButtonMethods.RenderableOptions(),
                new ButtonMethods.RenderableOptions(),
                output.data
        );

        verify(mockProviderDefReader, once()).read(any(), anyLong());
        verify(mockProviderDefReader, once()).read(mockRectDimensProviderDef, TIMESTAMP);
    }

    @Test
    public void testReadFromTextWithMinimalArgs() {
        when(mockTextLineRenderer.textLineLength(
                anyString(),
                any(),
                anyFloat(),
                anyList(),
                any(),
                anyFloat())
        ).thenReturn(TEXT_LINE_LENGTH_DEFAULT);
        //noinspection unchecked
        when(mockProviderDefReader.read(argThat(new FunctionalProviderDefMatcher(null, null)),
                anyLong())).thenReturn(mockRectDimensFuncProviderDefault);

        var definition = button(
                TEXT,
                FONT_ID,
                TEXT_HEIGHT,
                mockTextRenderingLocDef,
                Z
        );

        var output = reader.read(definition);

        assertNotNull(output);
        assertEquals(2, output.CONTENT.size());

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertSame(mockRectDimensFuncProviderDefault, rectDef.DIMENS_PROVIDER);
        assertSame(mockNullProvider, rectDef.topLeftColorProvider);
        assertSame(mockNullProvider, rectDef.topRightColorProvider);
        assertSame(mockNullProvider, rectDef.bottomLeftColorProvider);
        assertSame(mockNullProvider, rectDef.bottomRightColorProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atTextLineZ =
                output.CONTENT.stream().filter(d -> d.Z == TEXT_Z).findFirst().get();
        assertInstanceOf(TextLineRenderableDefinition.class, atTextLineZ);
        var textLineDef = (TextLineRenderableDefinition) atTextLineZ;
        assertEquals(FONT_ID, textLineDef.FONT_ID);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.TEXT_PROVIDER);
        assertEquals(TEXT, extractStaticVal(textLineDef.TEXT_PROVIDER));
        assertSame(mockTextRenderingLoc, textLineDef.LOCATION_PROVIDER);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(TextJustification.CENTER, textLineDef.JUSTIFICATION);
        assertEquals(0f, textLineDef.GLYPH_PADDING);
        assertTrue(textLineDef.colorProviderIndices.isEmpty());
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertNull(textLineDef.italicIndices);
        assertNull(textLineDef.boldIndices);

        @SuppressWarnings("unchecked") var expectedOptions = new ButtonMethods.RenderableOptions(
                mockRectDimensFuncProviderDefault,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                null,
                null,
                null,
                mapOf(),
                listOf(),
                listOf()

        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION_DATA_KEY,
                null,
                PRESS_SOUND_ID_DATA_KEY,
                null,
                MOUSE_OVER_SOUND_ID_DATA_KEY,
                null,
                MOUSE_LEAVE_SOUND_ID_DATA_KEY,
                null,
                RELEASE_SOUND_ID_DATA_KEY,
                null
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                new ButtonMethods.RenderableOptions(),
                new ButtonMethods.RenderableOptions(),
                output.data
        );

        verify(mockProviderDefReader, times(2)).read(any(), anyLong());
        verify(mockProviderDefReader, once()).read(mockTextRenderingLocDef, TIMESTAMP);
    }

    private void assertMapsEqualWithOptions(
            Map<String, Object> expected,
            ButtonMethods.RenderableOptions expectedDefaultOptions,
            ButtonMethods.RenderableOptions expectedHoverOptions,
            ButtonMethods.RenderableOptions expectedPressedOptions,
            Map<String, Object> actual
    ) {
        assertEquals(expected.size() + 3, actual.size());
        expected.forEach((key, value) -> assertEquals(value, actual.get(key)));
        assertOptionsEqual(expectedDefaultOptions,
                (ButtonMethods.RenderableOptions) actual.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY));
        assertOptionsEqual(expectedHoverOptions,
                (ButtonMethods.RenderableOptions) actual.get(HOVER_RENDERABLE_OPTIONS_DATA_KEY));
        assertOptionsEqual(expectedPressedOptions,
                (ButtonMethods.RenderableOptions) actual.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY));
    }

    private void assertOptionsEqual(
            ButtonMethods.RenderableOptions expected,
            ButtonMethods.RenderableOptions actual
    ) {
        assertSame(expected.rectDimens, actual.rectDimens);
        assertSame(expected.bgColorTopLeft, actual.bgColorTopLeft);
        assertSame(expected.bgColorTopRight, actual.bgColorTopRight);
        assertSame(expected.bgColorBottomRight, actual.bgColorBottomRight);
        assertSame(expected.bgColorBottomLeft, actual.bgColorBottomLeft);
        assertSame(expected.bgTexProvider, actual.bgTexProvider);
        assertEquals(expected.spriteId, actual.spriteId);
        assertSame(expected.spriteDimens, actual.spriteDimens);
        assertSame(expected.spriteShift, actual.spriteShift);
        assertEquals(expected.textColors, actual.textColors);
        assertEquals(expected.italics, actual.italics);
        assertEquals(expected.bolds, actual.bolds);
    }

    private ButtonDefinition definitionFromRectDimens() {
        return button(mockRectDimensProviderDef, Z);
    }

    private ButtonDefinition buttonDefinitionFromRectDimensAndDefsWithMaximalArgs() {
        return withMaximalArgsFromDefs(withText(definitionFromRectDimens()));
    }

    private ButtonDefinition buttonDefinitionFromTextAndDefsWithMaximalArgs() {
        return withMaximalArgsFromDefs(button(
                TEXT,
                FONT_ID,
                TEXT_HEIGHT,
                mockTextRenderingLocDef,
                Z
        ));
    }

    private ButtonDefinition withText(ButtonDefinition definition) {
        return definition
                .withText(
                        TEXT,
                        FONT_ID,
                        TEXT_HEIGHT
                )
                .withTextJustification(TEXT_JUSTIFICATION);
    }

    private ButtonDefinition withMaximalArgsFromDefs(ButtonDefinition definition) {
        return withMaximalDefaultArgs(definition)
                .withBgColorsHover(
                        mockBgColorTopLeftHoverDef,
                        mockBgColorTopRightHoverDef,
                        mockBgColorBottomLeftHoverDef,
                        mockBgColorBottomRightHoverDef
                )
                .withTextureHover(mockTexProviderHoverDef)
                .withSpriteHover(SPRITE_ID_HOVER, mockSpriteDimensHoverDef)
                .withSpriteColorShiftHover(mockSpriteShiftHoverDef)
                .withTextColorIndicesHover(textColorsHoverDefs)
                .withTextItalicIndicesHover(listOf(ITALIC_INDEX_HOVER))
                .withTextBoldIndicesHover(listOf(BOLD_INDEX_HOVER))
                .withBgColorsPressed(
                        mockBgColorTopLeftPressedDef,
                        mockBgColorTopRightPressedDef,
                        mockBgColorBottomLeftPressedDef,
                        mockBgColorBottomRightPressedDef
                )
                .withTexturePressed(mockTexProviderPressedDef)
                .withSpritePressed(SPRITE_ID_PRESSED, mockSpriteDimensPressedDef)
                .withSpriteColorShiftPressed(mockSpriteShiftPressedDef)
                .withTextColorIndicesPressed(textColorsPressedDefs)
                .withTextItalicIndicesPressed(listOf(ITALIC_INDEX_PRESSED))
                .withTextBoldIndicesPressed(listOf(BOLD_INDEX_PRESSED));
    }

    private ButtonDefinition withMaximalDefaultArgs(ButtonDefinition definition) {
        return definition
                .withKeys(KEY_BINDING_PRIORITY, arrayInts(KEY))
                .withTextPadding(TEXT_PADDING_VERT)
                .withGlyphPadding(TEXT_GLYPH_PADDING)
                .withBgColors(
                        mockBgColorTopLeftDefaultDef,
                        mockBgColorTopRightDefaultDef,
                        mockBgColorBottomLeftDefaultDef,
                        mockBgColorBottomRightDefaultDef
                )
                .withTexture(mockTexProviderDefaultDef)
                .withSprite(SPRITE_ID_DEFAULT, mockSpriteDimensDefaultDef)
                .withSpriteColorShift(mockSpriteShiftDefaultDef)
                .withTextColorIndices(textColorsDefaultDefs)
                .withTextItalicIndices(listOf(ITALIC_INDEX_DEFAULT))
                .withTextBoldIndices(listOf(BOLD_INDEX_DEFAULT))
                .onPress(PRESS_ACTION_ID)
                .withPressSound(PRESS_SOUND_ID)
                .withMouseOverSound(MOUSE_OVER_SOUND_ID)
                .withMouseLeaveSound(MOUSE_LEAVE_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID);
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
    }

    private <T> T extractStaticVal(AbstractProviderDefinition<T> provider) {
        return ((StaticProviderDefinition<T>) provider).VALUE;
    }

    // NB: This matcher doesn't verify whether the same type parameters were provided to
    // functionalProvider(), but if incorrect type parameters were provided in the
    // implementation, it would fail to compile, so this doesn't need testing
    @SuppressWarnings("rawtypes")
    private static class FunctionalProviderDefMatcher
            implements ArgumentMatcher<AbstractProviderDefinition> {
        private final String METHOD;
        private final Map<String, Object> DATA;

        private FunctionalProviderDefMatcher(String method, Map<String, Object> data) {
            METHOD = method;
            DATA = data;
        }

        @Override
        public boolean matches(AbstractProviderDefinition definition) {
            if (!(definition instanceof FunctionalProviderDefinition<?>)) {
                return false;
            }

            var functionalDef = (FunctionalProviderDefinition) definition;

            if (METHOD != null || DATA != null) {
                return Objects.equals(METHOD, functionalDef.PROVIDE_FUNCTION_ID) &&
                        Objects.equals(DATA, functionalDef.data);
            }
            else {
                return true;
            }
        }
    }
}
