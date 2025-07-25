package inaugural.soliloquy.ui.readers.content;

import org.apache.commons.lang3.function.TriFunction;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

public class ContentDefinitionReader {
    private final RasterizedLineSegmentRenderableDefinitionReader RASTERIZED_LINE_READER;
    private final AntialiasedLineSegmentRenderableDefinitionReader ANTIALIASED_LINE_READER;
    private final RectangleRenderableDefinitionReader RECTANGLE_READER;
    private final TriangleRenderableDefinitionReader TRIANGLE_READER;
    private final SpriteRenderableDefinitionReader SPRITE_READER;
    private final ImageAssetRenderableDefinitionReader IMAGE_ASSET_SET_READER;
    private final FiniteAnimationRenderableDefinitionReader FINITE_ANIMATION_READER;
    private final TextLineRenderableDefinitionReader TEXT_LINE_READER;

    private final TriFunction<ComponentDefinition, Component, Long, Component> READ_COMPONENT;

    public ContentDefinitionReader(
            RasterizedLineSegmentRenderableDefinitionReader rasterizedLineReader,
            AntialiasedLineSegmentRenderableDefinitionReader antialiasedLineReader,
            RectangleRenderableDefinitionReader rectangleReader,
            TriangleRenderableDefinitionReader triangleReader,
            SpriteRenderableDefinitionReader spriteReader,
            ImageAssetRenderableDefinitionReader imageAssetSetReader,
            FiniteAnimationRenderableDefinitionReader finiteAnimationReader,
            TextLineRenderableDefinitionReader textLineReader,
            TriFunction<ComponentDefinition, Component, Long, Component> readComponent) {
        RASTERIZED_LINE_READER = rasterizedLineReader;
        ANTIALIASED_LINE_READER = antialiasedLineReader;
        RECTANGLE_READER = rectangleReader;
        TRIANGLE_READER = triangleReader;
        SPRITE_READER = spriteReader;
        IMAGE_ASSET_SET_READER = imageAssetSetReader;
        FINITE_ANIMATION_READER = finiteAnimationReader;
        TEXT_LINE_READER = textLineReader;
        READ_COMPONENT = readComponent;
    }
}
