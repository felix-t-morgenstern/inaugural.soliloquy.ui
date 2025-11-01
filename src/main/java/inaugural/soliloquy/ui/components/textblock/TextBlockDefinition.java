package inaugural.soliloquy.ui.components.textblock;

import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.List;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockDefinition extends AbstractContentDefinition {
    public final String FONT_ID;
    public final float LINE_HEIGHT;
    public final float MAX_LINE_LENGTH;
    public final ProviderAtTime<Vertex> UPPER_LEFT_PROVIDER;
    public final AbstractProviderDefinition<Vertex> UPPER_LEFT_PROVIDER_DEF;
    public final float GLYPH_PADDING;
    public final float LINE_SPACING;
    public final float PARAGRAPH_SPACING;
    public final HorizontalAlignment ALIGNMENT;
    public final List<String> PARAGRAPHS;

    private TextBlockDefinition(String fontId,
                                float lineHeight,
                                float maxLineLength,
                                ProviderAtTime<Vertex> upperLeftProvider,
                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                float glyphPadding,
                                float lineSpacing,
                                float paragraphSpacing,
                                HorizontalAlignment alignment,
                                List<String> paragraphs,
                                int z) {
        super(z, randomUUID());
        FONT_ID = fontId;
        LINE_HEIGHT = lineHeight;
        MAX_LINE_LENGTH = maxLineLength;
        UPPER_LEFT_PROVIDER = upperLeftProvider;
        UPPER_LEFT_PROVIDER_DEF = upperLeftProviderDef;
        GLYPH_PADDING = glyphPadding;
        LINE_SPACING = lineSpacing;
        PARAGRAPH_SPACING = paragraphSpacing;
        ALIGNMENT = alignment;
        PARAGRAPHS = paragraphs;
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                ProviderAtTime<Vertex> upperLeftProvider,
                                                float glyphPadding,
                                                float lineSpacing,
                                                float paragraphSpacing,
                                                HorizontalAlignment alignment,
                                                List<String> paragraphs,
                                                int z) {
        return new TextBlockDefinition(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProvider,
                null,
                glyphPadding,
                lineSpacing,
                paragraphSpacing,
                alignment,
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                ProviderAtTime<Vertex> upperLeftProvider,
                                                float glyphPadding,
                                                float lineSpacing,
                                                HorizontalAlignment alignment,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProvider,
                glyphPadding,
                lineSpacing,
                0f,
                alignment,
                listOf(paragraph),
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                                float glyphPadding,
                                                float lineSpacing,
                                                float paragraphSpacing,
                                                HorizontalAlignment alignment,
                                                List<String> paragraphs,
                                                int z) {
        return new TextBlockDefinition(
                fontId,
                lineHeight,
                maxLineLength,
                null,
                upperLeftProviderDef,
                glyphPadding,
                lineSpacing,
                paragraphSpacing,
                alignment,
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                                float glyphPadding,
                                                float lineSpacing,
                                                HorizontalAlignment alignment,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProviderDef,
                glyphPadding,
                lineSpacing,
                0f,
                alignment,
                listOf(paragraph),
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                Vertex upperLeft,
                                                float glyphPadding,
                                                float lineSpacing,
                                                float paragraphSpacing,
                                                HorizontalAlignment alignment,
                                                List<String> paragraphs,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                staticVal(upperLeft),
                glyphPadding,
                lineSpacing,
                paragraphSpacing,
                alignment,
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                Vertex upperLeft,
                                                float glyphPadding,
                                                float lineSpacing,
                                                HorizontalAlignment alignment,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                staticVal(upperLeft),
                glyphPadding,
                lineSpacing,
                0f,
                alignment,
                listOf(paragraph),
                z
        );
    }
}
