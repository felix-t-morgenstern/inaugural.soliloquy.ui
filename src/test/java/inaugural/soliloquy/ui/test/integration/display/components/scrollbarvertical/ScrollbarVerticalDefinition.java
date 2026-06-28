package inaugural.soliloquy.ui.test.integration.display.components.scrollbarvertical;

import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.util.UUID;

import static java.util.UUID.randomUUID;

public class ScrollbarVerticalDefinition extends AbstractContentDefinition {
    public final RectangleRenderableDefinition BAR_DEF;
    public final ButtonDefinition HANDLE_DEF;

    public ButtonDefinition anchorTopDef;
    public ButtonDefinition anchorBottomDef;

    private ScrollbarVerticalDefinition(RectangleRenderableDefinition barDef,
                                        ButtonDefinition handleDef,
                                        int z,
                                        UUID uuid) {
        super(z, uuid);
        BAR_DEF = barDef;
        HANDLE_DEF = handleDef;
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(RectangleRenderableDefinition bar,
                                                                ButtonDefinition handle,
                                                                int z,
                                                                UUID uuid) {
        return new ScrollbarVerticalDefinition(bar, handle, z, uuid);
    }

    public static ScrollbarVerticalDefinition scrollbarVertical(RectangleRenderableDefinition bar,
                                                                ButtonDefinition handle) {
        return new ScrollbarVerticalDefinition(bar, handle, 0, randomUUID());
    }

    public ScrollbarVerticalDefinition withAnchors(ButtonDefinition anchorTopDef,
                                                   ButtonDefinition anchorBottomDef) {
        this.anchorTopDef = anchorTopDef;
        this.anchorBottomDef = anchorBottomDef;

        return this;
    }
}
