package inaugural.soliloquy.ui.components.button;

import org.apache.commons.lang3.function.TriConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.mouse.MouseEventHandler;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;
import static soliloquy.specs.io.input.mouse.MouseEventHandler.EventType.RELEASE;
import static soliloquy.specs.ui.EventInputs.eventInputs;

@ExtendWith(MockitoExtension.class)
public class ButtonMethodsTests {
    private final static String PRESS_STATE_DATA_KEY = "isPressed";
    private final static String PRESSED_KEY_DATA_KEY = "pressedKey";
    private final static String RECT_HOVER_STATE_DATA_KEY = "isHoveringRect";
    private final static String SPRITE_HOVER_STATE_DATA_KEY = "isHoveringSprite";

    private final static String PRESS_ACTION_DATA_KEY = "pressAction";
    private final static String PRESS_SOUND_ID_DATA_KEY = "pressSoundId";
    private final static String MOUSE_OVER_SOUND_ID_DATA_KEY = "mouseOverSoundId";
    private final static String MOUSE_LEAVE_SOUND_ID_DATA_KEY = "mouseLeaveSoundId";
    private final static String RELEASE_SOUND_ID_DATA_KEY = "releaseSoundId";

    private final static String DEFAULT_RENDERABLE_OPTIONS_DATA_KEY = "defaultRenderableOptions";
    private final static String HOVER_RENDERABLE_OPTIONS_DATA_KEY = "hoverRenderableOptions";
    private final static String PRESSED_RENDERABLE_OPTIONS_DATA_KEY = "pressedRenderableOptions";

    private final int MOUSE_BUTTON = randomInt();
    private final String PRESS_SOUND_ID = randomString();
    private final String MOUSE_OVER_SOUND_ID = randomString();
    private final String MOUSE_LEAVE_SOUND_ID = randomString();
    private final String RELEASE_SOUND_ID = randomString();

    private final String SPRITE_ID_DEFAULT = randomString();
    private final String SPRITE_ID_HOVER = randomString();
    private final String SPRITE_ID_PRESSED = randomString();
    private final LookupAndEntitiesWithId<Sprite> MOCK_SPRITES_AND_LOOKUP =
            generateMockLookupFunctionWithId(Sprite.class, SPRITE_ID_DEFAULT, SPRITE_ID_HOVER,
                    SPRITE_ID_PRESSED);
    private final Sprite MOCK_SPRITE_DEFAULT = MOCK_SPRITES_AND_LOOKUP.entities.getFirst();
    private final Sprite MOCK_SPRITE_HOVER = MOCK_SPRITES_AND_LOOKUP.entities.get(1);
    private final Sprite MOCK_SPRITE_PRESSED = MOCK_SPRITES_AND_LOOKUP.entities.get(2);
    private final Function<String, Sprite> MOCK_GET_SPRITE = MOCK_SPRITES_AND_LOOKUP.lookup;

    private final FloatBox RECT_DIMENS = randomFloatBox();
    private final float PADDING_VERT = randomFloat();
    private final float PADDING_HORIZ = randomFloat();
    private final float LINE_LENGTH = randomFloat();
    private final float TEXT_HEIGHT = randomFloat();
    private final float TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS =
            (RECT_DIMENS.TOP_Y + RECT_DIMENS.BOTTOM_Y - TEXT_HEIGHT) / 2f;
    private final long TIMESTAMP = randomLong();

    @Mock private ProviderAtTime<FloatBox> mockRectDimens;
    @Mock private ProviderAtTime<Color> mockBgTopLeft;
    @Mock private ProviderAtTime<Color> mockBgTopRight;
    @Mock private ProviderAtTime<Color> mockBgBottomRight;
    @Mock private ProviderAtTime<Color> mockBgBottomLeft;
    @Mock private ProviderAtTime<Integer> mockBgTexProvider;
    @Mock private ProviderAtTime<FloatBox> mockSpriteDimens;
    @Mock private ColorShift mockSpriteShift;
    @Mock private ProviderAtTime<Vertex> mockTextRenderingLoc;
    @Mock private Map<Integer, ProviderAtTime<Color>> mockTextColors;
    @Mock private List<Integer> mockItalics;
    @Mock private List<Integer> mockBolds;

    @Mock private Consumer<String> mockPlaySound;
    @Mock private TriConsumer<Integer, MouseEventHandler.EventType, Runnable>
            mockSubscribeToMouseEvents;

    @SuppressWarnings("rawtypes") @Mock private Action mockPressAction;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private List<ColorShift> mockSpriteShifts;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockComponent;
    @Mock private RenderableWithMouseEvents mockRenderable;

