package inaugural.soliloquy.ui.components.scrollbar;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.reflection.Reflection;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.timing.GlobalClock;
import soliloquy.specs.io.input.mouse.Mouse;
import soliloquy.specs.ui.EventInputs;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.*;
import static inaugural.soliloquy.tools.Tools.constrain;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.exception.CheckedExceptionWrapper.sleep;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.Constants.COMPONENT_DIMENS;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.EventInputs.eventInputs;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class ScrollbarMethods {
    public final static String TRACK_UNADJ_DIMENS_PROVIDER = "TRACK_UNADJ_DIMENS_PROVIDER";
    final static String TRACK_ADJ_DIMENS = "TRACK_ADJ_DIMENS";
    public final static String ORIGIN_ARROW_UUID = "ORIGIN_ARROW_UUID";
    final static String ORIGIN_ARROW_ORIGIN = "ORIGIN_ARROW_ORIGIN";
    public final static String TERMINUS_ARROW_UUID = "TERMINUS_ARROW_UUID";
    final static String TERMINUS_ARROW_ORIGIN = "TERMINUS_ARROW_ORIGIN";
    final static String IS_VERTICAL = "IS_VERTICAL";

    public final static String THUMB_LOC_IN_SCROLLABLE_RANGE = "THUMB_LOC_IN_SCROLLABLE_RANGE";

    final static String LAST_TIMESTAMP_THUMB_ORIGIN = "LAST_TIMESTAMP_THUMB_ORIGIN";
    final static String THUMB_ORIGIN = "THUMB_ORIGIN";
    public final static String THUMB_UUID = "THUMB_UUID";
    final static String THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE =
            "THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE";
    final static String THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE =
            "THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE";
    public final static String THUMB_MOVE_AMOUNT_PROVIDER = "THUMB_MOVE_AMOUNT_PROVIDER";
    public final static String THUMB_IS_PRESSED = "THUMB_IS_PRESSED";
    public final static String THUMB_INCREMENT_MOVE_DUR = "THUMB_INCREMENT_MOVE_DUR";
    final static String THUMB_MOVE_PROGRESS_PROVIDER = "THUMB_MOVE_PROGRESS_PROVIDER";

    final static String ARROW_BEING_HELD = "ARROW_BEING_HELD";
    public final static String ARROW_HOLD_START_THRESHOLD = "ARROW_HOLD_START_THRESHOLD";
    public final static String MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD =
            "MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD";
    public final static String ARROW_HELD_REPEATED_TIME_EXPONENT =
            "ARROW_HELD_REPEATED_TIME_EXPONENT";
    public final static String ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR =
            "ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR";

    private final Function<UUID, Component> GET_COMPONENT;
    private final BiFunction<Component, Long, FloatBox> GET_BUTTON_UNADJ_DIMENS;
    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final Supplier<Vertex> GET_MOST_RECENT_MOUSE_LOC;
    private final TriConsumer<Integer, Mouse.EventType, Runnable> SUBSCRIBE_TO_NEXT_MOUSE_EVENT;
    private final Consumer<EventInputs> PRESS_BUTTON;
    private final GlobalClock CLOCK;

    public ScrollbarMethods(Function<UUID, Component> getComponent,
                               BiFunction<Component, Long, FloatBox> getButtonUnadjDimens,
                               ProviderDefinitionReader providerDefReader,
                               Supplier<Vertex> getMostRecentMouseLoc,
                               TriConsumer<Integer, Mouse.EventType, Runnable> subscribeToNextMouseEvent,
                               Consumer<EventInputs> pressButton,
                               GlobalClock clock) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        GET_BUTTON_UNADJ_DIMENS = Check.ifNull(getButtonUnadjDimens, "getButtonUnadjDimens");
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
        GET_MOST_RECENT_MOUSE_LOC = Check.ifNull(getMostRecentMouseLoc, "getMostRecentMouseLoc");
        SUBSCRIBE_TO_NEXT_MOUSE_EVENT =
                Check.ifNull(subscribeToNextMouseEvent, "subscribeToNextMouseEvent");
        PRESS_BUTTON = Check.ifNull(pressButton, "pressButton");
        CLOCK = Check.ifNull(clock, "clock");
    }

    public final static String Scrollbar_getDimens = "Scrollbar_getDimens";

    public FloatBox Scrollbar_getDimens(Component scrollbar, long timestamp) {
        Long lastTimestamp = getFromData(scrollbar, LAST_TIMESTAMP);
        if (timestamp == defaultIfNull(lastTimestamp, Long.MIN_VALUE)) {
            return getFromData(scrollbar, COMPONENT_DIMENS);
        }

        ProviderAtTime<Vertex> scrollbarOriginProvider =
                getFromData(scrollbar, COMPONENT_ORIGIN_PROVIDER);
        var scrollbarOrigin = scrollbarOriginProvider.provide(timestamp);

        ProviderAtTime<FloatBox> trackUnadjDimensProvider =
                getFromData(scrollbar, TRACK_UNADJ_DIMENS_PROVIDER);
        var trackUnadjDimens = trackUnadjDimensProvider.provide(timestamp);

        UUID originArrowUuid = getFromData(scrollbar, ORIGIN_ARROW_UUID);
        // If there are arrows, the math gets more annoying
        if (originArrowUuid != null) {
            var originArrowUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(
                    GET_COMPONENT.apply(originArrowUuid),
                    timestamp
            );

            var terminusArrowUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(
                    GET_COMPONENT.apply(getFromData(scrollbar, TERMINUS_ARROW_UUID)),
                    timestamp
            );

            FloatBox scrollbarAdjDimens;
            float trackAdjLeftX;
            float trackAdjTopY;
            float originArrowAdjLeftX;
            float originArrowAdjTopY;
            float terminusArrowAdjLeftX;
            float terminusArrowAdjTopY;
            if (getFromData(scrollbar, IS_VERTICAL)) {
                var originArrowHalfWidth = originArrowUnadjDimens.width() / 2f;

                var terminusArrowHalfWidth = terminusArrowUnadjDimens.width() / 2f;

                var scrollbarUnadjLeftX = minOf(
                        trackUnadjDimens.LEFT_X,
                        originArrowUnadjDimens.LEFT_X,
                        terminusArrowUnadjDimens.LEFT_X
                );
                var scrollbarUnadjRightX = minOf(
                        trackUnadjDimens.RIGHT_X,
                        originArrowUnadjDimens.RIGHT_X,
                        terminusArrowUnadjDimens.RIGHT_X
                );
                var scrollbarUnadjWidth = scrollbarUnadjRightX - scrollbarUnadjLeftX;

                originArrowAdjTopY = scrollbarOrigin.Y;
                trackAdjTopY = originArrowAdjTopY + originArrowUnadjDimens.height();
                terminusArrowAdjTopY = trackAdjTopY + trackUnadjDimens.height();

                var scrollbarAdjCenterX = scrollbarOrigin.X + (scrollbarUnadjWidth / 2f);
                trackAdjLeftX = scrollbarAdjCenterX - (trackUnadjDimens.width() / 2f);
                originArrowAdjLeftX = scrollbarAdjCenterX - originArrowHalfWidth;
                terminusArrowAdjLeftX = scrollbarAdjCenterX - terminusArrowHalfWidth;

                scrollbarAdjDimens = floatBoxOf(
                        scrollbarOrigin,
                        scrollbarUnadjWidth,
                        originArrowUnadjDimens.height() + trackUnadjDimens.height() +
                                terminusArrowUnadjDimens.height()
                );
            }
            else {
                var originArrowHalfHeight = originArrowUnadjDimens.height() / 2f;

                var terminusArrowHalfHeight = terminusArrowUnadjDimens.height() / 2f;

                var scrollbarUnadjTopY = minOf(
                        trackUnadjDimens.TOP_Y,
                        originArrowUnadjDimens.TOP_Y,
                        terminusArrowUnadjDimens.TOP_Y
                );
                var scrollbarUnadjBottomY = minOf(
                        trackUnadjDimens.BOTTOM_Y,
                        originArrowUnadjDimens.BOTTOM_Y,
                        terminusArrowUnadjDimens.BOTTOM_Y
                );
                var scrollbarUnadjHeight = scrollbarUnadjBottomY - scrollbarUnadjTopY;

                originArrowAdjLeftX = scrollbarOrigin.X;
                trackAdjLeftX = originArrowAdjLeftX + originArrowUnadjDimens.width();
                terminusArrowAdjLeftX = trackAdjLeftX + trackUnadjDimens.width();

                var scrollbarAdjCenterY = scrollbarOrigin.Y + (scrollbarUnadjHeight / 2f);
                trackAdjTopY = scrollbarAdjCenterY - (trackUnadjDimens.height() / 2f);
                originArrowAdjTopY = scrollbarAdjCenterY - originArrowHalfHeight;
                terminusArrowAdjTopY = scrollbarAdjCenterY - terminusArrowHalfHeight;

                scrollbarAdjDimens = floatBoxOf(
                        scrollbarOrigin,
                        originArrowUnadjDimens.width() + trackUnadjDimens.width() +
                                terminusArrowUnadjDimens.width(),
                        scrollbarUnadjHeight
                );
            }
            scrollbar.data().putAll(mapOf(
                    COMPONENT_DIMENS,
                    scrollbarAdjDimens,
                    TRACK_ADJ_DIMENS,
                    floatBoxOf(
                            vertexOf(trackAdjLeftX, trackAdjTopY),
                            trackUnadjDimens.width(),
                            trackUnadjDimens.height()
                    ),
                    ORIGIN_ARROW_ORIGIN,
                    vertexOf(originArrowAdjLeftX, originArrowAdjTopY),
                    TERMINUS_ARROW_ORIGIN,
                    vertexOf(terminusArrowAdjLeftX, terminusArrowAdjTopY)
            ));

            return scrollbarAdjDimens;
        }
        // If there are no arrows, the math is significantly easier
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

    public final static String Scrollbar_provideAdjTrackDimens = "Scrollbar_provideAdjTrackDimens";

    public FloatBox Scrollbar_provideAdjTrackDimens(FunctionalProvider.Inputs inputs) {
        var scrollbar = getScrollbar(inputs);
        Scrollbar_getDimens(scrollbar, inputs.timestamp());
        return getInComponentData(inputs, TRACK_ADJ_DIMENS);
    }

    public final static String Scrollbar_provideAdjTopArrowOrigin =
            "Scrollbar_provideAdjTopArrowOrigin";

    public Vertex Scrollbar_provideAdjTopArrowOrigin(FunctionalProvider.Inputs inputs) {
        return getInComponentData(inputs, ORIGIN_ARROW_ORIGIN);
    }

    public final static String Scrollbar_provideAdjBottomArrowOrigin =
            "Scrollbar_provideAdjBottomArrowOrigin";

    public Vertex Scrollbar_provideAdjBottomArrowOrigin(FunctionalProvider.Inputs inputs) {
        return getInComponentData(inputs, TERMINUS_ARROW_ORIGIN);
    }

    private <T> T getInComponentData(FunctionalProvider.Inputs inputs, String key) {
        var scrollbar = getScrollbar(inputs);

        return getFromData(scrollbar, key);
    }

    public final static String Scrollbar_thumbOrigin = "Scrollbar_thumbOrigin";

    public Vertex Scrollbar_thumbOrigin(FunctionalProvider.Inputs inputs) {
        var scrollbar = getScrollbar(inputs);

        Long lastTimestampThumbDimens = getFromData(scrollbar, LAST_TIMESTAMP_THUMB_ORIGIN);
        if (lastTimestampThumbDimens != null && lastTimestampThumbDimens == inputs.timestamp()) {
            return getFromData(scrollbar, THUMB_ORIGIN);
        }

        var thumb = GET_COMPONENT.apply(getFromData(scrollbar, THUMB_UUID));
        var thumbUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(thumb, inputs.timestamp());
        // setDimens is the prerender hook, which is called prior to the rendering of contents
        FloatBox trackAdjDimens = getFromData(scrollbar, TRACK_ADJ_DIMENS);

        var isVertical = falseIfNull(getFromData(scrollbar, IS_VERTICAL));

        float thumbLocInScrollableRange;
        ProviderAtTime<Float> thumbMoveProgressProvider =
                getFromData(scrollbar, THUMB_MOVE_PROGRESS_PROVIDER);

        Float thumbAdjTopYMin = null;
        float thumbAdjTopYMax;
        Float thumbAdjLeftXMin = null;
        float thumbAdjLeftXMax;
        float scrollableLengthOnScreen;
        if (isVertical) {
            thumbAdjTopYMin = trackAdjDimens.TOP_Y;
            thumbAdjTopYMax = trackAdjDimens.BOTTOM_Y - thumbUnadjDimens.height();
            scrollableLengthOnScreen = thumbAdjTopYMax - thumbAdjTopYMin;
        }
        else {
            thumbAdjLeftXMin = trackAdjDimens.LEFT_X;
            thumbAdjLeftXMax = trackAdjDimens.RIGHT_X - thumbUnadjDimens.width();
            scrollableLengthOnScreen = thumbAdjLeftXMax - thumbAdjLeftXMin;
        }

        var thumbHalfWidth = thumbUnadjDimens.width() / 2f;
        var thumbHalfHeight = thumbUnadjDimens.height() / 2f;

        if (falseIfNull(getFromData(scrollbar, THUMB_IS_PRESSED))) {
            var mostRecentMouseLoc = GET_MOST_RECENT_MOUSE_LOC.get();

            if (isVertical) {
                var scrollableRangeOnScreenTopY = trackAdjDimens.TOP_Y + thumbHalfHeight;
                var scrollableRangeOnScreenBottomY = trackAdjDimens.BOTTOM_Y - thumbHalfHeight;
                var mouseLocWithinScrollableRange = constrain(
                        mostRecentMouseLoc.Y,
                        scrollableRangeOnScreenTopY,
                        scrollableRangeOnScreenBottomY
                );

                thumbLocInScrollableRange =
                        (mouseLocWithinScrollableRange - scrollableRangeOnScreenTopY) /
                                scrollableLengthOnScreen;
            }
            else {
                var scrollableRangeOnScreenLeftX = trackAdjDimens.LEFT_X + thumbHalfWidth;
                var scrollableRangeOnScreenRightX = trackAdjDimens.RIGHT_X - thumbHalfWidth;
                var mouseLocWithinScrollableRange = constrain(
                        mostRecentMouseLoc.X,
                        scrollableRangeOnScreenLeftX,
                        scrollableRangeOnScreenRightX
                );

                thumbLocInScrollableRange =
                        (mouseLocWithinScrollableRange - scrollableRangeOnScreenLeftX) /
                                scrollableLengthOnScreen;
            }
            scrollbar.data().put(THUMB_LOC_IN_SCROLLABLE_RANGE, thumbLocInScrollableRange);
        }
        else if (thumbMoveProgressProvider != null) {
            @SuppressWarnings("unchecked") Map<Long, Float> thumbMoveProviderVals =
                    (Map<Long, Float>) thumbMoveProgressProvider.representation();
            @SuppressWarnings("OptionalGetWithoutIsPresent") long
                    thumbMoveProviderLastTimestamp =
                    thumbMoveProviderVals.keySet().stream().max(Comparator.naturalOrder())
                            .get();

            // if we've reached the end, we're at the target; drop the move provider, etc.
            if (inputs.timestamp() >= thumbMoveProviderLastTimestamp) {
                thumbLocInScrollableRange =
                        getFromData(scrollbar, THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE);

                scrollbar.data().remove(THUMB_MOVE_PROGRESS_PROVIDER);
                scrollbar.data().remove(THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE);
                scrollbar.data().remove(THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE);
            }
            // if we haven't reached the end, figure out how far we've gone
            else {
                thumbLocInScrollableRange = thumbLocInScrollableRangeFromProvider(
                        scrollbar,
                        thumbMoveProgressProvider,
                        inputs.timestamp()
                );
            }

            scrollbar.data().put(THUMB_LOC_IN_SCROLLABLE_RANGE, thumbLocInScrollableRange);
        }
        else {
            thumbLocInScrollableRange =
                    defaultIfNull(getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE), 0f);
        }

        float thumbAdjLeftX;
        float thumbAdjTopY;
        if (isVertical) {
            var trackAdjCenterX = ave(trackAdjDimens.LEFT_X, trackAdjDimens.RIGHT_X);
            thumbAdjLeftX = trackAdjCenterX - thumbHalfWidth;

            thumbAdjTopY = thumbAdjTopYMin + (scrollableLengthOnScreen * thumbLocInScrollableRange);
        }
        else {
            var trackAdjCenterY = ave(trackAdjDimens.TOP_Y, trackAdjDimens.BOTTOM_Y);
            thumbAdjTopY = trackAdjCenterY - (thumbUnadjDimens.height() / 2f);

            thumbAdjLeftX =
                    thumbAdjLeftXMin + (scrollableLengthOnScreen * thumbLocInScrollableRange);

        }
        var thumbOrigin = vertexOf(thumbAdjLeftX, thumbAdjTopY);

        scrollbar.data().put(THUMB_ORIGIN, thumbOrigin);

        return thumbOrigin;
    }

    private float thumbLocInScrollableRangeFromProvider(
            Component scrollbar,
            ProviderAtTime<Float> thumbMoveProgressProvider,
            long timestamp
    ) {
        float thumbMoveOriginInScrollableRange =
                getFromData(scrollbar, THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE);
        float thumbTargetLocInScrollableRange =
                getFromData(scrollbar, THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE);
        var thumbMoveProgress = thumbMoveProgressProvider.provide(timestamp);

        return thumbMoveOriginInScrollableRange +
                (thumbMoveProgress *
                        (thumbTargetLocInScrollableRange - thumbMoveOriginInScrollableRange));
    }

    private Component getScrollbar(FunctionalProvider.Inputs inputs) {
        return GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
    }
    
    public final static String Scrollbar_trackPress = "Scrollbar_trackPress";

    public void Scrollbar_trackPress(EventInputs e) {
        var track = (RectangleRenderable) e.renderable;
        var trackDimens = track.getRenderingDimensionsProvider().provide(e.TIMESTAMP);

        var scrollbar = track.getContainingComponent();
        Component thumb = GET_COMPONENT.apply(getFromData(scrollbar, THUMB_UUID));
        var thumbUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(thumb, e.TIMESTAMP);
        
        float relativeMouseLocWithinScrollableRange;
        if (falseIfNull(getFromData(scrollbar, IS_VERTICAL))) {
            var halfThumbHeight = thumbUnadjDimens.height() / 2f;
            var scrollableRangeTop = trackDimens.TOP_Y + halfThumbHeight;
            var scrollableRangeBottom = trackDimens.BOTTOM_Y - halfThumbHeight;
            relativeMouseLocWithinScrollableRange = constrain(
                    (e.mouseLoc.Y - scrollableRangeTop) /
                            (scrollableRangeBottom - scrollableRangeTop),
                    0f,
                    1f
            );
        }
        else {
            var halfThumbWidth = thumbUnadjDimens.width() / 2f;
            var scrollableRangeLeft = trackDimens.LEFT_X + halfThumbWidth;
            var scrollableRangeRight = trackDimens.RIGHT_X - halfThumbWidth;
            relativeMouseLocWithinScrollableRange = constrain(
                    (e.mouseLoc.Y - scrollableRangeLeft) /
                            (scrollableRangeRight - scrollableRangeLeft),
                    0f,
                    1f
            );
        }

        scrollbar.data()
                .put(THUMB_LOC_IN_SCROLLABLE_RANGE, relativeMouseLocWithinScrollableRange);
        scrollbar.data().remove(THUMB_MOVE_PROGRESS_PROVIDER);

        var thumbPressEvent = eventInputs(e.TIMESTAMP)
                .withMouseEvent(e.mouseButton, e.mouseEvent, e.mouseLoc, null, thumb);
        PRESS_BUTTON.accept(thumbPressEvent);
    }

    public final static String Scrollbar_thumbPress = "Scrollbar_thumbPress";

    public void Scrollbar_thumbPress(EventInputs e) {
        var thumb = e.component;
        var scrollbar = thumb.getContainingComponent();

        scrollbar.data().remove(THUMB_MOVE_PROGRESS_PROVIDER);
        scrollbar.data().put(THUMB_IS_PRESSED, true);

        SUBSCRIBE_TO_NEXT_MOUSE_EVENT.accept(
                LEFT_MOUSE_BUTTON,
                Mouse.EventType.RELEASE,
                () -> scrollbar.data().put(THUMB_IS_PRESSED, false)
        );
    }

    public final static String Scrollbar_topArrowPress = "Scrollbar_topArrowPress";

    public void Scrollbar_topArrowPress(EventInputs e) {
        pressArrow(e, true);
    }

    public final static String Scrollbar_bottomArrowPress =
            "Scrollbar_bottomArrowPress";

    public void Scrollbar_bottomArrowPress(EventInputs e) {
        pressArrow(e, false);
    }

    private void pressArrow(EventInputs e, boolean moveUp) {
        var scrollbar = e.component.getContainingComponent();
        scrollbar.data().put(ARROW_BEING_HELD, true);

        final int startThreshold = getFromData(scrollbar, ARROW_HOLD_START_THRESHOLD);

        new Thread(() -> {
            var timesRepeated = 0;

            sleep(startThreshold);
            while (falseIfNull(getFromData(scrollbar, ARROW_BEING_HELD))) {
                var timestamp = CLOCK.globalTimestamp();

                incrementMovement(scrollbar, timestamp, moveUp);

                var timeUntilNextIncrement =
                        timeTilNextThumbMovementWhileArrowHeld(scrollbar, timesRepeated++);

                sleep(timeUntilNextIncrement);
            }
        }).start();
    }

    public final static String Scrollbar_topArrowReleaseAfterPress =
            "Scrollbar_topArrowReleaseAfterPress";

    public void Scrollbar_topArrowReleaseAfterPress(EventInputs e) {
        releaseArrowAndIncrementMovement(e, true);
    }

    public final static String Scrollbar_bottomArrowReleaseAfterPress =
            "Scrollbar_bottomArrowReleaseAfterPress";

    public void Scrollbar_bottomArrowReleaseAfterPress(EventInputs e) {
        releaseArrowAndIncrementMovement(e, false);
    }

    public void releaseArrowAndIncrementMovement(EventInputs e, boolean moveUp) {
        // The Renderable is a Rectangle within the Button, *within* the Scrollbar
        var scrollbar = e.renderable.getContainingComponent().getContainingComponent();
        scrollbar.data().put(ARROW_BEING_HELD, false);
        incrementMovement(scrollbar, e.TIMESTAMP, moveUp);
    }

    @Reflection.DoNotReadMethod
    public void incrementMovement(Component scrollbar, long timestamp, boolean moveUp) {
        var thumbLocInScrollableRangeFromData =
                defaultIfNull(getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE), 0f);
        ProviderAtTime<Float> thumbMoveAmountProvider =
                getFromData(scrollbar, THUMB_MOVE_AMOUNT_PROVIDER);
        var thumbMoveAmount = thumbMoveAmountProvider.provide(timestamp);
        if (moveUp) {
            if (thumbLocInScrollableRangeFromData <= 0f) {
                return;
            }
            thumbMoveAmount *= -1f;
        }
        else if (thumbLocInScrollableRangeFromData >= 1f) {
            return;
        }

        var newThumbMoveProgressProvider = makeNewMoveProgressProvider(scrollbar, timestamp);

        ProviderAtTime<Float> thumbMoveProgressProvider =
                getFromData(scrollbar, THUMB_MOVE_PROGRESS_PROVIDER);
        float newOrigin;
        if (thumbMoveProgressProvider != null) {
            // Set updated target
            newOrigin = getFromData(scrollbar, THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE);
        }
        else {
            // make new target
            newOrigin = thumbLocInScrollableRangeFromData;
        }
        var newTarget = constrain(
                newOrigin + thumbMoveAmount,
                0f,
                1f
        );

        scrollbar.data().putAll(mapOf(
                THUMB_MOVE_PROGRESS_PROVIDER,
                newThumbMoveProgressProvider,
                THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE,
                thumbLocInScrollableRangeFromData,
                THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE,
                newTarget
        ));
    }

    private ProviderAtTime<Float> makeNewMoveProgressProvider(Component scrollbar,
                                                              long timestamp) {
        var newThumbMovementProviderDef = finiteSinusoidMoving(
                pairOf(0, 0f),
                pairOf(getFromData(scrollbar, THUMB_INCREMENT_MOVE_DUR), 1f)
        );
        return PROVIDER_DEF_READER.read(newThumbMovementProviderDef, timestamp);
    }

    public int timeTilNextThumbMovementWhileArrowHeld(Component scrollbar,
                                                      int timesRepeatedThusFar) {
        int firstTimeBetween = getFromData(scrollbar, ARROW_HOLD_START_THRESHOLD);
        if (timesRepeatedThusFar == 0) {
            return firstTimeBetween;
        }
        int minTimeBetween = getFromData(scrollbar, MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD);
        float exponent = getFromData(scrollbar, ARROW_HELD_REPEATED_TIME_EXPONENT);
        float exponentFactor = getFromData(scrollbar, ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR);
        var calculatedTimeBetween =
                firstTimeBetween - (exponentFactor * Math.pow(exponent, timesRepeatedThusFar));
        return (int) Math.max(calculatedTimeBetween, minTimeBetween);
    }
}
