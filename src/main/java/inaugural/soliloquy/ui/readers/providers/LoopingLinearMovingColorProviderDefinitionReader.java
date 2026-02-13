package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.LoopingLinearMovingColorProviderFactory;
import soliloquy.specs.ui.definitions.providers.LoopingLinearMovingColorProviderDefinition;

import java.awt.*;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class
LoopingLinearMovingColorProviderDefinitionReader {
    private final LoopingLinearMovingColorProviderFactory FACTORY;

    public LoopingLinearMovingColorProviderDefinitionReader(
            LoopingLinearMovingColorProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public ProviderAtTime<Color> read(LoopingLinearMovingColorProviderDefinition definition,
                                      long contentRenderTimestamp) {
        Check.ifNull(definition, "definition");
        Check.ifNull(definition.VALUES_WITHIN_PERIOD, "definition.VALUES_WITHIN_PERIOD");
        var periodModuloOffset = definition.PERIOD_DURATION -
                (int) (contentRenderTimestamp % (definition.PERIOD_DURATION));

        return FACTORY.make(
                UUID.randomUUID(),
                mapOf(definition.VALUES_WITHIN_PERIOD),
                definition.HUE_MOVEMENT_IS_CLOCKWISE,
                definition.PERIOD_DURATION,
                periodModuloOffset,
                null
        );
    }
}
