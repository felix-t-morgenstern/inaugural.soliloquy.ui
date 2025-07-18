package definitions.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.FiniteLinearMovingColorProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.FiniteLinearMovingColorProviderFactory;
import soliloquy.specs.ui.definitions.providers.FiniteLinearMovingColorProviderDefinition;

import java.util.Arrays;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class FiniteLinearMovingColorProviderDefinitionReader {
    private final FiniteLinearMovingColorProviderFactory FACTORY;

    public FiniteLinearMovingColorProviderDefinitionReader(
            FiniteLinearMovingColorProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public FiniteLinearMovingColorProvider read(
            FiniteLinearMovingColorProviderDefinition definition, long contentRenderTimestamp) {
        Check.ifNull(definition, "definition");
        Check.ifNull(definition.VALUES_AT_TIMESTAMP_OFFSETS,
                "definition.VALUES_AT_TIMESTAMP_OFFSETS");
        var valsAtTimestamps = mapOf(Arrays.stream(definition.VALUES_AT_TIMESTAMP_OFFSETS)
                .map(val -> pairOf(Check.ifNull(val,
                        "val within definition.VALUES_AT_TIMESTAMP_OFFSETS").FIRST +
                        contentRenderTimestamp, val.SECOND)));
        return FACTORY.make(UUID.randomUUID(), valsAtTimestamps,
                definition.HUE_MOVEMENT_IS_CLOCKWISE, null, null);
    }
}
