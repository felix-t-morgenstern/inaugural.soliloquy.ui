package inaugural.soliloquy.ui.components.scrollbar.vertical;

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
import java.util.function.*;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.*;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.exception.CheckedExceptionWrapper.sleep;
import static inaugural.soliloquy.ui.Constants.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.EventInputs.eventInputs;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class ScrollbarVerticalMethods {
    final static String TRACK_UNADJ_DIMENS_PROVIDER = "TRACK_UNADJ_DIMENS_PROVIDER";
    final static String TRACK_ADJ_DIMENS = "TRACK_ADJ_DIMENS";
    final static String TOP_ARROW_UUID = "TOP_ARROW_UUID";
    final static String TOP_ARROW_ORIGIN = "TOP_ARROW_ORIGIN";
    final static String BOTTOM_ARROW_UUID = "BOTTOM_ARROW_UUID";
    final static String BOTTOM_ARROW_ORIGIN = "BOTTOM_ARROW_ORIGIN";

    public final static String THUMB_LOC_IN_SCROLLABLE_RANGE = "THUMB_LOC_IN_SCROLLABLE_RANGE";

    final static String LAST_TIMESTAMP_THUMB_ORIGIN = "LAST_TIMESTAMP_THUMB_ORIGIN";
    final static String THUMB_ORIGIN = "THUMB_ORIGIN";
    final static String THUMB_UUID = "THUMB_UUID";
    final static String THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE =
            "THUMB_MOVE_ORIGIN_IN_SCROLLABLE_RANGE";
    final static String THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE =
            "THUMB_TARGET_LOC_IN_SCROLLABLE_RANGE";
    final static String THUMB_MOVE_AMOUNT_PROVIDER = "THUMB_MOVE_AMOUNT_PROVIDER";
    final static String THUMB_IS_PRESSED = "THUMB_IS_PRESSED";
    final static String THUMB_INCREMENT_MOVE_DUR = "THUMB_INCREMENT_MOVE_DUR";
    final static String THUMB_MOVE_PROGRESS_PROVIDER = "THUMB_MOVE_PROGRESS_PROVIDER";

    final static String ARROW_BEING_HELD = "ARROW_BEING_HELD";
    final static String ARROW_HOLD_START_THRESHOLD = "ARROW_HOLD_START_THRESHOLD";
    final static String MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD =
            "MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD";
    final static String ARROW_HELD_REPEATED_TIME_EXPONENT = "ARROW_HELD_REPEATED_TIME_EXPONENT";
    final static String ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR =
            "ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR";

    private final Function<UUID, Component> GET_COMPONENT;
    private final BiFunction<Component, Long, FloatBox> GET_BUTTON_UNADJ_DIMENS;
    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final Supplier<Vertex> GET_MOST_RECENT_MOUSE_LOC;
    private final TriConsumer<Integer, Mouse.EventType, Runnable> SUBSCRIBE_TO_NEXT_MOUSE_EVENT;
    private final Consumer<EventInputs> PRESS_BUTTON;
    private final GlobalClock CLOCK;

    public ScrollbarVerticalMethods(Function<UUID, Component> getComponent,
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

    public final static String ScrollbarVertical_getDimens = "ScrollbarVertical_getDimens";

    public FloatBox ScrollbarVertical_getDimens(Component scrollbar, long timestamp) {
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

        UUID topArrowUuid = getFromData(scrollbar, TOP_ARROW_UUID);
        // If there are arrows, the math gets more annoying
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
                    vertexOf(topArrowAdjLeftX, topArrowAdjTopY),
                    BOTTOM_ARROW_ORIGIN,
                    vertexOf(bottomArrowAdjLeftX, bottomArrowAdjTopY)
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

    public final static String ScrollbarVertical_provideAdjTrackDimens =
            "ScrollbarVertical_provideAdjTrackDimens";

    public FloatBox ScrollbarVertical_provideAdjTrackDimens(FunctionalProvider.Inputs inputs) {
        var scrollbar = getScrollbar(inputs);
        ScrollbarVertical_getDimens(scrollbar, inputs.timestamp());
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

    private <T> T getInComponentData(FunctionalProvider.Inputs inputs, String key) {
        var scrollbar = getScrollbar(inputs);

        return getFromData(scrollbar, key);
    }

    public final static String ScrollbarVertical_thumbOrigin = "ScrollbarVertical_thumbOrigin";

    public Vertex ScrollbarVertical_thumbOrigin(FunctionalProvider.Inputs inputs) {
        var scrollbar = getScrollbar(inputs);

        Long lastTimestampThumbDimens = getFromData(scrollbar, LAST_TIMESTAMP_THUMB_ORIGIN);
        if (lastTimestampThumbDimens != null && lastTimestampThumbDimens == inputs.timestamp()) {
            return getFromData(scrollbar, THUMB_ORIGIN);
        }

        var thumb = GET_COMPONENT.apply(getFromData(scrollbar, THUMB_UUID));
        var thumbUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(thumb, inputs.timestamp());
        // setDimens is the prerender hook, which is called prior to the rendering of contents
        FloatBox trackAdjDimens = getFromData(scrollbar, TRACK_ADJ_DIMENS);
        var thumbAdjTopYMin = trackAdjDimens.TOP_Y;
        var thumbAdjTopYMax = trackAdjDimens.BOTTOM_Y - thumbUnadjDimens.height();
        var scrollableLengthOnScreen = thumbAdjTopYMax - thumbAdjTopYMin;

        float thumbLocInScrollableRange;
        ProviderAtTime<Float> thumbMoveProgressProvider =
                getFromData(scrollbar, THUMB_MOVE_PROGRESS_PROVIDER);

        if (falseIfNull(getFromData(scrollbar, THUMB_IS_PRESSED))) {
            var mostRecentMouseLoc = GET_MOST_RECENT_MOUSE_LOC.get();
            var thumbHalfHeight = thumbUnadjDimens.height() / 2f;
            var scrollableRangeOnScreenTopY = trackAdjDimens.TOP_Y + thumbHalfHeight;
            var scrollableRangeOnScreenBottomY = trackAdjDimens.BOTTOM_Y - thumbHalfHeight;
            var mouseLocWithinScrollableRange = constrain(
                    mostRecentMouseLoc.Y,
                    scrollableRangeOnScreenTopY,
                    scrollableRangeOnScreenBottomY
            );
            // "in scrollable range" means we get the "percent" scrolled, effectively
            thumbLocInScrollableRange =
                    (mouseLocWithinScrollableRange - scrollableRangeOnScreenTopY) /
                            scrollableLengthOnScreen;
            scrollbar.data().put(THUMB_LOC_IN_SCROLLABLE_RANGE, thumbLocInScrollableRange);
        }
        else if (thumbMoveProgressProvider != null) {
            @SuppressWarnings("unchecked") Map<Long, Float> thumbMoveProviderVals =
                    (Map<Long, Float>) thumbMoveProgressProvider.representation();
            @SuppressWarnings("OptionalGetWithoutIsPresent") long thumbMoveProviderLastTimestamp =
                    thumbMoveProviderVals.keySet().stream().max(Comparator.naturalOrder()).get();

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

        var trackAdjCenterX = (trackAdjDimens.LEFT_X + trackAdjDimens.RIGHT_X) / 2f;
        var thumbAdjLeftX = trackAdjCenterX - (thumbUnadjDimens.width() / 2f);

        var thumbAdjTopY =
                thumbAdjTopYMin + (scrollableLengthOnScreen * thumbLocInScrollableRange);

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

    public final static String ScrollbarVertical_trackPress = "ScrollbarVertical_trackPress";

    public void ScrollbarVertical_trackPress(EventInputs e) {
        var track = (RectangleRenderable) e.renderable;
        var trackDimens = track.getRenderingDimensionsProvider().provide(e.TIMESTAMP);

        var scrollbar = track.getContainingComponent();
        Component thumb = GET_COMPONENT.apply(getFromData(scrollbar, THUMB_UUID));
        var thumbUnadjDimens = GET_BUTTON_UNADJ_DIMENS.apply(thumb, e.TIMESTAMP);

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
        scrollbar.data().remove(THUMB_MOVE_PROGRESS_PROVIDER);

        var thumbPressEvent = eventInputs(e.TIMESTAMP)
                .withMouseEvent(e.mouseButton, e.mouseEvent, e.mouseLoc, null, thumb);
        PRESS_BUTTON.accept(thumbPressEvent);
    }

    public final static String ScrollbarVertical_thumbPress = "ScrollbarVertical_thumbPress";

    public void ScrollbarVertical_thumbPress(EventInputs e) {
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

    public final static String ScrollbarVertical_topArrowPress = "ScrollbarVertical_topArrowPress";

    public void ScrollbarVertical_topArrowPress(EventInputs e) {
        pressArrow(e, true);
    }

    public final static String ScrollbarVertical_bottomArrowPress = "ScrollbarVertical_bottomArrowPress";

    public void ScrollbarVertical_bottomArrowPress(EventInputs e) {
        pressArrow(e, false);
    }

    private void pressArrow(EventInputs e, boolean moveUp) {
        System.out.println(">> PRESS, moveUp = " + moveUp);
        var scrollbar = e.component.getContainingComponent();
        System.out.println("scrollbar data...");
        System.out.println(scrollbar.data());
        scrollbar.data().put(ARROW_BEING_HELD, true);

        final int startThreshold = getFromData(scrollbar, ARROW_HOLD_START_THRESHOLD);
        System.out.println("startThreshold = " + startThreshold);

        new Thread(() -> {
            var timesRepeated = 0;

            sleep(startThreshold);
            while (falseIfNull(getFromData(scrollbar, ARROW_BEING_HELD))) {
                System.out.println("[STILL HELD] initiating move");
                var timestamp = CLOCK.globalTimestamp();

                incrementMovement(scrollbar, timestamp, moveUp);

                var timeUntilNextIncrement =
                        timeTilNextThumbMovementWhileArrowHeld(scrollbar, timesRepeated++);
                System.out.println("timeUntilNextIncrement = " + timeUntilNextIncrement);

                sleep(timeUntilNextIncrement);
            }
        }).start();
    }

    public final static String ScrollbarVertical_topArrowReleaseAfterPress =
            "ScrollbarVertical_topArrowReleaseAfterPress";

    public void ScrollbarVertical_topArrowReleaseAfterPress(EventInputs e) {
        releaseArrowAndIncrementMovement(e, true);
    }

    public final static String ScrollbarVertical_bottomArrowReleaseAfterPress =
            "ScrollbarVertical_bottomArrowReleaseAfterPress";

    public void ScrollbarVertical_bottomArrowReleaseAfterPress(EventInputs e) {
        releaseArrowAndIncrementMovement(e, false);
    }

    public void releaseArrowAndIncrementMovement(EventInputs e, boolean moveUp) {
        System.out.println("::: in releaseArrowAndIncrementMovement");
        // The Renderable is a Rectangle within the Button, *within* the Scrollbar
        var scrollbar = e.renderable.getContainingComponent().getContainingComponent();
        System.out.println("scrollbar data = " + scrollbar.data());
        scrollbar.data().put(ARROW_BEING_HELD, false);
        System.out.println("scrollbar data AFTER change = " + scrollbar.data());
        incrementMovement(scrollbar, e.TIMESTAMP, moveUp);
    }

    @Reflection.DoNotReadMethod
    public void incrementMovement(Component scrollbar, long timestamp, boolean moveUp) {
        System.out.println("...in incrementMovement");
        System.out.println("moveUp = " + moveUp);
        var thumbLocInScrollableRangeFromData =
                defaultIfNull(getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE), 0f);
        System.out.println("thumbLocInScrollableRangeFromData = " + thumbLocInScrollableRangeFromData);
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
        System.out.println("thumb is not at edge of range!");

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
                firstTimeBetween - (exponentFactor * Math.pow(2f, timesRepeatedThusFar));
        return (int) Math.max(calculatedTimeBetween, minTimeBetween);
    }
}
