package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.content.AbstractContentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RasterizedLineSegmentRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RasterizedLineSegmentRenderableFactory;
import soliloquy.specs.ui.definitions.content.RasterizedLineSegmentRenderableDefinition;

import java.util.UUID;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public class RasterizedLineSegmentRenderableDefinitionReader
        extends AbstractContentDefinitionReader {
    private final RasterizedLineSegmentRenderableFactory FACTORY;
    private final short DEFAULT_STIPPLE_FACTOR;

    public RasterizedLineSegmentRenderableDefinitionReader(
            RasterizedLineSegmentRenderableFactory factory,
            ProviderDefinitionReader providerReader,
            short defaultStippleFactor) {
        super(providerReader);
        FACTORY = Check.ifNull(factory, "factory");
        DEFAULT_STIPPLE_FACTOR = defaultStippleFactor;
    }

    public RasterizedLineSegmentRenderable read(
            Component containingComponent,
            RasterizedLineSegmentRenderableDefinition definition,
            long timestamp
    ) {
        Check.ifNull(containingComponent, "containingComponent");
        Check.ifNull(definition, "definition");

        var vertex1 = PROVIDER_READER.read(
                Check.ifNull(definition.VERTEX_1_PROVIDER, "definition.VERTEX_1_PROVIDER"),
                timestamp);
        var vertex2 = PROVIDER_READER.read(
                Check.ifNull(definition.VERTEX_2_PROVIDER, "definition.VERTEX_2_PROVIDER"),
                timestamp);
        var thickness = PROVIDER_READER.read(
                Check.ifNull(definition.THICKNESS_PROVIDER, "definition.THICKNESS_PROVIDER"),
                timestamp);
        var stipplePattern = definition.stipplePattern;
        var stippleFactor = defaultIfNull(definition.stippleFactor, DEFAULT_STIPPLE_FACTOR);
        var color = PROVIDER_READER.read(
                Check.ifNull(definition.COLOR_PROVIDER, "definition.COLOR_PROVIDER"), timestamp);

        return FACTORY.make(vertex1, vertex2, thickness, stipplePattern, stippleFactor, color,
                definition.Z, UUID.randomUUID(), containingComponent);
    }
}
