package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class TextBlockMethods {
    public final static String TEXT_BLOCK_HEIGHT = "TEXT_BLOCK_HEIGHT";
    public final static String TEXT_BLOCK_WIDTH = "TEXT_BLOCK_WIDTH";

    private final Function<UUID, Component> GET_COMPONENT;

    public TextBlockMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    final static String TextBlock_topOffset = "TextBlock_topOffset";

    public final static String TextBlock_provideTextLineRenderingLoc =
            "TextBlock_provideTextLineRenderingLoc";

    public Vertex TextBlock_provideTextLineRenderingLoc(FunctionalProvider.Inputs inputs) {
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));

        var blockUpperLeft = TextBlock_getUpperLeft(component, inputs.timestamp());

        float topOffset = getFromData(inputs, TextBlock_topOffset);
        return vertexOf(blockUpperLeft.X, blockUpperLeft.Y + topOffset);
    }

    public final static String TextBlock_getDimens = "TextBlock_getDimens";

    public FloatBox TextBlock_getDimens(FunctionalProvider.Inputs inputs) {
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var blockUpperLeft = TextBlock_getUpperLeft(component, inputs.timestamp());
        return floatBoxOf(
                blockUpperLeft,
                translateVertex(
                        blockUpperLeft,
                        getFromData(component, TEXT_BLOCK_WIDTH),
                        getFromData(component, TEXT_BLOCK_HEIGHT)
                )
        );
    }

    private Vertex TextBlock_getUpperLeft(Component component,
                                          long timestamp) {
        long lastTimestamp = getFromData(component, LAST_TIMESTAMP);
        if (lastTimestamp == timestamp) {
            return getFromData(component, ORIGIN_OVERRIDE);
        }
        else {
            ProviderAtTime<Vertex> originOverrideProvider =
                    getFromData(component, ORIGIN_OVERRIDE_PROVIDER);
            var blockUpperLeft = originOverrideProvider.provide(timestamp);
            component.data().put(LAST_TIMESTAMP, timestamp);
            component.data().put(ORIGIN_OVERRIDE, blockUpperLeft);
            return blockUpperLeft;
        }
    }
}
