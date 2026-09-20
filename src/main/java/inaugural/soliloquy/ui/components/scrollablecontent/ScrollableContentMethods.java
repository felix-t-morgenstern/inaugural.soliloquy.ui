package inaugural.soliloquy.ui.components.scrollablecontent;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.Orientation;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.EventInputs;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.Orientation.VERTICAL;
import static inaugural.soliloquy.ui.components.scrollbar.ScrollbarMethods.THUMB_LOC_IN_SCROLLABLE_RANGE;
import static org.lwjgl.glfw.GLFW.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class ScrollableContentMethods {
    final static String SCROLLBAR_UUID = "SCROLLBAR_UUID";
    final static String SPAN_UUID = "SPAN_UUID";
    final static String SPAN_LENGTH = "SPAN_LENGTH";
    final static String SCROLLABLE_WINDOW_LENGTH_PROVIDER = "SCROLLABLE_WINDOW_LENGTH_PROVIDER";
    final static String SCROLLBAR_PADDING = "SCROLLBAR_PADDING";
    final static String HIDES_SCROLLBAR_WHEN_CONTENT_FITS = "HIDES_SCROLLBAR_WHEN_CONTENT_FITS";

    private final Function<UUID, Component> GET_COMPONENT;
    private final TriConsumer<Component, Long, Boolean> INCREMENT_MOVEMENT;

    public ScrollableContentMethods(Function<UUID, Component> getComponent,
                                    TriConsumer<Component, Long, Boolean> incrementMovement) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        INCREMENT_MOVEMENT = Check.ifNull(incrementMovement, "incrementMovement");
    }

    public static String ScrollableContent_spanOrigin = "ScrollableContent_spanOrigin";

    public Vertex ScrollableContent_spanOrigin(FunctionalProvider.Inputs inputs) {
        var scrollableContent = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        ProviderAtTime<Vertex> scrollableContentOriginProvider =
                getFromData(scrollableContent, COMPONENT_ORIGIN_PROVIDER);
        var scrollableContentOrigin = scrollableContentOriginProvider.provide(inputs.timestamp());

        var scrollbar = GET_COMPONENT.apply(getFromData(scrollableContent, SCROLLBAR_UUID));
        float thumbLocInScrollableRange = getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE);

        if (thumbLocInScrollableRange == 0f) {
            return scrollableContentOrigin;
        }

        var span = GET_COMPONENT.apply(getFromData(scrollableContent, SPAN_UUID));
        var spanUnadjDimensProvider = span.unadjustedDimensionsProvider();
        var spanUnadjDimens = spanUnadjDimensProvider.provide(inputs.timestamp());
        ProviderAtTime<Float> scrollableWindowLengthProvider =
                getFromData(scrollableContent, SCROLLABLE_WINDOW_LENGTH_PROVIDER);
        var scrollableWindowLength = scrollableWindowLengthProvider.provide(inputs.timestamp());
        Orientation orientation = getFromData(scrollableContent, ORIENTATION);
        if (orientation == VERTICAL) {
            if (spanUnadjDimens.height() <= scrollableWindowLength) {
                return scrollableContentOrigin;
            }
            else {
                var maxScrollingDisplacement = spanUnadjDimens.height() - scrollableWindowLength;
                var scrollingDisplacement = maxScrollingDisplacement * thumbLocInScrollableRange;
                return vertexOf(
                        scrollableContentOrigin.X,
                        scrollableContentOrigin.Y - scrollingDisplacement
                );
            }
        }
        else {
            if (spanUnadjDimens.width() <= scrollableWindowLength) {
                return scrollableContentOrigin;
            }
            else {
                var maxScrollingDisplacement = spanUnadjDimens.width() - scrollableWindowLength;
                var scrollingDisplacement = maxScrollingDisplacement * thumbLocInScrollableRange;
                return vertexOf(
                        scrollableContentOrigin.X - scrollingDisplacement,
                        scrollableContentOrigin.Y
                );
            }
        }
    }

    public static String ScrollableContent_spanRenderingDimens =
            "ScrollableContent_spanRenderingDimens";

    public FloatBox ScrollableContent_spanRenderingDimens(FunctionalProvider.Inputs inputs) {
        var scrollableContent = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var span = GET_COMPONENT.apply(getFromData(scrollableContent, SPAN_UUID));
        ProviderAtTime<Float> scrollableWindowLengthProvider =
                getFromData(scrollableContent, SCROLLABLE_WINDOW_LENGTH_PROVIDER);
        var scrollableWindowLength = scrollableWindowLengthProvider.provide(inputs.timestamp());
        ProviderAtTime<Vertex> scrollableContentOriginProvider =
                getFromData(scrollableContent, COMPONENT_ORIGIN_PROVIDER);
        var spanOrigin = scrollableContentOriginProvider.provide(inputs.timestamp());
        Orientation orientation = getFromData(scrollableContent, ORIENTATION);
        if (orientation == VERTICAL) {
            float spanLength = getFromData(span, COMPONENT_WIDTH);
            return floatBoxOf(
                    spanOrigin,
                    spanLength,
                    scrollableWindowLength
            );
        }
        else {
            float spanLength = getFromData(span, COMPONENT_HEIGHT);
            return floatBoxOf(
                    spanOrigin,
                    scrollableWindowLength,
                    spanLength
            );
        }
    }

    public final static String ScrollableContent_scrollbarOrigin =
            "ScrollableContent_scrollbarOrigin";

    public Vertex ScrollableContent_scrollbarOrigin(FunctionalProvider.Inputs inputs) {
        var scrollableContent = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        ProviderAtTime<Vertex> componentOriginProvider =
                getFromData(scrollableContent, COMPONENT_ORIGIN_PROVIDER);
        var componentOrigin = componentOriginProvider.provide(inputs.timestamp());
        Orientation orientation = getFromData(scrollableContent, ORIENTATION);
        var span = GET_COMPONENT.apply(getFromData(scrollableContent, SPAN_UUID));
        float scrollbarPadding = getFromData(scrollableContent, SCROLLBAR_PADDING);

        if (falseIfNull(getFromData(scrollableContent, HIDES_SCROLLBAR_WHEN_CONTENT_FITS))) {
            ProviderAtTime<Float> scrollableWindowLengthProvider =
                    getFromData(scrollableContent, SCROLLABLE_WINDOW_LENGTH_PROVIDER);
            var scrollableWindowLength = scrollableWindowLengthProvider.provide(inputs.timestamp());
            var spanUnadjDimensProvider = span.unadjustedDimensionsProvider();
            var spanUnadjDimens = spanUnadjDimensProvider.provide(inputs.timestamp());
            float spanContentLength;
            if (orientation == VERTICAL) {
                spanContentLength = spanUnadjDimens.height();
            }
            else {
                spanContentLength = spanUnadjDimens.width();
            }
            if (spanContentLength <= scrollableWindowLength) {
                return HIDDEN;
            }
        }

        if (orientation == VERTICAL) {
            float spanLength = getFromData(span, COMPONENT_WIDTH);
            var scrollbarOffset = spanLength + scrollbarPadding;
            return translateVertex(
                    componentOrigin,
                    scrollbarOffset,
                    0f
            );
        }
        else {
            float spanLength = getFromData(span, COMPONENT_HEIGHT);
            var scrollbarOffset = spanLength + scrollbarPadding;
            return translateVertex(
                    componentOrigin,
                    0f,
                    scrollbarOffset
            );
        }
    }

    public final static String ScrollableContent_getUnadjDimens =
            "ScrollableContent_getUnadjDimens";

    public FloatBox ScrollableContent_getUnadjDimens(FunctionalProvider.Inputs inputs) {
        var scrollableContent = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var span = GET_COMPONENT.apply(getFromData(scrollableContent, SPAN_UUID));
        var scrollbar = GET_COMPONENT.apply(getFromData(scrollableContent, SCROLLBAR_UUID));

        var scrollbarUnadjDimens =
                scrollbar.unadjustedDimensionsProvider().provide(inputs.timestamp());
        var spanUnadjDimens = span.unadjustedDimensionsProvider().provide(inputs.timestamp());

        Orientation orientation = getFromData(scrollableContent, ORIENTATION);
        float scrollbarPadding = getFromData(scrollableContent, SCROLLBAR_PADDING);

        if (orientation == VERTICAL) {
            return floatBoxOf(
                    spanUnadjDimens.width() + scrollbarPadding + scrollbarUnadjDimens.width(),
                    Math.max(spanUnadjDimens.height(), scrollbarUnadjDimens.height())
            );
        }
        else {
            return floatBoxOf(
                    Math.max(spanUnadjDimens.width(), scrollbarUnadjDimens.width()),
                    spanUnadjDimens.height() + scrollbarPadding + scrollbarUnadjDimens.height()
            );
        }
    }

    public final static String ScrollableContent_encompassingDimens =
            "ScrollableContent_encompassingDimens";

    public FloatBox ScrollableContent_encompassingDimens(FunctionalProvider.Inputs inputs) {
        var span = GET_COMPONENT.apply(getFromData(inputs, SPAN_UUID));
        var scrollbar = GET_COMPONENT.apply(getFromData(inputs, SCROLLBAR_UUID));

        var scrollbarDimens = scrollbar.dimensionsProvider().provide(inputs.timestamp());
        var spanDimens = span.dimensionsProvider().provide(inputs.timestamp());

        return encompassing(scrollbarDimens, spanDimens);
    }

    public final static String ScrollableContent_pressArrowKey = "ScrollableContent_pressArrowKey";

    public void ScrollableContent_pressArrowKey(EventInputs e) {
        var scrollableContent = e.component;
        var orientation = getFromData(scrollableContent, ORIENTATION);
        var scrollbar = GET_COMPONENT.apply(getFromData(scrollableContent, SCROLLBAR_UUID));
        if (orientation == VERTICAL) {
            if (e.keyCodepoint == GLFW_KEY_UP) {
                INCREMENT_MOVEMENT.accept(scrollbar, e.TIMESTAMP, true);
            }
            else if (e.keyCodepoint == GLFW_KEY_DOWN) {
                INCREMENT_MOVEMENT.accept(scrollbar, e.TIMESTAMP, false);
            }
        }
        else {
            if (e.keyCodepoint == GLFW_KEY_LEFT) {
                INCREMENT_MOVEMENT.accept(scrollbar, e.TIMESTAMP, true);
            }
            else if (e.keyCodepoint == GLFW_KEY_RIGHT) {
                INCREMENT_MOVEMENT.accept(scrollbar, e.TIMESTAMP, false);
            }
        }
    }
}
