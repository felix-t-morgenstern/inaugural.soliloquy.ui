package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.AntialiasedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.factories.AntialiasedLineSegmentRenderableFactory;
import soliloquy.specs.ui.definitions.content.AntialiasedLineSegmentRenderableDefinition;

import java.util.UUID;

public class AntialiasedLineSegmentRenderableDefinitionReader {
    private final AntialiasedLineSegmentRenderableFactory FACTORY;
    private final ProviderDefinitionReader PROVIDER_READER;

    public AntialiasedLineSegmentRenderableDefinitionReader(
            AntialiasedLineSegmentRenderableFactory factory,
            ProviderDefinitionReader providerReader) {
        FACTORY = Check.ifNull(factory, "factory");
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
    }

    public AntialiasedLineSegmentRenderable read(
            Component component,
            AntialiasedLineSegmentRenderableDefinition definition,
            long timestamp
    ) {
        var vertex1 = PROVIDER_READER.read(definition.VERTEX_1_PROVIDER, timestamp);
        var vertex2 = PROVIDER_READER.read(definition.VERTEX_2_PROVIDER, timestamp);
        var thickness = PROVIDER_READER.read(definition.THICKNESS_PROVIDER, timestamp);
        var color = PROVIDER_READER.read(definition.COLOR_PROVIDER, timestamp);
        var thicknessGradientPercent =
                PROVIDER_READER.read(definition.THICKNESS_GRADIENT_PERCENT_PROVIDER, timestamp);
        var lengthGradientPercent =
                PROVIDER_READER.read(definition.LENGTH_GRADIENT_PERCENT_PROVIDER, timestamp);

        return FACTORY.make(vertex1, vertex2, color, thickness, thicknessGradientPercent,
                lengthGradientPercent, definition.Z, UUID.randomUUID(), component);
    }
}
