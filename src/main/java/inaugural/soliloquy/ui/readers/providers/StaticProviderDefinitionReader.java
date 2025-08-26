package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.StaticProviderFactory;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.util.UUID;

public class StaticProviderDefinitionReader {
    private final StaticProviderFactory FACTORY;

    public StaticProviderDefinitionReader(StaticProviderFactory factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> StaticProvider<T> read(StaticProviderDefinition<T> definition) {
        return FACTORY.make(
                UUID.randomUUID(),
                Check.ifNull(definition, "definition").VALUE
        );
    }
}
