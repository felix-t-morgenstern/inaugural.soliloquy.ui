package inaugural.soliloquy.ui.components.button;

import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.Map;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonDefinition extends AbstractContentDefinition {
    public int[] keyCodepoints;
    public int keyEventPriority;

    public final AbstractProviderDefinition<FloatBox> RECT_DIMENS_DEF;
    public final AbstractProviderDefinition<Vertex> TEXT_RENDERING_LOC_DEF;

    public String text;
    public String fontId;
    public float textHeight;
    public float textPaddingVertical;
    public float textGlyphPadding;
    public TextJustification textJustification;

    // default state
    public AbstractProviderDefinition<Color> bgColorTopLeftDefault;
    public AbstractProviderDefinition<Color> bgColorTopRightDefault;
    public AbstractProviderDefinition<Color> bgColorBottomLeftDefault;
    public AbstractProviderDefinition<Color> bgColorBottomRightDefault;
    public AbstractProviderDefinition<Integer> bgTexProviderDefault;
    public String bgTexRelLocDefault;

    public String spriteIdDefault;
    public AbstractProviderDefinition<FloatBox> spriteDimensDefaultDef;
    public ShiftDefinition spriteShiftDefaultDef;

    public Map<Integer, AbstractProviderDefinition<Color>> textColorIndicesDefault;
    public int[] textItalicIndicesDefault;
    public int[] textBoldIndicesDefault;

    // hover
    public AbstractProviderDefinition<Color> bgColorTopLeftHover;
    public AbstractProviderDefinition<Color> bgColorTopRightHover;
    public AbstractProviderDefinition<Color> bgColorBottomLeftHover;
    public AbstractProviderDefinition<Color> bgColorBottomRightHover;
    public AbstractProviderDefinition<Integer> bgTexProviderHover;
    public String bgTexRelLocHover;

    public String spriteIdHover;
    public AbstractProviderDefinition<FloatBox> spriteDimensHoverDef;
    public ShiftDefinition spriteShiftHoverDef;

    public Map<Integer, AbstractProviderDefinition<Color>> textColorIndicesHover;
    public int[] textItalicIndicesHover;
    public int[] textBoldIndicesHover;

    // pressed
    public AbstractProviderDefinition<Color> bgColorTopLeftPressed;
    public AbstractProviderDefinition<Color> bgColorTopRightPressed;
    public AbstractProviderDefinition<Color> bgColorBottomLeftPressed;
    public AbstractProviderDefinition<Color> bgColorBottomRightPressed;
    public AbstractProviderDefinition<Integer> bgTexProviderPressed;
    public String bgTexRelLocPressed;

    public String spriteIdPressed;
    public AbstractProviderDefinition<FloatBox> spriteDimensPressedDef;
    public ShiftDefinition spriteShiftPressedDef;

    public Map<Integer, AbstractProviderDefinition<Color>> textColorIndicesPressed;
    public int[] textItalicIndicesPressed;
    public int[] textBoldIndicesPressed;

    public String onPressId;

    public String pressSoundId;
    public String mouseOverSoundId;
    public String mouseLeaveSoundId;
    public String releaseSoundId;

    private ButtonDefinition(int z,
                             AbstractProviderDefinition<FloatBox> rectDimensDef,
                             AbstractProviderDefinition<Vertex> textRenderingLocDef) {
        super(z);
        RECT_DIMENS_DEF = rectDimensDef;
        TEXT_RENDERING_LOC_DEF = textRenderingLocDef;
    }

    /**
     * <b>If you define a button in terms of its dimensions, then any text will be positioned
     * relative to these dimensions.</b>
     * <p>
     * (Text will always be vertically centered; its horizontal placement will depend on its
     * justification.)
     *
     * @param dimens The dimensions of the button on the screen
     * @param z      The z-index
     */
    public static ButtonDefinition button(AbstractProviderDefinition<FloatBox> dimens, int z) {
        return new ButtonDefinition(z, dimens, null);
    }

    /**
     * <b>If you define a button in terms of its dimensions, then any text will be positioned
     * relative to these dimensions.</b>
     * <p>
     * (Text will always be vertically centered; its horizontal placement will depend on its
     * justification.)
     *
     * @param dimens The dimensions of the button on the screen
     * @param z      The z-index
     */
    public static ButtonDefinition button(FloatBox dimens, int z) {
        return button(staticVal(dimens), z);
    }

    /**
     * <b>If you define a button in terms of its text, then a transparent rectangle will
     * automatically be generated beneath the text, wrapping around it, with any padding specified
     * by {@link #withTextPadding}.</b>
     *
     * @param text       The text of the button
     * @param fontId     The id of the font for the text
     * @param textHeight The line height of the text (c.f.
     *                   {@link TextLineRenderable#setLineHeightProvider(ProviderAtTime)})
     * @param z          The z-index
     */
    public static ButtonDefinition button(String text,
                                          String fontId,
                                          float textHeight,
                                          AbstractProviderDefinition<Vertex> textRenderingLoc,
                                          int z) {
        return new ButtonDefinition(z, null, textRenderingLoc)
                .withText(text, fontId, textHeight);
    }

    /**
     * <b>If you define a button in terms of its text, then a transparent rectangle will
     * automatically be generated beneath the text, wrapping around it, with any padding specified
     * by {@link #withTextPadding}.</b>
     *
     * @param text       The text of the button
     * @param fontId     The id of the font for the text
     * @param textHeight The line height of the text (c.f.
     *                   {@link TextLineRenderable#setLineHeightProvider(ProviderAtTime)})
     * @param z          The z-index
     */
    public static ButtonDefinition button(String text,
                                          String fontId,
                                          float textHeight,
                                          Vertex textRenderingLoc,
                                          int z) {
        return new ButtonDefinition(z, null, staticVal(textRenderingLoc))
                .withText(text, fontId, textHeight);
    }

    /**
     * <b>If you define a button as a sprite, then it will have neither text nor a rectangle. If you
     * instead want a button that combines both a Sprite along with a rectangle and some text, then
     * use one of the alternate constructors, in conjunction with some variant of
     * {@link #withSprite}.</b>
     */
    public static ButtonDefinition button(String spriteId,
                                          AbstractProviderDefinition<FloatBox> dimens,
                                          int z) {
        return new ButtonDefinition(z, null, null)
                .withSprite(spriteId, dimens);
    }

    /**
     * <b>If you define a button as a sprite, then it will have neither text nor a rectangle. If you
     * instead want a button that combines both a Sprite along with a rectangle and some text, then
     * use one of the alternate constructors, in conjunction with some variant of
     * {@link #withSprite}.</b>
     */
    public static ButtonDefinition button(String spriteId,
                                          FloatBox dimens,
                                          int z) {
        return button(spriteId, staticVal(dimens), z);
    }

    /**
     * @param keys     The keys which activate the button when pressed
     * @param priority The priority of the KeyBinding for this button (c.f.
     *                 {@link soliloquy.specs.io.input.keyboard.KeyEventHandler#addComponent})
     */
    public ButtonDefinition withKeys(int priority, int... keys) {
        this.keyCodepoints = keys;
        this.keyEventPriority = priority;

        return this;
    }

    /**
     * @param keyCodepoint The keyCodepoint which activates the button when pressed
     * @param priority The priority of the KeyBinding for this button (c.f.
     *                 {@link soliloquy.specs.io.input.keyboard.KeyEventHandler#addComponent})
     */
    public ButtonDefinition withKey(int keyCodepoint, int priority) {
        return this.withKeys(priority, keyCodepoint);
    }

    /**
     * @param bgColorTopLeft     A definition of the top left background color
     * @param bgColorTopRight    A definition of the top right background color
     * @param bgColorBottomLeft  A definition of the bottom left background color
     * @param bgColorBottomRight A definition of the bottom right background color
     */
    public ButtonDefinition withBgColors(
            AbstractProviderDefinition<Color> bgColorTopLeft,
            AbstractProviderDefinition<Color> bgColorTopRight,
            AbstractProviderDefinition<Color> bgColorBottomLeft,
            AbstractProviderDefinition<Color> bgColorBottomRight
    ) {
        this.bgColorTopLeftDefault = bgColorTopLeft;
        this.bgColorTopRightDefault = bgColorTopRight;
        this.bgColorBottomLeftDefault = bgColorBottomLeft;
        this.bgColorBottomRightDefault = bgColorBottomRight;

        return this;
    }

    /**
     * @param bgColorTopLeft     The top left background color
     * @param bgColorTopRight    The top right background color
     * @param bgColorBottomLeft  The bottom left background color
     * @param bgColorBottomRight The bottom right background color
     */
    public ButtonDefinition withBgColors(
            Color bgColorTopLeft,
            Color bgColorTopRight,
            Color bgColorBottomLeft,
            Color bgColorBottomRight
    ) {
        return this.withBgColors(
                staticVal(bgColorTopLeft),
                staticVal(bgColorTopRight),
                staticVal(bgColorBottomLeft),
                staticVal(bgColorBottomRight)
        );
    }

    /**
     * @param bgColor A definition of the background color
     */
    public ButtonDefinition withBgColor(AbstractProviderDefinition<Color> bgColor) {
        return this.withBgColors(bgColor, bgColor, bgColor, bgColor);
    }

    /**
     * @param bgColor The background color
     */
    public ButtonDefinition withBgColor(Color bgColor) {
        return this.withBgColor(staticVal(bgColor));
    }

    /**
     * @param bgTexProvider A definition of the provider of the background texture
     */
    public ButtonDefinition withTexture(AbstractProviderDefinition<Integer> bgTexProvider) {
        this.bgTexProviderDefault = bgTexProvider;

        return this;
    }

    /**
     * @param bgTexRelLoc The relative location of the file to be used as the texture
     */
    public ButtonDefinition withTexture(String bgTexRelLoc) {
        this.bgTexRelLocDefault = bgTexRelLoc;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSprite(String spriteId,
                                       AbstractProviderDefinition<FloatBox> spriteDimens) {
        this.spriteIdDefault = spriteId;
        this.spriteDimensDefaultDef = spriteDimens;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSprite(String spriteId, FloatBox spriteDimens) {
        return this.withSprite(spriteId, staticVal(spriteDimens));
    }

    public ButtonDefinition withSpriteColorShift(ShiftDefinition spriteShift) {
        this.spriteShiftDefaultDef = spriteShift;

        return this;
    }

    /**
     * @param text       The text of the button
     * @param fontId     The id of the font for the text
     * @param textHeight A definition of the provider of the line height of the text (c.f.
     *                   {@link TextLineRenderable#setLineHeightProvider(ProviderAtTime)})
     */
    public ButtonDefinition withText(String text,
                                     String fontId,
                                     float textHeight) {
        this.text = text;
        this.fontId = fontId;
        this.textHeight = textHeight;

        return this;
    }

    /**
     * <u>This property operates differently based on whether the dimensions have been set
     * manually.</u>
     * <p>
     * <i>If the dimensions have been set manually,</i> this property determines the distance of the
     * text from the left or right edge. When {@link #withTextJustification} is set to
     * {@link TextJustification#LEFT}, this property determines how far the left end of the text is
     * from the left edge of the button. Conversely, when justification is set to
     * {@link TextJustification#RIGHT}, it's how far the end of the text line is from the right edge
     * of the button. When the justification is {@link TextJustification#CENTER}, this property has
     * no effect.
     * <p>
     * <i>If the dimensions have not been set manually,</i> this property expands the boundaries of
     * the button on each side
     * <p>
     * In all cases, textPaddingVertical is expressed in <i>percent of screen height</i>.
     *
     * @param textPaddingVertical The distance from the boundaries of the
     *                            {@link soliloquy.specs.io.graphics.renderables.TextLineRenderable}
     *                            and the
     *                            boundaries of the button, expressed in percentage of screen
     *                            height
     */
    public ButtonDefinition withTextPadding(float textPaddingVertical) {
        this.textPaddingVertical = textPaddingVertical;

        return this;
    }

    /**
     * @param glyphPadding A modification to the space between each glyph, c.f.
     *                     {@link TextLineRenderable#getPaddingBetweenGlyphs()}
     */
    public ButtonDefinition withGlyphPadding(float glyphPadding) {
        textGlyphPadding = glyphPadding;

        return this;
    }

    /**
     * Defaults to {@link TextJustification#CENTER}. (Vertical alignment is centered.) <i>This has
     * no effect unless you manually set the button's dimensions when creating the definition, via
     * e.g. {@link ButtonDefinition#button(AbstractProviderDefinition, int)}.</i>
     *
     * @param justification The justification of the text within the button
     */
    public ButtonDefinition withTextJustification(TextJustification justification) {
        this.textJustification = justification;

        return this;
    }

    /**
     * @param colorIndices Definitions of providers for colors at indices within the text (c.f.
     *                     {@link TextLineRenderable#colorProviderIndices()})
     */
    public ButtonDefinition withTextColorIndices(
            Map<Integer, AbstractProviderDefinition<Color>> colorIndices) {
        this.textColorIndicesDefault = colorIndices;

        return this;
    }

    /**
     * @param color The Definition of the text color
     */
    public ButtonDefinition withTextColor(AbstractProviderDefinition<Color> color) {
        return this.withTextColorIndices(mapOf(0, color));
    }

    /**
     * @param color The text color
     */
    public ButtonDefinition withTextColor(Color color) {
        return this.withTextColor(staticVal(color));
    }

    /**
     * @param italicIndices The indices which mark the starts and ends of italicization within the
     *                      text (c.f. {@link TextLineRenderable#italicIndices()})
     */
    public ButtonDefinition withTextItalicIndices(int... italicIndices) {
        this.textItalicIndicesDefault = italicIndices;

        return this;
    }

    /**
     * Makes the whole text italicized
     */
    public ButtonDefinition italic() {
        return this.withTextItalicIndices(0);
    }

    /**
     * @param boldIndices The indices which mark the starts and ends of boldface within the text
     *                    (c.f. {@link TextLineRenderable#boldIndices()} ()})
     */
    public ButtonDefinition withTextBoldIndices(int... boldIndices) {
        this.textBoldIndicesDefault = boldIndices;

        return this;
    }

    /**
     * Makes the whole text bold
     */
    public ButtonDefinition bold() {
        return this.withTextBoldIndices(0);
    }

    /**
     * These background colors are used <i>when the button is being pressed down</i>
     *
     * @param bgColorTopLeft     A definition of the top left background color
     * @param bgColorTopRight    A definition of the top right background color
     * @param bgColorBottomLeft  A definition of the bottom left background color
     * @param bgColorBottomRight A definition of the bottom right background color
     */
    public ButtonDefinition withBgColorsHover(
            AbstractProviderDefinition<Color> bgColorTopLeft,
            AbstractProviderDefinition<Color> bgColorTopRight,
            AbstractProviderDefinition<Color> bgColorBottomLeft,
            AbstractProviderDefinition<Color> bgColorBottomRight
    ) {
        this.bgColorTopLeftHover = bgColorTopLeft;
        this.bgColorTopRightHover = bgColorTopRight;
        this.bgColorBottomLeftHover = bgColorBottomLeft;
        this.bgColorBottomRightHover = bgColorBottomRight;

        return this;
    }

    /**
     * These background colors are used <i>when the button is being pressed down</i>
     *
     * @param bgColorTopLeft     The top left background color
     * @param bgColorTopRight    The top right background color
     * @param bgColorBottomLeft  The bottom left background color
     * @param bgColorBottomRight The bottom right background color
     */
    public ButtonDefinition withBgColorsHover(
            Color bgColorTopLeft,
            Color bgColorTopRight,
            Color bgColorBottomLeft,
            Color bgColorBottomRight
    ) {
        return this.withBgColorsHover(
                staticVal(bgColorTopLeft),
                staticVal(bgColorTopRight),
                staticVal(bgColorBottomLeft),
                staticVal(bgColorBottomRight)
        );
    }

    /**
     * @param bgColor A definition of the background color <i>when the button is being pressed
     *                down</i>
     */
    public ButtonDefinition withBgColorHover(AbstractProviderDefinition<Color> bgColor) {
        return this.withBgColorsHover(bgColor, bgColor, bgColor, bgColor);
    }

    /**
     * @param bgColor The background color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withBgColorHover(Color bgColor) {
        return this.withBgColorHover(staticVal(bgColor));
    }

    /**
     * @param bgTexProvider A definition of the provider of the background texture <i>when the
     *                      button is being pressed down</i>
     */
    public ButtonDefinition withTextureHover(AbstractProviderDefinition<Integer> bgTexProvider) {
        this.bgTexProviderHover = bgTexProvider;

        return this;
    }

    /**
     * @param bgTexRelLoc The relative location of the file to be used as the texture <i>when the
     *                    button is being pressed down</i>
     */
    public ButtonDefinition withTextureHover(String bgTexRelLoc) {
        this.bgTexRelLocHover = bgTexRelLoc;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite <i>when the button is being pressed down</i>
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSpriteHover(String spriteId,
                                            AbstractProviderDefinition<FloatBox> spriteDimens) {
        this.spriteIdHover = spriteId;
        this.spriteDimensHoverDef = spriteDimens;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite <i>when the button is being pressed down</i>
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSpriteHover(String spriteId, FloatBox spriteDimens) {
        return this.withSpriteHover(spriteId, staticVal(spriteDimens));
    }

    public ButtonDefinition withSpriteColorShiftHover(ShiftDefinition spriteShift) {
        this.spriteShiftHoverDef = spriteShift;

        return this;
    }

    /**
     * @param colorIndices Definitions of providers for colors at indices within the text <i>when
     *                     the button is being pressed down</i> (c.f.
     *                     {@link TextLineRenderable#colorProviderIndices()})
     */
    public ButtonDefinition withTextColorIndicesHover(
            Map<Integer, AbstractProviderDefinition<Color>> colorIndices) {
        this.textColorIndicesHover = colorIndices;

        return this;
    }

    /**
     * @param color The Definition of the text color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withTextColorHover(AbstractProviderDefinition<Color> color) {
        return this.withTextColorIndicesHover(mapOf(0, color));
    }

    /**
     * @param color The text color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withTextColorHover(Color color) {
        return this.withTextColorHover(staticVal(color));
    }

    /**
     * @param italicIndices The indices which mark the starts and ends of italicization within the
     *                      text <i>when the button is being pressed down</i> (c.f.
     *                      {@link TextLineRenderable#italicIndices()})
     */
    public ButtonDefinition withTextItalicIndicesHover(int... italicIndices) {
        this.textItalicIndicesHover = italicIndices;

        return this;
    }

    /**
     * Makes the whole text italicized <i>when the button is being pressed down</i>
     */
    public ButtonDefinition italicHover() {
        return this.withTextItalicIndicesHover(0);
    }

    /**
     * @param boldIndices The indices which mark the starts and ends of boldface within the text
     *                    <i>when the button is being pressed down</i> (c.f.
     *                    {@link TextLineRenderable#boldIndices()})
     */
    public ButtonDefinition withTextBoldIndicesHover(int... boldIndices) {
        this.textBoldIndicesHover = boldIndices;

        return this;
    }

    /**
     * Makes the whole text bold <i>when the button is being pressed down</i>
     */
    public ButtonDefinition boldHover() {
        return this.withTextBoldIndicesHover(0);
    }

    /**
     * These background colors are used <i>when the button is being pressed down</i>
     *
     * @param bgColorTopLeft     A definition of the top left background color
     * @param bgColorTopRight    A definition of the top right background color
     * @param bgColorBottomLeft  A definition of the bottom left background color
     * @param bgColorBottomRight A definition of the bottom right background color
     */
    public ButtonDefinition withBgColorsPressed(
            AbstractProviderDefinition<Color> bgColorTopLeft,
            AbstractProviderDefinition<Color> bgColorTopRight,
            AbstractProviderDefinition<Color> bgColorBottomLeft,
            AbstractProviderDefinition<Color> bgColorBottomRight
    ) {
        this.bgColorTopLeftPressed = bgColorTopLeft;
        this.bgColorTopRightPressed = bgColorTopRight;
        this.bgColorBottomLeftPressed = bgColorBottomLeft;
        this.bgColorBottomRightPressed = bgColorBottomRight;

        return this;
    }

    /**
     * These background colors are used <i>when the button is being pressed down</i>
     *
     * @param bgColorTopLeft     The top left background color
     * @param bgColorTopRight    The top right background color
     * @param bgColorBottomLeft  The bottom left background color
     * @param bgColorBottomRight The bottom right background color
     */
    public ButtonDefinition withBgColorsPressed(
            Color bgColorTopLeft,
            Color bgColorTopRight,
            Color bgColorBottomLeft,
            Color bgColorBottomRight
    ) {
        return this.withBgColorsPressed(
                staticVal(bgColorTopLeft),
                staticVal(bgColorTopRight),
                staticVal(bgColorBottomLeft),
                staticVal(bgColorBottomRight)
        );
    }

    /**
     * @param bgColor A definition of the background color <i>when the button is being pressed
     *                down</i>
     */
    public ButtonDefinition withBgColorPressed(AbstractProviderDefinition<Color> bgColor) {
        return this.withBgColorsPressed(bgColor, bgColor, bgColor, bgColor);
    }

    /**
     * @param bgColor The background color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withBgColorPressed(Color bgColor) {
        return this.withBgColorPressed(staticVal(bgColor));
    }

    /**
     * @param bgTexProvider A definition of the provider of the background texture <i>when the
     *                      button is being pressed down</i>
     */
    public ButtonDefinition withTexturePressed(AbstractProviderDefinition<Integer> bgTexProvider) {
        this.bgTexProviderPressed = bgTexProvider;

        return this;
    }

    /**
     * @param bgTexRelLoc The relative location of the file to be used as the texture <i>when the
     *                    button is being pressed down</i>
     */
    public ButtonDefinition withTexturePressed(String bgTexRelLoc) {
        this.bgTexRelLocPressed = bgTexRelLoc;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite <i>when the button is being pressed down</i>
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSpritePressed(String spriteId,
                                              AbstractProviderDefinition<FloatBox> spriteDimens) {
        this.spriteIdPressed = spriteId;
        this.spriteDimensPressedDef = spriteDimens;

        return this;
    }

    /**
     * @param spriteId     The id of the Sprite <i>when the button is being pressed down</i>
     * @param spriteDimens Provides the dimensions in which to render the Sprite
     */
    public ButtonDefinition withSpritePressed(String spriteId, FloatBox spriteDimens) {
        return this.withSpritePressed(spriteId, staticVal(spriteDimens));
    }

    public ButtonDefinition withSpriteColorShiftPressed(ShiftDefinition spriteShift) {
        this.spriteShiftPressedDef = spriteShift;

        return this;
    }

    /**
     * @param colorIndices Definitions of providers for colors at indices within the text <i>when
     *                     the button is being pressed down</i> (c.f.
     *                     {@link TextLineRenderable#colorProviderIndices()})
     */
    public ButtonDefinition withTextColorIndicesPressed(
            Map<Integer, AbstractProviderDefinition<Color>> colorIndices) {
        this.textColorIndicesPressed = colorIndices;

        return this;
    }

    /**
     * @param color The Definition of the text color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withTextColorPressed(AbstractProviderDefinition<Color> color) {
        return this.withTextColorIndicesPressed(mapOf(0, color));
    }

    /**
     * @param color The text color <i>when the button is being pressed down</i>
     */
    public ButtonDefinition withTextColorPressed(Color color) {
        return this.withTextColorPressed(staticVal(color));
    }

    /**
     * @param italicIndices The indices which mark the starts and ends of italicization within the
     *                      text <i>when the button is being pressed down</i> (c.f.
     *                      {@link TextLineRenderable#italicIndices()})
     */
    public ButtonDefinition withTextItalicIndicesPressed(int... italicIndices) {
        this.textItalicIndicesPressed = italicIndices;

        return this;
    }

    /**
     * Makes the whole text italicized <i>when the button is being pressed down</i>
     */
    public ButtonDefinition italicPressed() {
        return this.withTextItalicIndicesPressed(0);
    }

    /**
     * @param boldIndices The indices which mark the starts and ends of boldface within the text
     *                    <i>when the button is being pressed down</i> (c.f.
     *                    {@link TextLineRenderable#boldIndices()})
     */
    public ButtonDefinition withTextBoldIndicesPressed(int... boldIndices) {
        this.textBoldIndicesPressed = boldIndices;

        return this;
    }

    /**
     * Makes the whole text bold <i>when the button is being pressed down</i>
     */
    public ButtonDefinition boldPressed() {
        return this.withTextBoldIndicesPressed(0);
    }

    /**
     * @param onPressId The id of the Action fired when the button is clicked (and released) or its
     *                  key is pressed (and released)
     */
    public ButtonDefinition onPress(String onPressId) {
        this.onPressId = onPressId;

        return this;
    }

    /**
     * @param soundId The id of the sound played when the button is clicked or its key pressed
     */
    public ButtonDefinition withPressSound(String soundId) {
        pressSoundId = soundId;

        return this;
    }

    /**
     * @param soundId The id of the sound played when the mouse starts hovering over this button
     */
    public ButtonDefinition withMouseOverSound(String soundId) {
        mouseOverSoundId = soundId;

        return this;
    }

    /**
     * @param soundId The id of the sound played when the mouse stops hovering over this button
     */
    public ButtonDefinition withMouseLeaveSound(String soundId) {
        mouseLeaveSoundId = soundId;

        return this;
    }

    /**
     * @param soundId The id of the sound played when the mouse is released over the button (after
     *                it pressed the button) or its key is released (after having being pressed)
     */
    public ButtonDefinition withReleaseSound(String soundId) {
        releaseSoundId = soundId;

        return this;
    }
}
