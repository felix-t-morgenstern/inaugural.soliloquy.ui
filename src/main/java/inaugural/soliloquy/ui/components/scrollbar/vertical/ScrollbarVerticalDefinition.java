package inaugural.soliloquy.ui.components.scrollbar.vertical;

import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollbarVerticalDefinition extends AbstractContentDefinition {
    public final static int INSTANT_ARROW_CLICK_MOVEMENT_SPEED = 0;

    public final AbstractProviderDefinition<Vertex> ORIGIN_PROVIDER_DEF;
    public final ProviderAtTime<Vertex> ORIGIN_PROVIDER;
    public final RectangleRenderableDefinition TRACK_DEF;
    public final ButtonDefinition THUMB_DEF;
    public final AbstractProviderDefinition<Float> THUMB_MOVE_AMOUNT_PROVIDER_DEF;
    public final int THUMB_INCREMENT_MOVE_DUR;

    public ButtonDefinition topArrowDef;
    public ButtonDefinition bottomArrowDef;
    public int arrowHoldStartThreshold;
    public int minDurBetweenMovesWhileArrowHeld;
    public float arrowHeldRepeatedTimeExponent;
    public float arrowHeldRepeatedTimeExponentFactor;

    private ScrollbarVerticalDefinition(
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
        TRACK_DEF = trackDef;
        THUMB_DEF = thumbDef;
        ORIGIN_PROVIDER_DEF = originProviderDef;
        ORIGIN_PROVIDER = originProvider;
        THUMB_MOVE_AMOUNT_PROVIDER_DEF = thumbMoveAmountProviderDef;
        THUMB_INCREMENT_MOVE_DUR = thumbIncrementMoveDur;
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
     *                                   0; the constant {@link #INSTANT_ARROW_CLICK_MOVEMENT_SPEED}
     *                                   will also provide this value, and may make for more legible
     *                                   code.
     */
    public static ScrollbarVerticalDefinition scrollbarVertical(
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(null, originProvider, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(originProviderDef, null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(staticVal(origin), null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(null, originProvider, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(originProviderDef, null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            AbstractProviderDefinition<Float> thumbMoveAmountProviderDef,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(staticVal(origin), null, track, thumb,
                thumbMoveAmountProviderDef, thumbIncrementMoveDur, 0, randomUUID());
    }

    /**
     * <i>Note that arrows change the net dimensions of the Scrollbar!</i> Take the size of the
     * arrows into account when determining where you place the Component origin and how much space
     * you expect it to take up.
     *
     * @param arrowTopDef                         The definition of the Button used to press "up" on
     *                                            the Scrollbar
     * @param arrowBottomDef                      The definition of the Button used to press "down"
     *                                            on the Scrollbar
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
    public ScrollbarVerticalDefinition withArrowButtons(
            ButtonDefinition arrowTopDef,
            ButtonDefinition arrowBottomDef,
            int arrowHoldStartThreshold,
            int minDurBetweenMovesWhileArrowHeld,
            float arrowHeldRepeatedTimeExponent,
            float arrowHeldRepeatedTimeExponentFactor
    ) {
        this.topArrowDef = arrowTopDef;
        this.bottomArrowDef = arrowBottomDef;
        this.arrowHoldStartThreshold = arrowHoldStartThreshold;
        this.minDurBetweenMovesWhileArrowHeld = minDurBetweenMovesWhileArrowHeld;
        this.arrowHeldRepeatedTimeExponent = arrowHeldRepeatedTimeExponent;
        this.arrowHeldRepeatedTimeExponentFactor = arrowHeldRepeatedTimeExponentFactor;

        return this;
    }
}
