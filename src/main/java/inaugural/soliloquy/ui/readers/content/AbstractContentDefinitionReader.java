package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public abstract class AbstractContentDefinitionReader {
    protected final ProviderDefinitionReader PROVIDER_READER;
    @SuppressWarnings("rawtypes") protected final ProviderAtTime NULL_PROVIDER;

    protected AbstractContentDefinitionReader(ProviderDefinitionReader providerReader,
                                              @SuppressWarnings("rawtypes")
                                              ProviderAtTime nullProvider) {
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
    }

    protected AbstractContentDefinitionReader(ProviderDefinitionReader providerReader) {
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = null;
    }

    protected <T> ProviderAtTime<T> provider(AbstractProviderDefinition<T> definition,
                                             long timestamp) {
        //noinspection unchecked
        return defaultIfNull(definition, d -> PROVIDER_READER.read(d, timestamp), NULL_PROVIDER);
    }

    protected <T> ProviderAtTime<T> provider(ProviderAtTime<T> provider,
                                             AbstractProviderDefinition<T> definition,
                                             long timestamp) {
        return provider != null ? provider : provider(definition, timestamp);
    }
}