    private Map<String, Object> mockData;

    private ButtonMethods buttonMethods;

    @BeforeEach
    public void setUp() {
        mockData = generateMockMap(
                pairOf(PRESS_ACTION_DATA_KEY, mockPressAction),
                pairOf(PRESS_SOUND_ID_DATA_KEY, PRESS_SOUND_ID),
                pairOf(MOUSE_OVER_SOUND_ID_DATA_KEY, MOUSE_OVER_SOUND_ID),
                pairOf(MOUSE_LEAVE_SOUND_ID_DATA_KEY, MOUSE_LEAVE_SOUND_ID),
                pairOf(RELEASE_SOUND_ID_DATA_KEY, RELEASE_SOUND_ID),
                pairOf(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY, options(SPRITE_ID_DEFAULT)),
                pairOf(HOVER_RENDERABLE_OPTIONS_DATA_KEY, new ButtonMethods.RenderableOptions()),
                pairOf(PRESSED_RENDERABLE_OPTIONS_DATA_KEY, new ButtonMethods.RenderableOptions())
        );

        lenient().when(mockRectDimens.provide(anyLong())).thenReturn(RECT_DIMENS);
        lenient().when(mockRectangleRenderable.getZ()).thenReturn(RECT_Z);
        lenient().when(mockSpriteRenderable.colorShifts()).thenReturn(mockSpriteShifts);
        lenient().when(mockSpriteRenderable.getZ()).thenReturn(SPRITE_Z);
        lenient().when(mockTextLineRenderable.colorProviderIndices()).thenReturn(mockTextColors);
        lenient().when(mockTextLineRenderable.italicIndices()).thenReturn(mockItalics);
        lenient().when(mockTextLineRenderable.boldIndices()).thenReturn(mockBolds);
        lenient().when(mockTextLineRenderable.getZ()).thenReturn(TEXT_Z);
        lenient().when(mockComponent.contentsRepresentation()).thenReturn(setOf(
                mockRectangleRenderable,
                mockSpriteRenderable,
                mockTextLineRenderable
        ));
        lenient().when(mockComponent.data()).thenReturn(mockData);
        lenient().when(mockRenderable.containingComponent()).thenReturn(mockComponent);

        buttonMethods =
                new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, MOCK_GET_SPRITE);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(null, mockSubscribeToMouseEvents, MOCK_GET_SPRITE));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, null, MOCK_GET_SPRITE));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, null));
    }

    @Test
    public void testMousePressOnRectAndReleaseOnRect() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockSubscribeToMouseEvents,
                mockPressAction);
        inOrder.verify(mockComponent, once()).data();
        inOrder.verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, true);
        inOrder.verify(mockData, once()).put(PRESSED_KEY_DATA_KEY, null);
        inOrder.verify(mockData, once()).get(PRESS_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(PRESS_SOUND_ID);
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        inOrder.verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        inOrder.verify(mockData, once()).get(RECT_HOVER_STATE_DATA_KEY);
        inOrder.verify(mockData, never()).get(SPRITE_HOVER_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
        inOrder.verify(mockData, once()).put(PRESSED_KEY_DATA_KEY, null);
        inOrder.verify(mockData, once()).get(RELEASE_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockData, once()).get(PRESS_ACTION_DATA_KEY);
        //noinspection unchecked
        inOrder.verify(mockPressAction, once()).accept(null);
    }

    @Test
    public void testMousePressOnRectAndReleaseOnSprite() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(false);
        when(mockData.get(SPRITE_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockData, once()).get(RECT_HOVER_STATE_DATA_KEY);
        verify(mockData, once()).get(SPRITE_HOVER_STATE_DATA_KEY);
        verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
    }

    @Test
    public void testMousePressOnSpriteAndReleaseOnRect() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);
        // the check technically won't be made, but this makes the test more explicit
        lenient().when(mockData.get(SPRITE_HOVER_STATE_DATA_KEY)).thenReturn(false);

        runnableCaptor.getValue().run();

        verify(mockData, once()).get(RECT_HOVER_STATE_DATA_KEY);
        verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
    }

    @Test
    public void testMousePressOnSpriteAndReleaseOnSprite() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockData.get(SPRITE_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockData, once()).get(RECT_HOVER_STATE_DATA_KEY);
        verify(mockData, once()).get(SPRITE_HOVER_STATE_DATA_KEY);
        verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
    }

    @Test
    public void testPressMouse_ButtonWhenButtonAlreadyPressed() {
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockData, once()).get(anyString());
        verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressMouse_ButtonWhenNoPressSoundId() {
        when(mockData.get(PRESS_SOUND_ID_DATA_KEY)).thenReturn(null);

        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressMouse_ButtonReleaseWhenNotHovering() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(null);

        runnableCaptor.getValue().run();

        //noinspection unchecked
        verify(mockPressAction, never()).accept(any());
    }

    @Test
    public void testPressMouse_ButtonWhenNoReleaseSoundId() {
        when(mockData.get(RELEASE_SOUND_ID_DATA_KEY)).thenReturn(null);

        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockPlaySound, never()).accept(eq(RELEASE_SOUND_ID));
    }

    @Test
    public void testPressMouse_ButtonWhenNoPressAction() {
        when(mockData.get(PRESS_ACTION_DATA_KEY)).thenReturn(null);

        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        //noinspection unchecked
        verify(mockPressAction, never()).accept(any());
    }

    @Test
    public void testMouseOver_ButtonOnRectSetsHoverState() {
        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(RECT_HOVER_STATE_DATA_KEY, true);
    }

    @Test
    public void testMouseOver_ButtonOnSpriteSetsHoverState() {
        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(SPRITE_HOVER_STATE_DATA_KEY, true);
    }

    @Test
    public void testMouseLeave_ButtonRectSetsHoverState() {
        buttonMethods.mouseLeave_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(RECT_HOVER_STATE_DATA_KEY, false);
    }

    @Test
    public void testMouseLeave_ButtonSpriteSetsHoverState() {
        buttonMethods.mouseLeave_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(SPRITE_HOVER_STATE_DATA_KEY, false);
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToHoverWhenNotAlreadyHovering() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.RenderableOptions());
        when(mockData.get(HOVER_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_HOVER));
        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyHoverRenderableOptionsSet();
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToPressedWhenNotAlreadyHoveringAndButtonIsPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.RenderableOptions());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testMouseOverDoesNotSetRenderableOptionsWhenAlreadyHovering() {
        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testPressMouseButtonSetsRenderableOptionsToPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.RenderableOptions());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testPressKeyButtonSetsRenderableOptionsToPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.RenderableOptions());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.pressKey_Button(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveSetsRenderableOptionsToDefaultWhenAlreadyHoveringIfNewHoverStateIsNotHovering() {
        when(mockData.get(RECT_HOVER_STATE_DATA_KEY))
                .thenReturn(true)
                .thenReturn(false);

        buttonMethods.mouseLeave_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveDoesNotSetRenderableOptionsIfWasNotHoveringAlready() {
        when(mockData.get(RECT_HOVER_STATE_DATA_KEY))
                .thenReturn(false)
                .thenReturn(false);

        buttonMethods.mouseLeave_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveDoesNotSetRenderableOptionsIfItIsStillHovering() {
        when(mockData.get(RECT_HOVER_STATE_DATA_KEY))
                .thenReturn(true)
                .thenReturn(true);

        buttonMethods.mouseLeave_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testReleaseMouseButtonSetsRenderableOptionsToDefault() {
        buttonMethods.pressMouse_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        runnableCaptor.getValue().run();

        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testSetHoverRenderableOptionsSetToDefaultWhenNull() {
        when(mockData.get(HOVER_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.RenderableOptions());
        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockData, atLeastOnce()).get(HOVER_RENDERABLE_OPTIONS_DATA_KEY);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testSetPressedRenderableOptionsSetToDefaultWhenNull() {
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockData, atLeastOnce()).get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testRenderablesAtUnspecifiedZValuesDoNotReceiveRenderableOptions() {
        // just gotta make sure it isn't RECT_Z, SPRITE_Z, or TEXT_Z, respectively
        when(mockRectangleRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));
        when(mockSpriteRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));
        when(mockTextLineRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));

        buttonMethods.mouseOver_Button(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockRectangleRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockSpriteRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockTextLineRenderable, never()).setRenderingLocationProvider(any());
    }

    private ButtonMethods.RenderableOptions options(String spriteId) {
        return new ButtonMethods.RenderableOptions(mockRectDimens,
                mockBgTopLeft,
                mockBgTopRight,
                mockBgBottomLeft,
                mockBgBottomRight,
                mockBgTexProvider,
                spriteId,
                mockSpriteDimens,
                mockSpriteShift,
                mockTextRenderingLoc,
                mockTextColors,
                mockItalics,
                mockBolds);
    }

    private void verifyDefaultRenderableOptionsSet() {
        verify(mockData, atLeastOnce()).get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY);
        verify(MOCK_GET_SPRITE, atLeastOnce()).apply(SPRITE_ID_DEFAULT);
        verify(mockSpriteRenderable, atLeastOnce()).setSprite(MOCK_SPRITE_DEFAULT);
        verifyRenderableOptionsSet();
    }

    private void verifyHoverRenderableOptionsSet() {
        verify(mockData, once()).get(HOVER_RENDERABLE_OPTIONS_DATA_KEY);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID_HOVER);
        verify(mockSpriteRenderable, once()).setSprite(MOCK_SPRITE_HOVER);
        verifyRenderableOptionsSet();
    }

    private void verifyPressedRenderableOptionsSet() {
        verify(mockData, once()).get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID_PRESSED);
        verify(mockSpriteRenderable, once()).setSprite(MOCK_SPRITE_PRESSED);
        verifyRenderableOptionsSet();
    }

    private void verifyRenderableOptionsSet() {
        verify(mockRectangleRenderable, atLeastOnce()).setRenderingDimensionsProvider(mockRectDimens);
        verify(mockRectangleRenderable, atLeastOnce()).setTopLeftColorProvider(mockBgTopLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setTopRightColorProvider(mockBgTopRight);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomLeftColorProvider(mockBgBottomLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomRightColorProvider(mockBgBottomRight);
        verify(mockRectangleRenderable, atLeastOnce()).setTextureIdProvider(mockBgTexProvider);
        verify(mockSpriteRenderable, atLeastOnce()).setRenderingDimensionsProvider(mockSpriteDimens);
        verify(mockSpriteRenderable, atLeastOnce()).colorShifts();
        verify(mockSpriteShifts, atLeastOnce()).clear();
        verify(mockSpriteShifts, atLeastOnce()).add(mockSpriteShift);
        verify(mockTextLineRenderable, atLeastOnce()).setRenderingLocationProvider(mockTextRenderingLoc);
        verify(mockTextLineRenderable, atLeastOnce()).colorProviderIndices();
        verify(mockTextColors, atLeastOnce()).clear();
        verify(mockTextColors, atLeastOnce()).putAll(mockTextColors);
        verify(mockTextLineRenderable, atLeastOnce()).italicIndices();
        verify(mockItalics, atLeastOnce()).clear();
        verify(mockItalics, atLeastOnce()).addAll(mockItalics);
        verify(mockTextLineRenderable, atLeastOnce()).boldIndices();
        verify(mockBolds, atLeastOnce()).clear();
        verify(mockBolds, atLeastOnce()).addAll(mockBolds);
    }

    private void verifyNoRenderableOptionsSet() {
        verify(mockData, never()).get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY);
        verify(mockData, never()).get(HOVER_RENDERABLE_OPTIONS_DATA_KEY);
        verify(mockData, never()).get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY);
        verify(mockRectangleRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockRectangleRenderable, never()).setTopLeftColorProvider(any());
        verify(mockRectangleRenderable, never()).setTopRightColorProvider(any());
        verify(mockRectangleRenderable, never()).setBottomRightColorProvider(any());
        verify(mockRectangleRenderable, never()).setBottomLeftColorProvider(any());
        verify(mockRectangleRenderable, never()).setTextureIdProvider(any());
        verify(MOCK_GET_SPRITE, never()).apply(anyString());
        verify(mockSpriteRenderable, never()).setSprite(any());
        verify(mockSpriteRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockSpriteRenderable, never()).colorShifts();
        verify(mockSpriteShifts, never()).clear();
        verify(mockSpriteShifts, never()).add(any());
        verify(mockTextLineRenderable, never()).setRenderingLocationProvider(any());
        verify(mockTextLineRenderable, never()).colorProviderIndices();
        verify(mockTextColors, never()).clear();
        verify(mockTextColors, never()).putAll(any());
        verify(mockTextLineRenderable, never()).italicIndices();
        verify(mockItalics, never()).clear();
        verify(mockItalics, never()).addAll(any());
        verify(mockTextLineRenderable, never()).boldIndices();
        verify(mockBolds, never()).clear();
        verify(mockBolds, never()).addAll(any());
    }

    @Test
    public void testPressKey_Button() {
        buttonMethods.pressKey_Button(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockPressAction);
        inOrder.verify(mockComponent, once()).data();
        inOrder.verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, true);
        inOrder.verify(mockData, once()).get(PRESS_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(PRESS_SOUND_ID);
    }

    @Test
    public void testPressKey_ButtonWhenButtonAlreadyPressed() {
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.pressKey_Button(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        verify(mockData, once()).get(anyString());
        verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressKey_ButtonWhenNoPressSoundId() {
        when(mockData.get(PRESS_SOUND_ID_DATA_KEY)).thenReturn(null);

        buttonMethods.pressKey_Button(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testReleaseKey_Button() {
        var key = randomChar();
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);
        when(mockData.get(PRESSED_KEY_DATA_KEY)).thenReturn(key);

        buttonMethods.releaseKey_Button(
                eventInputs(randomLong())
                        .withKeyEvent(key, mockComponent)
        );

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockPressAction);
        inOrder.verify(mockComponent, once()).data();
        inOrder.verify(mockData, once()).get(PRESSED_KEY_DATA_KEY);
        inOrder.verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        verify(mockData, never()).get(RECT_HOVER_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
        inOrder.verify(mockData, once()).put(PRESSED_KEY_DATA_KEY, null);
        inOrder.verify(mockData, once()).get(RELEASE_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockData, once()).get(PRESS_ACTION_DATA_KEY);
        //noinspection unchecked
        inOrder.verify(mockPressAction, once()).accept(null);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonLeftJustified() {
        var mockInputsData = generateMockMap(
                pairOf(provideTextRenderingLocFromRect_Button_textJustification,
                        TextJustification.LEFT),
                pairOf(provideTextRenderingLocFromRect_Button_rectDimensProvider, mockRectDimens),
                pairOf(provideTextRenderingLocFromRect_Button_paddingHoriz, PADDING_HORIZ),
                pairOf(provideTextRenderingLocFromRect_Button_lineLength, LINE_LENGTH),
                pairOf(provideTextRenderingLocFromRect_Button_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.provideTextRenderingLocFromRect_Button(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = RECT_DIMENS.LEFT_X + PADDING_HORIZ;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonCenterJustified() {
        var mockInputsData = generateMockMap(
                pairOf(provideTextRenderingLocFromRect_Button_textJustification,
                        TextJustification.CENTER),
                pairOf(provideTextRenderingLocFromRect_Button_rectDimensProvider, mockRectDimens),
                pairOf(provideTextRenderingLocFromRect_Button_paddingHoriz, PADDING_HORIZ),
                pairOf(provideTextRenderingLocFromRect_Button_lineLength, LINE_LENGTH),
                pairOf(provideTextRenderingLocFromRect_Button_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.provideTextRenderingLocFromRect_Button(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = (RECT_DIMENS.LEFT_X + RECT_DIMENS.RIGHT_X - LINE_LENGTH) / 2f;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonRightJustified() {
        var mockInputsData = generateMockMap(
                pairOf(provideTextRenderingLocFromRect_Button_textJustification,
                        TextJustification.RIGHT),
                pairOf(provideTextRenderingLocFromRect_Button_rectDimensProvider, mockRectDimens),
                pairOf(provideTextRenderingLocFromRect_Button_paddingHoriz, PADDING_HORIZ),
                pairOf(provideTextRenderingLocFromRect_Button_lineLength, LINE_LENGTH),
                pairOf(provideTextRenderingLocFromRect_Button_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.provideTextRenderingLocFromRect_Button(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = RECT_DIMENS.RIGHT_X - PADDING_HORIZ - LINE_LENGTH;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideRectDimensFromText_Button() {
        var textRenderingLoc = randomVertex();
        when(mockTextRenderingLoc.provide(anyLong())).thenReturn(textRenderingLoc);
        var mockInputsData = generateMockMap(
                pairOf(provideRectDimensFromText_Button_textRenderingLocProvider,
                        mockTextRenderingLoc),
                pairOf(provideRectDimensFromText_Button_lineLength, LINE_LENGTH),
                pairOf(provideRectDimensFromText_Button_textHeight, TEXT_HEIGHT),
                pairOf(provideRectDimensFromText_Button_textPaddingVert, PADDING_VERT),
                pairOf(provideRectDimensFromText_Button_textPaddingHoriz, PADDING_HORIZ)
        );

        var output = buttonMethods.provideRectDimensFromText_Button(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expected = floatBoxOf(
                textRenderingLoc.X - PADDING_HORIZ,
                textRenderingLoc.Y - PADDING_VERT,
                textRenderingLoc.X + LINE_LENGTH + PADDING_HORIZ,
                textRenderingLoc.Y + TEXT_HEIGHT + PADDING_VERT
        );
        assertEquals(expected, output);
        verify(mockTextRenderingLoc, once()).provide(TIMESTAMP);
    }
}
