package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.ui.definitions.content.*;

import static java.util.UUID.randomUUID;

public class RenderableDefinitionReader extends AbstractContentDefinitionReader {
    private final RasterizedLineSegmentRenderableDefinitionReader RASTERIZED_LINE_READER;
    private final AntialiasedLineSegmentRenderableDefinitionReader ANTIALIASED_LINE_READER;
    private final RectangleRenderableDefinitionReader RECTANGLE_READER;
    private final TriangleRenderableDefinitionReader TRIANGLE_READER;
    private final SpriteRenderableDefinitionReader SPRITE_READER;
    private final ImageAssetSetRenderableDefinitionReader IMAGE_ASSET_SET_READER;
    private final FiniteAnimationRenderableDefinitionReader FINITE_ANIMATION_READER;
    private final TextLineRenderableDefinitionReader TEXT_LINE_READER;

    private final ComponentFactory COMPONENT_FACTORY;

    public RenderableDefinitionReader(
            RasterizedLineSegmentRenderableDefinitionReader rasterizedLineReader,
            AntialiasedLineSegmentRenderableDefinitionReader antialiasedLineReader,
            RectangleRenderableDefinitionReader rectangleReader,
            TriangleRenderableDefinitionReader triangleReader,
            SpriteRenderableDefinitionReader spriteReader,
            ImageAssetSetRenderableDefinitionReader imageAssetSetReader,
            FiniteAnimationRenderableDefinitionReader finiteAnimationReader,
            TextLineRenderableDefinitionReader textLineReader,
            ComponentFactory componentFactory,
            ProviderDefinitionReader providerReader) {
        super(providerReader);
        RASTERIZED_LINE_READER = Check.ifNull(rasterizedLineReader, "rasterizedLineReader");
        ANTIALIASED_LINE_READER = Check.ifNull(antialiasedLineReader, "antialiasedLineReader");
        RECTANGLE_READER = Check.ifNull(rectangleReader, "rectangleReader");
        TRIANGLE_READER = Check.ifNull(triangleReader, "triangleReader");
        SPRITE_READER = Check.ifNull(spriteReader, "spriteReader");
        IMAGE_ASSET_SET_READER = Check.ifNull(imageAssetSetReader, "imageAssetSetReader");
        FINITE_ANIMATION_READER = Check.ifNull(finiteAnimationReader, "finiteAnimationReader");
        TEXT_LINE_READER = Check.ifNull(textLineReader, "textLineReader");
        COMPONENT_FACTORY = Check.ifNull(componentFactory, "componentFactory");
    }

    public <TDef extends AbstractContentDefinition, TRend extends Renderable> TRend read(
            Component containingComponent,
            TDef definition,
            long timestamp
    ) {
        Check.ifNull(containingComponent, "containingComponent");
        Check.ifNull(definition, "definition");
        return switch (definition) {
            case RasterizedLineSegmentRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) RASTERIZED_LINE_READER.read(containingComponent, d, timestamp);
            case AntialiasedLineSegmentRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) ANTIALIASED_LINE_READER.read(containingComponent, d, timestamp);
            case RectangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) RECTANGLE_READER.read(containingComponent, d, timestamp);
            case TriangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TRIANGLE_READER.read(containingComponent, d, timestamp);
            case SpriteRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) SPRITE_READER.read(containingComponent, d, timestamp);
            case ImageAssetSetRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) IMAGE_ASSET_SET_READER.read(containingComponent, d, timestamp);
            case FiniteAnimationRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) FINITE_ANIMATION_READER.read(containingComponent, d, timestamp);
            case TextLineRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TEXT_LINE_READER.read(containingComponent, d, timestamp);
            case ComponentDefinition d -> {
                var readComponent = COMPONENT_FACTORY.make(
                        randomUUID(),
                        d.Z,
                        PROVIDER_READER.read(d.DIMENSIONS_PROVIDER, timestamp),
                        containingComponent
                );
                for (var contentDef : d.CONTENT) {
                    read(readComponent, contentDef, timestamp);
                }
                //noinspection unchecked
                yield (TRend) readComponent;
            }
            default -> throw new IllegalArgumentException(
                    "ContentDefinitionReader.read: Unexpected definition type (" +
                            definition.getClass().getCanonicalName() + ")");
        };
    }
}
