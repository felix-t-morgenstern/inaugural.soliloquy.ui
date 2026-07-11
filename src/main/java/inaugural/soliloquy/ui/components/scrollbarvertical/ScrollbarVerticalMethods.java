package inaugural.soliloquy.ui.components.scrollbarvertical;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.mouse.Mouse;
import soliloquy.specs.ui.EventInputs;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.*;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.input.mouse.Mouse.EventType.RELEASE;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class ScrollbarVerticalMethods {
    final static String TRACK_UNADJ_DIMENS_PROVIDER = "TRACK_UNADJ_DIMENS_PROVIDER";
    final static String TRACK_ADJ_DIMENS = "TRACK_ADJ_DIMENS";
    final static String TOP_ARROW_UUID = "TOP_ARROW_UUID";
    final static String TOP_ARROW_ORIGIN = "TOP_ARROW_ORIGIN";
    final static String BOTTOM_ARROW_UUID = "BOTTOM_ARROW_UUID";
    final static String BOTTOM_ARROW_ORIGIN = "BOTTOM_ARROW_ORIGIN";

    final static String THUMB_LOC_IN_SCROLLABLE_RANGE = "THUMB_LOCATION_IN_SCROLLABLE_RANGE";
    final static String THUMB_MOVE_ORIGIN = "THUMB_MOVE_ORIGIN";
    final static String THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE =
            "THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE";
    final static String THUMB_MOVE_AMOUNT_PROVIDER = "THUMB_MOVE_AMOUNT_PROVIDER";
    final static String THUMB_IS_PRESSED = "THUMB_IS_PRESSED";
    final static String ARROW_CLICK_THUMB_MOVE_SPEED = "ARROW_CLICK_THUMB_MOVE_SPEED";
    final static String THUMB_MOVEMENT_PROVIDER = "THUMB_MOVEMENT_PROVIDER";
    final static String THUMB_UNADJ_DIMENS_PROVIDER = "THUMB_UNADJ_DIMENS_PROVIDER";

    private final Function<UUID, Component> GET_COMPONENT;
    private final BiFunction<Component, Long, FloatBox> GET_BUTTON_UNADJ_DIMENS;
    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final TriConsumer<Integer, Mouse.EventType, Runnable>
            SUBSCRIBE_TO_MOUSE_EVENTS;

    public ScrollbarVerticalMethods(Function<UUID, Component> getComponent,
                                    BiFunction<Component, Long, FloatBox> getButtonUnadjDimens,
                                    ProviderDefinitionReader providerDefReader,
                                    TriConsumer<Integer, Mouse.EventType, Runnable> subscribeToMouseEvents) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        GET_BUTTON_UNADJ_DIMENS = Check.ifNull(getButtonUnadjDimens, "getButtonUnadjDimens");
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
        SUBSCRIBE_TO_MOUSE_EVENTS = Check.ifNull(subscribeToMouseEvents, "subscribeToMouseEvents");
    }

    public final static String ScrollbarVertical_setDimens = "ScrollbarVertical_setDimens";

    public FloatBox ScrollbarVertical_setDimens(Component scrollbar, long timestamp) {
        ProviderAtTime<Vertex> scrollbarOriginProvider =
                getFromData(scrollbar, COMPONENT_ORIGIN_PROVIDER);
        var scrollbarOrigin = scrollbarOriginProvider.provide(timestamp);
        scrollbar.data().put(COMPONENT_ORIGIN, scrollbarOrigin);

        ProviderAtTime<FloatBox> trackUnadjDimensProvider =
                getFromData(scrollbar, TRACK_UNADJ_DIMENS_PROVIDER);
        var trackUnadjDimens = trackUnadjDimensProvider.provide(timestamp);

        UUID topArrowUuid =
                getFromData(scrollbar, TOP_ARROW_UUID);
        if (topArrowUuid != null) {
            var topArrowUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(
                    GET_COMPONENT.apply(topArrowUuid),
                    timestamp
            );
            var topArrowHalfWidth = topArrowUnadjDimens.width() / 2f;

            var bottomArrowUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(
                    GET_COMPONENT.apply(getFromData(scrollbar, BOTTOM_ARROW_UUID)),
                    timestamp
            );
            var bottomArrowHalfWidth = bottomArrowUnadjDimens.width() / 2f;

            var scrollbarUnadjLeftX = minOf(
                    trackUnadjDimens.LEFT_X,
                    topArrowUnadjDimens.LEFT_X,
                    bottomArrowUnadjDimens.LEFT_X
            );
            var scrollbarUnadjRightX = minOf(
                    trackUnadjDimens.RIGHT_X,
                    topArrowUnadjDimens.RIGHT_X,
                    bottomArrowUnadjDimens.RIGHT_X
            );
            var scrollbarUnadjWidth = scrollbarUnadjRightX - scrollbarUnadjLeftX;

            var topArrowAdjTopY = scrollbarOrigin.Y;
            var trackAdjTopY = topArrowAdjTopY + topArrowUnadjDimens.height();
            var bottomArrowAdjTopY = trackAdjTopY + trackUnadjDimens.height();

            var scrollbarAdjCenterX = scrollbarOrigin.X + (scrollbarUnadjWidth / 2f);
            var trackAdjLeftX = scrollbarAdjCenterX - (trackUnadjDimens.width() / 2f);
            var topArrowAdjLeftX = scrollbarAdjCenterX - topArrowHalfWidth;
            var bottomArrowAdjLeftX = scrollbarAdjCenterX - bottomArrowHalfWidth;

            var scrollbarAdjDimens = floatBoxOf(
                    scrollbarOrigin,
                    scrollbarUnadjWidth,
                    topArrowUnadjDimens.height() + trackUnadjDimens.height() +
                            bottomArrowUnadjDimens.height()
            );
            scrollbar.data().putAll(mapOf(
                    COMPONENT_DIMENS,
                    scrollbarAdjDimens,
                    TRACK_ADJ_DIMENS,
                    floatBoxOf(
                            vertexOf(trackAdjLeftX, trackAdjTopY),
                            trackUnadjDimens.width(),
                            trackUnadjDimens.height()
                    ),
                    TOP_ARROW_ORIGIN,
                    floatBoxOf(
                            vertexOf(topArrowAdjLeftX, topArrowAdjTopY),
                            topArrowUnadjDimens.width(),
                            topArrowUnadjDimens.height()
                    ),
                    BOTTOM_ARROW_ORIGIN,
                    floatBoxOf(
                            vertexOf(bottomArrowAdjLeftX, bottomArrowAdjTopY),
                            bottomArrowUnadjDimens.width(),
                            bottomArrowUnadjDimens.height()
                    )
            ));

            return scrollbarAdjDimens;
        }
        else {
            var trackAdjDimens = floatBoxOf(
                    scrollbarOrigin,
                    trackUnadjDimens.width(),
                    trackUnadjDimens.height()
            );
            scrollbar.data().putAll(mapOf(
                    COMPONENT_DIMENS,
                    trackAdjDimens,
                    TRACK_ADJ_DIMENS,
                    trackAdjDimens
            ));

            return trackAdjDimens;
        }
    }

    public final static String ScrollbarVertical_provideAdjTrackDimens =
            "ScrollbarVertical_provideAdjTrackDimens";

    public FloatBox ScrollbarVertical_provideAdjTrackDimens(FunctionalProvider.Inputs inputs) {
        return getInComponentData(inputs, TRACK_ADJ_DIMENS);
    }

    public final static String ScrollbarVertical_provideAdjTopArrowOrigin =
            "ScrollbarVertical_provideAdjTopArrowOrigin";

    public Vertex ScrollbarVertical_provideAdjTopArrowOrigin(FunctionalProvider.Inputs inputs) {
        return getInComponentData(inputs, TOP_ARROW_ORIGIN);
    }

    public final static String ScrollbarVertical_provideAdjBottomArrowOrigin =
            "ScrollbarVertical_provideAdjBottomArrowOrigin";

    public Vertex ScrollbarVertical_provideAdjBottomArrowOrigin(FunctionalProvider.Inputs inputs) {
        return getInComponentData(inputs, BOTTOM_ARROW_ORIGIN);
    }
    
    public final static String ScrollbarVertical_provideThumbLocInScrollableRange =
            "ScrollbarVertical_provideThumbLocInScrollableRange";

    public float ScrollbarVertical_provideThumbLocInScrollableRange(
            FunctionalProvider.Inputs inputs
    ) {
        return getInComponentData(inputs, THUMB_LOC_IN_SCROLLABLE_RANGE);
    }

    private <T> T getInComponentData(FunctionalProvider.Inputs inputs, String key) {
        var scrollbar = getScrollbar(inputs);

        return getFromData(scrollbar, key);
    }

    private Component getScrollbar(FunctionalProvider.Inputs inputs) {
        return GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
    }

    public final static String ScrollbarVertical_thumbLoc = "ScrollbarVertical_thumbLoc";

    public FloatBox ScrollbarVertical_thumbLoc(FunctionalProvider.Inputs inputs) {
        var scrollbar = getScrollbar(inputs);
        ProviderAtTime<Float> thumbMoveProvider = getFromData(scrollbar, THUMB_MOVEMENT_PROVIDER);
        if (thumbMoveProvider != null) {
            float thumbMoveOrigin = getFromData(scrollbar, THUMB_MOVE_ORIGIN);
        }
        
        // TODO: Restart work here
        // TODO: Restart work here
        // TODO: Restart work here
        // TODO: Restart work here
        // TODO: Restart work here
        return null;
    }

    public final static String ScrollbarVertical_trackClick = "ScrollbarVertical_trackClick";

    public void ScrollbarVertical_trackClick(EventInputs e) {
        var track = (RectangleRenderable) e.renderable;
        var trackDimens = track.getRenderingDimensionsProvider().provide(e.TIMESTAMP);

        var scrollbar = track.getContainingComponent();
        ProviderAtTime<FloatBox> thumbUnadjDimensProvider =
                getFromData(scrollbar, THUMB_UNADJ_DIMENS_PROVIDER);
        var thumbUnadjDimens = thumbUnadjDimensProvider.provide(e.TIMESTAMP);

        var halfThumbHeight = thumbUnadjDimens.height() / 2f;
        var scrollableRangeTop = trackDimens.TOP_Y + halfThumbHeight;
        var scrollableRangeBottom = trackDimens.BOTTOM_Y - halfThumbHeight;
        var relativeMouseLocWithinScrollableRange = constrain(
                (e.mouseLoc.Y - scrollableRangeTop) / (scrollableRangeBottom - scrollableRangeTop),
                0f,
                1f
        );

        scrollbar.data()
                .put(THUMB_LOC_IN_SCROLLABLE_RANGE, relativeMouseLocWithinScrollableRange);
        scrollbar.data().put(THUMB_MOVE_AMOUNT_PROVIDER, null);
    }

    public final static String ScrollbarVertical_thumbPress = "ScrollbarVertical_thumbPress";

    public void ScrollbarVertical_thumbPress(EventInputs e) {
        var scrollbar = e.renderable.getContainingComponent();

        scrollbar.data().remove(THUMB_MOVEMENT_PROVIDER);
        scrollbar.data().put(THUMB_IS_PRESSED, true);

        SUBSCRIBE_TO_MOUSE_EVENTS.accept(
                LEFT_MOUSE_BUTTON,
                RELEASE,
                () -> ScrollbarVertical_thumbRelease(e)
        );
    }

    public final static String ScrollbarVertical_thumbRelease = "ScrollbarVertical_thumbRelease";

    public void ScrollbarVertical_thumbRelease(EventInputs e) {
        var scrollbar = e.renderable.getContainingComponent();

        scrollbar.data().remove(THUMB_IS_PRESSED);
    }

    public final static String ScrollbarVertical_topArrowClick = "ScrollbarVertical_topArrowClick";

    public void ScrollbarVertical_topArrowClick(EventInputs e) {
        var scrollbar = e.renderable.getContainingComponent();

        ProviderAtTime<Float> arrowClickThumbMoveAmountProvider =
                getFromData(scrollbar, THUMB_MOVE_AMOUNT_PROVIDER);
        var arrowClickThumbMoveAmount = arrowClickThumbMoveAmountProvider.provide(e.TIMESTAMP);
        float thumbLocInScrollableRange = getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE);
        var thumbTargetLocInScrollableRange = constrain(
                thumbLocInScrollableRange - arrowClickThumbMoveAmount,
                0f,
                1f
        );

        ProviderAtTime<Float> thumbMovementProvider =
                getFromData(scrollbar, THUMB_MOVEMENT_PROVIDER);
        // We only get the max time if we need it; but if we do, we'll need it twice
        var thumbMovementProviderMaxTime = supplyIfNullElseTransform(
                thumbMovementProvider,
                this::maxTime,
                () -> Long.MIN_VALUE
        );
        if (e.TIMESTAMP >= thumbMovementProviderMaxTime) {
            // make new
            var newThumbMovementProviderDef = finiteSinusoidMoving(
                    pairOf(0, 0f),
                    pairOf(getFromData(scrollbar, ARROW_CLICK_THUMB_MOVE_SPEED), 1f)
            );
            var newThumbMovementProvider =
                    PROVIDER_DEF_READER.read(newThumbMovementProviderDef, e.TIMESTAMP);
            scrollbar.data().put(THUMB_MOVEMENT_PROVIDER, newThumbMovementProvider);
            scrollbar.data().put(THUMB_MOVE_ORIGIN, thumbLocInScrollableRange);
        }
        else {
            // "update" prev
            @SuppressWarnings("unchecked")
            var valsAtTimes = (Map<Long, Float>) thumbMovementProvider.representation();

        }
    }

    private long maxTime(ProviderAtTime<Float> provider) {
        @SuppressWarnings("unchecked")
        var valsAtTimes = (Map<Long, Float>) provider.representation();
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        var maxTime = valsAtTimes.keySet().stream().max(Comparator.comparingLong(k -> k)).get();

        return maxTime;
    }
}
