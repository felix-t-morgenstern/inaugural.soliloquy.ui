package inaugural.soliloquy.ui.readers.providers;

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
        Check.ifNull(definition, "definition");
        Check.ifNull(definition.VALUES_AT_TIMESTAMP_OFFSETS,
                "definition.VALUES_AT_TIMESTAMP_OFFSETS");
        var valsAtTimestamps = mapOf(Arrays.stream(definition.VALUES_AT_TIMESTAMP_OFFSETS)
                .map(val -> pairOf(Check.ifNull(val,
                        "val within definition.VALUES_AT_TIMESTAMP_OFFSETS").FIRST +
                        contentRenderTimestamp, val.SECOND)));
        return FACTORY.make(
                UUID.randomUUID(),
                valsAtTimestamps,
                null
        );
    }
}
