package inaugural.soliloquy.ui.components;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.Tools.supplyIfNull;

public abstract class AbstractCustomComponentDefinitionReader<TDef extends AbstractContentDefinition> {
    protected final ProviderDefinitionReader PROVIDER_DEF_READER;

    protected AbstractCustomComponentDefinitionReader(ProviderDefinitionReader providerDefReader) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    protected abstract ComponentDefinition read(TDef def, long timestamp);

    protected <T> ProviderAtTime<T> providerOrReadDef(
            ProviderAtTime<T> provider,
            AbstractProviderDefinition<T> providerDef,
            long timestamp
    ) {
        return supplyIfNull(
                provider,
                () -> PROVIDER_DEF_READER.read(providerDef, timestamp)
        );
    }
}
