package inaugural.soliloquy.ui.definitions;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.RasterizedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RasterizedLineSegmentRenderableFactory;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.RasterizedLineSegmentRenderableDefinition;

import java.util.UUID;

public class RasterizedLineSegmentRenderableDefinitionReader {
    private final RasterizedLineSegmentRenderableFactory FACTORY;
    private final ProviderDefinitionReader PROVIDER_READER;
    private final short DEFAULT_STIPPLE_PATTERN;
    private final short DEFAULT_STIPPLE_FACTOR;

    public RasterizedLineSegmentRenderableDefinitionReader(
            RasterizedLineSegmentRenderableFactory factory,
            ProviderDefinitionReader providerReader,
            short defaultStipplePattern,
            short defaultStippleFactor) {
        FACTORY = Check.ifNull(factory, "factory");
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        DEFAULT_STIPPLE_PATTERN = defaultStipplePattern;
        DEFAULT_STIPPLE_FACTOR = defaultStippleFactor;
    }

    public RasterizedLineSegmentRenderable read(RenderableStack stack,
                                                RasterizedLineSegmentRenderableDefinition definition) {
        var vertex1 = PROVIDER_READER.read(definition.VERTEX_1_PROVIDER);
        var vertex2 = PROVIDER_READER.read(definition.VERTEX_2_PROVIDER);
        var thickness = PROVIDER_READER.read(definition.THICKNESS_PROVIDER);
        short stipplePattern = definition.stipplePattern == null ? DEFAULT_STIPPLE_PATTERN :
                definition.stipplePattern;
        short stippleFactor = definition.stippleFactor == null ? DEFAULT_STIPPLE_FACTOR :
                definition.stippleFactor;
        var color = PROVIDER_READER.read(definition.COLOR_PROVIDER);

        return FACTORY.make(vertex1, vertex2, thickness, stipplePattern, stippleFactor, color,
                definition.Z, UUID.randomUUID(), stack);
    }
}
