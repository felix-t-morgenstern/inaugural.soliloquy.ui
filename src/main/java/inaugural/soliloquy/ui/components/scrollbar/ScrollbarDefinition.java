package inaugural.soliloquy.ui.components.scrollbar;

import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.UUID;

public class ScrollbarDefinition extends AbstractContentDefinition {
    public final ProviderAtTime<Vertex> BAR_RENDERING_LOC;
    public final AbstractProviderDefinition<Vertex> BAR_RENDERING_LOC_DEF;
    public final FloatBox BAR_DIMENS;
    public final float HANDLE_THICKNESS;

    public Boolean barAnchorsAreInactive;
    public Boolean barAnchorsJumpToEnd;
    public Float minHandleLength;

    public SpriteRenderableDefinition barTopAnchorDefault;
    public RectangleRenderableDefinition barDefault;
    public SpriteRenderableDefinition barBottomAnchorDefault;

    public SpriteRenderableDefinition handleTopAnchorDefault;
    public SpriteRenderableDefinition handleSpriteDefault;
    public RectangleRenderableDefinition handleDefault;
    public SpriteRenderableDefinition handleBottomAnchorDefault;

    public SpriteRenderableDefinition barTopAnchorHover;
    public RectangleRenderableDefinition barHover;
    public SpriteRenderableDefinition barBottomAnchorHover;

    public SpriteRenderableDefinition handleTopAnchorHover;
    public SpriteRenderableDefinition handleSpriteHover;
    public RectangleRenderableDefinition handleHover;
    public SpriteRenderableDefinition handleBottomAnchorHover;

    public SpriteRenderableDefinition barTopAnchorPressed;
    public RectangleRenderableDefinition barPressed;
    public SpriteRenderableDefinition barBottomAnchorPressed;

    public SpriteRenderableDefinition handleTopAnchorPressed;
    public SpriteRenderableDefinition handleSpritePressed;
    public RectangleRenderableDefinition handlePressed;
    public SpriteRenderableDefinition handleBottomAnchorPressed;

    private ScrollbarDefinition(ProviderAtTime<Vertex> barRenderingLoc,
                                AbstractProviderDefinition<Vertex> barRenderingLocDef,
                                FloatBox barDimens,
                                float handleThickness,
                                RectangleRenderableDefinition barDefault,
                                RectangleRenderableDefinition handleDefault,
                                SpriteRenderableDefinition handleSpriteDefault,
                                int z,
                                UUID uuid) {
        super(z, uuid);
        BAR_RENDERING_LOC = barRenderingLoc;
        BAR_RENDERING_LOC_DEF = barRenderingLocDef;
        BAR_DIMENS = barDimens;
        HANDLE_THICKNESS = handleThickness;
        this.barDefault = barDefault;
        this.handleDefault = handleDefault;
        this.handleSpriteDefault = handleSpriteDefault;
    }

