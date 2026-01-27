package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.test.unit.components.ComponentDefinitionReaderTest;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ButtonDefinitionReaderTests extends ComponentDefinitionReaderTest {
    private static final String PRESS_MOUSE_METHOD = "Button_pressMouse";
    private static final String MOUSE_OVER_METHOD = "Button_mouseOver";
    private static final String MOUSE_LEAVE_METHOD = "Button_mouseLeave";
    private static final String PRESS_KEY_METHOD = "Button_pressKey";
    private static final String RELEASE_KEY_METHOD = "Button_releaseKey";

    private static final int RECT_Z = 0;
    private static final int SPRITE_Z = 1;
    private static final int TEXT_Z = 2;

    private final String PRESS_CONSUMER_ID = randomString();
    @SuppressWarnings("rawtypes") private final LookupAndEntitiesWithId<Consumer>
            MOCK_ACTION_AND_LOOKUP =
            generateMockLookupFunctionWithId(Consumer.class, PRESS_CONSUMER_ID);
    @SuppressWarnings("unchecked") private final Consumer<EventInputs> MOCK_PRESS_ACTION =
            MOCK_ACTION_AND_LOOKUP.entities.getFirst();
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> MOCK_GET_CONSUMER =
            MOCK_ACTION_AND_LOOKUP.lookup;

    private final float WIDTH_TO_HEIGHT_RATIO = randomFloat();

    private final int KEY = randomInt();
    private final int KEY_BINDING_PRIORITY = randomInt();
    private final String SPRITE_ID_DEFAULT = randomString();
    private final String SPRITE_ID_HOVER = randomString();
    private final String SPRITE_ID_PRESSED = randomString();
    private final String TEXT = randomString();
    private final HorizontalAlignment ALIGNMENT =
            HorizontalAlignment.fromValue(randomIntInRange(1, 3));
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

    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteUnadjDimensDefaultDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteUnadjDimensHoverDef;
    @Mock private AbstractProviderDefinition<FloatBox> mockSpriteUnadjDimensPressedDef;
    @Mock private ProviderAtTime<FloatBox> mockSpriteUnadjDimensDefault;
    @Mock private ProviderAtTime<FloatBox> mockSpriteUnadjDimensHover;
    @Mock private ProviderAtTime<FloatBox> mockSpriteUnadjDimensPressed;

    @Mock private ShiftDefinition mockSpriteShiftDefaultDef;
    @Mock private ShiftDefinition mockSpriteShiftHoverDef;
    @Mock private ShiftDefinition mockSpriteShiftPressedDef;

    @Mock private AbstractProviderDefinition<Vertex> mockTextRenderingLocDef;

    @Mock private AbstractProviderDefinition<Color> mockTextColorDefaultDef;
    @Mock private AbstractProviderDefinition<Color> mockTextColorHoverDef;
    @Mock private AbstractProviderDefinition<Color> mockTextColorPressedDef;

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

    @Mock private ProviderAtTime<FloatBox> mockSpriteAdjDimens;
    @Mock private ColorShift mockSpriteShiftDefault;
    @Mock private ColorShift mockSpriteShiftHover;
    @Mock private ColorShift mockSpriteShiftPressed;

    @Mock private ProviderAtTime<Vertex> mockTextUnadjLoc;
    @Mock private ProviderAtTime<Vertex> mockTextAdjLoc;

    @Mock private ProviderAtTime<Color> mockTextColorDefault;
    @Mock private ProviderAtTime<Color> mockTextColorHover;
    @Mock private ProviderAtTime<Color> mockTextColorPressed;

    @Mock private ProviderAtTime<FloatBox> mockRectDimensUnadjProviderDefault;
    @Mock private ProviderAtTime<FloatBox> mockRectDimensUnadjProviderHover;
    @Mock private ProviderAtTime<FloatBox> mockRectDimensUnadjProviderPressed;

    @SuppressWarnings("rawtypes") @Mock private ProviderAtTime mockNullProvider;
    @Mock private TextLineRenderer mockTextLineRenderer;
    @Mock private Function<String, Integer> mockGetTexId;
    @Mock private Supplier<Float> mockGetWidthToHeightRatio;

    private ButtonDefinition definitionFromRectDimens;

    private Map<Integer, AbstractProviderDefinition<Color>> textColorsDefaultDefs;
    private Map<Integer, AbstractProviderDefinition<Color>> textColorsHoverDefs;
    private Map<Integer, AbstractProviderDefinition<Color>> textColorsPressedDefs;
    private Map<Integer, ProviderAtTime<Color>> textColorsDefault;
    private Map<Integer, ProviderAtTime<Color>> textColorsHover;
    private Map<Integer, ProviderAtTime<Color>> textColorsPressed;

    private ButtonDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        definitionFromRectDimens = button(mockRectDimensProviderDef, Z);

        lenient().when(mockProviderDefReader.read(same(mockRectDimensProviderDef), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderDefault);

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
                .thenReturn(mockTextUnadjLoc);

        lenient().when(mockProviderDefReader.read(same(mockTextColorDefaultDef), anyLong()))
                .thenReturn(mockTextColorDefault);
        lenient().when(mockProviderDefReader.read(same(mockTextColorHoverDef), anyLong()))
                .thenReturn(mockTextColorHover);
        lenient().when(mockProviderDefReader.read(same(mockTextColorPressedDef), anyLong()))
                .thenReturn(mockTextColorPressed);

        lenient().when(mockProviderDefReader.read(same(mockSpriteUnadjDimensDefaultDef), anyLong()))
                .thenReturn(mockSpriteUnadjDimensDefault);
        lenient().when(mockProviderDefReader.read(same(mockSpriteUnadjDimensHoverDef), anyLong()))
                .thenReturn(mockSpriteUnadjDimensHover);
        lenient().when(mockProviderDefReader.read(same(mockSpriteUnadjDimensPressedDef), anyLong()))
                .thenReturn(mockSpriteUnadjDimensPressed);

        lenient().when(mockShiftReader.read(same(mockSpriteShiftDefaultDef), anyLong()))
                .thenReturn(mockSpriteShiftDefault);
        lenient().when(mockShiftReader.read(same(mockSpriteShiftHoverDef), anyLong()))
                .thenReturn(mockSpriteShiftHover);
        lenient().when(mockShiftReader.read(same(mockSpriteShiftPressedDef), anyLong()))
                .thenReturn(mockSpriteShiftPressed);

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

        lenient().when(mockProviderDefReader.read(
                        argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<Vertex>>(
                                Button_provideUnadjTextLocFromRect, mapOf(
                                Button_provideUnadjTextLocFromRect_horizontalAlignment,
                                ALIGNMENT,
                                Button_provideUnadjTextLocFromRect_paddingHoriz,
                                TEXT_PADDING_HORIZ,
                                Button_provideUnadjTextLocFromRect_textHeight,
                                TEXT_HEIGHT
                        ))), anyLong()))
                .thenReturn(mockTextUnadjLoc);

        lenient().when(mockGetWidthToHeightRatio.get()).thenReturn(WIDTH_TO_HEIGHT_RATIO);

        textColorsDefaultDefs = mapOf(COLOR_INDEX_DEFAULT, mockTextColorDefaultDef);
        textColorsHoverDefs = mapOf(COLOR_INDEX_HOVER, mockTextColorHoverDef);
        textColorsPressedDefs = mapOf(COLOR_INDEX_PRESSED, mockTextColorPressedDef);
        textColorsDefault = mapOf(COLOR_INDEX_DEFAULT, mockTextColorDefault);
        textColorsHover = mapOf(COLOR_INDEX_HOVER, mockTextColorHover);
        textColorsPressed = mapOf(COLOR_INDEX_PRESSED, mockTextColorPressed);

        reader =
                new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader, mockNullProvider,
                        mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(null, mockShiftReader, mockNullProvider,
                        mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, null, mockNullProvider,
                        mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader, null,
                        mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, null, MOCK_GET_CONSUMER, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockTextLineRenderer, null, MOCK_GET_FONT, mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockTextLineRenderer, MOCK_GET_CONSUMER, null,
                        mockGetTexId,
                        mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT,
                        null, mockGetWidthToHeightRatio));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonDefinitionReader(mockProviderDefReader, mockShiftReader,
                        mockNullProvider, mockTextLineRenderer, MOCK_GET_CONSUMER, MOCK_GET_FONT,
                        mockGetTexId, null));
    }

    @Test
    public void testReadFromRectDimensAndDefsWithMaximalArgs() {
        var definition = buttonDefinitionFromRectDimensAndDefsWithMaximalArgs();

        when(mockProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                        Button_provideTexTileWidth,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )
                )
        ), anyLong())).thenReturn(mockTexTileWidthProvider);
        when(mockProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                        Button_provideTexTileHeight,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )
                )
        ), anyLong())).thenReturn(mockTexTileHeightProvider);

        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_DEFAULT,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderDefault);
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_HOVER,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderHover);
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_PRESSED,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderPressed);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertNotNull(output.dimensionsProviderDef);
        @SuppressWarnings("rawtypes") var functionalDimensionsProviderDef =
                (FunctionalProviderDefinition) output.dimensionsProviderDef;
        assertEquals(Button_getDimens, functionalDimensionsProviderDef.PROVIDE_FUNCTION_ID);
        assertEquals(mapOf(COMPONENT_UUID, definition.UUID), functionalDimensionsProviderDef.data);
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_CONSUMER_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_CONSUMER_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertNull(rectDef.DIMENS_PROVIDER);
        assertNotNull(rectDef.DIMENS_PROVIDER_DEF);
        assertIsFunctionalProviderWithData(
                rectDef.DIMENS_PROVIDER_DEF,
                Button_rectDimensWithAdj,
                mapOf(COMPONENT_UUID, definitionFromRectDimens.UUID)
        );
        assertSame(mockBgColorTopLeftDefault, rectDef.topLeftColorProvider);
        assertSame(mockBgColorTopRightDefault, rectDef.topRightColorProvider);
        assertSame(mockBgColorBottomLeftDefault, rectDef.bottomLeftColorProvider);
        assertSame(mockBgColorBottomRightDefault, rectDef.bottomRightColorProvider);
        assertSame(mockTexTileWidthProvider, rectDef.textureTileWidthProvider);
        assertSame(mockTexTileHeightProvider, rectDef.textureTileHeightProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        assertSpriteHasFullDef(output, definition.UUID);

        assertTextHasFullDef(output);

        var expectedDefaultOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteUnadjDimensDefault,
                mockSpriteShiftDefault,
                mockTextUnadjLoc,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedHoverOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockBgColorTopLeftHover,
                mockBgColorTopRightHover,
                mockBgColorBottomLeftHover,
                mockBgColorBottomRightHover,
                mockTexProviderHover,
                SPRITE_ID_HOVER,
                mockSpriteUnadjDimensHover,
                mockSpriteShiftHover,
                mockTextUnadjLoc,
                textColorsHover,
                listInts(ITALIC_INDEX_HOVER),
                listInts(BOLD_INDEX_HOVER)
        );
        var expectedPressedOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockBgColorTopLeftPressed,
                mockBgColorTopRightPressed,
                mockBgColorBottomLeftPressed,
                mockBgColorBottomRightPressed,
                mockTexProviderPressed,
                SPRITE_ID_PRESSED,
                mockSpriteUnadjDimensPressed,
                mockSpriteShiftPressed,
                mockTextUnadjLoc,
                textColorsPressed,
                listInts(ITALIC_INDEX_PRESSED),
                listInts(BOLD_INDEX_PRESSED)
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION,
                MOCK_PRESS_ACTION,
                ButtonMethods.PRESS_SOUND_ID,
                PRESS_SOUND_ID,
                ButtonMethods.MOUSE_OVER_SOUND_ID,
                MOUSE_OVER_SOUND_ID,
                ButtonMethods.MOUSE_LEAVE_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID,
                ButtonMethods.RELEASE_SOUND_ID,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedDefaultOptions,
                expectedHoverOptions,
                expectedPressedOptions,
                output.data
        );

        verify(MOCK_GET_CONSUMER, once()).apply(PRESS_CONSUMER_ID);
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
                .thenReturn(mockTextUnadjLoc);

        var definition = buttonDefinitionFromTextAndDefsWithMaximalArgs();

        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                                Button_provideTexTileWidth,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))), anyLong())).
                thenReturn(mockTexTileWidthProvider);
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                                Button_provideTexTileHeight,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))), anyLong())).
                thenReturn(mockTexTileHeightProvider);

        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_DEFAULT,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderDefault);
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_HOVER,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderHover);
        lenient().when(mockProviderDefReader.read(argThat(
                        new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(
                                Button_provideUnadjRectDimensFromText,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                        mockTextUnadjLoc,
                                        Button_provideUnadjRectDimensFromText_lineLength,
                                        TEXT_LINE_LENGTH_PRESSED,
                                        Button_provideUnadjRectDimensFromText_textHeight,
                                        TEXT_HEIGHT,
                                        Button_provideUnadjRectDimensFromText_textPaddingVert,
                                        TEXT_PADDING_VERT,
                                        Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                        TEXT_PADDING_HORIZ
                                ))), anyLong()))
                .thenReturn(mockRectDimensUnadjProviderPressed);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_CONSUMER_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_CONSUMER_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertNull(rectDef.DIMENS_PROVIDER);
        assertNotNull(rectDef.DIMENS_PROVIDER_DEF);
        assertIsFunctionalProviderWithData(
                rectDef.DIMENS_PROVIDER_DEF,
                Button_rectDimensWithAdj,
                mapOf(COMPONENT_UUID, definition.UUID)
        );
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

        assertSpriteHasFullDef(output, definition.UUID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atTextLineZ =
                output.CONTENT.stream().filter(d -> d.Z == TEXT_Z).findFirst().get();
        assertInstanceOf(TextLineRenderableDefinition.class, atTextLineZ);
        var textLineDef = (TextLineRenderableDefinition) atTextLineZ;
        assertEquals(FONT_ID, textLineDef.FONT_ID);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.TEXT_PROVIDER);
        assertEquals(TEXT, extractStaticVal(textLineDef.TEXT_PROVIDER));
        assertIsFunctionalProviderWithData(
                textLineDef.LOCATION_PROVIDER_DEF,
                Button_textLocWithAdj,
                mapOf(COMPONENT_UUID, definition.UUID)
        );
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(HorizontalAlignment.CENTER, textLineDef.ALIGNMENT);
        assertEquals(TEXT_GLYPH_PADDING, textLineDef.GLYPH_PADDING);
        assertEquals(textColorsDefault, textLineDef.colorProviderIndices);
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertEquals(listOf(ITALIC_INDEX_DEFAULT), textLineDef.italicIndices);
        assertEquals(listOf(BOLD_INDEX_DEFAULT), textLineDef.boldIndices);

        var expectedDefaultOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteUnadjDimensDefault,
                mockSpriteShiftDefault,
                mockTextUnadjLoc,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedHoverOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderHover,
                mockBgColorTopLeftHover,
                mockBgColorTopRightHover,
                mockBgColorBottomLeftHover,
                mockBgColorBottomRightHover,
                mockTexProviderHover,
                SPRITE_ID_HOVER,
                mockSpriteUnadjDimensHover,
                mockSpriteShiftHover,
                mockTextUnadjLoc,
                textColorsHover,
                listInts(ITALIC_INDEX_HOVER),
                listInts(BOLD_INDEX_HOVER)
        );
        var expectedPressedOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderPressed,
                mockBgColorTopLeftPressed,
                mockBgColorTopRightPressed,
                mockBgColorBottomLeftPressed,
                mockBgColorBottomRightPressed,
                mockTexProviderPressed,
                SPRITE_ID_PRESSED,
                mockSpriteUnadjDimensPressed,
                mockSpriteShiftPressed,
                mockTextUnadjLoc,
                textColorsPressed,
                listInts(ITALIC_INDEX_PRESSED),
                listInts(BOLD_INDEX_PRESSED)
        );

        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION,
                MOCK_PRESS_ACTION,
                ButtonMethods.PRESS_SOUND_ID,
                PRESS_SOUND_ID,
                ButtonMethods.MOUSE_OVER_SOUND_ID,
                MOUSE_OVER_SOUND_ID,
                ButtonMethods.MOUSE_LEAVE_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID,
                ButtonMethods.RELEASE_SOUND_ID,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedDefaultOptions,
                expectedHoverOptions,
                expectedPressedOptions,
                output.data
        );

        verify(MOCK_GET_CONSUMER, once()).apply(PRESS_CONSUMER_ID);
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
        var definition = withMaximalDefaultArgs(withText(definitionFromRectDimens));

        when(mockProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                        Button_provideTexTileWidth,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )
                )
        ), anyLong())).thenReturn(mockTexTileWidthProvider);
        when(mockProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<AbstractProviderDefinition<Float>>(
                        Button_provideTexTileHeight,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )
                )
        ), anyLong())).thenReturn(mockTexTileHeightProvider);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(Z, output.Z);
        assertEquals(3, output.CONTENT.size());
        assertEquals(1, output.bindings.length);
        var binding = output.bindings[0];
        assertArrayEquals(arrayInts(KEY), binding.KEY_CODEPOINTS);
        assertEquals(KEY_BINDING_PRIORITY, output.keyBindingPriority);
        assertEquals(PRESS_KEY_METHOD, binding.PRESS_CONSUMER_ID);
        assertEquals(RELEASE_KEY_METHOD, binding.RELEASE_CONSUMER_ID);

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertNull(rectDef.DIMENS_PROVIDER);
        assertNotNull(rectDef.DIMENS_PROVIDER_DEF);
        assertIsFunctionalProviderWithData(
                rectDef.DIMENS_PROVIDER_DEF,
                Button_rectDimensWithAdj,
                mapOf(COMPONENT_UUID, definitionFromRectDimens.UUID)
        );
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

        assertSpriteHasFullDef(output, definition.UUID, true);

        assertTextHasFullDef(output);

        var expectedOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockBgColorTopLeftDefault,
                mockBgColorTopRightDefault,
                mockBgColorBottomLeftDefault,
                mockBgColorBottomRightDefault,
                mockTexProviderDefault,
                SPRITE_ID_DEFAULT,
                mockSpriteUnadjDimensDefault,
                mockSpriteShiftDefault,
                mockTextUnadjLoc,
                textColorsDefault,
                listInts(ITALIC_INDEX_DEFAULT),
                listInts(BOLD_INDEX_DEFAULT)
        );
        var expectedOptionsHoverAndPressed = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                null,
                null,
                null,
                null,
                null,
                null,
                mockSpriteUnadjDimensDefault,
                null,
                mockTextUnadjLoc,
                null,
                null,
                null
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION,
                MOCK_PRESS_ACTION,
                ButtonMethods.PRESS_SOUND_ID,
                PRESS_SOUND_ID,
                ButtonMethods.MOUSE_OVER_SOUND_ID,
                MOUSE_OVER_SOUND_ID,
                ButtonMethods.MOUSE_LEAVE_SOUND_ID,
                MOUSE_LEAVE_SOUND_ID,
                ButtonMethods.RELEASE_SOUND_ID,
                RELEASE_SOUND_ID
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                expectedOptionsHoverAndPressed,
                expectedOptionsHoverAndPressed,
                output.data
        );

        verify(MOCK_GET_CONSUMER, once()).apply(PRESS_CONSUMER_ID);
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
        var definition = button(SPRITE_ID_DEFAULT, mockSpriteUnadjDimensDefaultDef, Z);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(1, output.CONTENT.size());
        assertSpriteHasBasicDef(output, definition.UUID);
    }

    private void assertSpriteHasFullDef(ComponentDefinition output,
                                        UUID expectedButtonUuid,
                                        boolean justDefault) {
        var spriteDef = assertSpriteHasBasicDef(output, expectedButtonUuid);
        assertArrayEquals(arrayOf(mockSpriteShiftDefault), spriteDef.colorShifts);
        verify(mockShiftReader, once()).read(mockSpriteShiftDefaultDef, TIMESTAMP);
        verify(mockShiftReader, times(justDefault ? 1 : 3)).read(any(), anyLong());
        if (!justDefault) {
            verify(mockShiftReader, once()).read(mockSpriteShiftHoverDef, TIMESTAMP);
            verify(mockShiftReader, once()).read(mockSpriteShiftPressedDef, TIMESTAMP);
        }
    }

    private SpriteRenderableDefinition assertSpriteHasBasicDef(ComponentDefinition output,
                                                               UUID expectedButtonUuid) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") var atSpriteZ =
                output.CONTENT.stream().filter(d -> d.Z == SPRITE_Z).findFirst().get();
        assertInstanceOf(SpriteRenderableDefinition.class, atSpriteZ);
        var spriteDef = (SpriteRenderableDefinition) atSpriteZ;
        assertEquals(SPRITE_ID_DEFAULT, spriteDef.SPRITE_ID);
        assertInstanceOf(FunctionalProviderDefinition.class, spriteDef.DIMENSIONS_PROVIDER_DEF);
        var spriteAdjDimensDef =
                (FunctionalProviderDefinition<FloatBox>) spriteDef.DIMENSIONS_PROVIDER_DEF;
        assertEquals(Button_spriteDimensWithAdj, spriteAdjDimensDef.PROVIDE_FUNCTION_ID);
        assertEquals(mapOf(COMPONENT_UUID, expectedButtonUuid), spriteAdjDimensDef.data);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), spriteDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, spriteDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, spriteDef.onMouseLeaveId);
        return spriteDef;
    }

    private void assertSpriteHasFullDef(ComponentDefinition output, UUID expectedButtonUuid) {
        assertSpriteHasFullDef(output, expectedButtonUuid, false);
    }

    private void assertTextHasFullDef(ComponentDefinition output) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") var atTextLineZ =
                output.CONTENT.stream().filter(d -> d.Z == TEXT_Z).findFirst().get();
        assertInstanceOf(TextLineRenderableDefinition.class, atTextLineZ);
        var textLineDef = (TextLineRenderableDefinition) atTextLineZ;
        assertEquals(FONT_ID, textLineDef.FONT_ID);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.TEXT_PROVIDER);
        assertEquals(TEXT, extractStaticVal(textLineDef.TEXT_PROVIDER));
        assertInstanceOf(FunctionalProviderDefinition.class, textLineDef.LOCATION_PROVIDER_DEF);
        var textLineLocDef =
                (FunctionalProviderDefinition<Vertex>) textLineDef.LOCATION_PROVIDER_DEF;
        assertEquals(Button_textLocWithAdj, textLineLocDef.PROVIDE_FUNCTION_ID);
        assertEquals(mapOf(COMPONENT_UUID, definitionFromRectDimens.UUID), textLineLocDef.data);
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(ALIGNMENT, textLineDef.ALIGNMENT);
        assertEquals(TEXT_GLYPH_PADDING, textLineDef.GLYPH_PADDING);
        assertEquals(textColorsDefault, textLineDef.colorProviderIndices);
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertEquals(listOf(ITALIC_INDEX_DEFAULT), textLineDef.italicIndices);
        assertEquals(listOf(BOLD_INDEX_DEFAULT), textLineDef.boldIndices);
    }

    @Test
    public void testReadFromRectDimensWithMinimalArgs() {
        var definition = definitionFromRectDimens;

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(1, output.CONTENT.size());
        assertEquals(0, output.bindings.length);
        //noinspection OptionalGetWithoutIsPresent
        var content = output.CONTENT.stream().findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, content);
        var rectDef = (RectangleRenderableDefinition) content;
        assertNull(rectDef.DIMENS_PROVIDER);
        assertNotNull(rectDef.DIMENS_PROVIDER_DEF);
        assertIsFunctionalProviderWithData(
                rectDef.DIMENS_PROVIDER_DEF,
                Button_rectDimensWithAdj,
                mapOf(COMPONENT_UUID, definition.UUID)
        );
        assertSame(mockNullProvider, rectDef.topLeftColorProvider);
        assertSame(mockNullProvider, rectDef.topRightColorProvider);
        assertSame(mockNullProvider, rectDef.bottomLeftColorProvider);
        assertSame(mockNullProvider, rectDef.bottomRightColorProvider);
        assertEquals(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD), rectDef.onPressIds);
        assertEquals(MOUSE_OVER_METHOD, rectDef.onMouseOverId);
        assertEquals(MOUSE_LEAVE_METHOD, rectDef.onMouseLeaveId);

        @SuppressWarnings("unchecked") var expectedOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
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
                null,
                null

        );
        var expectedOptionsHoverAndPressed = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION,
                null,
                ButtonMethods.PRESS_SOUND_ID,
                null,
                ButtonMethods.MOUSE_OVER_SOUND_ID,
                null,
                ButtonMethods.MOUSE_LEAVE_SOUND_ID,
                null,
                ButtonMethods.RELEASE_SOUND_ID,
                null
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                expectedOptionsHoverAndPressed,
                expectedOptionsHoverAndPressed,
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
        when(mockProviderDefReader.read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition<FloatBox>>(null,
                        null)),
                anyLong())).thenReturn(mockRectDimensUnadjProviderDefault);

        var definition = button(
                TEXT,
                FONT_ID,
                TEXT_HEIGHT,
                mockTextRenderingLocDef,
                Z
        );

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(definition.UUID, output.UUID);
        assertEquals(2, output.CONTENT.size());

        @SuppressWarnings("OptionalGetWithoutIsPresent") var atRectZ =
                output.CONTENT.stream().filter(d -> d.Z == RECT_Z).findFirst().get();
        assertInstanceOf(RectangleRenderableDefinition.class, atRectZ);
        var rectDef = (RectangleRenderableDefinition) atRectZ;
        assertNull(rectDef.DIMENS_PROVIDER);
        assertNotNull(rectDef.DIMENS_PROVIDER_DEF);
        assertIsFunctionalProviderWithData(
                rectDef.DIMENS_PROVIDER_DEF,
                Button_rectDimensWithAdj,
                mapOf(COMPONENT_UUID, definition.UUID)
        );
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
        assertIsFunctionalProviderWithData(
                textLineDef.LOCATION_PROVIDER_DEF,
                Button_textLocWithAdj,
                mapOf(COMPONENT_UUID, definition.UUID)
        );
        assertInstanceOf(StaticProviderDefinition.class, textLineDef.HEIGHT_PROVIDER);
        assertEquals(TEXT_HEIGHT, extractStaticVal(textLineDef.HEIGHT_PROVIDER));
        assertEquals(HorizontalAlignment.CENTER, textLineDef.ALIGNMENT);
        assertEquals(0f, textLineDef.GLYPH_PADDING);
        assertTrue(textLineDef.colorProviderIndices.isEmpty());
        assertNull(textLineDef.colorProviderIndicesDefs);
        assertNull(textLineDef.italicIndices);
        assertNull(textLineDef.boldIndices);

        @SuppressWarnings("unchecked") var expectedOptions = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                mockNullProvider,
                null,
                null,
                null,
                mockTextUnadjLoc,
                mapOf(),
                listOf(),
                listOf()

        );
        var expectedOptionsHoverAndPressed = new ButtonMethods.Options(
                mockRectDimensUnadjProviderDefault,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mockTextUnadjLoc,
                null,
                null,
                null
        );
        var expectedData = Collections.<String, Object>mapOf(
                PRESS_ACTION,
                null,
                PRESS_SOUND_ID,
                null,
                MOUSE_OVER_SOUND_ID,
                null,
                MOUSE_LEAVE_SOUND_ID,
                null,
                RELEASE_SOUND_ID,
                null
        );
        assertMapsEqualWithOptions(
                expectedData,
                expectedOptions,
                expectedOptionsHoverAndPressed,
                expectedOptionsHoverAndPressed,
                output.data
        );

        verify(mockProviderDefReader, times(4)).read(any(), anyLong());
        verify(mockProviderDefReader, once()).read(mockTextRenderingLocDef, TIMESTAMP);
    }

    private void assertMapsEqualWithOptions(
            Map<String, Object> expected,
            ButtonMethods.Options expectedDefaultOptions,
            ButtonMethods.Options expectedHoverOptions,
            ButtonMethods.Options expectedPressedOptions,
            Map<String, Object> actual
    ) {
        assertEquals(expected.size() + 3, actual.size());
        expected.forEach((key, value) -> assertEquals(value, actual.get(key)));
        assertOptionsEqual(expectedDefaultOptions,
                (ButtonMethods.Options) actual.get(RENDERABLE_OPTIONS_DEFAULT));
        assertOptionsEqual(expectedHoverOptions,
                (ButtonMethods.Options) actual.get(RENDERABLE_OPTIONS_HOVER));
        assertOptionsEqual(expectedPressedOptions,
                (ButtonMethods.Options) actual.get(RENDERABLE_OPTIONS_PRESSED));
    }

    private void assertOptionsEqual(
            ButtonMethods.Options expected,
            ButtonMethods.Options actual
    ) {
        assertSame(expected.unadjRectDimens, actual.unadjRectDimens);
        assertSame(expected.bgColorTopLeft, actual.bgColorTopLeft);
        assertSame(expected.bgColorTopRight, actual.bgColorTopRight);
        assertSame(expected.bgColorBottomRight, actual.bgColorBottomRight);
        assertSame(expected.bgColorBottomLeft, actual.bgColorBottomLeft);
        assertSame(expected.bgTexProvider, actual.bgTexProvider);
        assertEquals(expected.spriteId, actual.spriteId);
        assertSame(expected.unadjSpriteDimens, actual.unadjSpriteDimens);
        assertSame(expected.spriteShift, actual.spriteShift);
        assertSame(expected.unadjTextLoc, actual.unadjTextLoc);
        assertEquals(expected.textColors, actual.textColors);
        assertEquals(expected.italics, actual.italics);
        assertEquals(expected.bolds, actual.bolds);
    }

    private ButtonDefinition buttonDefinitionFromRectDimensAndDefsWithMaximalArgs() {
        return withMaximalArgsFromDefs(withText(definitionFromRectDimens));
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
                .withHorizontalAlignment(ALIGNMENT);
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
                .withSpriteHover(SPRITE_ID_HOVER, mockSpriteUnadjDimensHoverDef)
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
                .withSpritePressed(SPRITE_ID_PRESSED, mockSpriteUnadjDimensPressedDef)
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
                .withSprite(SPRITE_ID_DEFAULT, mockSpriteUnadjDimensDefaultDef)
                .withSpriteColorShift(mockSpriteShiftDefaultDef)
                .withTextColorIndices(textColorsDefaultDefs)
                .withTextItalicIndices(listOf(ITALIC_INDEX_DEFAULT))
                .withTextBoldIndices(listOf(BOLD_INDEX_DEFAULT))
                .onPress(PRESS_CONSUMER_ID)
                .withPressSound(PRESS_SOUND_ID)
                .withMouseOverSound(MOUSE_OVER_SOUND_ID)
                .withMouseLeaveSound(MOUSE_LEAVE_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID);
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
    }
}
