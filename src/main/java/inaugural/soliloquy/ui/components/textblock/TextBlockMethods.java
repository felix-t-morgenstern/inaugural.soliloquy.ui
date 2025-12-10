package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.components.ComponentMethods.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.ComponentMethods.LAST_TIMESTAMP;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class TextBlockMethods {
    final static String BLOCK_UPPER_LEFT = "BLOCK_UPPER_LEFT";

    private final Function<UUID, Component> GET_COMPONENT;

    public TextBlockMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    final static String TextBlock_blockUpperLeftProvider = "TextBlock_blockUpperLeftProvider";
    final static String TextBlock_topOffset = "TextBlock_topOffset";

    public Vertex provideTextRenderingLoc_TextBlock(FunctionalProvider.Inputs inputs) {
        Vertex blockUpperLeft;
        var component = GET_COMPONENT.apply(getFromData(inputs.data(), COMPONENT_UUID));
        long lastTimestamp = getFromData(component.data(), LAST_TIMESTAMP);
        if (lastTimestamp == inputs.timestamp()) {
            blockUpperLeft = getFromData(component.data(), BLOCK_UPPER_LEFT);
        }
        else {
            ProviderAtTime<Vertex> blockUpperLeftProvider =
                    getFromData(inputs.data(), TextBlock_blockUpperLeftProvider);
            blockUpperLeft = blockUpperLeftProvider.provide(inputs.timestamp());
            component.data().put(LAST_TIMESTAMP, inputs.timestamp());
            component.data().put(BLOCK_UPPER_LEFT, blockUpperLeft);
        }

        float topOffset = getFromData(inputs.data(), TextBlock_topOffset);
        return vertexOf(blockUpperLeft.X, blockUpperLeft.Y + topOffset);
    }
}
