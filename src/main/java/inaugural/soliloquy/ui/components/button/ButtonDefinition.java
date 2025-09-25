package inaugural.soliloquy.ui.components.button;

import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonDefinition extends AbstractContentDefinition {
    public char key;

    public AbstractProviderDefinition<FloatBox> dimens;

    public String text;
    public String fontId;
    public AbstractProviderDefinition<Float> textHeight;
    public float textPadding;

    public AbstractProviderDefinition<Color> bgColorTopLeft;
    public AbstractProviderDefinition<Color> bgColorTopRight;
    public AbstractProviderDefinition<Color> bgColorBottomLeft;
    public AbstractProviderDefinition<Color> bgColorBottomRight;

    public AbstractProviderDefinition<Integer> bgTexProvider;
    public String bgTexRelLoc;

    public String onPressId;

    public String pressSoundId;
    public String releaseSoundId;

    private ButtonDefinition(int z) {
        super(z);
    }

    public static ButtonDefinition button(AbstractProviderDefinition<FloatBox> dimens, int z) {
        return new ButtonDefinition(z)
                .withDimens(dimens);
    }

    public static ButtonDefinition button(FloatBox dimens, int z) {
        return new ButtonDefinition(z)
                .withDimens(dimens);
    }

    public static ButtonDefinition button(String text,
                                          String fontId,
                                          AbstractProviderDefinition<Float> textHeight,
                                          int z) {
        return new ButtonDefinition(z)
                .withText(text, fontId, textHeight);
    }

    public static ButtonDefinition button(String text, String fontId, float textHeight, int z) {
        return new ButtonDefinition(z)
                .withText(text, fontId, textHeight);
    }

    public ButtonDefinition withKey(char key) {
        this.key = key;

        return this;
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

    public ButtonDefinition withText(String text, String fontId, float textHeight) {
        return withText(text, fontId, staticVal(textHeight));
    }

    public ButtonDefinition withTextPadding(float textPadding) {
        this.textPadding = textPadding;

        return this;
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

    public ButtonDefinition withTexture(AbstractProviderDefinition<Integer> bgTexProvider) {
        this.bgTexProvider = bgTexProvider;

        return this;
    }

    public ButtonDefinition withTexture(String bgTexRelLoc) {
        this.bgTexRelLoc = bgTexRelLoc;

        return this;
    }

    public ButtonDefinition onPress(String onPressId) {
        this.onPressId = onPressId;

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
