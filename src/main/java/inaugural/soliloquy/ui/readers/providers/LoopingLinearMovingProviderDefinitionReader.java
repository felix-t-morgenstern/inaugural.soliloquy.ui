package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.LoopingLinearMovingProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.LoopingLinearMovingProviderFactory;
import soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition;

import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class LoopingLinearMovingProviderDefinitionReader {
    private final LoopingLinearMovingProviderFactory FACTORY;

    public LoopingLinearMovingProviderDefinitionReader(LoopingLinearMovingProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> LoopingLinearMovingProvider<T> read(
            LoopingLinearMovingProviderDefinition<T> definition, long contentRenderTimestamp) {
        Check.ifNull(definition, "definition");
        Check.ifNull(definition.VALUES_WITHIN_PERIOD, "definition.VALUES_WITHIN_PERIOD");
        var periodModuloOffset = definition.PERIOD_DURATION -
                (int) (contentRenderTimestamp % (definition.PERIOD_DURATION));

        return FACTORY.make(
                UUID.randomUUID(),
                definition.PERIOD_DURATION,
                periodModuloOffset,
                mapOf(definition.VALUES_WITHIN_PERIOD),
                null
        );
    }
}
