package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.button.ButtonMethods;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;

import java.awt.*;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.BUTTON_RECT_DIMENS;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class BeveledButtonMethods {
    final static String BEVELED_BUTTON_LAST_TIMESTAMP = "BEVELED_BUTTON_LAST_TIMESTAMP";
    final static String BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT =
            "BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT";

    final static String BEVEL_PERCENT = "BEVEL_PERCENT";
    final static String BEVEL_COLOR_LIT = "BEVEL_COLOR_LIT";
    final static String BEVEL_COLOR_UNLIT = "BEVEL_COLOR_UNLIT";

    private final Function<UUID, Component> GET_COMPONENT;

    public BeveledButtonMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    final static String BeveledButton_provideVertex = "BeveledButton_provideVertex";
    final static String BeveledButton_xSlot = "BeveledButton_xSlot";
    final static String BeveledButton_ySlot = "BeveledButton_ySlot";

    public Vertex BeveledButton_provideVertex(FunctionalProvider.Inputs inputs) {
        var dimensAndTransforms = getRectDimensAndInnerTransformsUpperLeft(inputs);
        var rectDimens = dimensAndTransforms.FIRST;
        var transformsUpperLeft = dimensAndTransforms.SECOND;

        int xSlot = getFromData(inputs, BeveledButton_xSlot);
        int ySlot = getFromData(inputs, BeveledButton_ySlot);

        float x = getXSlotVal(rectDimens, transformsUpperLeft, xSlot);
        float y = getYSlotVal(rectDimens, transformsUpperLeft, ySlot);

        return vertexOf(x, y);
    }

    final static String BeveledButton_provideBox = "BeveledButton_provideBox";
    final static String BeveledButton_provideBox_xSlotRight = "BeveledButton_provideBox_xSlotRight";

    public FloatBox BeveledButton_provideBox(FunctionalProvider.Inputs inputs) {
        var dimensAndTransforms = getRectDimensAndInnerTransformsUpperLeft(inputs);
        var rectDimens = dimensAndTransforms.FIRST;
        var transformsUpperLeft = dimensAndTransforms.SECOND;

        int xSlotLeft = getFromData(inputs, BeveledButton_xSlot);
        int ySlotTop = getFromData(inputs, BeveledButton_ySlot);
        int xSlotRight = getFromData(inputs, BeveledButton_provideBox_xSlotRight);
        var ySlotBottom = ySlotTop + 1;

        var leftX = getXSlotVal(rectDimens, transformsUpperLeft, xSlotLeft);
        var topY = getYSlotVal(rectDimens, transformsUpperLeft, ySlotTop);
        var rightX = getXSlotVal(rectDimens, transformsUpperLeft, xSlotRight);
        var bottomY = getYSlotVal(rectDimens, transformsUpperLeft, ySlotBottom);

        return floatBoxOf(leftX, topY, rightX, bottomY);
    }

    private static float getXSlotVal(FloatBox rectDimens,
                                     Vertex transformsUpperLeft,
                                     int xSlot) {
        return switch (xSlot) {
            case 0 -> rectDimens.LEFT_X;
            case 1 -> rectDimens.LEFT_X + transformsUpperLeft.X;
            case 2 -> rectDimens.RIGHT_X - transformsUpperLeft.X;
            case 3 -> rectDimens.RIGHT_X;
            default -> throw new IllegalStateException("Illegal xSlot (" + xSlot + ")");
        };
    }

    private static float getYSlotVal(FloatBox rectDimens,
                                     Vertex transformsUpperLeft,
                                     int ySlot) {
        return switch (ySlot) {
            case 0 -> rectDimens.TOP_Y;
            case 1 -> rectDimens.TOP_Y + transformsUpperLeft.Y;
            case 2 -> rectDimens.BOTTOM_Y - transformsUpperLeft.Y;
            case 3 -> rectDimens.BOTTOM_Y;
            default -> throw new IllegalStateException("Illegal ySlot (" + ySlot + ")");
        };
    }

    private Pair<FloatBox, Vertex> getRectDimensAndInnerTransformsUpperLeft(
            FunctionalProvider.Inputs inputs
    ) {
        Vertex innerTransformsUpperLeft;

        var button = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        FloatBox rectDimens = getFromData(button, BUTTON_RECT_DIMENS);
        long lastTimestamp = getFromData(button, BEVELED_BUTTON_LAST_TIMESTAMP);
        if (lastTimestamp == inputs.timestamp()) {
            innerTransformsUpperLeft =
                    getFromData(button, BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT);
        }
        else {
            float bevelPercent = getFromData(button, BEVEL_PERCENT);
            innerTransformsUpperLeft = getInnerTransformsTopLeft(rectDimens, bevelPercent);

            button.data().put(BEVELED_BUTTON_LAST_TIMESTAMP, inputs.timestamp());
            button.data().put(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT, innerTransformsUpperLeft);
        }

        return pairOf(rectDimens, innerTransformsUpperLeft);
    }

    private static Vertex getInnerTransformsTopLeft(FloatBox rectDimens,
                                                    float bevelPercent) {
        return vertexOf(rectDimens.width() * bevelPercent, rectDimens.height() * bevelPercent);
    }

    final static String BeveledButton_provideColor = "BeveledButton_provideColor";
    final static String BeveledButton_provideColor_bevelIntensity =
            "BeveledButton_provideColor_bevelIntensity";
    final static String BeveledButton_provideColor_isLitByDefault =
            "BeveledButton_provideColor_isLitByDefault";

    public Color BeveledButton_provideColor(FunctionalProvider.Inputs inputs) {
        boolean isLitByDefault =
                getFromData(inputs, BeveledButton_provideColor_isLitByDefault);
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        boolean isPressed = falseIfNull(getFromData(component, ButtonMethods.IS_PRESSED));
        Color renderableColor;
        // If it's lit by default XOR it's pressed, return the lit color
        if (isLitByDefault != isPressed) {
            renderableColor = getFromData(component, BEVEL_COLOR_LIT);
            if (renderableColor == null) {
                float bevelIntensity = getFromData(inputs,
                        BeveledButton_provideColor_bevelIntensity);
                renderableColor = new Color(1f, 1f, 1f, bevelIntensity);
                component.data().put(BEVEL_COLOR_LIT, renderableColor);
            }
        }
        else {
            renderableColor = getFromData(component, BEVEL_COLOR_UNLIT);
            if (renderableColor == null) {
                float bevelIntensity = getFromData(inputs,
                        BeveledButton_provideColor_bevelIntensity);
                renderableColor = new Color(0f, 0f, 0f, bevelIntensity);
                component.data().put(BEVEL_COLOR_UNLIT, renderableColor);
            }
        }

        return renderableColor;
    }
}
