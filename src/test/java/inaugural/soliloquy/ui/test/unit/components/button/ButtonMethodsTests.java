package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.ui.Constants;
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
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.mouse.MouseEventHandler;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.assertFloatBoxesEqual;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.*;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.translateFloatBox;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;
import static soliloquy.specs.io.input.mouse.MouseEventHandler.EventType.RELEASE;
import static soliloquy.specs.ui.EventInputs.eventInputs;

@ExtendWith(MockitoExtension.class)
public class ButtonMethodsTests {
    private final UUID BUTTON_UUID = randomUUID();

    private final int MOUSE_BUTTON = randomInt();
    private final String PRESS_SOUND_ID = randomString();
    private final String MOUSE_OVER_SOUND_ID = randomString();
    private final String MOUSE_LEAVE_SOUND_ID = randomString();
    private final String RELEASE_SOUND_ID = randomString();

    private final FloatBox RECT_UNADJ_DIMENS = randomFloatBox();
    private final float PADDING_VERT = randomFloat();
    private final float PADDING_HORIZ = randomFloat();
    private final float LINE_LENGTH = randomFloat();
    private final float TEXT_HEIGHT = randomFloat();
    private final float TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS =
            (RECT_UNADJ_DIMENS.TOP_Y + RECT_UNADJ_DIMENS.BOTTOM_Y - TEXT_HEIGHT) / 2f;
    private final long TIMESTAMP = randomLong();

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
    private final FloatBox SPRITE_UNADJ_DIMENS = randomFloatBox();

    private final FloatBox BUTTON_UNADJ_DIMENS =
            encompassing(RECT_UNADJ_DIMENS, SPRITE_UNADJ_DIMENS);
    private final Vertex ORIGIN_OVERRIDE = randomVertex();
    private final Vertex ORIGIN_ADJUST = difference(BUTTON_UNADJ_DIMENS.topLeft(), ORIGIN_OVERRIDE);
    private final FloatBox BUTTON_ADJ_DIMENS = floatBoxOf(
            ORIGIN_OVERRIDE,
            BUTTON_UNADJ_DIMENS.width(),
            BUTTON_UNADJ_DIMENS.height()
    );
    private final FloatBox BUTTON_RECT_ADJ_DIMENS = floatBoxOf(
            translateVertex(RECT_UNADJ_DIMENS.topLeft(),ORIGIN_ADJUST),
            translateVertex(RECT_UNADJ_DIMENS.bottomRight(),ORIGIN_ADJUST)
    );

    @Mock private ProviderAtTime<FloatBox> mockRectUnadjDimens;
    @Mock private ProviderAtTime<Color> mockBgTopLeft;
    @Mock private ProviderAtTime<Color> mockBgTopRight;
    @Mock private ProviderAtTime<Color> mockBgBottomRight;
    @Mock private ProviderAtTime<Color> mockBgBottomLeft;
    @Mock private ProviderAtTime<Integer> mockBgTexProvider;
    @Mock private ProviderAtTime<FloatBox> mockSpriteUnadjDimens;
    @Mock private ColorShift mockSpriteShift;
    @Mock private ProviderAtTime<Vertex> mockTextUnadjLoc;
    @Mock private Map<Integer, ProviderAtTime<Color>> mockTextColors;
    @Mock private List<Integer> mockItalics;
    @Mock private List<Integer> mockBolds;

    @Mock private ProviderAtTime<Vertex> mockOriginOverrideProvider;

    @Mock private Consumer<String> mockPlaySound;
    @Mock private TriConsumer<Integer, MouseEventHandler.EventType, Runnable>
            mockSubscribeToMouseEvents;

    @SuppressWarnings("rawtypes") @Mock private Consumer mockPress;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private List<ColorShift> mockSpriteShifts;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockButton;
    @Mock private RenderableWithMouseEvents mockRenderable;

    @Mock private Function<UUID, Component> mockGetComponent;

    @Mock private Map<UUID, ProviderAtTime<FloatBox>> mockOrigContentDimensProviders;

    private Map<String, Object> mockButtonData;

    private ButtonMethods buttonMethods;

