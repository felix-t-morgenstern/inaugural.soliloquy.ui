package inaugural.soliloquy.ui;

import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.Map;

import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonDefinition {
    public AbstractProviderDefinition<FloatBox> dimens;

    public String text;
    public String fontId;
    public AbstractProviderDefinition<Float> textHeight;
    public float textPadding;

    public AbstractProviderDefinition<Color> bgColorTopLeft;
    public AbstractProviderDefinition<Color> bgColorTopRight;
    public AbstractProviderDefinition<Color> bgColorBottomLeft;
    public AbstractProviderDefinition<Color> bgColorBottomRight;

    public String bgTexRelLoc;

    public Map<Integer, String> onPressIds;
    public Map<Integer, String> onReleaseIds;
    public String onMouseOverId;
    public String onMouseLeaveId;

    public String pressSoundId;
    public String releaseSoundId;

    private ButtonDefinition() {
    }

    public static ButtonDefinition button() {
        return new ButtonDefinition();
    }

    public ButtonDefinition withDimens(AbstractProviderDefinition<FloatBox> dimens) {
        this.dimens = dimens;

        return this;
    }

    public ButtonDefinition withDimens(FloatBox dimens) {
        return withDimens(staticVal(dimens));
    }

    public ButtonDefinition withText(String text,
                                     String fontId,
                                     AbstractProviderDefinition<Float> textHeight) {
        this.text = text;
        this.fontId = fontId;
        this.textHeight = textHeight;

        return this;
    }

    public ButtonDefinition withTextPadding(float textPadding) {
        this.textPadding = textPadding;

        return this;
    }

    public ButtonDefinition withText(String text, String fontId, float textHeight) {
        return withText(text, fontId, staticVal(textHeight));
    }

    public ButtonDefinition withBgColor(AbstractProviderDefinition<Color> bgColor) {
        bgColorTopLeft = bgColorTopRight = bgColorBottomLeft = bgColorBottomRight = bgColor;

        return this;
    }

    public ButtonDefinition withBgColors(
            AbstractProviderDefinition<Color> bgColorTopLeft,
            AbstractProviderDefinition<Color> bgColorTopRight,
            AbstractProviderDefinition<Color> bgColorBottomLeft,
            AbstractProviderDefinition<Color> bgColorBottomRight
    ) {
        this.bgColorTopLeft = bgColorTopLeft;
        this.bgColorTopRight = bgColorTopRight;
        this.bgColorBottomLeft = bgColorBottomLeft;
        this.bgColorBottomRight = bgColorBottomRight;

        return this;
    }

    public ButtonDefinition withTexture(String bgTexRelLoc) {
        this.bgTexRelLoc = bgTexRelLoc;

        return this;
    }

    public ButtonDefinition onPress(Map<Integer, String> onPressIds) {
        this.onPressIds = onPressIds;

        return this;
    }

    public ButtonDefinition onRelease(Map<Integer, String> onReleaseIds) {
        this.onReleaseIds = onReleaseIds;

        return this;
    }

    public ButtonDefinition onMouseOver(String onMouseOverId) {
        this.onMouseOverId = onMouseOverId;

        return this;
    }

    public ButtonDefinition onMouseLeave(String onMouseLeaveId) {
        this.onMouseLeaveId = onMouseLeaveId;

        return this;
    }

    public ButtonDefinition withPressSound(String soundId) {
        this.pressSoundId = soundId;

        return this;
    }

    public ButtonDefinition withReleaseSound(String soundId) {
        this.releaseSoundId = soundId;

        return this;
    }
}
