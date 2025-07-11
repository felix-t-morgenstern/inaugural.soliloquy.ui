package definitions.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.FiniteLinearMovingProviderFactory;
import soliloquy.specs.ui.definitions.providers.FiniteLinearMovingProviderDefinition;

import java.util.Arrays;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class FiniteLinearMovingProviderDefinitionReader {
    private final FiniteLinearMovingProviderFactory FACTORY;

    public FiniteLinearMovingProviderDefinitionReader(FiniteLinearMovingProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> ProviderAtTime<T> read(FiniteLinearMovingProviderDefinition<T> definition,
                                      long contentRenderTimestamp) {
        var valsAtTimestamps = mapOf(Arrays.stream(definition.VALUES_AT_TIMESTAMPS)
                .map(val -> pairOf(val.FIRST + contentRenderTimestamp, val.SECOND)));
        return FACTORY.make(UUID.randomUUID(), valsAtTimestamps, null, null);
    }
}
