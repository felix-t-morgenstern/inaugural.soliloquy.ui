package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.FunctionalProviderFactory;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.UUID;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static java.util.UUID.randomUUID;

public class FunctionalProviderDefinitionReader {
    private final FunctionalProviderFactory FACTORY;

    public FunctionalProviderDefinitionReader(FunctionalProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> ProviderAtTime<T> read(FunctionalProviderDefinition<T> definition) {
        return FACTORY.make(
                defaultIfNull(definition.uuid, randomUUID()),
                definition.PROVIDE_FUNCTION_ID,
                definition.pauseActionId,
                definition.unpauseActionId,
                definition.pauseTimestamp,
                defaultIfNull(definition.data, mapOf())
        );
    }
}
