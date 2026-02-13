package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Map;
import java.util.function.BiFunction;

public class ProviderDefinitionReader {
    @SuppressWarnings("rawtypes") private final Map<Class, BiFunction<Object, Long, ProviderAtTime>>
            READERS;

    public ProviderDefinitionReader(
            @SuppressWarnings("rawtypes") Map<Class, BiFunction<Object, Long, ProviderAtTime>> readers) {
        READERS = Check.ifNull(readers, "readers");
    }

    public <T> ProviderAtTime<T> read(AbstractProviderDefinition<T> definition, long timestamp) {
        var reader = READERS.get(definition.getClass());

        //noinspection unchecked
        return (ProviderAtTime<T>) (reader.apply(definition, timestamp));
    }
}
