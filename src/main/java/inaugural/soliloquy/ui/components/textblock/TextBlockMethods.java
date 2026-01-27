package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class TextBlockMethods {
    public final static String HEIGHT = "HEIGHT";
    public final static String WIDTH = "WIDTH";

    private final Function<UUID, Component> GET_COMPONENT;

    public TextBlockMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    final static String TextBlock_blockUpperLeftProvider = "TextBlock_blockUpperLeftProvider";
    final static String TextBlock_topOffset = "TextBlock_topOffset";

    public final static String TextBlock_provideTextRenderingLoc =
            "TextBlock_provideTextRenderingLoc";

    public Vertex TextBlock_provideTextRenderingLoc(FunctionalProvider.Inputs inputs) {
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));

        var blockUpperLeft = TextBlock_getUpperLeft(component, inputs.timestamp(), inputs.data());

        float topOffset = getFromData(inputs, TextBlock_topOffset);
        return vertexOf(blockUpperLeft.X, blockUpperLeft.Y + topOffset);
    }

    public final static String TextBlock_getDimens = "TextBlock_getDimens";

    public FloatBox TextBlock_getDimens(FunctionalProvider.Inputs inputs) {
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var blockUpperLeft = TextBlock_getUpperLeft(component, inputs.timestamp(), inputs.data());
        return floatBoxOf(blockUpperLeft,
                translateVertex(blockUpperLeft, getFromData(component, WIDTH),
                        getFromData(component, HEIGHT)));
    }

    private Vertex TextBlock_getUpperLeft(Component component,
                                          long timestamp,
                                          Map<String, Object> inputsData) {
        long lastTimestamp = getFromData(component, LAST_TIMESTAMP);
        if (lastTimestamp == timestamp) {
            return getFromData(component, ORIGIN_OVERRIDE);
        }
        else {
            Vertex blockUpperLeft;
            ProviderAtTime<Vertex> originOverrideProvider =
                    getFromData(component, ORIGIN_OVERRIDE_PROVIDER);
            if (originOverrideProvider != null) {
                blockUpperLeft = originOverrideProvider.provide(timestamp);
            }
            else {
                ProviderAtTime<Vertex> blockUpperLeftProvider =
                        getFromData(inputsData, TextBlock_blockUpperLeftProvider);
                blockUpperLeft = blockUpperLeftProvider.provide(timestamp);
            }
            component.data().put(LAST_TIMESTAMP, timestamp);
            component.data().put(ORIGIN_OVERRIDE, blockUpperLeft);
            return blockUpperLeft;
        }
    }
}
