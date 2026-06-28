package inaugural.soliloquy.ui.components.button;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.mouse.Mouse;
import soliloquy.specs.ui.EventInputs;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.defaultIfNullElseTransform;
import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.IMAGE_ASSET_Z;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.RECT_Z;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.input.mouse.Mouse.EventType.RELEASE;

public class ButtonMethods {
    public final static String BUTTON_DIMENS = "BUTTON_DIMENS";
    public final static String BUTTON_UNADJ_DIMENS = "BUTTON_UNADJ_DIMENS";
    public final static String BUTTON_RECT_DIMENS = "BUTTON_RECT_DIMENS";

    public final static String IS_PRESSED = "IS_PRESSED";

    final static String PRESSED_KEY = "PRESSED_KEY";
    final static String RECT_HOVER_STATE = "RECT_HOVER_STATE";
    final static String SPRITE_HOVER_STATE = "SPRITE_HOVER_STATE";

    final static String PRESS_CONSUMER = "PRESS_CONSUMER";
    final static String PRESS_SOUND_ID = "PRESS_SOUND_ID";
    final static String MOUSE_OVER_SOUND_ID = "MOUSE_OVER_SOUND_ID";
    final static String MOUSE_LEAVE_SOUND_ID = "MOUSE_LEAVE_SOUND_ID";
    final static String RELEASE_SOUND_ID = "RELEASE_SOUND_ID";

    final static String RENDERABLE_OPTIONS_DEFAULT = "RENDERABLE_OPTIONS_DEFAULT";
    final static String RENDERABLE_OPTIONS_HOVER = "RENDERABLE_OPTIONS_HOVER";
    final static String RENDERABLE_OPTIONS_PRESSED = "RENDERABLE_OPTIONS_PRESSED";

    final static String RECT_UNADJ_DIMENS_PROVIDER = "RECT_UNADJ_DIMENS_PROVIDER";
    final static String TEXT_BLOCK_UNADJ_LOC_PROVIDER = "TEXT_BLOCK_UNADJ_LOC_PROVIDER";

    private final Consumer<String> PLAY_SOUND;
    private final TriConsumer<Integer, Mouse.EventType, Runnable>
            SUBSCRIBE_TO_MOUSE_EVENTS;
    private final Function<UUID, Component> GET_COMPONENT;

