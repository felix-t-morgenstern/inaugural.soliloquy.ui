package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.FunctionalProviderFactory;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class FunctionalProviderDefinitionReader {
    private final FunctionalProviderFactory FACTORY;

    public FunctionalProviderDefinitionReader(FunctionalProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> ProviderAtTime<T> read(FunctionalProviderDefinition<T> definition) {
        return FACTORY.make(
                definition.uuid == null ? UUID.randomUUID() : definition.uuid,
                definition.PROVIDE_FUNCTION_ID,
                definition.pauseActionId,
                definition.unpauseActionId,
                definition.pauseTimestamp,
                definition.data == null ? mapOf() : definition.data
        );
    }
}
