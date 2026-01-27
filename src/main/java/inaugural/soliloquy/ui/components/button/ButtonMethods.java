package inaugural.soliloquy.ui.components.button;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class ButtonMethods {
    public final static String BUTTON_DIMENS = "BUTTON_DIMENS";
    public final static String BUTTON_RECT_DIMENS = "BUTTON_RECT_DIMENS";

    public final static String IS_PRESSED = "IS_PRESSED";

    final static String PRESSED_KEY = "PRESSED_KEY";
    final static String RECT_HOVER_STATE = "RECT_HOVER_STATE";
    final static String SPRITE_HOVER_STATE = "SPRITE_HOVER_STATE";

    final static String PRESS_ACTION = "PRESS_ACTION";
    final static String PRESS_SOUND_ID = "PRESS_SOUND_ID";
    final static String MOUSE_OVER_SOUND_ID = "MOUSE_OVER_SOUND_ID";
    final static String MOUSE_LEAVE_SOUND_ID = "MOUSE_LEAVE_SOUND_ID";
    final static String RELEASE_SOUND_ID = "RELEASE_SOUND_ID";

    final static String RENDERABLE_OPTIONS_DEFAULT = "RENDERABLE_OPTIONS_DEFAULT";
    final static String RENDERABLE_OPTIONS_HOVER = "RENDERABLE_OPTIONS_HOVER";
    final static String RENDERABLE_OPTIONS_PRESSED = "RENDERABLE_OPTIONS_PRESSED";

    private final Consumer<String> PLAY_SOUND;
    private final TriConsumer<Integer, MouseEventHandler.EventType, Runnable>
            SUBSCRIBE_TO_MOUSE_EVENTS;
    private final Function<String, Sprite> GET_SPRITE;
    private final Function<UUID, Component> GET_COMPONENT;

    public ButtonMethods(Consumer<String> playSound,
                         TriConsumer<Integer, MouseEventHandler.EventType, Runnable> subscribeToMouseEvents,
                         Function<String, Sprite> getSprite, Function<UUID, Component> getComponent) {
        PLAY_SOUND = Check.ifNull(playSound, "playSound");
        SUBSCRIBE_TO_MOUSE_EVENTS = Check.ifNull(subscribeToMouseEvents, "subscribeToMouseEvents");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    public final static String Button_setDimensForComponentAndContent =
            "Button_setDimensForComponentAndContent";

    public FloatBox Button_setDimensForComponentAndContent(Component button, long timestamp) {
        Long lastTimestamp = getFromData(button, LAST_TIMESTAMP);

        FloatBox unadjButtonDimens;

        if (lastTimestamp == null || timestamp != lastTimestamp) {
            Options currentStateOptions;
            if (getPressedState(button.data())) {
                currentStateOptions = getFromData(button, RENDERABLE_OPTIONS_PRESSED);
            }
            else if (getHoverState(button.data())) {
                currentStateOptions = getFromData(button, RENDERABLE_OPTIONS_HOVER);
            }
            else {
                currentStateOptions = getFromData(button, RENDERABLE_OPTIONS_DEFAULT);
            }

            var unadjRectDimens = defaultIfNull(currentStateOptions.unadjRectDimens, null,
                    dimens -> dimens.provide(timestamp));
            var unadjSpriteDimens = defaultIfNull(currentStateOptions.unadjSpriteDimens, null,
                    dimens -> dimens.provide(timestamp));

            if (unadjRectDimens != null) {
                if (unadjSpriteDimens != null) {
                    unadjButtonDimens = encompassing(unadjRectDimens, unadjSpriteDimens);
                }
                else {
                    unadjButtonDimens = unadjRectDimens;
                }
            }
            else {
                unadjButtonDimens = unadjSpriteDimens;
            }

            ProviderAtTime<Vertex> originOverrideProvider =
                    getFromData(button, ORIGIN_OVERRIDE_PROVIDER);
            var originOverride = defaultIfNull(originOverrideProvider, null, p -> p.provide(timestamp));
            FloatBox buttonDimens;
            if (originOverride != null) {
                var originAdjust = difference(unadjButtonDimens.topLeft(), originOverride);
                button.data().put(ORIGIN_ADJUST, originAdjust);
                if (unadjRectDimens != null) {
                    button.data().put(BUTTON_RECT_DIMENS, floatBoxOf(
                            translateVertex(unadjRectDimens.topLeft(), originAdjust),
                            translateVertex(unadjRectDimens.bottomRight(), originAdjust)
                    ));
                }
                else {
                    button.data().put(BUTTON_RECT_DIMENS, null);
                }
                buttonDimens = floatBoxOf(
                        originOverride,
                        unadjButtonDimens.width(),
                        unadjButtonDimens.height()
                );
            }
            else {
                button.data().put(ORIGIN_ADJUST, null);
                button.data().put(BUTTON_RECT_DIMENS, unadjRectDimens);
                buttonDimens = unadjButtonDimens;
            }

            button.data().put(BUTTON_DIMENS, buttonDimens);

            return buttonDimens;
        }
        else {
            return getFromData(button, BUTTON_DIMENS);
        }
    }

    public final static String Button_getDimens = "Button_getDimens";

    public FloatBox Button_getDimens(FunctionalProvider.Inputs inputs) {
        UUID componentId = getFromData(inputs, COMPONENT_UUID);
        var button = GET_COMPONENT.apply(componentId);
        return Button_setDimensForComponentAndContent(button, inputs.timestamp());
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
        return falseIfNull(getFromData(data, IS_PRESSED));
    }

    public void Button_pressKey(EventInputs e) {
        pressButton(e.component, e.keyCodepoint, e, null);
    }

    public void Button_releaseKey(EventInputs e) {
        releaseButton(e, e.keyCodepoint);
    }

    private void pressButton(Component c, Integer key, EventInputs e, Runnable afterFire) {
        var data = c.data();
        if (!falseIfNull(data.get(IS_PRESSED))) {
            data.put(IS_PRESSED, true);
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
                falseIfNull(data.get(IS_PRESSED)) &&
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
                data.put(IS_PRESSED, false);
            }
        }

        if (willFire) {
            data.put(IS_PRESSED, false);
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
        setRenderables(e, getFromData(e.component, RENDERABLE_OPTIONS_DEFAULT));
    }

    private void setRenderablesHover(EventInputs e) {
        setRenderables(e, getFromData(e.component, RENDERABLE_OPTIONS_HOVER));
    }

    private void setRenderablesPressed(EventInputs e) {
        setRenderables(e, getFromData(e.component, RENDERABLE_OPTIONS_PRESSED));
    }

    private void setRenderables(
            EventInputs e,
            Options options
    ) {
        Options defaultOptions = getFromData(e.component, RENDERABLE_OPTIONS_DEFAULT);

        var content = e.component.contentsRepresentation();
        var rect = getRect(content);
        var sprite = getSprite(content);
        var text = getText(content);

        if (rect != null) {
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
        ProviderAtTime<FloatBox> unadjRectDimens;
        ProviderAtTime<Color> bgColorTopLeft;
        ProviderAtTime<Color> bgColorTopRight;
        ProviderAtTime<Color> bgColorBottomLeft;
        ProviderAtTime<Color> bgColorBottomRight;
        ProviderAtTime<Integer> bgTexProvider;
        ProviderAtTime<Float> bgTexTileWidth;
        ProviderAtTime<Float> bgTexTileHeight;
        String spriteId;
        ProviderAtTime<FloatBox> unadjSpriteDimens;
        ColorShift spriteShift;
        ProviderAtTime<Vertex> unadjTextLoc;
        Map<Integer, ProviderAtTime<Color>> textColors;
        List<Integer> italics;
        List<Integer> bolds;

        public Options() {
        }

        public Options(
                ProviderAtTime<FloatBox> unadjRectDimens,
                ProviderAtTime<Color> bgColorTopLeft,
                ProviderAtTime<Color> bgColorTopRight,
                ProviderAtTime<Color> bgColorBottomLeft,
                ProviderAtTime<Color> bgColorBottomRight,
                ProviderAtTime<Integer> bgTexProvider,
                String spriteId,
                ProviderAtTime<FloatBox> unadjSpriteDimens,
                ColorShift spriteShift,
                ProviderAtTime<Vertex> unadjTextLoc,
                Map<Integer, ProviderAtTime<Color>> textColors,
                List<Integer> italics,
                List<Integer> bolds
        ) {
            this.unadjRectDimens = unadjRectDimens;
            this.bgColorTopLeft = bgColorTopLeft;
            this.bgColorTopRight = bgColorTopRight;
            this.bgColorBottomLeft = bgColorBottomLeft;
            this.bgColorBottomRight = bgColorBottomRight;
            this.bgTexProvider = bgTexProvider;
            this.spriteId = spriteId;
            this.unadjSpriteDimens = unadjSpriteDimens;
            this.spriteShift = spriteShift;
            this.unadjTextLoc = unadjTextLoc;
            this.textColors = textColors;
            this.italics = italics;
            this.bolds = bolds;
        }
    }

    final static String Button_provideUnadjTextLocFromRect = "Button_provideUnadjTextLocFromRect";
    final static String Button_provideUnadjTextLocFromRect_horizontalAlignment =
            "Button_provideUnadjTextLocFromRect_horizontalAlignment";
    final static String Button_provideUnadjTextLocFromRect_paddingHoriz =
            "Button_provideUnadjTextLocFromRect_paddingHoriz";
    final static String Button_provideUnadjTextLocFromRect_textHeight =
            "Button_provideUnadjTextLocFromRect_textHeight";

    public Vertex Button_provideUnadjTextLocFromRect(FunctionalProvider.Inputs inputs) {
        HorizontalAlignment horizontalAlignment =
                getFromData(inputs, Button_provideUnadjTextLocFromRect_horizontalAlignment);
        ProviderAtTime<FloatBox> unadjRectDimensProvider = getCurrentOptions(
                GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID))).unadjRectDimens;
        var unadjRectDimens = unadjRectDimensProvider.provide(inputs.timestamp());
        float paddingHoriz =
                getFromData(inputs, Button_provideUnadjTextLocFromRect_paddingHoriz);
        float textHeight = getFromData(inputs, Button_provideUnadjTextLocFromRect_textHeight);

        var unadjTextLocX = switch (horizontalAlignment) {
            case LEFT -> unadjRectDimens.LEFT_X + paddingHoriz;
            case CENTER -> (unadjRectDimens.LEFT_X + unadjRectDimens.RIGHT_X) / 2f;
            case RIGHT -> unadjRectDimens.RIGHT_X - paddingHoriz;
        };
        var texRenderingLocY = (unadjRectDimens.TOP_Y + unadjRectDimens.BOTTOM_Y - textHeight) / 2f;

        return vertexOf(unadjTextLocX, texRenderingLocY);
    }

    final static String Button_provideUnadjRectDimensFromText =
            "Button_provideUnadjRectDimensFromText";
    final static String Button_provideUnadjRectDimensFromText_unadjTextLoc =
            "Button_provideUnadjRectDimensFromText_unadjTextLoc";
    final static String Button_provideUnadjRectDimensFromText_lineLength =
            "Button_provideUnadjRectDimensFromText_lineLength";
    final static String Button_provideUnadjRectDimensFromText_textHeight =
            "Button_provideUnadjRectDimensFromText_textHeight";
    final static String Button_provideUnadjRectDimensFromText_textPaddingVert =
            "Button_provideUnadjRectDimensFromText_textPaddingVert";
    final static String Button_provideUnadjRectDimensFromText_textPaddingHoriz =
            "Button_provideUnadjRectDimensFromText_textPaddingHoriz";

    public FloatBox Button_provideUnadjRectDimensFromText(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<Vertex> unadjTextLocProvider = getFromData(inputs,
                Button_provideUnadjRectDimensFromText_unadjTextLoc);
        var textLoc = unadjTextLocProvider.provide(inputs.timestamp());
        float lineLength = getFromData(inputs, Button_provideUnadjRectDimensFromText_lineLength);
        float textHeight = getFromData(inputs, Button_provideUnadjRectDimensFromText_textHeight);
        float textPaddingVert =
                getFromData(inputs, Button_provideUnadjRectDimensFromText_textPaddingVert);
        float textPaddingHoriz =
                getFromData(inputs, Button_provideUnadjRectDimensFromText_textPaddingHoriz);
        var distFromCenterHoriz = textPaddingHoriz + lineLength / 2f;

        return floatBoxOf(
                textLoc.X - distFromCenterHoriz,
                textLoc.Y - textPaddingVert,
                textLoc.X + distFromCenterHoriz,
                textLoc.Y + textHeight + textPaddingVert
        );
    }

    final static String Button_provideTexTileWidth = "Button_provideTexTileWidth";

    public float Button_provideTexTileWidth(FunctionalProvider.Inputs inputs) {
        return Button_provideTexTileDimensComponent(inputs, FloatBox::width);
    }

    final static String Button_provideTexTileHeight = "Button_provideTexTileHeight";

    public float Button_provideTexTileHeight(FunctionalProvider.Inputs inputs) {
        return Button_provideTexTileDimensComponent(inputs, FloatBox::height);
    }

    private float Button_provideTexTileDimensComponent(FunctionalProvider.Inputs inputs,
                                                       Function<FloatBox, Float> getDimensComponent) {
        var button = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var currentOptions = getCurrentOptions(button);
        var rectDimens = currentOptions.unadjRectDimens.provide(inputs.timestamp());
        return getDimensComponent.apply(rectDimens);
    }

    final static String Button_rectDimensWithAdj = "Button_rectDimensWithAdj";

    public FloatBox Button_rectDimensWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                o -> o.unadjRectDimens,
                inaugural.soliloquy.tools.valueobjects.FloatBox::translateFloatBox
        );
    }

    final static String Button_spriteDimensWithAdj = "Button_spriteDimensWithAdj";

    public FloatBox Button_spriteDimensWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                o -> o.unadjSpriteDimens,
                inaugural.soliloquy.tools.valueobjects.FloatBox::translateFloatBox
        );
    }

    final static String Button_textLocWithAdj = "Button_textLocWithAdj";

    public Vertex Button_textLocWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                o -> o.unadjTextLoc,
                inaugural.soliloquy.tools.valueobjects.Vertex::translateVertex
        );
    }

    private <T> T provideWithAdj(FunctionalProvider.Inputs inputs,
                                 Function<Options, ProviderAtTime<T>> provideUnadj,
                                 BiFunction<T, Vertex, T> adjustment) {
        var button = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var currentOptions = getCurrentOptions(button);
        var unadj = provideUnadj.apply(currentOptions).provide(inputs.timestamp());
        Vertex originAdjust = getFromData(button, ORIGIN_ADJUST);
        if (originAdjust != null) {
            return adjustment.apply(unadj, originAdjust);
        }
        else {
            return unadj;
        }
    }

    private Options getCurrentOptions(Component button) {
        if (getPressedState(button.data())) {
            return getFromData(button, RENDERABLE_OPTIONS_PRESSED);
        }
        else if (getHoverState(button.data())) {
            return getFromData(button, RENDERABLE_OPTIONS_HOVER);
        }
        else {
            return getFromData(button, RENDERABLE_OPTIONS_DEFAULT);
        }
    }
}