    public ButtonMethods(Consumer<String> playSound,
                         TriConsumer<Integer, Mouse.EventType, Runnable> subscribeToMouseEvents,
                         Function<UUID, Component> getComponent) {
        PLAY_SOUND = Check.ifNull(playSound, "playSound");
        SUBSCRIBE_TO_MOUSE_EVENTS = Check.ifNull(subscribeToMouseEvents, "subscribeToMouseEvents");
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    public final static String Button_setDimensForComponentAndContent =
            "Button_setDimensForComponentAndContent";

    public FloatBox Button_setDimensForComponentAndContent(Component button, long timestamp) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (button) {
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

                @SuppressWarnings("unchecked") var unadjRectDimens = defaultIfNullElseTransform(
                        (ProviderAtTime<FloatBox>) getFromData(button, RECT_UNADJ_DIMENS_PROVIDER),
                        dimens -> dimens.provide(timestamp),
                        null
                );
                var unadjImageAssetDimens = defaultIfNullElseTransform(
                        currentStateOptions.unadjImageAssetDimens,
                        dimens -> dimens.provide(timestamp),
                        null
                );

                if (unadjRectDimens != null) {
                    if (unadjImageAssetDimens != null) {
                        unadjButtonDimens = encompassing(unadjRectDimens, unadjImageAssetDimens);
                    }
                    else {
                        unadjButtonDimens = unadjRectDimens;
                    }
                }
                else {
                    unadjButtonDimens = unadjImageAssetDimens;
                }

                button.data().put(BUTTON_UNADJ_DIMENS, unadjButtonDimens);

                ProviderAtTime<Vertex> componentOriginProvider =
                        getFromData(button, COMPONENT_ORIGIN_PROVIDER);
                var componentOrigin =
                        defaultIfNullElseTransform(componentOriginProvider,
                                p -> p.provide(timestamp),
                                null);

                FloatBox buttonAdjDimens;
                if (componentOrigin != null) {
                    var originAdjust = difference(unadjButtonDimens.topLeft(), componentOrigin);
                    button.data().put(COMPONENT_ORIGIN_ADJUST, originAdjust);
                    if (unadjRectDimens != null) {
                        button.data().put(BUTTON_RECT_DIMENS, floatBoxOf(
                                translateVertex(unadjRectDimens.topLeft(), originAdjust),
                                translateVertex(unadjRectDimens.bottomRight(), originAdjust)
                        ));
                    }
                    else {
                        button.data().put(BUTTON_RECT_DIMENS, null);
                    }
                    buttonAdjDimens = floatBoxOf(
                            componentOrigin,
                            unadjButtonDimens.width(),
                            unadjButtonDimens.height()
                    );
                }
                else {
                    button.data().put(COMPONENT_ORIGIN_ADJUST, null);
                    button.data().put(BUTTON_RECT_DIMENS, unadjRectDimens);
                    buttonAdjDimens = unadjButtonDimens;
                }

                button.data().put(BUTTON_DIMENS, buttonAdjDimens);

                return buttonAdjDimens;
            }
            else {
                return getFromData(button, BUTTON_DIMENS);
            }
        }
    }

    public final static String Button_getDimens = "Button_getDimens";

    public FloatBox Button_getDimens(FunctionalProvider.Inputs inputs) {
        return Button_setDimensForComponentAndContent(getButton(inputs), inputs.timestamp());
    }

    public final static String Button_pressMouse = "Button_pressMouse";

    public void Button_pressMouse(EventInputs e) {
        // When the button is pressed, it subscribes to the next release of the left mouse button.
        pressButton(e.component, null, e, () ->
                SUBSCRIBE_TO_MOUSE_EVENTS.accept(LEFT_MOUSE_BUTTON, RELEASE,
                        () -> releaseButton(e, null)));

    }

    public final static String Button_mouseOver = "Button_mouseOver";

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

    public final static String Button_mouseLeave = "Button_mouseLeave";

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

    public final static String Button_pressKey = "Button_pressKey";

    public void Button_pressKey(EventInputs e) {
        pressButton(e.component, e.keyCodepoint, e, null);
    }

    public final static String Button_releaseKey = "Button_releaseKey";

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
            var pressConsumer = data.get(PRESS_CONSUMER);
            //noinspection rawtypes
            if (pressConsumer instanceof Consumer pressActionCast) {
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
        var imageAsset = getImageAsset(content);

        if (rect != null) {
            e.component.remove(rect);
            var rectFromOptions = optionOrDefault(options, defaultOptions, o -> o.rect);
            rectFromOptions.setContainingComponent(e.component);
            e.component.add(rectFromOptions);
        }
        if (imageAsset != null) {
            e.component.remove(imageAsset);
            var imageAssetFromOptions = optionOrDefault(options, defaultOptions, o -> o.imageAsset);
            imageAssetFromOptions.setContainingComponent(e.component);
            e.component.add(imageAssetFromOptions);
        }
    }

    private RectangleRenderable getRect(Set<Renderable> content) {
        var fromContent =
                content.stream().filter(c -> c instanceof RectangleRenderable && c.getZ() == RECT_Z)
                        .findFirst();
        return (RectangleRenderable) fromContent.orElse(null);
    }

    private ImageAssetRenderable getImageAsset(Set<Renderable> content) {
        var fromContent = content.stream()
                .filter(c -> c instanceof ImageAssetRenderable && c.getZ() == IMAGE_ASSET_Z)
                .findFirst();
        return (ImageAssetRenderable) fromContent.orElse(null);
    }

    private <T> T optionOrDefault(Options options, Options defaults,
                                  Function<Options, T> getVal) {
        var option = getVal.apply(options);
        if (option != null) {
            return option;
        }
        return getVal.apply(defaults);
    }

    final static String Button_provideUnadjTextBlockLocFromRect =
            "Button_provideUnadjTextBlockLocFromRect";
    final static String Button_provideUnadjTextBlockLocFromRect_horizontalAlignment =
            "Button_provideUnadjTextBlockLocFromRect_horizontalAlignment";
    final static String Button_provideUnadjTextBlockLocFromRect_paddingHoriz =
            "Button_provideUnadjTextBlockLocFromRect_paddingHoriz";
    final static String Button_provideUnadjTextBlockLocFromRect_textBlockHeight =
            "Button_provideUnadjTextBlockLocFromRect_textBlockHeight";
    final static String Button_provideUnadjTextBlockLocFromRect_lineLength =
            "Button_provideUnadjTextBlockLocFromRect_lineLength";

    public Vertex Button_provideUnadjTextBlockLocFromRect(FunctionalProvider.Inputs inputs) {
        HorizontalAlignment horizontalAlignment =
                getFromData(inputs, Button_provideUnadjTextBlockLocFromRect_horizontalAlignment);
        var button = getButton(inputs);
        ProviderAtTime<FloatBox> unadjRectDimensProvider =
                getFromData(button, RECT_UNADJ_DIMENS_PROVIDER);
        var unadjRectDimens = unadjRectDimensProvider.provide(inputs.timestamp());
        float paddingHoriz =
                getFromData(inputs, Button_provideUnadjTextBlockLocFromRect_paddingHoriz);
        float textHeight =
                getFromData(inputs, Button_provideUnadjTextBlockLocFromRect_textBlockHeight);
        float lineLength =
                getFromData(inputs, Button_provideUnadjTextBlockLocFromRect_lineLength);

        var unadjTextBlockLocX = switch (horizontalAlignment) {
            case LEFT -> unadjRectDimens.LEFT_X + paddingHoriz;
            case CENTER -> ((unadjRectDimens.LEFT_X + unadjRectDimens.RIGHT_X - lineLength) / 2f);
            case RIGHT -> unadjRectDimens.RIGHT_X - paddingHoriz - lineLength;
        };
        var unadjTextBlockLocY =
                (unadjRectDimens.TOP_Y + unadjRectDimens.BOTTOM_Y - textHeight) / 2f;

        return vertexOf(unadjTextBlockLocX, unadjTextBlockLocY);
    }

    final static String Button_provideCenteredUnadjTextBlockLocFromRect =
            "Button_provideCenteredUnadjTextBlockLocFromRect";
    final static String Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider =
            "Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider";
    final static String Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens =
            "Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens";

    public Vertex Button_provideCenteredUnadjTextBlockLocFromRect(
            FunctionalProvider.Inputs inputs
    ) {
        ProviderAtTime<Vertex> uncenteredUnadjLocProvider = getFromData(inputs,
                Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider);
        var uncenteredUnadjLoc = uncenteredUnadjLocProvider.provide(inputs.timestamp());

        Vertex textBlockDimens = getFromData(inputs,
                Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens);

        return vertexOf(
                uncenteredUnadjLoc.X - (textBlockDimens.X / 2f),
                uncenteredUnadjLoc.Y - (textBlockDimens.Y / 2f)
        );
    }

    final static String Button_provideUnadjRectDimensFromTextBlock =
            "Button_provideUnadjRectDimensFromTextBlock";
    final static String Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft =
            "Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft";
    final static String Button_provideUnadjRectDimensFromTextBlock_textBlockDimens =
            "Button_provideUnadjRectDimensFromTextBlock_textBlockDimens";
    final static String Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz =
            "Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz";
    final static String Button_provideUnadjRectDimensFromTextBlock_textPaddingVert =
            "Button_provideUnadjRectDimensFromTextBlock_textPaddingVert";

    public FloatBox Button_provideUnadjRectDimensFromTextBlock(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<Vertex> unadjTextBlockUpperLeftProvider = getFromData(inputs,
                Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft);
        var textBlockUpperLeft = unadjTextBlockUpperLeftProvider.provide(inputs.timestamp());

        Vertex textBlockDimens =
                getFromData(inputs, Button_provideUnadjRectDimensFromTextBlock_textBlockDimens);

        float textPaddingHoriz =
                getFromData(inputs, Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz);
        float textPaddingVert =
                getFromData(inputs, Button_provideUnadjRectDimensFromTextBlock_textPaddingVert);

        return floatBoxOf(
                textBlockUpperLeft.X - textPaddingHoriz,
                textBlockUpperLeft.Y - textPaddingVert,
                textBlockUpperLeft.X + textBlockDimens.X + textPaddingHoriz,
                textBlockUpperLeft.Y + textBlockDimens.Y + textPaddingVert
        );
    }

    final static String Button_rectDimensWithAdj = "Button_rectDimensWithAdj";

    public FloatBox Button_rectDimensWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                (_, data) -> getFromData(data, RECT_UNADJ_DIMENS_PROVIDER),
                inaugural.soliloquy.tools.valueobjects.FloatBox::translateFloatBox
        );
    }

    final static String Button_imageAssetDimensWithAdj = "Button_imageAssetDimensWithAdj";

    public FloatBox Button_imageAssetDimensWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                (o, _) -> o.unadjImageAssetDimens,
                inaugural.soliloquy.tools.valueobjects.FloatBox::translateFloatBox
        );
    }

    final static String Button_textBlockLocWithAdj = "Button_textBlockLocWithAdj";

    public Vertex Button_textBlockLocWithAdj(FunctionalProvider.Inputs inputs) {
        return provideWithAdj(
                inputs,
                (_, data) -> getFromData(data, TEXT_BLOCK_UNADJ_LOC_PROVIDER),
                inaugural.soliloquy.tools.valueobjects.Vertex::translateVertex
        );
    }

    private Component getButton(FunctionalProvider.Inputs inputs) {
        UUID buttonUuid = getFromData(inputs, COMPONENT_UUID);
        return GET_COMPONENT.apply(buttonUuid);
    }

    private <T> T provideWithAdj(FunctionalProvider.Inputs inputs,
                                 BiFunction<Options, Map<String, Object>, ProviderAtTime<T>> provideUnadj,
                                 BiFunction<T, Vertex, T> adjustment) {
        var button = getButton(inputs);
        var currentOptions = getCurrentOptions(button);
        var unadj = provideUnadj.apply(currentOptions, button.data()).provide(inputs.timestamp());
        Vertex originAdjust = getFromData(button, COMPONENT_ORIGIN_ADJUST);
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

    static class Options {
        RectangleRenderable rect;
        ImageAssetRenderable imageAsset;
        ProviderAtTime<FloatBox> unadjImageAssetDimens;

        public Options() {
        }

        @SuppressWarnings("unused")
        public Options(
                RectangleRenderable rect,
                ImageAssetRenderable imageAsset,
                ProviderAtTime<FloatBox> unadjImageAssetDimens
        ) {
            this.rect = rect;
            this.imageAsset = imageAsset;
            this.unadjImageAssetDimens = unadjImageAssetDimens;
        }
    }
}
