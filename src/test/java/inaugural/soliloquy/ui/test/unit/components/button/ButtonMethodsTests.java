package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.ui.components.ComponentMethods;
import org.apache.commons.lang3.function.TriConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.entities.Consumer;
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
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.assertFloatBoxesEqual;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
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
    private final static String MOUSE_OVER_SOUND_ID_DATA_KEY = "Button_mouseOverSoundId";
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

    private final FloatBox COMPONENT_DIMENS = randomFloatBox();

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

    @SuppressWarnings("rawtypes") @Mock private Consumer mockPress;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private List<ColorShift> mockSpriteShifts;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockComponent;
    @Mock private RenderableWithMouseEvents mockRenderable;

    @Mock private ComponentMethods mockComponentMethods;

    @Mock private Map<UUID, ProviderAtTime<FloatBox>> mockOrigContentDimensProviders;
    @Mock private Map<UUID, ProviderAtTime<Vertex>> mockOrigContentLocProviders;

    private Map<String, Object> mockData;

    private ButtonMethods buttonMethods;

    @BeforeEach
    public void setUp() {
        mockData = generateMockMap(
                pairOf(PRESS_ACTION_DATA_KEY, mockPress),
                pairOf(PRESS_SOUND_ID_DATA_KEY, PRESS_SOUND_ID),
                pairOf(MOUSE_OVER_SOUND_ID_DATA_KEY, MOUSE_OVER_SOUND_ID),
                pairOf(MOUSE_LEAVE_SOUND_ID_DATA_KEY, MOUSE_LEAVE_SOUND_ID),
                pairOf(RELEASE_SOUND_ID_DATA_KEY, RELEASE_SOUND_ID),
                pairOf(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY, options(SPRITE_ID_DEFAULT)),
                pairOf(HOVER_RENDERABLE_OPTIONS_DATA_KEY, new ButtonMethods.Options()),
                pairOf(PRESSED_RENDERABLE_OPTIONS_DATA_KEY, new ButtonMethods.Options()),
                pairOf(ORIG_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(ORIG_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders)
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
        lenient().when(mockRectangleRenderable.getRenderingDimensionsProvider())
                .thenReturn(mockRectDimens);
        lenient().when(mockSpriteRenderable.getRenderingDimensionsProvider())
                .thenReturn(mockSpriteDimens);

        lenient().when(
                        mockComponentMethods.Component_setDimensForComponentAndContent(any(),
                                anyLong()))
                .thenReturn(COMPONENT_DIMENS);

        buttonMethods =
                new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        mockComponentMethods);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(null, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        mockComponentMethods));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, null, MOCK_GET_SPRITE,
                        mockComponentMethods));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, null,
                        mockComponentMethods));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        null));
    }

    @Test
    public void testButton_setDimensForComponentAndContent_updateDefault() {
        var options = options(randomString());
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS)).thenReturn(options);

        var output = buttonMethods.Button_setDimensForComponentAndContent(mockComponent, TIMESTAMP);

        assertEquals(COMPONENT_DIMENS, output);
        assertSame(mockRectDimens, options.rectDimens);
        assertSame(mockSpriteDimens, options.spriteDimens);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(PRESS_STATE);
        verify(mockData, once()).get(RECT_HOVER_STATE);
        verify(mockData, once()).get(SPRITE_HOVER_STATE);
        verify(mockData, once()).get(ORIG_CONTENT_IS_LOADED_DEFAULT);
        verify(mockData, once()).get(ORIG_CONTENT_DIMENS_PROVIDERS);
        verify(mockData, once()).get(ORIG_CONTENT_LOC_PROVIDERS);
        verify(mockData, once()).put(ORIG_CONTENT_DIMENS_PROVIDERS_DEFAULT,
                mockOrigContentDimensProviders);
        verify(mockData, once()).put(ORIG_CONTENT_LOC_PROVIDERS_DEFAULT,
                mockOrigContentLocProviders);
        verify(mockComponent, once()).contentsRepresentation();
        verify(mockSpriteRenderable, once()).getRenderingDimensionsProvider();
        verify(mockRectangleRenderable, once()).getRenderingDimensionsProvider();
        verify(mockData, once()).put(ORIG_CONTENT_IS_LOADED_DEFAULT, true);
    }

    @Test
    public void testButton_setDimensForComponentAndContent_updateHover() {
        var options = options(randomString());
        when(mockData.get(HOVER_RENDERABLE_OPTIONS)).thenReturn(options);
        when(mockData.get(RECT_HOVER_STATE)).thenReturn(true);

        var output = buttonMethods.Button_setDimensForComponentAndContent(mockComponent, TIMESTAMP);

        assertEquals(COMPONENT_DIMENS, output);
        assertSame(mockRectDimens, options.rectDimens);
        assertSame(mockSpriteDimens, options.spriteDimens);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(PRESS_STATE);
        verify(mockData, once()).get(RECT_HOVER_STATE);
        verify(mockData, once()).get(ORIG_CONTENT_IS_LOADED_HOVER);
        verify(mockData, once()).get(ORIG_CONTENT_DIMENS_PROVIDERS);
        verify(mockData, once()).get(ORIG_CONTENT_LOC_PROVIDERS);
        verify(mockData, once()).put(ORIG_CONTENT_DIMENS_PROVIDERS_HOVER,
                mockOrigContentDimensProviders);
        verify(mockData, once()).put(ORIG_CONTENT_LOC_PROVIDERS_HOVER, mockOrigContentLocProviders);
        verify(mockComponent, once()).contentsRepresentation();
        verify(mockSpriteRenderable, once()).getRenderingDimensionsProvider();
        verify(mockRectangleRenderable, once()).getRenderingDimensionsProvider();
        verify(mockData, once()).put(ORIG_CONTENT_IS_LOADED_HOVER, true);
    }

    @Test
    public void testButton_setDimensForComponentAndContent_updatePressed() {
        var options = options(randomString());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS)).thenReturn(options);
        when(mockData.get(PRESS_STATE)).thenReturn(true);

        var output = buttonMethods.Button_setDimensForComponentAndContent(mockComponent, TIMESTAMP);

        assertEquals(COMPONENT_DIMENS, output);
        assertSame(mockRectDimens, options.rectDimens);
        assertSame(mockSpriteDimens, options.spriteDimens);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentMethods, once()).Component_setDimensForComponentAndContent(
                mockComponent, TIMESTAMP);
        verify(mockData, once()).get(PRESS_STATE);
        verify(mockData, once()).get(ORIG_CONTENT_IS_LOADED_PRESSED);
        verify(mockData, once()).get(ORIG_CONTENT_DIMENS_PROVIDERS);
        verify(mockData, once()).get(ORIG_CONTENT_LOC_PROVIDERS);
        verify(mockData, once()).put(ORIG_CONTENT_DIMENS_PROVIDERS_PRESSED,
                mockOrigContentDimensProviders);
        verify(mockData, once()).put(ORIG_CONTENT_LOC_PROVIDERS_PRESSED,
                mockOrigContentLocProviders);
        verify(mockComponent, once()).contentsRepresentation();
        verify(mockSpriteRenderable, once()).getRenderingDimensionsProvider();
        verify(mockRectangleRenderable, once()).getRenderingDimensionsProvider();
        verify(mockData, once()).put(ORIG_CONTENT_IS_LOADED_PRESSED, true);
    }

    @Test
    public void testMousePressOnRectAndReleaseOnRect() {
        var eventInputs = eventInputs(randomLong())
                .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent);

        buttonMethods.Button_pressMouse(eventInputs);

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockSubscribeToMouseEvents,
                mockPress);
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
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
        inOrder.verify(mockData, never()).get(SPRITE_HOVER_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESSED_KEY_DATA_KEY, null);
        inOrder.verify(mockData, once()).get(RELEASE_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockData, once()).get(PRESS_ACTION_DATA_KEY);
        //noinspection unchecked
        inOrder.verify(mockPress, once()).accept(eventInputs);
    }

    @Test
    public void testMousePressOnRectAndReleaseOnSprite() {
        buttonMethods.Button_pressMouse(
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
        buttonMethods.Button_pressMouse(
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
        buttonMethods.Button_pressMouse(
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

        buttonMethods.Button_pressMouse(
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

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressMouse_ButtonReleaseWhenNotHovering() {
        buttonMethods.Button_pressMouse(
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
        verify(mockPress, never()).accept(any());
        verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
    }

    @Test
    public void testPressMouse_ButtonWhenNoReleaseSoundId() {
        when(mockData.get(RELEASE_SOUND_ID_DATA_KEY)).thenReturn(null);

        buttonMethods.Button_pressMouse(
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
    public void testPressMouse_ButtonWhenNoPressConsumer() {
        when(mockData.get(PRESS_ACTION_DATA_KEY)).thenReturn(null);

        buttonMethods.Button_pressMouse(
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
        verify(mockPress, never()).accept(any());
    }

    @Test
    public void testMouseOver_ButtonOnRectSetsHoverState() {
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(RECT_HOVER_STATE_DATA_KEY, true);
    }

    @Test
    public void testMouseOver_ButtonOnSpriteSetsHoverState() {
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(SPRITE_HOVER_STATE_DATA_KEY, true);
    }

    @Test
    public void testMouseLeave_ButtonRectSetsHoverState() {
        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(RECT_HOVER_STATE_DATA_KEY, false);
    }

    @Test
    public void testMouseLeave_ButtonSpriteSetsHoverState() {
        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockComponent)
        );

        verify(mockComponent, atLeastOnce()).data();
        verify(mockData, once()).put(SPRITE_HOVER_STATE_DATA_KEY, false);
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToHoverWhenNotAlreadyHovering() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.Options());
        when(mockData.get(HOVER_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_HOVER));
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyHoverRenderableOptionsSet();
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToPressedWhenNotAlreadyHoveringAndButtonIsPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.Options());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testMouseOverDoesNotSetRenderableOptionsWhenAlreadyHovering() {
        when(mockData.get(RECT_HOVER_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testPressMouseButtonSetsRenderableOptionsToPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.Options());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testPressKeyButtonSetsRenderableOptionsToPressed() {
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(new ButtonMethods.Options());
        when(mockData.get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.Button_pressKey(
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

        buttonMethods.Button_mouseLeave(
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

        buttonMethods.Button_mouseLeave(
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

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testReleaseMouseButtonSetsRenderableOptionsToDefault() {
        buttonMethods.Button_pressMouse(
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
                .thenReturn(new ButtonMethods.Options());
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockData, atLeastOnce()).get(HOVER_RENDERABLE_OPTIONS_DATA_KEY);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testSetPressedRenderableOptionsSetToDefaultWhenNull() {
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockData, atLeastOnce()).get(PRESSED_RENDERABLE_OPTIONS_DATA_KEY);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testWhenNoShiftsInOptionsShiftIsNotAdded() {
        var optionsWithoutShift = new ButtonMethods.Options(mockRectDimens,
                mockBgTopLeft,
                mockBgTopRight,
                mockBgBottomLeft,
                mockBgBottomRight,
                mockBgTexProvider,
                SPRITE_ID_DEFAULT,
                mockSpriteDimens,
                null,
                mockTextColors,
                mockItalics,
                mockBolds);
        when(mockData.get(DEFAULT_RENDERABLE_OPTIONS_DATA_KEY)).thenReturn(optionsWithoutShift);

        when(mockData.get(RECT_HOVER_STATE_DATA_KEY))
                .thenReturn(true)
                .thenReturn(false);

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockSpriteShifts, once()).clear();
        verify(mockSpriteShifts, never()).add(any());
    }

    @Test
    public void testRenderablesAtUnspecifiedZValuesDoNotReceiveRenderableOptions() {
        // just gotta make sure it isn't RECT_Z, SPRITE_Z, or TEXT_Z, respectively
        when(mockRectangleRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));
        when(mockSpriteRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));
        when(mockTextLineRenderable.getZ()).thenReturn(randomIntWithInclusiveCeiling(-1));

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockComponent)
        );

        verify(mockRectangleRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockSpriteRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockTextLineRenderable, never()).setRenderingLocationProvider(any());
    }

    private ButtonMethods.Options options(String spriteId) {
        return new ButtonMethods.Options(mockRectDimens,
                mockBgTopLeft,
                mockBgTopRight,
                mockBgBottomLeft,
                mockBgBottomRight,
                mockBgTexProvider,
                spriteId,
                mockSpriteDimens,
                mockSpriteShift,
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
        verify(mockRectangleRenderable, atLeastOnce()).setRenderingDimensionsProvider(
                mockRectDimens);
        verify(mockRectangleRenderable, atLeastOnce()).setTopLeftColorProvider(mockBgTopLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setTopRightColorProvider(mockBgTopRight);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomLeftColorProvider(mockBgBottomLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomRightColorProvider(
                mockBgBottomRight);
        verify(mockRectangleRenderable, atLeastOnce()).setTextureIdProvider(mockBgTexProvider);
        verify(mockSpriteRenderable, atLeastOnce()).setRenderingDimensionsProvider(
                mockSpriteDimens);
        verify(mockSpriteRenderable, atLeastOnce()).colorShifts();
        verify(mockSpriteShifts, atLeastOnce()).clear();
        verify(mockSpriteShifts, atLeastOnce()).add(mockSpriteShift);
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
    public void testButton_PressKey() {
        buttonMethods.Button_pressKey(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockPress);
        inOrder.verify(mockComponent, once()).data();
        inOrder.verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, true);
        inOrder.verify(mockData, once()).get(PRESS_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(PRESS_SOUND_ID);
    }

    @Test
    public void testPressKey_ButtonWhenButtonAlreadyPressed() {
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);

        buttonMethods.Button_pressKey(
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

        buttonMethods.Button_pressKey(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockComponent)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testButton_ReleaseKey() {
        var key = randomInt();
        when(mockData.get(PRESS_STATE_DATA_KEY)).thenReturn(true);
        when(mockData.get(PRESSED_KEY_DATA_KEY)).thenReturn(key);
        var eventInputs = eventInputs(randomLong())
                .withKeyEvent(key, mockComponent);

        buttonMethods.Button_releaseKey(eventInputs);

        var inOrder = inOrder(mockComponent, mockData, mockPlaySound, mockPress);
        inOrder.verify(mockComponent, once()).data();
        inOrder.verify(mockData, once()).get(PRESSED_KEY_DATA_KEY);
        inOrder.verify(mockData, once()).get(PRESS_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESS_STATE_DATA_KEY, false);
        verify(mockData, never()).get(RECT_HOVER_STATE_DATA_KEY);
        inOrder.verify(mockData, once()).put(PRESSED_KEY_DATA_KEY, null);
        inOrder.verify(mockData, once()).get(RELEASE_SOUND_ID_DATA_KEY);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockData, once()).get(PRESS_ACTION_DATA_KEY);
        //noinspection unchecked
        inOrder.verify(mockPress, once()).accept(eventInputs);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonLeftJustified() {
        var mockInputsData = generateMockMap(
                pairOf(Button_provideTextRenderingLocFromRect_horizontalAlignment,
                        HorizontalAlignment.LEFT),
                pairOf(Button_provideTextRenderingLocFromRect_rectDimensProvider, mockRectDimens),
                pairOf(Button_provideTextRenderingLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideTextRenderingLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideTextRenderingLocFromRect(providerInputs(
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
                pairOf(Button_provideTextRenderingLocFromRect_horizontalAlignment,
                        HorizontalAlignment.CENTER),
                pairOf(Button_provideTextRenderingLocFromRect_rectDimensProvider, mockRectDimens),
                pairOf(Button_provideTextRenderingLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideTextRenderingLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideTextRenderingLocFromRect(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = (RECT_DIMENS.LEFT_X + RECT_DIMENS.RIGHT_X) / 2f;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonRightJustified() {
        var mockInputsData = generateMockMap(
                pairOf(Button_provideTextRenderingLocFromRect_horizontalAlignment,
                        HorizontalAlignment.RIGHT),
                pairOf(Button_provideTextRenderingLocFromRect_rectDimensProvider, mockRectDimens),
                pairOf(Button_provideTextRenderingLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideTextRenderingLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideTextRenderingLocFromRect(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = RECT_DIMENS.RIGHT_X - PADDING_HORIZ;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_ProvideRectDimensFromText() {
        var textRenderingLoc = randomVertex();
        when(mockTextRenderingLoc.provide(anyLong())).thenReturn(textRenderingLoc);
        var mockInputsData = generateMockMap(
                pairOf(Button_provideRectDimensFromText_textRenderingLocProvider,
                        mockTextRenderingLoc),
                pairOf(Button_provideRectDimensFromText_lineLength, LINE_LENGTH),
                pairOf(Button_provideRectDimensFromText_textHeight, TEXT_HEIGHT),
                pairOf(Button_provideRectDimensFromText_textPaddingVert, PADDING_VERT),
                pairOf(Button_provideRectDimensFromText_textPaddingHoriz, PADDING_HORIZ)
        );

        var output = buttonMethods.Button_provideRectDimensFromText(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var distFromCenterHoriz = PADDING_HORIZ + (LINE_LENGTH / 2f);
        var expected = floatBoxOf(
                textRenderingLoc.X - distFromCenterHoriz,
                textRenderingLoc.Y - PADDING_VERT,
                textRenderingLoc.X + distFromCenterHoriz,
                textRenderingLoc.Y + TEXT_HEIGHT + PADDING_VERT
        );
        assertFloatBoxesEqual(expected, output);
        verify(mockTextRenderingLoc, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_ProvideTexTileWidth() {
        var rectDimens = randomFloatBox();
        when(mockRectDimens.provide(anyLong())).thenReturn(rectDimens);
        Map<String, Object> mockInputsData = generateMockMap(
                pairOf(
                        provideTexTileDimens_Button_rectDimensProvider,
                        mockRectDimens
                )
        );

        var output = buttonMethods.Button_provideTexTileWidth(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        assertEquals(rectDimens.width(), output);

        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_ProvideTexTileHeight() {
        var rectDimens = randomFloatBox();
        when(mockRectDimens.provide(anyLong())).thenReturn(rectDimens);
        Map<String, Object> mockInputsData = generateMockMap(
                pairOf(
                        provideTexTileDimens_Button_rectDimensProvider,
                        mockRectDimens
                )
        );

        var output = buttonMethods.Button_provideTexTileHeight(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        assertEquals(rectDimens.height(), output);

        verify(mockRectDimens, once()).provide(TIMESTAMP);
    }
}
