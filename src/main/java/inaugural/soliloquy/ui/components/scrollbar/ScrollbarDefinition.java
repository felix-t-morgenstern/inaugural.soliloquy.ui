package inaugural.soliloquy.ui.components.scrollbar;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollbarDefinition extends AbstractContentDefinition {
    public final Orientation ORIENTATION;
    public final AbstractProviderDefinition<Vertex> ORIGIN_PROVIDER_DEF;
    public final ProviderAtTime<Vertex> ORIGIN_PROVIDER;
    public final RectangleRenderableDefinition TRACK_DEF;
    public final ButtonDefinition THUMB_DEF;
    public final AbstractProviderDefinition<Float> THUMB_MOVE_AMOUNT_PROVIDER_DEF;
    public final int THUMB_INCREMENT_MOVE_DUR;

    public ButtonDefinition originArrowDef;
    public ButtonDefinition terminusArrowDef;
    public int arrowHoldStartThreshold;
    public int minDurBetweenMovesWhileArrowHeld;
    public float arrowHeldRepeatedTimeExponent;
    public float arrowHeldRepeatedTimeExponentFactor;

    private ScrollbarDefinition(
            Orientation orientation,
            AbstractProviderDefinition<Vertex> originProviderDef,
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition trackDef,
            ButtonDefinition thumbDef,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        super(z, uuid);
        ORIENTATION = Check.ifNull(orientation, "orientation");
        TRACK_DEF = Check.ifNull(trackDef, "trackDef");
        THUMB_DEF = Check.ifNull(thumbDef, "thumbDef");
        ORIGIN_PROVIDER_DEF = originProviderDef;
        ORIGIN_PROVIDER = originProvider;
        THUMB_MOVE_AMOUNT_PROVIDER_DEF =
                Check.ifNull(thumbMoveAmountProviderDef, "thumbMoveAmountProviderDef");
        THUMB_INCREMENT_MOVE_DUR = Check.ifNull(thumbIncrementMoveDur, "thumbIncrementMoveDur");
    }

    /**
     * @param originProvider             Provides the origin (upper-left corner) of the Scrollbar
     * @param track                      The RectangleDefinition for the track (the actual bar)
     * @param thumb                      The ButtonDefinition for the thumb (the part you click and
     *                                   drag)
     * @param thumbMoveAmountProviderDef The amount of scrollable range traversed when the thumb is
     *                                   moved incrementally by either clicking one of the arrow
     *                                   buttons (c.f. {@link #withArrowButtons}) or rotating the
     *                                   scroll wheel one tick on the scrollbar. The "scrollable
     *                                   range" is a range from 0.0 to 1.0, where 0.0 is the top of
     *                                   the scroll, and 1.0 is the bottom. (An example provider
     *                                   might, for instance, calculate a certain percentage of
     *                                   screen height to scroll based on the height of the
     *                                   contents; or, it might provide a flat percentage of
     *                                   scrolling per movement.)
     * @param thumbIncrementMoveDur      The time in milliseconds needed to move the Thumb from its
     *                                   current location to the new target (calculated using
     *                                   arrowClickThumbMoveAmount). If you want the movement to be
     *                                   instantaneous, you can use any value less than or equal to
     *                                   0; the constant
     *                                   {@link Constants#INSTANT_ARROW_CLICK_MOVEMENT_SPEED} will
     *                                   also provide this value, and may make for more legible
     *                                   code.
     */
    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(orientation, null, originProvider, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(orientation, originProviderDef, null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(orientation, staticVal(origin), null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarDefinition(orientation, null, originProvider, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarDefinition(orientation, originProviderDef, null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarDefinition scrollbar(
            Orientation orientation,
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarDefinition(orientation, staticVal(origin), null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    /**
     * <i>Note that arrows change the net dimensions of the Scrollbar!</i> Take the size of the
     * arrows into account when determining where you place the Component origin and how much space
     * you expect it to take up.
     *
     * @param originArrowDef                      The definition of the Button used to press "up" or
     *                                            "left" on the Scrollbar
     * @param terminusArrowDef                    The definition of the Button used to press "down"
     *                                            or "right" on the Scrollbar
     * @param arrowHoldStartThreshold             The time in ms it takes when the arrow is first
     *                                            press and held down before repeated incremented
     *                                            movement of the thumb occurs
     * @param minDurBetweenMovesWhileArrowHeld    The minimum duration of a movement at its fastest
     *                                            speed when an arrow button is being held down
     *                                            continuously
     * @param arrowHeldRepeatedTimeExponent       The degree that one movement comes faster than the
     *                                            next when the button is held down is exponential.
     *                                            The larger this value, the faster that speed
     *                                            increases.
     * @param arrowHeldRepeatedTimeExponentFactor This is a scalar value which is multiplied by the
     *                                            times repeated to the exponent provided in the
     *                                            prior argument. This doesn't determine the pace of
     *                                            acceleration, but is rather a flat multiplier in
     *                                            speed.
     */
    public ScrollbarDefinition withArrowButtons(
            ButtonDefinition originArrowDef,
            ButtonDefinition terminusArrowDef,
            int arrowHoldStartThreshold,
            int minDurBetweenMovesWhileArrowHeld,
            float arrowHeldRepeatedTimeExponent,
            float arrowHeldRepeatedTimeExponentFactor
    ) {
        this.originArrowDef = originArrowDef;
        this.terminusArrowDef = terminusArrowDef;
        this.arrowHoldStartThreshold = arrowHoldStartThreshold;
        this.minDurBetweenMovesWhileArrowHeld = minDurBetweenMovesWhileArrowHeld;
        this.arrowHeldRepeatedTimeExponent = arrowHeldRepeatedTimeExponent;
        this.arrowHeldRepeatedTimeExponentFactor = arrowHeldRepeatedTimeExponentFactor;

        return this;
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}
