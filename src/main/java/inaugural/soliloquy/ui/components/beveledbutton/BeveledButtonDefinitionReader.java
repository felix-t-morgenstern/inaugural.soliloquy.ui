package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.components.button.ButtonDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.awt.*;
import java.util.Map;
import java.util.Set;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.COMPONENT_ID;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods.*;
import static inaugural.soliloquy.ui.components.button.ButtonDefinitionReader.RECT_Z;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class BeveledButtonDefinitionReader {
    private static final int BEVEL_Z = 3;

    private static final String PROVIDE_VERTEX_METHOD = "provideVertex_BeveledButton";
    private static final String PROVIDE_BOX_METHOD = "provideBox_BeveledButton";
    private static final String PROVIDE_COLOR_METHOD = "provideColor_BeveledButton";

    private final ButtonDefinitionReader BUTTON_DEF_READER;
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public BeveledButtonDefinitionReader(ButtonDefinitionReader buttonDefReader,
                                         ProviderDefinitionReader providerDefReader) {
        BUTTON_DEF_READER = Check.ifNull(buttonDefReader, "buttonDefReader");
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ComponentDefinition read(BeveledButtonDefinition definition, long timestamp) {
        var componentDef = BUTTON_DEF_READER.read(definition, timestamp);

        componentDef.data.put(BEVEL_LAST_TIMESTAMP, timestamp - 1L);

        @SuppressWarnings("OptionalGetWithoutIsPresent") RectangleRenderableDefinition rectDef =
                (RectangleRenderableDefinition) componentDef.CONTENT.stream()
                        .filter(c -> c.Z == RECT_Z).findFirst().get();

        return componentDef.withContent(
                makeBevel(definition, rectDef.DIMENS_PROVIDER, timestamp)
        );
    }

    private Set<AbstractContentDefinition> makeBevel(BeveledButtonDefinition definition,
                                                     ProviderAtTime<FloatBox> rectDimensProvider,
                                                     long timestamp) {
        Map<Integer, Map<Integer, ProviderAtTime<Vertex>>> points = mapOf();
        var slots = listInts(0, 1, 2, 3);
        slots.forEach(xSlot -> {
            points.put(xSlot, mapOf());
            var ySlots = points.get(xSlot);
            slots.forEach(ySlot -> ySlots.put(ySlot, bevelPointProvider(
                    definition,
                    rectDimensProvider,
                    xSlot,
                    ySlot,
                    timestamp
            )));
        });

        var leftBox = bevelBoxProvider(
                definition,
                rectDimensProvider,
                0,
                1,
                1,
                timestamp
        );
        var upperBox = bevelBoxProvider(
                definition,
                rectDimensProvider,
                1,
                3,
                0,
                timestamp
        );
        var rightBox = bevelBoxProvider(
                definition,
                rectDimensProvider,
                2,
                3,
                1,
                timestamp
        );
        var bottomBox = bevelBoxProvider(
                definition,
                rectDimensProvider,
                0,
                2,
                2,
                timestamp
        );

        var litByDefault =
                bevelColorProvider(definition, true, definition.BEVEL_INTENSITY, timestamp);
        var unlitByDefault =
                bevelColorProvider(definition, false, definition.BEVEL_INTENSITY, timestamp);

        var upperLeftLit = triangle(
                points.get(0).get(0),
                points.get(1).get(0),
                points.get(1).get(1),
                BEVEL_Z
        )
                .withColor(litByDefault);
        var upperBoxLit = rectangle(upperBox, BEVEL_Z)
                .withColor(litByDefault);
        var rightBoxLit = rectangle(rightBox, BEVEL_Z)
                .withColor(litByDefault);
        var lowerRightLit = triangle(
                points.get(2).get(2),
                points.get(3).get(2),
                points.get(3).get(3),
                BEVEL_Z
        )
                .withColor(litByDefault);

        var lowerRightUnlit = triangle(
                points.get(2).get(2),
                points.get(2).get(3),
                points.get(3).get(3),
                BEVEL_Z
        )
                .withColor(unlitByDefault);
        var lowerBoxUnlit = rectangle(bottomBox, BEVEL_Z)
                .withColor(unlitByDefault);
        var leftBoxUnlit = rectangle(leftBox, BEVEL_Z)
                .withColor(unlitByDefault);
        var upperLeftUnlit = triangle(
                points.get(0).get(0),
                points.get(0).get(1),
                points.get(1).get(1),
                BEVEL_Z
        )
                .withColor(unlitByDefault);

        return setOf(
                upperLeftLit,
                upperBoxLit,
                rightBoxLit,
                lowerRightLit,
                lowerRightUnlit,
                lowerBoxUnlit,
                leftBoxUnlit,
                upperLeftUnlit
        );
    }

    private ProviderAtTime<Vertex> bevelPointProvider(
            BeveledButtonDefinition definition,
            ProviderAtTime<FloatBox> rectDimensProvider,
            int xSlot,
            int ySlot,
            long timestamp
    ) {
        return PROVIDER_DEF_READER.read(
                functionalProvider(PROVIDE_VERTEX_METHOD, Vertex.class)
                        .withData(mapOf(
                                COMPONENT_ID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                rectDimensProvider,
                                BeveledButton_xSlot,
                                xSlot,
                                BeveledButton_ySlot,
                                ySlot,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        )),
                timestamp
        );
    }

    private ProviderAtTime<FloatBox> bevelBoxProvider(
            BeveledButtonDefinition definition,
            ProviderAtTime<FloatBox> rectDimensProvider,
            int xSlotLeft,
            int xSlotRight,
            int ySlot,
            long timestamp
    ) {
        return PROVIDER_DEF_READER.read(
                functionalProvider(PROVIDE_BOX_METHOD, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_ID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                rectDimensProvider,
                                BeveledButton_xSlot,
                                xSlotLeft,
                                provideBox_BeveledButton_xSlotRight,
                                xSlotRight,
                                BeveledButton_ySlot,
                                ySlot,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        )),
                timestamp
        );
    }

    private ProviderAtTime<Color> bevelColorProvider(
            BeveledButtonDefinition definition,
            boolean isLitByDefault,
            float bevelIntensity,
            long timestamp
    ) {
        return PROVIDER_DEF_READER.read(
                functionalProvider(PROVIDE_COLOR_METHOD, Color.class)
                        .withData(mapOf(
                                COMPONENT_ID,
                                definition.UUID,
                                provideColor_BeveledButton_isLitByDefault,
                                isLitByDefault,
                                provideColor_BeveledButton_bevelIntensity,
                                bevelIntensity
                        )),
                timestamp
        );
    }
}
