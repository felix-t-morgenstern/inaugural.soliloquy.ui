package inaugural.soliloquy.ui.definitions;

import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

public class RectangleRenderableDefinitionReader {
    private final RectangleRenderableFactory FACTORY;
    private final ProviderDefinitionReader PROVIDER_READER;

    public RectangleRenderableDefinitionReader(RectangleRenderableFactory factory,
                                               ProviderDefinitionReader providerReader) {
        FACTORY = factory;
        PROVIDER_READER = providerReader;
    }

    public RectangleRenderable read(RectangleRenderableDefinition definition) {
        return null;
    }
}
