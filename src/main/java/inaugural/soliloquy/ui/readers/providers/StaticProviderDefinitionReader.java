package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.util.UUID;
import java.util.function.BiFunction;

public class StaticProviderDefinitionReader {
    @SuppressWarnings("rawtypes") private final BiFunction<UUID, Object, ProviderAtTime> FACTORY;

    public StaticProviderDefinitionReader(
            @SuppressWarnings("rawtypes") BiFunction<UUID, Object, ProviderAtTime> factory) {
        FACTORY = Check.ifNull(factory, "factory");
    }

    public <T> ProviderAtTime<T> read(StaticProviderDefinition<T> definition) {
        //noinspection unchecked
        return (ProviderAtTime<T>) FACTORY.apply(
                UUID.randomUUID(),
                Check.ifNull(definition, "definition").VALUE
        );
    }
}
