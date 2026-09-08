package inaugural.soliloquy.ui.components.scrollablecontent;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.content.AbstractContentSpanDefinition;
import inaugural.soliloquy.ui.components.scrollbar.ScrollbarDefinition;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollableContentDefinition extends AbstractContentDefinition {
    public final ProviderAtTime<Vertex> ORIGIN_PROVIDER;
    public final AbstractProviderDefinition<Vertex> ORIGIN_PROVIDER_DEF;
    public final AbstractContentSpanDefinition SPAN;
    public final ProviderAtTime<Float> SCROLLABLE_WINDOW_LENGTH_PROVIDER;
    public final AbstractProviderDefinition<Float> SCROLLABLE_WINDOW_LENGTH_PROVIDER_DEF;
    public final ScrollbarDefinition SCROLLBAR;

    public float scrollbarPadding;

    private ScrollableContentDefinition(
            ProviderAtTime<Vertex> originProvider,
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        super(z, uuid);
        ORIGIN_PROVIDER = originProvider;
        ORIGIN_PROVIDER_DEF = originProviderDef;
        SPAN = Check.ifNull(span, "span");
        SCROLLABLE_WINDOW_LENGTH_PROVIDER = scrollableWindowLengthProvider;
        SCROLLABLE_WINDOW_LENGTH_PROVIDER_DEF = scrollableWindowLengthProviderDef;
        SCROLLBAR = Check.ifNull(scrollbar, "scrollbar");
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(originProvider, null, span,
                scrollableWindowLengthProvider, null, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span,
                scrollableWindowLengthProvider, null, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span,
                scrollableWindowLengthProvider, null, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(originProvider, null, span, null,
                scrollableWindowLengthProviderDef, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span, null,
                scrollableWindowLengthProviderDef, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span, null,
                scrollableWindowLengthProviderDef, scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(originProvider, null, span, null,
                staticVal(scrollableWindowLength), scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span, null,
                staticVal(scrollableWindowLength), scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span, null,
                staticVal(scrollableWindowLength), scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(originProvider, null, span,
                scrollableWindowLengthProvider, null, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span,
                scrollableWindowLengthProvider, null, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span,
                scrollableWindowLengthProvider, null, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(originProvider, null, span, null,
                scrollableWindowLengthProviderDef, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span, null,
                scrollableWindowLengthProviderDef, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span, null,
                scrollableWindowLengthProviderDef, scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            ProviderAtTime<Vertex> originProvider,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(originProvider, null, span, null,
                staticVal(scrollableWindowLength), scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractProviderDefinition<Vertex> originProviderDef,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, originProviderDef, span, null,
                staticVal(scrollableWindowLength), scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            Vertex origin,
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(null, staticVal(origin), span, null,
                staticVal(scrollableWindowLength), scrollbar, 0, randomUUID());
    }

    public ScrollableContentDefinition withScrollbarPadding(float scrollbarPadding) {
        this.scrollbarPadding = scrollbarPadding;

        return this;
    }
}