    @BeforeEach
    public void setUp() {
        mockButtonData = generateMockMap(
                pairOf(PRESS_ACTION, mockPress),
                pairOf(ButtonMethods.PRESS_SOUND_ID, PRESS_SOUND_ID),
                pairOf(ButtonMethods.MOUSE_OVER_SOUND_ID, MOUSE_OVER_SOUND_ID),
                pairOf(ButtonMethods.MOUSE_LEAVE_SOUND_ID, MOUSE_LEAVE_SOUND_ID),
                pairOf(ButtonMethods.RELEASE_SOUND_ID, RELEASE_SOUND_ID),
                pairOf(RENDERABLE_OPTIONS_DEFAULT, options(SPRITE_ID_DEFAULT)),
                pairOf(RENDERABLE_OPTIONS_HOVER, new ButtonMethods.Options()),
                pairOf(RENDERABLE_OPTIONS_PRESSED, new ButtonMethods.Options()),
                pairOf(CONTENT_UNADJUSTED_DIMENS_PROVIDERS, mockOrigContentDimensProviders)
        );

        lenient().when(mockRectUnadjDimens.provide(anyLong())).thenReturn(RECT_UNADJ_DIMENS);
        lenient().when(mockRectangleRenderable.getZ()).thenReturn(RECT_Z);
        lenient().when(mockSpriteUnadjDimens.provide(anyLong())).thenReturn(SPRITE_UNADJ_DIMENS);
        lenient().when(mockSpriteRenderable.colorShifts()).thenReturn(mockSpriteShifts);
        lenient().when(mockSpriteRenderable.getZ()).thenReturn(SPRITE_Z);
        lenient().when(mockTextLineRenderable.colorProviderIndices()).thenReturn(mockTextColors);
        lenient().when(mockTextLineRenderable.italicIndices()).thenReturn(mockItalics);
        lenient().when(mockTextLineRenderable.boldIndices()).thenReturn(mockBolds);
        lenient().when(mockTextLineRenderable.getZ()).thenReturn(TEXT_Z);
        lenient().when(mockButton.contentsRepresentation()).thenReturn(setOf(
                mockRectangleRenderable,
                mockSpriteRenderable,
                mockTextLineRenderable
        ));
        lenient().when(mockButton.data()).thenReturn(mockButtonData);
        lenient().when(mockRenderable.containingComponent()).thenReturn(mockButton);
        lenient().when(mockRectangleRenderable.getRenderingDimensionsProvider())
                .thenReturn(mockRectUnadjDimens);
        lenient().when(mockSpriteRenderable.getRenderingDimensionsProvider())
                .thenReturn(mockSpriteUnadjDimens);

        lenient().when(mockOriginOverrideProvider.provide(anyLong())).thenReturn(ORIGIN_OVERRIDE);

        lenient().when(mockGetComponent.apply(any())).thenReturn(mockButton);

        buttonMethods =
                new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        mockGetComponent);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(null, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        mockGetComponent));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, null, MOCK_GET_SPRITE,
                        mockGetComponent));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, null,
                        mockGetComponent));
        assertThrows(IllegalArgumentException.class,
                () -> new ButtonMethods(mockPlaySound, mockSubscribeToMouseEvents, MOCK_GET_SPRITE,
                        null));
    }

    @Test
    public void testButton_getDimens() {
        var inputs = providerInputs(TIMESTAMP, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID
        ));

        var output = buttonMethods.Button_getDimens(inputs);

        assertEquals(BUTTON_UNADJ_DIMENS, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(LAST_TIMESTAMP);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockSpriteUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockButtonData, once()).put(BUTTON_RECT_DIMENS, RECT_UNADJ_DIMENS);
        verify(mockButtonData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, never()).provide(anyLong());
        verify(mockButtonData, once()).put(Constants.ORIGIN_ADJUST, null);
        verify(mockButtonData, once()).put(BUTTON_DIMENS, BUTTON_UNADJ_DIMENS);
        verify(mockButtonData, once()).put(BUTTON_RECT_DIMENS, RECT_UNADJ_DIMENS);
    }

    @Test
    public void testButton_setDimensForComponentAndContentWithOverride() {
        when(mockButtonData.get(ORIGIN_OVERRIDE_PROVIDER)).thenReturn(mockOriginOverrideProvider);

        var output = buttonMethods.Button_setDimensForComponentAndContent(mockButton, TIMESTAMP);

        assertEquals(BUTTON_ADJ_DIMENS, output);

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(LAST_TIMESTAMP);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockButtonData, once()).put(BUTTON_RECT_DIMENS, BUTTON_RECT_ADJ_DIMENS);
        verify(mockSpriteUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockButtonData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, once()).provide(anyLong());
        verify(mockButtonData, once()).put(Constants.ORIGIN_ADJUST, ORIGIN_ADJUST);
        verify(mockButtonData, once()).put(BUTTON_DIMENS, BUTTON_ADJ_DIMENS);
    }

    @Test
    public void testButton_setDimensForComponentAndContentOnLastTimestamp() {
        var dimensAtLastTimestamp = randomFloatBox();
        when(mockButtonData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockButtonData.get(BUTTON_DIMENS)).thenReturn(dimensAtLastTimestamp);

        var output = buttonMethods.Button_setDimensForComponentAndContent(mockButton, TIMESTAMP);

        assertEquals(dimensAtLastTimestamp, output);

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(LAST_TIMESTAMP);
        verify(mockButtonData, never()).get(IS_PRESSED);
        verify(mockButtonData, never()).get(RECT_HOVER_STATE);
        verify(mockButtonData, never()).put(eq(BUTTON_RECT_DIMENS), any());
        verify(mockButtonData, never()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, never()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockRectUnadjDimens, never()).provide(anyLong());
        verify(mockSpriteUnadjDimens, never()).provide(anyLong());
        verify(mockButtonData, never()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, never()).provide(anyLong());
        verify(mockButtonData, never()).put(anyString(), any());
    }

    @Test
    public void testButton_setDimensForComponentAndContentWithoutOverride() {
        var output = buttonMethods.Button_setDimensForComponentAndContent(mockButton, TIMESTAMP);

        assertEquals(BUTTON_UNADJ_DIMENS, output);

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(LAST_TIMESTAMP);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockSpriteUnadjDimens, once()).provide(TIMESTAMP);
        verify(mockButtonData, once()).put(BUTTON_RECT_DIMENS, RECT_UNADJ_DIMENS);
        verify(mockButtonData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, never()).provide(anyLong());
        verify(mockButtonData, once()).put(Constants.ORIGIN_ADJUST, null);
        verify(mockButtonData, once()).put(BUTTON_DIMENS, BUTTON_UNADJ_DIMENS);
        verify(mockButtonData, once()).put(BUTTON_RECT_DIMENS, RECT_UNADJ_DIMENS);
    }

    @Test
    public void testMousePressOnRectAndReleaseOnRect() {
        var eventInputs = eventInputs(randomLong())
                .withMouseEvent(MOUSE_BUTTON, null, null, mockButton);

        buttonMethods.Button_pressMouse(eventInputs);

        var inOrder = inOrder(mockButton, mockButtonData, mockPlaySound, mockSubscribeToMouseEvents,
                mockPress);
        inOrder.verify(mockButton, once()).data();
        inOrder.verify(mockButtonData, once()).get(IS_PRESSED);
        inOrder.verify(mockButtonData, once()).put(IS_PRESSED, true);
        inOrder.verify(mockButtonData, once()).put(PRESSED_KEY, null);
        inOrder.verify(mockButtonData, once()).get(ButtonMethods.PRESS_SOUND_ID);
        inOrder.verify(mockPlaySound, once()).accept(this.PRESS_SOUND_ID);
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        inOrder.verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        inOrder.verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        inOrder.verify(mockButtonData, never()).get(SPRITE_HOVER_STATE);
        inOrder.verify(mockButtonData, once()).put(PRESSED_KEY, null);
        inOrder.verify(mockButtonData, once()).get(ButtonMethods.RELEASE_SOUND_ID);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockButtonData, once()).get(PRESS_ACTION);
        //noinspection unchecked
        inOrder.verify(mockPress, once()).accept(eventInputs);
    }

    @Test
    public void testMousePressOnRectAndReleaseOnSprite() {
        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(false);
        when(mockButtonData.get(SPRITE_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
    }

    @Test
    public void testMousePressOnSpriteAndReleaseOnRect() {
        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockButton)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);
        // the check technically won't be made, but this makes the test more explicit
        lenient().when(mockButtonData.get(SPRITE_HOVER_STATE)).thenReturn(false);

        runnableCaptor.getValue().run();

        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
    }

    @Test
    public void testMousePressOnSpriteAndReleaseOnSprite() {
        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockButton)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                eq(LEFT_MOUSE_BUTTON),
                eq(RELEASE),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(SPRITE_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
    }

    @Test
    public void testPressMouse_ButtonWhenButtonAlreadyPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockButtonData, once()).get(anyString());
        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressMouse_ButtonWhenNoPressSoundId() {
        when(mockButtonData.get(ButtonMethods.PRESS_SOUND_ID)).thenReturn(null);

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressMouse_ButtonReleaseWhenNotHovering() {
        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(null);

        runnableCaptor.getValue().run();

        //noinspection unchecked
        verify(mockPress, never()).accept(any());
    }

    @Test
    public void testPressMouse_ButtonWhenNoReleaseSoundId() {
        when(mockButtonData.get(ButtonMethods.RELEASE_SOUND_ID)).thenReturn(null);

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        verify(mockPlaySound, never()).accept(eq(RELEASE_SOUND_ID));
    }

    @Test
    public void testPressMouse_ButtonWhenNoPressConsumer() {
        when(mockButtonData.get(PRESS_ACTION)).thenReturn(null);

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        //noinspection unchecked
        verify(mockPress, never()).accept(any());
    }

    @Test
    public void testMouseOver_ButtonOnRectSetsHoverState() {
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).put(RECT_HOVER_STATE, true);
    }

    @Test
    public void testMouseOver_ButtonOnSpriteSetsHoverState() {
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockButton)
        );

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).put(SPRITE_HOVER_STATE, true);
    }

    @Test
    public void testMouseLeave_ButtonRectSetsHoverState() {
        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).put(RECT_HOVER_STATE, false);
    }

    @Test
    public void testMouseLeave_ButtonSpriteSetsHoverState() {
        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, mockSpriteRenderable, mockButton)
        );

        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).put(SPRITE_HOVER_STATE, false);
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToHoverWhenNotAlreadyHovering() {
        when(mockButtonData.get(RENDERABLE_OPTIONS_DEFAULT))
                .thenReturn(new ButtonMethods.Options());
        when(mockButtonData.get(RENDERABLE_OPTIONS_HOVER))
                .thenReturn(options(SPRITE_ID_HOVER));
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyHoverRenderableOptionsSet();
    }

    @Test
    public void testMouseOverSetsRenderableOptionsToPressedWhenNotAlreadyHoveringAndButtonIsPressed() {
        when(mockButtonData.get(RENDERABLE_OPTIONS_DEFAULT))
                .thenReturn(new ButtonMethods.Options());
        when(mockButtonData.get(RENDERABLE_OPTIONS_PRESSED))
                .thenReturn(options(SPRITE_ID_PRESSED));
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testMouseOverDoesNotSetRenderableOptionsWhenAlreadyHovering() {
        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testPressMouseButtonSetsRenderableOptionsToPressed() {
        when(mockButtonData.get(RENDERABLE_OPTIONS_DEFAULT))
                .thenReturn(new ButtonMethods.Options());
        when(mockButtonData.get(RENDERABLE_OPTIONS_PRESSED))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testPressKeyButtonSetsRenderableOptionsToPressed() {
        when(mockButtonData.get(RENDERABLE_OPTIONS_DEFAULT))
                .thenReturn(new ButtonMethods.Options());
        when(mockButtonData.get(RENDERABLE_OPTIONS_PRESSED))
                .thenReturn(options(SPRITE_ID_PRESSED));

        buttonMethods.Button_pressKey(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockButton)
        );

        verifyPressedRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveSetsRenderableOptionsToDefaultWhenAlreadyHoveringIfNewHoverStateIsNotHovering() {
        when(mockButtonData.get(RECT_HOVER_STATE))
                .thenReturn(true)
                .thenReturn(false);

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveDoesNotSetRenderableOptionsIfWasNotHoveringAlready() {
        when(mockButtonData.get(RECT_HOVER_STATE))
                .thenReturn(false)
                .thenReturn(false);

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testMouseLeaveDoesNotSetRenderableOptionsIfItIsStillHovering() {
        when(mockButtonData.get(RECT_HOVER_STATE))
                .thenReturn(true)
                .thenReturn(true);

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verifyNoRenderableOptionsSet();
    }

    @Test
    public void testReleaseMouseButtonSetsRenderableOptionsToDefault() {
        buttonMethods.Button_pressMouse(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );
        var runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockSubscribeToMouseEvents, once()).accept(
                anyInt(),
                any(),
                runnableCaptor.capture()
        );

        when(mockButtonData.get(RECT_HOVER_STATE)).thenReturn(true);

        runnableCaptor.getValue().run();

        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testSetHoverRenderableOptionsSetToDefaultWhenNull() {
        when(mockButtonData.get(RENDERABLE_OPTIONS_HOVER))
                .thenReturn(new ButtonMethods.Options());
        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockButtonData, atLeastOnce()).get(RENDERABLE_OPTIONS_HOVER);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testSetPressedRenderableOptionsSetToDefaultWhenNull() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        buttonMethods.Button_mouseOver(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockButtonData, atLeastOnce()).get(RENDERABLE_OPTIONS_PRESSED);
        verifyDefaultRenderableOptionsSet();
    }

    @Test
    public void testWhenNoShiftsInOptionsShiftIsNotAdded() {
        var optionsWithoutShift = new ButtonMethods.Options(mockRectUnadjDimens,
                mockBgTopLeft,
                mockBgTopRight,
                mockBgBottomLeft,
                mockBgBottomRight,
                mockBgTexProvider,
                SPRITE_ID_DEFAULT,
                mockSpriteUnadjDimens,
                null,
                mockTextUnadjLoc,
                mockTextColors,
                mockItalics,
                mockBolds);
        when(mockButtonData.get(RENDERABLE_OPTIONS_DEFAULT)).thenReturn(optionsWithoutShift);

        when(mockButtonData.get(RECT_HOVER_STATE))
                .thenReturn(true)
                .thenReturn(false);

        buttonMethods.Button_mouseLeave(
                eventInputs(randomLong())
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
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
                        .withMouseEvent(MOUSE_BUTTON, null, null, mockButton)
        );

        verify(mockRectangleRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockSpriteRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockTextLineRenderable, never()).setRenderingLocationProvider(any());
    }

    private ButtonMethods.Options options(String spriteId) {
        return new ButtonMethods.Options(mockRectUnadjDimens,
                mockBgTopLeft,
                mockBgTopRight,
                mockBgBottomLeft,
                mockBgBottomRight,
                mockBgTexProvider,
                spriteId,
                mockSpriteUnadjDimens,
                mockSpriteShift,
                mockTextUnadjLoc,
                mockTextColors,
                mockItalics,
                mockBolds);
    }

    private void verifyDefaultRenderableOptionsSet() {
        verify(mockButtonData, atLeastOnce()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(MOCK_GET_SPRITE, atLeastOnce()).apply(SPRITE_ID_DEFAULT);
        verify(mockSpriteRenderable, atLeastOnce()).setSprite(MOCK_SPRITE_DEFAULT);
        verifyRenderableOptionsSet();
    }

    private void verifyHoverRenderableOptionsSet() {
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_HOVER);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID_HOVER);
        verify(mockSpriteRenderable, once()).setSprite(MOCK_SPRITE_HOVER);
        verifyRenderableOptionsSet();
    }

    private void verifyPressedRenderableOptionsSet() {
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_PRESSED);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID_PRESSED);
        verify(mockSpriteRenderable, once()).setSprite(MOCK_SPRITE_PRESSED);
        verifyRenderableOptionsSet();
    }

    private void verifyRenderableOptionsSet() {
        verify(mockRectangleRenderable, never()).setRenderingDimensionsProvider(any());
        verify(mockRectangleRenderable, atLeastOnce()).setTopLeftColorProvider(mockBgTopLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setTopRightColorProvider(mockBgTopRight);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomLeftColorProvider(mockBgBottomLeft);
        verify(mockRectangleRenderable, atLeastOnce()).setBottomRightColorProvider(
                mockBgBottomRight);
        verify(mockRectangleRenderable, atLeastOnce()).setTextureIdProvider(mockBgTexProvider);
        verify(mockSpriteRenderable, never()).setRenderingDimensionsProvider(any());
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
        verify(mockButtonData, never()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockButtonData, never()).get(RENDERABLE_OPTIONS_HOVER);
        verify(mockButtonData, never()).get(RENDERABLE_OPTIONS_PRESSED);
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
                        .withKeyEvent(randomChar(), mockButton)
        );

        var inOrder = inOrder(mockButton, mockButtonData, mockPlaySound, mockPress);
        inOrder.verify(mockButton, once()).data();
        inOrder.verify(mockButtonData, once()).get(ButtonMethods.PRESS_SOUND_ID);
        inOrder.verify(mockPlaySound, once()).accept(PRESS_SOUND_ID);
    }

    @Test
    public void testPressKey_ButtonWhenButtonAlreadyPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        buttonMethods.Button_pressKey(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockButton)
        );

        verify(mockButtonData, once()).get(anyString());
        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testPressKey_ButtonWhenNoPressSoundId() {
        when(mockButtonData.get(ButtonMethods.PRESS_SOUND_ID)).thenReturn(null);

        buttonMethods.Button_pressKey(
                eventInputs(randomLong())
                        .withKeyEvent(randomChar(), mockButton)
        );

        verify(mockPlaySound, never()).accept(anyString());
    }

    @Test
    public void testButton_ReleaseKey() {
        var key = randomInt();
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);
        when(mockButtonData.get(PRESSED_KEY)).thenReturn(key);
        var eventInputs = eventInputs(randomLong())
                .withKeyEvent(key, mockButton);

        buttonMethods.Button_releaseKey(eventInputs);

        var inOrder = inOrder(mockButton, mockButtonData, mockPlaySound, mockPress);
        inOrder.verify(mockButton, once()).data();
        inOrder.verify(mockButtonData, once()).get(PRESSED_KEY);
        verify(mockButtonData, never()).get(RECT_HOVER_STATE);
        inOrder.verify(mockButtonData, once()).put(PRESSED_KEY, null);
        inOrder.verify(mockButtonData, once()).get(ButtonMethods.RELEASE_SOUND_ID);
        inOrder.verify(mockPlaySound, once()).accept(RELEASE_SOUND_ID);
        inOrder.verify(mockButtonData, once()).get(PRESS_ACTION);
        //noinspection unchecked
        inOrder.verify(mockPress, once()).accept(eventInputs);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonLeftJustified() {
        var mockInputsData = inaugural.soliloquy.tools.testing.Mock.<String, Object>generateMockMap(
                pairOf(Button_provideUnadjTextLocFromRect_horizontalAlignment,
                        HorizontalAlignment.LEFT),
                pairOf(Button_provideUnadjTextLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideUnadjTextLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideUnadjTextLocFromRect(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = RECT_UNADJ_DIMENS.LEFT_X + PADDING_HORIZ;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonCenterJustified() {
        var mockInputsData = inaugural.soliloquy.tools.testing.Mock.<String, Object>generateMockMap(
                pairOf(Button_provideUnadjTextLocFromRect_horizontalAlignment,
                        HorizontalAlignment.CENTER),
                pairOf(Button_provideUnadjTextLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideUnadjTextLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideUnadjTextLocFromRect(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = (RECT_UNADJ_DIMENS.LEFT_X + RECT_UNADJ_DIMENS.RIGHT_X) / 2f;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testProvideTextRenderingLocFromRect_ButtonRightJustified() {
        var mockInputsData = inaugural.soliloquy.tools.testing.Mock.<String, Object>generateMockMap(
                pairOf(Button_provideUnadjTextLocFromRect_horizontalAlignment,
                        HorizontalAlignment.RIGHT),
                pairOf(Button_provideUnadjTextLocFromRect_paddingHoriz, PADDING_HORIZ),
                pairOf(Button_provideUnadjTextLocFromRect_textHeight, TEXT_HEIGHT)
        );

        var output = buttonMethods.Button_provideUnadjTextLocFromRect(providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        var expectedX = RECT_UNADJ_DIMENS.RIGHT_X - PADDING_HORIZ;
        assertEquals(vertexOf(expectedX, TEX_RENDERING_LOC_Y_FROM_RECT_DIMENS), output);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_provideUnadjRectDimensFromText() {
        var textRenderingLoc = randomVertex();
        when(mockTextUnadjLoc.provide(anyLong())).thenReturn(textRenderingLoc);
        var mockInputsData = generateMockMap(
                pairOf(Button_provideUnadjRectDimensFromText_unadjTextLoc,
                        mockTextUnadjLoc),
                pairOf(Button_provideUnadjRectDimensFromText_lineLength, LINE_LENGTH),
                pairOf(Button_provideUnadjRectDimensFromText_textHeight, TEXT_HEIGHT),
                pairOf(Button_provideUnadjRectDimensFromText_textPaddingVert, PADDING_VERT),
                pairOf(Button_provideUnadjRectDimensFromText_textPaddingHoriz, PADDING_HORIZ)
        );

        var output = buttonMethods.Button_provideUnadjRectDimensFromText(providerInputs(
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
        verify(mockTextUnadjLoc, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_provideTexTileWidth() {
        testButton_provideTexTileDimension(
                ButtonMethods::Button_provideTexTileWidth,
                FloatBox::width
        );
    }

    @Test
    public void testButton_provideTexTileHeight() {
        testButton_provideTexTileDimension(
                ButtonMethods::Button_provideTexTileHeight,
                FloatBox::height
        );
    }

    private void testButton_provideTexTileDimension(
            BiFunction<ButtonMethods, FunctionalProvider.Inputs, Float> method,
            Function<FloatBox, Float> getDimension
    ) {
        Map<String, Object> mockInputsData = generateMockMap(
                pairOf(COMPONENT_UUID, BUTTON_UUID)
        );

        var output = method.apply(buttonMethods, providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        assertEquals(getDimension.apply(RECT_UNADJ_DIMENS), output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockRectUnadjDimens, once()).provide(TIMESTAMP);
    }

    @Test
    public void testButton_rectDimensWithAdj() {
        testButton_componentWithAdj(
                ButtonMethods::Button_rectDimensWithAdj,
                translateFloatBox(RECT_UNADJ_DIMENS, ORIGIN_ADJUST),
                mockRectUnadjDimens
        );
    }

    @Test
    public void testButton_spriteDimensWithAdj() {
        testButton_componentWithAdj(
                ButtonMethods::Button_spriteDimensWithAdj,
                translateFloatBox(SPRITE_UNADJ_DIMENS, ORIGIN_ADJUST),
                mockSpriteUnadjDimens
        );
    }

    @Test
    public void testButton_textLocWithAdj() {
        var unadjTextLoc = randomVertex();
        when(mockTextUnadjLoc.provide(anyLong())).thenReturn(unadjTextLoc);

        testButton_componentWithAdj(
                ButtonMethods::Button_textLocWithAdj,
                translateVertex(unadjTextLoc,
                        ORIGIN_ADJUST),
                mockTextUnadjLoc
        );
    }

    private <T> void testButton_componentWithAdj(
            BiFunction<ButtonMethods, FunctionalProvider.Inputs, T> method,
            T expectedOutput,
            ProviderAtTime<T> mockUnadjProvider
    ) {
        Map<String, Object> mockInputsData = generateMockMap(
                pairOf(COMPONENT_UUID, BUTTON_UUID)
        );
        when(mockButtonData.get(Constants.ORIGIN_ADJUST)).thenReturn(ORIGIN_ADJUST);

        var output = method.apply(buttonMethods, providerInputs(
                TIMESTAMP,
                null,
                mockInputsData
        ));

        assertEquals(expectedOutput, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(RECT_HOVER_STATE);
        verify(mockButtonData, once()).get(SPRITE_HOVER_STATE);
        verify(mockButtonData, once()).get(RENDERABLE_OPTIONS_DEFAULT);
        verify(mockUnadjProvider, once()).provide(TIMESTAMP);
        verify(mockButtonData, once()).get(Constants.ORIGIN_ADJUST);
    }
}
