package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import org.apache.commons.lang3.function.TriFunction;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.ui.definitions.content.*;

public class ContentDefinitionReader {
    private final RasterizedLineSegmentRenderableDefinitionReader RASTERIZED_LINE_READER;
    private final AntialiasedLineSegmentRenderableDefinitionReader ANTIALIASED_LINE_READER;
    private final RectangleRenderableDefinitionReader RECTANGLE_READER;
    private final TriangleRenderableDefinitionReader TRIANGLE_READER;
    private final SpriteRenderableDefinitionReader SPRITE_READER;
    private final ImageAssetSetRenderableDefinitionReader IMAGE_ASSET_SET_READER;
    private final FiniteAnimationRenderableDefinitionReader FINITE_ANIMATION_READER;
    private final TextLineRenderableDefinitionReader TEXT_LINE_READER;

    private final TriFunction<ComponentDefinition, Component, Long, Component> READ_COMPONENT;

    public ContentDefinitionReader(
            RasterizedLineSegmentRenderableDefinitionReader rasterizedLineReader,
            AntialiasedLineSegmentRenderableDefinitionReader antialiasedLineReader,
            RectangleRenderableDefinitionReader rectangleReader,
            TriangleRenderableDefinitionReader triangleReader,
            SpriteRenderableDefinitionReader spriteReader,
            ImageAssetSetRenderableDefinitionReader imageAssetSetReader,
            FiniteAnimationRenderableDefinitionReader finiteAnimationReader,
            TextLineRenderableDefinitionReader textLineReader,
            TriFunction<ComponentDefinition, Component, Long, Component> readComponent) {
        RASTERIZED_LINE_READER = Check.ifNull(rasterizedLineReader, "rasterizedLineReader");
        ANTIALIASED_LINE_READER = Check.ifNull(antialiasedLineReader, "antialiasedLineReader");
        RECTANGLE_READER = Check.ifNull(rectangleReader, "rectangleReader");
        TRIANGLE_READER = Check.ifNull(triangleReader, "triangleReader");
        SPRITE_READER = Check.ifNull(spriteReader, "spriteReader");
        IMAGE_ASSET_SET_READER = Check.ifNull(imageAssetSetReader, "imageAssetSetReader");
        FINITE_ANIMATION_READER = Check.ifNull(finiteAnimationReader, "finiteAnimationReader");
        TEXT_LINE_READER = Check.ifNull(textLineReader, "textLineReader");
        READ_COMPONENT = Check.ifNull(readComponent, "readComponent");
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
                    (TRend) RASTERIZED_LINE_READER.read(containingComponent, d);
            case AntialiasedLineSegmentRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) ANTIALIASED_LINE_READER.read(containingComponent, d);
            case RectangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) RECTANGLE_READER.read(containingComponent, d);
            case TriangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TRIANGLE_READER.read(containingComponent, d);
            case SpriteRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) SPRITE_READER.read(containingComponent, d);
            case ImageAssetSetRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) IMAGE_ASSET_SET_READER.read(containingComponent, d);
            case FiniteAnimationRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) FINITE_ANIMATION_READER.read(containingComponent, d, timestamp);
            case TextLineRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TEXT_LINE_READER.read(containingComponent, d);
            case ComponentDefinition d ->
                //noinspection unchecked
                    (TRend) READ_COMPONENT.apply(d, containingComponent, timestamp);
            default -> throw new IllegalArgumentException(
                    "ContentDefinitionReader.read: Unexpected definition type (" +
                            definition.getClass().getCanonicalName() + ")");
        };
    }
}
