package inaugural.soliloquy.ui.components.button;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.ComponentMethods;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.mouse.MouseEventHandler;
import soliloquy.specs.ui.EventInputs;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class ButtonMethods {
    public final static String PRESS_STATE = "isPressed";

    final static String PRESSED_KEY = "pressedKey";
    final static String RECT_HOVER_STATE = "isHoveringRect";
    final static String SPRITE_HOVER_STATE = "isHoveringSprite";

    final static String PRESS_ACTION = "pressAction";
    final static String PRESS_SOUND_ID = "pressSoundId";
    final static String MOUSE_OVER_SOUND_ID = "Button_mouseOverSoundId";
    final static String MOUSE_LEAVE_SOUND_ID = "mouseLeaveSoundId";
    final static String RELEASE_SOUND_ID = "releaseSoundId";

    final static String DEFAULT_RENDERABLE_OPTIONS = "defaultRenderableOptions";
    final static String HOVER_RENDERABLE_OPTIONS = "hoverRenderableOptions";
    final static String PRESSED_RENDERABLE_OPTIONS = "pressedRenderableOptions";

    public final static String ORIG_CONTENT_IS_LOADED_DEFAULT = "ORIG_CONTENT_IS_LOADED_DEFAULT";
    public final static String ORIG_CONTENT_DIMENS_PROVIDERS_DEFAULT =
            "ORIG_CONTENT_DIMENS_PROVIDERS_DEFAULT";
    public final static String ORIG_CONTENT_LOC_PROVIDERS_DEFAULT =
            "ORIG_CONTENT_LOC_PROVIDERS_DEFAULT";

    public final static String ORIG_CONTENT_IS_LOADED_HOVER = "ORIG_CONTENT_IS_LOADED_HOVER";
    public final static String ORIG_CONTENT_DIMENS_PROVIDERS_HOVER =
            "ORIG_CONTENT_DIMENS_PROVIDERS_HOVER";
    public final static String ORIG_CONTENT_LOC_PROVIDERS_HOVER =
            "ORIG_CONTENT_LOC_PROVIDERS_HOVER";

    public final static String ORIG_CONTENT_IS_LOADED_PRESSED = "ORIG_CONTENT_IS_LOADED_PRESSED";
    public final static String ORIG_CONTENT_DIMENS_PROVIDERS_PRESSED =
            "ORIG_CONTENT_DIMENS_PROVIDERS_PRESSED";
    public final static String ORIG_CONTENT_LOC_PROVIDERS_PRESSED =
            "ORIG_CONTENT_LOC_PROVIDERS_PRESSED";

    private final Consumer<String> PLAY_SOUND;
    private final TriConsumer<Integer, MouseEventHandler.EventType, Runnable>
            SUBSCRIBE_TO_MOUSE_EVENTS;
    private final Function<String, Sprite> GET_SPRITE;
    private final ComponentMethods COMPONENT_METHODS;

    public ButtonMethods(Consumer<String> playSound,
                         TriConsumer<Integer, MouseEventHandler.EventType, Runnable> subscribeToMouseEvents,
                         Function<String, Sprite> getSprite, ComponentMethods componentMethods) {
        PLAY_SOUND = Check.ifNull(playSound, "playSound");
        SUBSCRIBE_TO_MOUSE_EVENTS = Check.ifNull(subscribeToMouseEvents, "subscribeToMouseEvents");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
        COMPONENT_METHODS = Check.ifNull(componentMethods, "componentMethods");
    }

    public final static String Button_setDimensForComponentAndContent =
            "Button_setDimensForComponentAndContent";

    public FloatBox Button_setDimensForComponentAndContent(Component component, long timestamp) {
        Long lastTimestamp = getFromData(component.data(), LAST_TIMESTAMP);

        var componentDimens =
                COMPONENT_METHODS.Component_setDimensForComponentAndContent(component, timestamp);

        if (lastTimestamp == null || timestamp != lastTimestamp) {
            if (getPressedState(component.data()) && !falseIfNull(getFromData(component.data(), ORIG_CONTENT_IS_LOADED_PRESSED))) {
                updateProviders(
                        component,
                        ORIG_CONTENT_DIMENS_PROVIDERS_PRESSED,
                        ORIG_CONTENT_LOC_PROVIDERS_PRESSED,
                        PRESSED_RENDERABLE_OPTIONS,
                        ORIG_CONTENT_IS_LOADED_PRESSED
                );
            }
            else if (getHoverState(component.data()) && !falseIfNull(getFromData(component.data(), ORIG_CONTENT_IS_LOADED_HOVER))) {
                updateProviders(
                        component,
                        ORIG_CONTENT_DIMENS_PROVIDERS_HOVER,
                        ORIG_CONTENT_LOC_PROVIDERS_HOVER,
                        HOVER_RENDERABLE_OPTIONS,
                        ORIG_CONTENT_IS_LOADED_HOVER
                );
            }
            else if(!falseIfNull(getFromData(component.data(), ORIG_CONTENT_IS_LOADED_DEFAULT))) {
                updateProviders(
                        component,
                        ORIG_CONTENT_DIMENS_PROVIDERS_DEFAULT,
                        ORIG_CONTENT_LOC_PROVIDERS_DEFAULT,
                        DEFAULT_RENDERABLE_OPTIONS,
                        ORIG_CONTENT_IS_LOADED_DEFAULT
                );
            }
        }

        return componentDimens;
    }

    private void updateProviders(Component component,
                                 String origDimensProvidersForStateKey,
                                 String origLocProvidersForStateKey,
                                 String optionsKey,
                                 String origContentIsLoadedForStateKey) {

        Map<UUID, ProviderAtTime<FloatBox>> origContentDimensProviders =
                getFromData(component.data(), ORIG_CONTENT_DIMENS_PROVIDERS);
        Map<UUID, ProviderAtTime<Vertex>> origContentLocProviders =
                getFromData(component.data(), ORIG_CONTENT_LOC_PROVIDERS);
        component.data()
                .put(origDimensProvidersForStateKey, origContentDimensProviders);
        component.data().put(origLocProvidersForStateKey, origContentLocProviders);

        var content = component.contentsRepresentation();
        var rect = getRect(content);
        var sprite = getSprite(content);
        if (rect != null) {
            ((Options) component.data().get(optionsKey)).rectDimens =
                    rect.getRenderingDimensionsProvider();
        }
        if (sprite != null) {
            ((Options) component.data().get(optionsKey)).spriteDimens =
                    sprite.getRenderingDimensionsProvider();
        }

        component.data().put(origContentIsLoadedForStateKey, true);
    }

    public void Button_pressMouse(EventInputs e) {
        // When the button is pressed, it subscribes to the next release of the left mouse button.
        pressButton(e.component, null, e, () ->
                SUBSCRIBE_TO_MOUSE_EVENTS.accept(LEFT_MOUSE_BUTTON,
                        MouseEventHandler.EventType.RELEASE, () -> releaseButton(e, null)));

    }

    public void Button_mouseOver(EventInputs e) {
        var isHovering = getHoverState(e.component.data());
        e.component.data().put(getHoverStateDataKey(e), true);
        if (!isHovering && isNotPressedByKey(e.component.data())) {
            if (getPressedState(e.component.data())) {
                setRenderablesPressed(e);
            }
            else {
                setRenderablesHover(e);
            }
        }
    }
    
    public void Button_mouseLeave(EventInputs e) {
        var isHoveringPrev = getHoverState(e.component.data());
        e.component.data().put(getHoverStateDataKey(e), false);
        var isHoveringNow = getHoverState(e.component.data());
        if (isHoveringPrev && !isHoveringNow && isNotPressedByKey(e.component.data())) {
            setRenderablesDefault(e);
        }
    }

    private boolean isNotPressedByKey(Map<String, Object> data) {
        var isPressed = getPressedState(data);
        Integer pressedKey = getFromData(data, PRESSED_KEY);
        return !isPressed || pressedKey == null;
    }

    private String getHoverStateDataKey(EventInputs e) {
        return e.renderable instanceof SpriteRenderable ? SPRITE_HOVER_STATE : RECT_HOVER_STATE;
    }

    private boolean getHoverState(Map<String, Object> data) {
        return falseIfNull(getFromData(data, RECT_HOVER_STATE)) ||
                falseIfNull(getFromData(data, SPRITE_HOVER_STATE));
    }

    private boolean getPressedState(Map<String, Object> data) {
        return falseIfNull(getFromData(data, PRESS_STATE));
    }

    public void Button_pressKey(EventInputs e) {
        pressButton(e.component, e.keyCodepoint, e, null);
    }

    public void Button_releaseKey(EventInputs e) {
        releaseButton(e, e.keyCodepoint);
    }

    private void pressButton(Component c, Integer key, EventInputs e, Runnable afterFire) {
        var data = c.data();
        if (!falseIfNull(data.get(PRESS_STATE))) {
            data.put(PRESS_STATE, true);
            data.put(PRESSED_KEY, key);
            var pressSoundId = data.get(PRESS_SOUND_ID);
            if (pressSoundId instanceof String pressSoundIdStr &&
                    !Strings.isNullOrEmpty(pressSoundIdStr)) {
                PLAY_SOUND.accept(pressSoundIdStr);
            }
            setRenderablesPressed(e);

            if (afterFire != null) {
                afterFire.run();
            }
        }
    }

    private void releaseButton(EventInputs e, Integer keyCodepoint) {
        var willFire = false;
        var data = e.component.data();
        var pressedKey = data.get(PRESSED_KEY);
        // If it's a key action, we need to make sure the button has already been pressed by the
        // key being released.
        if (keyCodepoint != null &&
                falseIfNull(data.get(PRESS_STATE)) &&
                keyCodepoint.equals(pressedKey)) {
            willFire = true;
        }
        // If it's a mouse action, we need to verify whether the mouse is still hovering on the
        // button.
        else if (keyCodepoint == null) {
            if (getHoverState(data)) {
                willFire = true;
            }
            else {
                data.put(PRESS_STATE, false);
            }
        }

        if (willFire) {
            data.put(PRESS_STATE, false);
            data.put(PRESSED_KEY, null);
            var releaseSoundId = data.get(RELEASE_SOUND_ID);
            if (releaseSoundId instanceof String releaseSoundIdStr &&
                    !Strings.isNullOrEmpty(releaseSoundIdStr)) {
                PLAY_SOUND.accept(releaseSoundIdStr);
            }
            var pressAction = data.get(PRESS_ACTION);
            //noinspection rawtypes
            if (pressAction instanceof Consumer pressActionCast) {
                //noinspection unchecked
                pressActionCast.accept(e);
            }
            setRenderablesDefault(e);
        }
    }

    private void setRenderablesDefault(EventInputs e) {
        setRenderables(e, getFromData(e.component.data(), DEFAULT_RENDERABLE_OPTIONS));
    }

    private void setRenderablesHover(EventInputs e) {
        setRenderables(e, getFromData(e.component.data(), HOVER_RENDERABLE_OPTIONS));
    }

    private void setRenderablesPressed(EventInputs e) {
        setRenderables(e, getFromData(e.component.data(), PRESSED_RENDERABLE_OPTIONS));
    }

    private void setRenderables(
            EventInputs e,
            Options options
    ) {
        Options defaultOptions =
                getFromData(e.component.data(), DEFAULT_RENDERABLE_OPTIONS);

        var content = e.component.contentsRepresentation();
        var rect = getRect(content);
        var sprite = getSprite(content);
        var text = getText(content);

        if (rect != null) {
            rect.setRenderingDimensionsProvider(
                    optionOrDefault(options, defaultOptions, o -> o.rectDimens));
            rect.setTopLeftColorProvider(
                    optionOrDefault(options, defaultOptions, o -> o.bgColorTopLeft));
            rect.setTopRightColorProvider(
                    optionOrDefault(options, defaultOptions, o -> o.bgColorTopRight));
            rect.setBottomLeftColorProvider(
                    optionOrDefault(options, defaultOptions, o -> o.bgColorBottomLeft));
            rect.setBottomRightColorProvider(
                    optionOrDefault(options, defaultOptions, o -> o.bgColorBottomRight));
            rect.setTextureIdProvider(
                    optionOrDefault(options, defaultOptions, o -> o.bgTexProvider));
        }
        if (sprite != null) {
            sprite.setSprite(
                    GET_SPRITE.apply(optionOrDefault(options, defaultOptions, o -> o.spriteId)));
            sprite.setRenderingDimensionsProvider(
                    optionOrDefault(options, defaultOptions, o -> o.spriteDimens));

            sprite.colorShifts().clear();
            var shift = optionOrDefault(options, defaultOptions, o -> o.spriteShift);
            if (shift != null) {
                sprite.colorShifts().add(shift);
            }
        }
        if (text != null) {
            text.colorProviderIndices().clear();
            text.colorProviderIndices()
                    .putAll(optionOrDefault(options, defaultOptions, o -> o.textColors));
            text.italicIndices().clear();
            text.italicIndices().addAll(optionOrDefault(options, defaultOptions, o -> o.italics));
            text.boldIndices().clear();
            text.boldIndices().addAll(optionOrDefault(options, defaultOptions, o -> o.bolds));
        }
    }

    private RectangleRenderable getRect(Set<Renderable> content) {
        var fromContent =
                content.stream().filter(c -> c instanceof RectangleRenderable && c.getZ() == RECT_Z)
                        .findFirst();
        return (RectangleRenderable) fromContent.orElse(null);
    }

    private TextLineRenderable getText(Set<Renderable> content) {
        var fromContent =
                content.stream().filter(c -> c instanceof TextLineRenderable && c.getZ() == TEXT_Z)
                        .findFirst();
        return (TextLineRenderable) fromContent.orElse(null);
    }

    private SpriteRenderable getSprite(Set<Renderable> content) {
        var fromContent =
                content.stream().filter(c -> c instanceof SpriteRenderable && c.getZ() == SPRITE_Z)
                        .findFirst();
        return (SpriteRenderable) fromContent.orElse(null);
    }

    private <T> T optionOrDefault(Options options, Options defaults,
                                  Function<Options, T> getVal) {
        var option = getVal.apply(options);
        if (option != null) {
            return option;
        }
        return getVal.apply(defaults);
    }

    static class Options {
        ProviderAtTime<FloatBox> rectDimens;
        ProviderAtTime<Color> bgColorTopLeft;
        ProviderAtTime<Color> bgColorTopRight;
        ProviderAtTime<Color> bgColorBottomLeft;
        ProviderAtTime<Color> bgColorBottomRight;
        ProviderAtTime<Integer> bgTexProvider;
        String spriteId;
        ProviderAtTime<FloatBox> spriteDimens;
        ColorShift spriteShift;
        Map<Integer, ProviderAtTime<Color>> textColors;
        List<Integer> italics;
        List<Integer> bolds;

        public Options() {
        }

        public Options(ProviderAtTime<FloatBox> rectDimens,
                       ProviderAtTime<Color> bgColorTopLeft,
                       ProviderAtTime<Color> bgColorTopRight,
                       ProviderAtTime<Color> bgColorBottomLeft,
                       ProviderAtTime<Color> bgColorBottomRight,
                       ProviderAtTime<Integer> bgTexProvider,
                       String spriteId,
                       ProviderAtTime<FloatBox> spriteDimens,
                       ColorShift spriteShift,
                       Map<Integer, ProviderAtTime<Color>> textColors,
                       List<Integer> italics,
                       List<Integer> bolds) {
            this.rectDimens = rectDimens;
            this.bgColorTopLeft = bgColorTopLeft;
            this.bgColorTopRight = bgColorTopRight;
            this.bgColorBottomLeft = bgColorBottomLeft;
            this.bgColorBottomRight = bgColorBottomRight;
            this.bgTexProvider = bgTexProvider;
            this.spriteId = spriteId;
            this.spriteDimens = spriteDimens;
            this.spriteShift = spriteShift;
            this.textColors = textColors;
            this.italics = italics;
            this.bolds = bolds;
        }
    }

    final static String Button_provideTextRenderingLocFromRect = "Button_provideTextRenderingLocFromRect";
    final static String Button_provideTextRenderingLocFromRect_horizontalAlignment =
            "Button_provideTextRenderingLocFromRect_horizontalAlignment";
    final static String Button_provideTextRenderingLocFromRect_rectDimensProvider =
            "Button_provideTextRenderingLocFromRect_rectDimensProvider";
    final static String Button_provideTextRenderingLocFromRect_paddingHoriz =
            "Button_provideTextRenderingLocFromRect_paddingHoriz";
    final static String Button_provideTextRenderingLocFromRect_textHeight =
            "Button_provideTextRenderingLocFromRect_textHeight";

    public Vertex Button_provideTextRenderingLocFromRect(FunctionalProvider.Inputs inputs) {
        HorizontalAlignment horizontalAlignment =
                getFromData(inputs.data(),
                        Button_provideTextRenderingLocFromRect_horizontalAlignment);
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(),
                        Button_provideTextRenderingLocFromRect_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        float paddingHoriz =
                getFromData(inputs.data(), Button_provideTextRenderingLocFromRect_paddingHoriz);
        float textHeight =
                getFromData(inputs.data(), Button_provideTextRenderingLocFromRect_textHeight);

        var texRenderingLocX = switch (horizontalAlignment) {
            case LEFT -> rectDimens.LEFT_X + paddingHoriz;
            case CENTER -> (rectDimens.LEFT_X + rectDimens.RIGHT_X) / 2f;
            case RIGHT -> rectDimens.RIGHT_X - paddingHoriz;
            default -> 0F;
        };
        var texRenderingLocY = (rectDimens.TOP_Y + rectDimens.BOTTOM_Y - textHeight) / 2f;

        return vertexOf(texRenderingLocX, texRenderingLocY);
    }
    
    final static String Button_provideRectDimensFromText = "Button_provideRectDimensFromText";
    final static String Button_provideRectDimensFromText_textRenderingLocProvider =
            "Button_provideRectDimensFromText_textRenderingLocProvider";
    final static String Button_provideRectDimensFromText_lineLength =
            "Button_provideRectDimensFromText_lineLength";
    final static String Button_provideRectDimensFromText_textHeight =
            "Button_provideRectDimensFromText_textHeight";
    final static String Button_provideRectDimensFromText_textPaddingVert =
            "Button_provideRectDimensFromText_textPaddingVert";
    final static String Button_provideRectDimensFromText_textPaddingHoriz =
            "Button_provideRectDimensFromText_textPaddingHoriz";

    public FloatBox Button_provideRectDimensFromText(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<Vertex> textRenderingLocProvider = getFromData(inputs.data(),
                Button_provideRectDimensFromText_textRenderingLocProvider);
        var textRenderingLoc = textRenderingLocProvider.provide(inputs.timestamp());
        float lineLength = getFromData(inputs.data(), Button_provideRectDimensFromText_lineLength);
        float textHeight = getFromData(inputs.data(), Button_provideRectDimensFromText_textHeight);
        float textPaddingVert =
                getFromData(inputs.data(), Button_provideRectDimensFromText_textPaddingVert);
        float textPaddingHoriz =
                getFromData(inputs.data(), Button_provideRectDimensFromText_textPaddingHoriz);
        var distFromCenterHoriz = textPaddingHoriz + lineLength / 2f;

        return floatBoxOf(
                textRenderingLoc.X - distFromCenterHoriz,
                textRenderingLoc.Y - textPaddingVert,
                textRenderingLoc.X + distFromCenterHoriz,
                textRenderingLoc.Y + textHeight + textPaddingVert
        );
    }

    final static String Button_provideTexTileWidth = "Button_provideTexTileWidth";
    final static String provideTexTileDimens_Button_rectDimensProvider =
            "provideTexTileDimens_Button_rectDimensProvider";

    public float Button_provideTexTileWidth(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(), provideTexTileDimens_Button_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        return rectDimens.width();
    }

    final static String Button_provideTexTileHeight = "Button_provideTexTileHeight";

    public float Button_provideTexTileHeight(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(), provideTexTileDimens_Button_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        return rectDimens.height();
    }
}
