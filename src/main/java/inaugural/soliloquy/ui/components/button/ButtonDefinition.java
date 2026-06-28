package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.ui.components.textblock.TextBlockDefinition;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.AbstractImageAssetRenderableDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Map;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

@SuppressWarnings("unused")
public class ButtonDefinition extends AbstractContentDefinition {
    public int[] keyCodepoints;
    public int keyEventPriority;

    public Boolean rectDefinesTextDimens = null;

    public TextBlockDefinition textBlockDef;
    public HorizontalAlignment textBlockHorizontalAlignment;
    public float textBlockXPadding;
    public Float textBlockYPadding;
    public ProviderAtTime<Vertex> textBlockCenterProvider;
    public AbstractProviderDefinition<Vertex> textBlockCenterProviderDef;

    public RectangleRenderableDefinition rectDefaultDef;
    public RectangleRenderableDefinition rectHoverDef;
    public RectangleRenderableDefinition rectPressedDef;

    public AbstractImageAssetRenderableDefinition imageAssetDefault;
    public AbstractImageAssetRenderableDefinition imageAssetHover;
    public AbstractImageAssetRenderableDefinition imageAssetPressed;

    public String onPressId;

    public String pressSoundId;
    public String Button_mouseOverSoundId;
    public String mouseLeaveSoundId;
    public String releaseSoundId;

    public final Map<String, Object> DATA;

    protected ButtonDefinition(int z, UUID uuid) {
        super(z, uuid);
        DATA = mapOf();
    }

    public static ButtonDefinition button(int z, UUID uuid) {
        return new ButtonDefinition(z, uuid);
    }

    public static ButtonDefinition button(int z) {
        return button(z, randomUUID());
    }

    /**
     * <u>If you want to define a Button's dimensions according to a TextBox instead of a Rectangle,
     * use {@link #textBlockDefinesRectDimens} and {@link #textBlockCenterProvider}, etc.</u>
     * <p>
     * If you define a button in terms of a rectangle, then any text will be positioned relative
     * to its dimensions. Conversely, if you define a button in terms of its text, then a
     * transparent rectangle will automatically be generated beneath the text, wrapping around it,
     * with any padding specified by {@link #withTextBlockPadding}.
     * <p>
     * (Text will always be vertically centered; its horizontal placement will depend on its
     * alignment. <i>If you make a block of text too large to fit within the rectangle dimensions,
     * it will just overflow.</i> And keep in mind that TextBlocks don't capture mouse events.)
     * <p>
     * Also, if you do not have both a default Rectangle (c.f. {@link #withRectDefault}) and
     * TextBlock (c.f. {@link #withTextBlockDef}) defined, this method has no impact. (C.f.
     * withTextBlockDef for what happens when a TextBlock is defined, and a default Rectangle
     * isn't.)
     *
     * @param textBlockHorizontalAlignment This is the alignment of the TextBlock <i>within the
     *                                     Rectangle</i>. This is different from
     *                                     {@link TextBlockDefinition#withHorizontalAlignment},
     *                                     since that field specifies the alignment of the text
     *                                     <i>within the TextBlock</i>. (So, for instance, you can
     *                                     have a block center-justified text, which doesn't take up
     *                                     the span of the Button, but instead is left-aligned.)
     */
    public ButtonDefinition rectDefinesTextDimens(
            HorizontalAlignment textBlockHorizontalAlignment
    ) {
        if (rectDefinesTextDimens != null) {
            throw new IllegalStateException(
                    "ButtonDefinition#rectDefinesTextDimens: TextBlock already defines dimens");
        }
        rectDefinesTextDimens = true;
        this.textBlockHorizontalAlignment = textBlockHorizontalAlignment;

        return this;
    }

