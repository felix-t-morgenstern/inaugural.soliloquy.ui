package inaugural.soliloquy.ui.components.button;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.entities.Action;
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
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
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
    final static String MOUSE_OVER_SOUND_ID = "mouseOverSoundId";
    final static String MOUSE_LEAVE_SOUND_ID = "mouseLeaveSoundId";
    final static String RELEASE_SOUND_ID = "releaseSoundId";

    final static String DEFAULT_RENDERABLE_OPTIONS = "defaultRenderableOptions";
    final static String HOVER_RENDERABLE_OPTIONS = "hoverRenderableOptions";
    final static String PRESSED_RENDERABLE_OPTIONS = "pressedRenderableOptions";

    private final Consumer<String> PLAY_SOUND;
    private final TriConsumer<Integer, MouseEventHandler.EventType, Runnable>
            SUBSCRIBE_TO_MOUSE_EVENTS;
    private final Function<String, Sprite> GET_SPRITE;

    public ButtonMethods(Consumer<String> playSound,
                         TriConsumer<Integer, MouseEventHandler.EventType, Runnable> subscribeToMouseEvents,
                         Function<String, Sprite> getSprite) {
        PLAY_SOUND = Check.ifNull(playSound, "playSound");
        SUBSCRIBE_TO_MOUSE_EVENTS = Check.ifNull(subscribeToMouseEvents, "subscribeToMouseEvents");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
    }

    public void pressMouse_Button(EventInputs e) {
        // When the button is pressed, it subscribes to the next release of the left mouse button.
        pressButton(e.component, null, e, () ->
                SUBSCRIBE_TO_MOUSE_EVENTS.accept(LEFT_MOUSE_BUTTON,
                        MouseEventHandler.EventType.RELEASE, () -> releaseButton(e, null)));

    }

    public void mouseOver_Button(EventInputs e) {
        var isHovering = getHoverState(e.component.data());
        e.component.data().put(getHoverStateDataKey(e), true);
        if (!isHovering && isNotPressedByKey(e.component.data())) {
            if (falseIfNull(getFromData(e.component.data(), PRESS_STATE))) {
                setRenderablesPressed(e);
            }
            else {
                setRenderablesHover(e);
            }
        }
    }

    public void mouseLeave_Button(EventInputs e) {
        var isHoveringPrev = getHoverState(e.component.data());
        e.component.data().put(getHoverStateDataKey(e), false);
        var isHoveringNow = getHoverState(e.component.data());
        if (isHoveringPrev && !isHoveringNow && isNotPressedByKey(e.component.data())) {
            setRenderablesDefault(e);
        }
    }

    private boolean isNotPressedByKey(Map<String, Object> data) {
        var isPressed = falseIfNull(getFromData(data, PRESS_STATE));
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

    public void pressKey_Button(EventInputs e) {
        pressButton(e.component, e.keyCodepoint, e, null);
    }

    public void releaseKey_Button(EventInputs e) {
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
            if (pressAction instanceof Action pressActionCast) {
                //noinspection unchecked
                pressActionCast.accept(null);
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
        var rectResult =
                content.stream().filter(c -> c instanceof RectangleRenderable && c.getZ() == RECT_Z)
                        .findFirst();
        var spriteResult =
                content.stream().filter(c -> c instanceof SpriteRenderable && c.getZ() == SPRITE_Z)
                        .findFirst();
        var textResult =
                content.stream().filter(c -> c instanceof TextLineRenderable && c.getZ() == TEXT_Z)
                        .findFirst();

        if (rectResult.isPresent()) {
            var rect = (RectangleRenderable) rectResult.get();
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
        if (spriteResult.isPresent()) {
            var sprite = (SpriteRenderable) spriteResult.get();
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
        if (textResult.isPresent()) {
            var text = (TextLineRenderable) textResult.get();
            text.colorProviderIndices().clear();
            text.colorProviderIndices()
                    .putAll(optionOrDefault(options, defaultOptions, o -> o.textColors));
            text.italicIndices().clear();
            text.italicIndices().addAll(optionOrDefault(options, defaultOptions, o -> o.italics));
            text.boldIndices().clear();
            text.boldIndices().addAll(optionOrDefault(options, defaultOptions, o -> o.bolds));
        }
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

    final static String provideTextRenderingLocFromRect_Button_horizontalAlignment =
            "provideTextRenderingLocFromRect_Button_horizontalAlignment";
    final static String provideTextRenderingLocFromRect_Button_rectDimensProvider =
            "provideTextRenderingLocFromRect_Button_rectDimensProvider";
    final static String provideTextRenderingLocFromRect_Button_paddingHoriz =
            "provideTextRenderingLocFromRect_Button_paddingHoriz";
    final static String provideTextRenderingLocFromRect_Button_textHeight =
            "provideTextRenderingLocFromRect_Button_textHeight";

    public Vertex provideTextRenderingLocFromRect_Button(FunctionalProvider.Inputs inputs) {
        HorizontalAlignment horizontalAlignment =
                getFromData(inputs.data(),
                        provideTextRenderingLocFromRect_Button_horizontalAlignment);
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(),
                        provideTextRenderingLocFromRect_Button_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        float paddingHoriz =
                getFromData(inputs.data(), provideTextRenderingLocFromRect_Button_paddingHoriz);
        float textHeight =
                getFromData(inputs.data(), provideTextRenderingLocFromRect_Button_textHeight);

        var texRenderingLocX = switch (horizontalAlignment) {
            case LEFT -> rectDimens.LEFT_X + paddingHoriz;
            case CENTER -> (rectDimens.LEFT_X + rectDimens.RIGHT_X) / 2f;
            case RIGHT -> rectDimens.RIGHT_X - paddingHoriz;
            default -> 0F;
        };
        var texRenderingLocY = (rectDimens.TOP_Y + rectDimens.BOTTOM_Y - textHeight) / 2f;

        return vertexOf(texRenderingLocX, texRenderingLocY);
    }

    final static String provideRectDimensFromText_Button_textRenderingLocProvider =
            "provideRectDimensFromText_Button_textRenderingLocProvider";
    final static String provideRectDimensFromText_Button_lineLength =
            "provideRectDimensFromText_Button_lineLength";
    final static String provideRectDimensFromText_Button_textHeight =
            "provideRectDimensFromText_Button_textHeight";
    final static String provideRectDimensFromText_Button_textPaddingVert =
            "provideRectDimensFromText_Button_textPaddingVert";
    final static String provideRectDimensFromText_Button_textPaddingHoriz =
            "provideRectDimensFromText_Button_textPaddingHoriz";

    public FloatBox provideRectDimensFromText_Button(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<Vertex> textRenderingLocProvider = getFromData(inputs.data(),
                provideRectDimensFromText_Button_textRenderingLocProvider);
        var textRenderingLoc = textRenderingLocProvider.provide(inputs.timestamp());
        float lineLength = getFromData(inputs.data(), provideRectDimensFromText_Button_lineLength);
        float textHeight = getFromData(inputs.data(), provideRectDimensFromText_Button_textHeight);
        float textPaddingVert =
                getFromData(inputs.data(), provideRectDimensFromText_Button_textPaddingVert);
        float textPaddingHoriz =
                getFromData(inputs.data(), provideRectDimensFromText_Button_textPaddingHoriz);
        var distFromCenterHoriz = textPaddingHoriz + lineLength / 2f;

        return floatBoxOf(
                textRenderingLoc.X - distFromCenterHoriz,
                textRenderingLoc.Y - textPaddingVert,
                textRenderingLoc.X + distFromCenterHoriz,
                textRenderingLoc.Y + textHeight + textPaddingVert
        );
    }

    final static String provideTexTileDimens_Button_rectDimensProvider =
            "provideTexTileDimens_Button_rectDimensProvider";

    public float provideTexTileWidth_Button(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(), provideTexTileDimens_Button_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        return rectDimens.width();
    }

    public float provideTexTileHeight_Button(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> rectDimensProvider =
                getFromData(inputs.data(), provideTexTileDimens_Button_rectDimensProvider);
        var rectDimens = rectDimensProvider.provide(inputs.timestamp());
        return rectDimens.height();
    }
}
