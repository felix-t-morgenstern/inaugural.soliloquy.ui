package inaugural.soliloquy.ui.components.content;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.UUID;

public abstract class AbstractContentSpanDefinition extends AbstractContentDefinition {
    public final float LENGTH;

    public ProviderAtTime<Vertex> renderingLocProvider;
    public AbstractProviderDefinition<Vertex> renderingLocProviderDef;

    protected AbstractContentSpanDefinition(ProviderAtTime<Vertex> renderingLocProvider,
                                            AbstractProviderDefinition<Vertex> renderingLocProviderDef,
                                            float length,
                                            int z,
                                            UUID uuid) {
        super(z, uuid);
        this.renderingLocProvider = renderingLocProvider;
        this.renderingLocProviderDef = renderingLocProviderDef;
        LENGTH = Check.throwOnLteZero(length, "length");
    }
}