    /**
     * C.f. {@link #rectDefinesTextDimens(HorizontalAlignment)} for the counterpart. This method is
     * intended to be used in tandem with {@link #withTextBlockCenterProvider}, etc.
     */
    public ButtonDefinition textBlockDefinesRectDimens() {
        if (rectDefinesTextDimens != null) {
            throw new IllegalStateException(
                    "ButtonDefinition#rectDefinesTextDimens: Rect already defines dimens");
        }
        rectDefinesTextDimens = false;

        return this;
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
     * @param priority     The priority of the KeyBinding for this button (c.f.
     *                     {@link soliloquy.specs.io.input.keyboard.KeyEventHandler#addComponent})
     */
    public ButtonDefinition withKey(int keyCodepoint, int priority) {
        return this.withKeys(priority, keyCodepoint);
    }

    /**
     * This method is used to provide a Rectangle to accompany the text of a button, if it was
     * defined by text first
     */
    public ButtonDefinition withRectDefault(RectangleRenderableDefinition rectDefault) {
        this.rectDefaultDef = rectDefault;

        return this;
    }

    /**
     * This method is used to make the Rectangle part of a Button change appearance when the mouse
     * hovers over it. <u>While it changes the appearance of the Button's Rectangle, it does NOT
     * change its dimensions; the dimensions fields of rectHover are ignored.</u>
     */
    public ButtonDefinition withRectHover(RectangleRenderableDefinition rectHover) {
        this.rectHoverDef = rectHover;

        return this;
    }

    /**
     * This method is used to make the Rectangle part of a Button change appearance when it is being
     * pressed down by the mouse or a keypress. <u>While it changes the appearance of the Button's
     * Rectangle, it does NOT change its dimensions; the dimensions fields of rectPressed are
     * ignored.</u>
     */
    public ButtonDefinition withRectPressed(RectangleRenderableDefinition rectPressed) {
        this.rectPressedDef = rectPressed;

        return this;
    }

    /**
     * If you define a TextBlock and <i>don't</i> define a default Rectangle, a transparent
     * Rectangle will be created to wrap around the TextBlock, purely for the purpose of mouse
     * event detection. The values passed to {@link #withTextBlockPadding} and
     * {@link #withTextBlockPadding} will therefore still impact the total size of the Button.
     * <p>
     * If you set {@link TextBlockDefinition#maxLineLength} to a value of 0 or less, then the
     * ButtonDefinitionReader will determine the length of the first paragraph as a single line,
     * and will set the maxLineLength of this TextBlockDefinition to the length of that line. (This
     * behavior is intended to support single-line text for buttons, e.g., "Okay", "Cancel", etc.)
     *
     * @param textBlockDef The definition of the TextBlock component to use for the Button
     */
    public ButtonDefinition withTextBlockDef(TextBlockDefinition textBlockDef) {
        this.textBlockDef = textBlockDef;

        return this;
    }

    /**
     * The behavior of these values depends on whether the Button's dimensions were predefined,
     * c.f. {@link #rectDefinesTextDimens(HorizontalAlignment)}.
     * <p>
     * If the Button's dimensions are predefined, then textBlockXPadding determines the distance of
     * the TextBlock from either the left or right edge of the Button, if the horizontal alignment
     * is set to {@link HorizontalAlignment#LEFT} or {@link HorizontalAlignment#RIGHT},
     * respectively. If it's set to {@link HorizontalAlignment#CENTER}, then this value has no
     * impact.
     * <p>
     * If the Button's dimensions aren't defined by the Rectangle, then both values determine the
     * amount of padding the Rectangle wraps around the TextBlock.
     * <p>
     * <b>If you leave textBlockYPadding null, the ButtonDefinitionReader will automatically use
     * the window's width-to-height ratio to calculate a pixel-equivalent textBlockYPadding.</b>
     *
     * @param textBlockXPadding The percent of window width to use as padding on either side of the
     *                          text
     * @param textBlockYPadding The percent of window height to use as padding above and below the
     *                          text
     */
    public ButtonDefinition withTextBlockPadding(float textBlockXPadding, Float textBlockYPadding) {
        this.textBlockXPadding = textBlockXPadding;
        this.textBlockYPadding = textBlockYPadding;

        return this;
    }

    /**
     * This field is only used when the Button's location is defined by the Rectangle, c.f.
     * {@link #rectDefinesTextDimens}, and the TextBlock has a dynamic length, c.f.
     * {@link #withTextBlockDef}. It overrides the location of the TextBlock in the
     * {@link TextBlockDefinition} with a location at which the TextBlock (and therefore the
     * Button) will be centered. The intended use case is where a Button's dimensions are defined
     * by
     * a TextBlock, whose width is dynamic; this property will allow the ButtonDefinitionReader to
     * properly set the rendering location of the TextBlock so that it is centered where specified.
     * <p>
     * <u>If you cet a center provider when the max length is not dynamic, this value will be
     * ignored.</u>
     */
    public ButtonDefinition withTextBlockCenterProvider(
            ProviderAtTime<Vertex> textBlockCenterProvider
    ) {
        this.textBlockCenterProvider = textBlockCenterProvider;

        return this;
    }

    /**
     * C.f. {@link #withTextBlockCenterProvider} for full info
     */
    public ButtonDefinition withTextBlockCenterProviderDef(
            AbstractProviderDefinition<Vertex> textBlockCenterProviderDef
    ) {
        this.textBlockCenterProviderDef = textBlockCenterProviderDef;

        return this;
    }

    /**
     * C.f. {@link #withTextBlockCenterProvider} for full info
     */
    public ButtonDefinition withTextBlockCenter(Vertex textBlockCenter) {
        this.textBlockCenterProviderDef = staticVal(textBlockCenter);

        return this;
    }

    /**
     * C.f. {@link #withTextBlockPadding(float, Float)} for a full explanation; this method assigns
     * the x padding value, while the y value remains null.
     *
     * @param textBlockXPadding The percent of window width to use as padding on either side of the
     *                          text
     */
    public ButtonDefinition withTextBlockPadding(float textBlockXPadding) {
        this.textBlockXPadding = textBlockXPadding;

        return this;
    }

    public ButtonDefinition withImageAsset(
            AbstractImageAssetRenderableDefinition imageAssetDefault) {
        this.imageAssetDefault = imageAssetDefault;

        return this;
    }

    /**
     * NB: If imageAssetHover doesn't provide a dimensions provider or a dimensions provider
     * definition, it will inherit the one from the default ImageAsset.
     *
     * @param imageAssetHover The definition of the ImageAsset used when the mouse hovers over the
     *                        Button
     */
    public ButtonDefinition withImageAssetHover(
            AbstractImageAssetRenderableDefinition imageAssetHover
    ) {
        this.imageAssetHover = imageAssetHover;

        return this;
    }

    /**
     * NB: If imageAssetPressed doesn't provide a dimensions provider or a dimensions provider
     * definition, it will inherit the one from the default ImageAsset.
     *
     * @param imageAssetPressed The definition of the ImageAsset used when the Button is pressed
     *                          down
     */
    public ButtonDefinition withImageAssetPressed(
            AbstractImageAssetRenderableDefinition imageAssetPressed
    ) {
        this.imageAssetPressed = imageAssetPressed;

        return this;
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
        Button_mouseOverSoundId = soundId;

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

    /**
     * This method does not overwrite all previous data, only data with the same keys entered
     */
    public ButtonDefinition withData(Map<String, Object> data) {
        DATA.putAll(data);

        return this;
    }
}
