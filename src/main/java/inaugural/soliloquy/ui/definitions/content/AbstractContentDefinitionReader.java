package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

public abstract class AbstractContentDefinitionReader {
    protected final ProviderDefinitionReader PROVIDER_READER;
    @SuppressWarnings("rawtypes") protected final StaticProvider NULL_PROVIDER;

    protected AbstractContentDefinitionReader(ProviderDefinitionReader providerReader,
                                              @SuppressWarnings("rawtypes")
                                              StaticProvider nullProvider) {
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
    }

    protected AbstractContentDefinitionReader(ProviderDefinitionReader providerReader) {
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = null;
    }

    protected <T> ProviderAtTime<T> provider(AbstractProviderDefinition<T> definition) {
        //noinspection unchecked
        return definition == null ? NULL_PROVIDER : PROVIDER_READER.read(definition);
    }
}
