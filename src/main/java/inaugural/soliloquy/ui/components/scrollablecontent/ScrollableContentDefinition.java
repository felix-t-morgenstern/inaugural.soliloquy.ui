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
    public final AbstractContentSpanDefinition SPAN;
    public final ProviderAtTime<Float> SCROLLABLE_WINDOW_LENGTH_PROVIDER;
    public final AbstractProviderDefinition<Float> SCROLLABLE_WINDOW_LENGTH_PROVIDER_DEF;
    public final ScrollbarDefinition SCROLLBAR;

    public float scrollbarPadding;

    public boolean hideScrollbarWhenContentFits;

    private ScrollableContentDefinition(
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        super(z, uuid);
        SPAN = Check.ifNull(span, "span");
        SCROLLABLE_WINDOW_LENGTH_PROVIDER = scrollableWindowLengthProvider;
        SCROLLABLE_WINDOW_LENGTH_PROVIDER_DEF = scrollableWindowLengthProviderDef;
        SCROLLBAR = Check.ifNull(scrollbar, "scrollbar");
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(span, scrollableWindowLengthProvider, null,
                scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(span, null, scrollableWindowLengthProviderDef,
                scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar,
            int z,
            UUID uuid
    ) {
        return new ScrollableContentDefinition(span, null, staticVal(scrollableWindowLength),
                scrollbar, z, uuid);
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar,
            int z
    ) {
        return scrollableContent(span, staticVal(scrollableWindowLength), scrollbar, z,
                randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            ProviderAtTime<Float> scrollableWindowLengthProvider,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(span, scrollableWindowLengthProvider, null,
                scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            AbstractProviderDefinition<Float> scrollableWindowLengthProviderDef,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(span, null, scrollableWindowLengthProviderDef,
                scrollbar, 0, randomUUID());
    }

    public static ScrollableContentDefinition scrollableContent(
            AbstractContentSpanDefinition span,
            float scrollableWindowLength,
            ScrollbarDefinition scrollbar
    ) {
        return new ScrollableContentDefinition(span, null, staticVal(scrollableWindowLength),
                scrollbar, 0, randomUUID());
    }

    public ScrollableContentDefinition withScrollbarPadding(float scrollbarPadding) {
        this.scrollbarPadding = scrollbarPadding;

        return this;
    }

    public ScrollableContentDefinition hidesScrollbarWhenContentFits() {
        this.hideScrollbarWhenContentFits = true;

        return this;
    }
}
