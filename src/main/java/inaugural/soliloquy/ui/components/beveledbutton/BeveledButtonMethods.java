package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.button.ButtonMethods;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.awt.*;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.components.ComponentMethods.COMPONENT_UUID;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class BeveledButtonMethods {
    final static String BEVEL_LAST_TIMESTAMP = "BEVEL_LAST_TIMESTAMP";
    final static String BEVEL_LAST_RECT_DIMENS = "BEVEL_LAST_RECT_DIMENS";
    final static String BEVEL_LAST_INNER_TRANSFORMS_UPPER_LEFT =
            "BEVEL_LAST_INNER_TRANSFORMS_UPPER_LEFT";

    final static String BEVEL_COLOR_LIT = "BEVEL_COLOR_LIT";
    final static String BEVEL_COLOR_UNLIT = "BEVEL_COLOR_UNLIT";

    private final Function<UUID, Component> GET_COMPONENT;

    public BeveledButtonMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    final static String BeveledButton_rectDimensProvider = "BeveledButton_rectDimensProvider";
    final static String BeveledButton_bevelPercent = "BeveledButton_bevelPercent";
    final static String BeveledButton_xSlot = "BeveledButton_xSlot";
    final static String BeveledButton_ySlot = "BeveledButton_ySlot";

    public Vertex provideVertex_BeveledButton(FunctionalProvider.Inputs inputs) {
        var dimensAndTransforms = getRectDimensAndInnerTransformsUpperLeft(inputs);
        var rectDimens = dimensAndTransforms.FIRST;
        var transformsUpperLeft = dimensAndTransforms.SECOND;

        int xSlot = getFromData(inputs, BeveledButton_xSlot);
        int ySlot = getFromData(inputs, BeveledButton_ySlot);

        float x = getXSlotVal(rectDimens, transformsUpperLeft, xSlot);
        float y = getYSlotVal(rectDimens, transformsUpperLeft, ySlot);

        return vertexOf(x, y);
    }

    final static String provideBox_BeveledButton_xSlotRight = "provideBox_BeveledButton_xSlotRight";

    public FloatBox provideBox_BeveledButton(FunctionalProvider.Inputs inputs) {
        var dimensAndTransforms = getRectDimensAndInnerTransformsUpperLeft(inputs);
        var rectDimens = dimensAndTransforms.FIRST;
        var transformsUpperLeft = dimensAndTransforms.SECOND;

        int xSlotLeft = getFromData(inputs, BeveledButton_xSlot);
        int ySlotTop = getFromData(inputs, BeveledButton_ySlot);
        int xSlotRight = getFromData(inputs, provideBox_BeveledButton_xSlotRight);
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
        return switch(xSlot) {
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
        return switch(ySlot) {
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
        FloatBox rectDimens;
        Vertex innerTransformsUpperLeft;

        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        long lastTimestamp = getFromData(component, BEVEL_LAST_TIMESTAMP);
        if (lastTimestamp == inputs.timestamp()) {
            rectDimens = getFromData(component, BEVEL_LAST_RECT_DIMENS);
            innerTransformsUpperLeft =
                    getFromData(component, BEVEL_LAST_INNER_TRANSFORMS_UPPER_LEFT);
        }
        else {
            ProviderAtTime<FloatBox> rectDimensProvider =
                    getFromData(inputs, BeveledButton_rectDimensProvider);
            rectDimens = rectDimensProvider.provide(inputs.timestamp());
            float bevelPercent =
                    getFromData(inputs, BeveledButton_bevelPercent);
            innerTransformsUpperLeft = getInnerTransformsUpperLeft(rectDimens, bevelPercent);

            component.data().put(BEVEL_LAST_TIMESTAMP, inputs.timestamp());
            component.data().put(BEVEL_LAST_RECT_DIMENS, rectDimens);
            component.data().put(BEVEL_LAST_INNER_TRANSFORMS_UPPER_LEFT, innerTransformsUpperLeft);
        }

        return pairOf(rectDimens, innerTransformsUpperLeft);
    }

    private static Vertex getInnerTransformsUpperLeft(FloatBox rectDimens,
                                                      float bevelPercent) {
        return vertexOf(rectDimens.width() * bevelPercent, rectDimens.height() * bevelPercent);
    }

    final static String provideColor_BeveledButton_bevelIntensity =
            "provideColor_BeveledButton_bevelIntensity";
    final static String provideColor_BeveledButton_isLitByDefault =
            "provideColor_BeveledButton_isLitByDefault";

    public Color provideColor_BeveledButton(FunctionalProvider.Inputs inputs) {
        boolean isLitByDefault =
                getFromData(inputs, provideColor_BeveledButton_isLitByDefault);
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        boolean isPressed = falseIfNull(getFromData(component, ButtonMethods.PRESS_STATE));
        Color renderableColor;
        // If it's lit by default XOR it's pressed, return the lit color
        if (isLitByDefault != isPressed) {
            renderableColor = getFromData(component, BEVEL_COLOR_LIT);
            if (renderableColor == null) {
                float bevelIntensity = getFromData(inputs,
                        provideColor_BeveledButton_bevelIntensity);
                renderableColor = new Color(1f, 1f, 1f, bevelIntensity);
                component.data().put(BEVEL_COLOR_LIT, renderableColor);
            }
        }
        else {
            renderableColor = getFromData(component, BEVEL_COLOR_UNLIT);
            if (renderableColor == null) {
                float bevelIntensity = getFromData(inputs,
                        provideColor_BeveledButton_bevelIntensity);
                renderableColor = new Color(0f, 0f, 0f, bevelIntensity);
                component.data().put(BEVEL_COLOR_UNLIT, renderableColor);
            }
        }

        return renderableColor;
    }
}
