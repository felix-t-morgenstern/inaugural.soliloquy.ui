package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RasterizedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RasterizedLineSegmentRenderableFactory;
import soliloquy.specs.ui.definitions.content.RasterizedLineSegmentRenderableDefinition;

import java.util.UUID;

public class RasterizedLineSegmentRenderableDefinitionReader extends AbstractContentDefinitionReader {
    private final RasterizedLineSegmentRenderableFactory FACTORY;
    private final short DEFAULT_STIPPLE_PATTERN;
    private final short DEFAULT_STIPPLE_FACTOR;

    public RasterizedLineSegmentRenderableDefinitionReader(
            RasterizedLineSegmentRenderableFactory factory,
            ProviderDefinitionReader providerReader,
            short defaultStipplePattern,
            short defaultStippleFactor) {
        super(providerReader);
        FACTORY = Check.ifNull(factory, "factory");
        DEFAULT_STIPPLE_PATTERN = defaultStipplePattern;
        DEFAULT_STIPPLE_FACTOR = defaultStippleFactor;
    }

    public RasterizedLineSegmentRenderable read(
            Component component,
            RasterizedLineSegmentRenderableDefinition definition
    ) {
        Check.ifNull(component, "component");
        Check.ifNull(definition, "definition");

        var vertex1 = PROVIDER_READER.read(
                Check.ifNull(definition.VERTEX_1_PROVIDER, "definition.VERTEX_1_PROVIDER"));
        var vertex2 = PROVIDER_READER.read(
                Check.ifNull(definition.VERTEX_2_PROVIDER, "definition.VERTEX_2_PROVIDER"));
        var thickness = PROVIDER_READER.read(
                Check.ifNull(definition.THICKNESS_PROVIDER, "definition.THICKNESS_PROVIDER"));
        short stipplePattern = definition.stipplePattern == null ? DEFAULT_STIPPLE_PATTERN :
                definition.stipplePattern;
        short stippleFactor = definition.stippleFactor == null ? DEFAULT_STIPPLE_FACTOR :
                definition.stippleFactor;
        var color = PROVIDER_READER.read(
                Check.ifNull(definition.COLOR_PROVIDER, "definition.COLOR_PROVIDER"));

        return FACTORY.make(vertex1, vertex2, thickness, stipplePattern, stippleFactor, color,
                definition.Z, UUID.randomUUID(), component);
    }
}
