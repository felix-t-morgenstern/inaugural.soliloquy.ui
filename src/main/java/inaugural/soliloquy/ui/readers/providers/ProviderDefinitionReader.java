package inaugural.soliloquy.ui.readers.providers;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Map;
import java.util.function.Function;

public class ProviderDefinitionReader {
    @SuppressWarnings("rawtypes") private final Map<Class, Function<Object, ProviderAtTime>>
            READERS;

    public ProviderDefinitionReader(
            @SuppressWarnings("rawtypes") Map<Class, Function<Object, ProviderAtTime>> readers) {
        READERS = Check.ifNull(readers, "readers");
    }

    public <T> ProviderAtTime<T> read(AbstractProviderDefinition<T> definition) {
        var reader = READERS.get(definition.getClass());

        //noinspection unchecked
        return (ProviderAtTime<T>) reader.apply(definition);
    }
}