    public static ScrollbarDefinition scrollbar(
            ProviderAtTime<Vertex> barRenderingLoc,
            FloatBox barDimens,
            float handleThickness,
            RectangleRenderableDefinition barDefault,
            RectangleRenderableDefinition handleDefault,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(barRenderingLoc, null, barDimens, handleThickness,
                barDefault, handleDefault, null, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            ProviderAtTime<Vertex> barRenderingLoc,
            FloatBox barDimens,
            float handleThickness,
            RectangleRenderableDefinition barDefault,
            SpriteRenderableDefinition handleSpriteDefault,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(barRenderingLoc, null, barDimens, handleThickness,
                barDefault, null, handleSpriteDefault, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            AbstractProviderDefinition<Vertex> barRenderingLocDef,
            FloatBox barDimens,
            float handleThickness,
            RectangleRenderableDefinition barDefault,
            RectangleRenderableDefinition handleDefault,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(null, barRenderingLocDef, barDimens,
                handleThickness, barDefault, handleDefault, null, z, uuid);
    }

    public static ScrollbarDefinition scrollbar(
            AbstractProviderDefinition<Vertex> barRenderingLocDef,
            FloatBox barDimens,
            float handleThickness,
            RectangleRenderableDefinition barDefault,
            SpriteRenderableDefinition handleSpriteDefault,
            int z,
            UUID uuid
    ) {
        return new ScrollbarDefinition(null, barRenderingLocDef, barDimens,
                handleThickness, barDefault, null, handleSpriteDefault, z, uuid);
    }

    public ScrollbarDefinition withBarAnchorsDefault(
            SpriteRenderableDefinition barTopAnchorDefault,
            SpriteRenderableDefinition barBottomAnchorDefault
    ) {
        this.barTopAnchorDefault = barTopAnchorDefault;
        this.barBottomAnchorDefault = barBottomAnchorDefault;

        return this;
    }

    public ScrollbarDefinition withHandleSpriteDefault(
            SpriteRenderableDefinition handleSpriteDefault
    ) {
        this.handleSpriteDefault = handleSpriteDefault;

        return this;
    }

    public ScrollbarDefinition withHandleAnchorsDefault(
            SpriteRenderableDefinition handleTopAnchorDefault,
            SpriteRenderableDefinition handleBottomAnchorDefault
    ) {
        this.handleTopAnchorDefault = handleTopAnchorDefault;
        this.handleBottomAnchorDefault = handleBottomAnchorDefault;

        return this;
    }

    public ScrollbarDefinition withBarAnchorsHover(
            SpriteRenderableDefinition barTopAnchorHover,
            SpriteRenderableDefinition barBottomAnchorHover
    ) {
        this.barTopAnchorHover = barTopAnchorHover;
        this.barBottomAnchorHover = barBottomAnchorHover;

        return this;
    }

    public ScrollbarDefinition withHandleSpriteHover(
            SpriteRenderableDefinition handleSpriteHover
    ) {
        this.handleSpriteHover = handleSpriteHover;

        return this;
    }

    public ScrollbarDefinition withHandleAnchorsHover(
            SpriteRenderableDefinition handleTopAnchorHover,
            SpriteRenderableDefinition handleBottomAnchorHover
    ) {
        this.handleTopAnchorHover = handleTopAnchorHover;
        this.handleBottomAnchorHover = handleBottomAnchorHover;

        return this;
    }

    public ScrollbarDefinition withBarAnchorsPressed(
            SpriteRenderableDefinition barTopAnchorPressed,
            SpriteRenderableDefinition barBottomAnchorPressed
    ) {
        this.barTopAnchorPressed = barTopAnchorPressed;
        this.barBottomAnchorPressed = barBottomAnchorPressed;

        return this;
    }

    public ScrollbarDefinition withHandleSpritePressed(
            SpriteRenderableDefinition handleSpritePressed
    ) {
        this.handleSpritePressed = handleSpritePressed;

        return this;
    }

    public ScrollbarDefinition withHandleAnchorsPressed(
            SpriteRenderableDefinition handleTopAnchorPressed,
            SpriteRenderableDefinition handleBottomAnchorPressed
    ) {
        this.handleTopAnchorPressed = handleTopAnchorPressed;
        this.handleBottomAnchorPressed = handleBottomAnchorPressed;

        return this;
    }

    /**
     * Normally, bar anchors either take a step or jump to the top or bottom of the scroll; with
     * this method, they instead do nothing.
     */
    public ScrollbarDefinition barAnchorsAreInactive() {
        barAnchorsAreInactive = true;

        return this;
    }

    /**
     * Normally, bar anchors take a step; this method sets the scrollbar so its anchor buttons jump
     * to the top or bottom of the scroll.
     */
    public ScrollbarDefinition barAnchorsJumpToEnd() {
        barAnchorsJumpToEnd = true;

        return this;
    }

    /**
     * (This may not be necessary if you have handle anchors sufficiently large to be clicked on
     * easily by the user)
     */
    public ScrollbarDefinition withMinimumHandleLength(float minHandleLength) {
        this.minHandleLength = minHandleLength;

        return this;
    }
}
