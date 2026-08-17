package inaugural.soliloquy.ui.components.scrollbarvertical;

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
    public final int THUMB_INCREMENT_MOVE_DUR;

    public ButtonDefinition topArrowDef;
    public ButtonDefinition bottomArrowDef;
    public AbstractProviderDefinition<Float> arrowClickThumbMoveAmount;
    public int arrowClickThumbMoveDur;
    public int arrowHoldStartThreshold;

    private ScrollbarVerticalDefinition(AbstractProviderDefinition<Vertex> originProviderDef,
                                        ProviderAtTime<Vertex> originProvider,
                                        RectangleRenderableDefinition trackDef,
                                        ButtonDefinition thumbDef,
                                        int thumbIncrementMoveDur,
                                        int z,
                                        UUID uuid) {
        super(z, uuid);
        TRACK_DEF = trackDef;
        THUMB_DEF = thumbDef;
        ORIGIN_PROVIDER_DEF = originProviderDef;
        ORIGIN_PROVIDER = originProvider;
        THUMB_INCREMENT_MOVE_DUR = thumbIncrementMoveDur;
    }

    /**
     * @param originProvider        Provides the origin (upper-left corner) of the Scrollbar
     * @param track                 The RectangleDefinition for the track (the actual bar)
     * @param thumb                 The ButtonDefinition for the thumb (the part you click and drag)
     * @param thumbIncrementMoveDur The time in milliseconds needed to move the Thumb from its
     *                              current location to the new target (calculated using
     *                              arrowClickThumbMoveAmount). If you want the movement to be
     *                              instantaneous, you can use any value less than or equal to 0;
     *                              the constant {@link #INSTANT_ARROW_CLICK_MOVEMENT_SPEED} will
     *                              also provide this value, and may make for more legible code.
     */
    public static ScrollbarVerticalDefinition scrollbarVertical(
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(null, originProvider, track, thumb,
                thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(originProviderDef, null, track, thumb,
                thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur,
            int z,
            UUID uuid
    ) {
        return new ScrollbarVerticalDefinition(staticVal(origin), null, track, thumb,
                thumbIncrementMoveDur, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            ProviderAtTime<Vertex> originProvider,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(null, originProvider, track, thumb,
                thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            AbstractProviderDefinition<Vertex> originProviderDef,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(originProviderDef, null, track, thumb,
                thumbIncrementMoveDur, 0, randomUUID());
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(
            Vertex origin,
            RectangleRenderableDefinition track,
            ButtonDefinition thumb,
            int thumbIncrementMoveDur
    ) {
        return new ScrollbarVerticalDefinition(staticVal(origin), null, track, thumb,
                thumbIncrementMoveDur, 0, randomUUID());
    }

    /**
     * <i>Note that arrows change the net dimensions of the Scrollbar!</i> Take the size of the
     * arrows into account when determining where you place the Component origin and how much space
     * you expect it to take up.
     *
     * @param arrowTopDef               The definition of the Button used to press "up" on the
     *                                  Scrollbar
     * @param arrowBottomDef            The definition of the Button used to press "down" on the
     *                                  Scrollbar
     * @param arrowClickThumbMoveAmount The definition of a Provider which provides the scrolling
     *                                  percent to move the Thumb with each click
     * @param arrowClickThumbMoveDur    The time in ms it takes for a thumb to complete a movement
     *                                  triggered by pressing or holding an arrow button
     * @param arrowHoldStartThreshold   The time in ms it takes when the arrow is first press and
     *                                  held down before repeated incremented movement of the thumb
     *                                  occurs
     */
    public ScrollbarVerticalDefinition withAnchors(
            ButtonDefinition arrowTopDef,
            ButtonDefinition arrowBottomDef,
            AbstractProviderDefinition<Float> arrowClickThumbMoveAmount,
            int arrowClickThumbMoveDur,
            int arrowHoldStartThreshold
    ) {
        this.topArrowDef = arrowTopDef;
        this.bottomArrowDef = arrowBottomDef;
        this.arrowClickThumbMoveAmount = arrowClickThumbMoveAmount;
        this.arrowClickThumbMoveDur = arrowClickThumbMoveDur;
        this.arrowHoldStartThreshold = arrowHoldStartThreshold;

        return this;
    }
}
