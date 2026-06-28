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

public class TextBlockMethods {
    public final static String TEXT_BLOCK_HEIGHT = "TEXT_BLOCK_HEIGHT";
    public final static String TEXT_BLOCK_WIDTH = "TEXT_BLOCK_WIDTH";
    public final static String TEXT_BLOCK_LINE_OFFSET = "TEXT_BLOCK_LINE_OFFSET";

    private final Function<UUID, Component> GET_COMPONENT;

    public TextBlockMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    public final static String TextBlock_provideTextLineRenderingLoc =
            "TextBlock_provideTextLineRenderingLoc";

    public Vertex TextBlock_provideTextLineRenderingLoc(FunctionalProvider.Inputs inputs) {
        var textBlock = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));

        var blockUpperLeft = TextBlock_getBlockUpperLeft(textBlock, inputs.timestamp());

        return translateVertex(blockUpperLeft, getFromData(inputs, TEXT_BLOCK_LINE_OFFSET));
    }

    public final static String TextBlock_getDimens = "TextBlock_getDimens";

    public FloatBox TextBlock_getDimens(FunctionalProvider.Inputs inputs) {
        var component = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        var blockUpperLeft = TextBlock_getBlockUpperLeft(component, inputs.timestamp());
        return floatBoxOf(
                blockUpperLeft,
                translateVertex(
                        blockUpperLeft,
                        getFromData(component, TEXT_BLOCK_WIDTH),
                        getFromData(component, TEXT_BLOCK_HEIGHT)
                )
        );
    }

    private Vertex TextBlock_getBlockUpperLeft(Component textBlock,
                                               long timestamp) {
        Long lastTimestamp = getFromData(textBlock, LAST_TIMESTAMP);
        if (lastTimestamp != null && lastTimestamp == timestamp) {
            return getFromData(textBlock, COMPONENT_ORIGIN);
        }
        else {
            ProviderAtTime<Vertex> componentOriginProvider =
                    getFromData(textBlock, COMPONENT_ORIGIN_PROVIDER);
            var blockUpperLeft = componentOriginProvider.provide(timestamp);
            textBlock.data().put(LAST_TIMESTAMP, timestamp);
            textBlock.data().put(COMPONENT_ORIGIN, blockUpperLeft);
            return blockUpperLeft;
        }
    }
}
