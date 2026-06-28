package inaugural.soliloquy.ui.components.textblock;

import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.List;
import java.util.Map;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockDefinition extends AbstractContentDefinition {
    public final String FONT_ID;
    public final float LINE_HEIGHT;
    public final List<String> PARAGRAPHS;
    public final Map<String, Object> DATA;

    public float maxLineLength;
    public ProviderAtTime<Vertex> upperLeftProvider;
    public AbstractProviderDefinition<Vertex> upperLeftProviderDef;
    public float glyphPadding;
    public float lineSpacing;
    public float paragraphSpacing;
    public HorizontalAlignment horizontalAlignment;

    private TextBlockDefinition(String fontId,
                                float lineHeight,
                                float maxLineLength,
                                ProviderAtTime<Vertex> upperLeftProvider,
                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                List<String> paragraphs,
                                int z) {
        super(z, randomUUID());
        FONT_ID = fontId;
        LINE_HEIGHT = lineHeight;
        this.maxLineLength = maxLineLength;
        this.upperLeftProvider = upperLeftProvider;
        this.upperLeftProviderDef = upperLeftProviderDef;
        PARAGRAPHS = paragraphs;
        DATA = mapOf();
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                ProviderAtTime<Vertex> upperLeftProvider,
                                                List<String> paragraphs,
                                                int z) {
        return new TextBlockDefinition(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProvider,
                null,
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                ProviderAtTime<Vertex> upperLeftProvider,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProvider,
                listOf(paragraph),
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                                List<String> paragraphs,
                                                int z) {
        return new TextBlockDefinition(
                fontId,
                lineHeight,
                maxLineLength,
                null,
                upperLeftProviderDef,
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                AbstractProviderDefinition<Vertex> upperLeftProviderDef,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProviderDef,
                listOf(paragraph),
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                Vertex upperLeft,
                                                List<String> paragraphs,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                staticVal(upperLeft),
                paragraphs,
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                Vertex upperLeft,
                                                String paragraph,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                staticVal(upperLeft),
                listOf(paragraph),
                z
        );
    }

    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                Vertex upperLeft,
                                                String paragraph) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                staticVal(upperLeft),
                listOf(paragraph),
                0
        );
    }

    /**
     * (The no-location method exists for the convenience of components which will place their own
     * rendering locations, e.g., Button)
     */
    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                List<String> paragraphs,
                                                int z) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                (AbstractProviderDefinition<Vertex>) null,
                paragraphs,
                z
        );
    }

    /**
     * (The no-location method exists for the convenience of components which will place their own
     * rendering locations, e.g., Button)
     */
    public static TextBlockDefinition textBlock(
            String fontId,
            float lineHeight,
            float maxLineLength,
            AbstractProviderDefinition<Vertex> upperLeftProviderDef,
            String paragraph
    ) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                upperLeftProviderDef,
                listOf(paragraph),
                0
        );
    }

    /**
     * (The no-location method exists for the convenience of components which will place their own
     * rendering locations, e.g., ContentColumn)
     */
    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                List<String> paragraphs) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                (AbstractProviderDefinition<Vertex>) null,
                paragraphs,
                0
        );
    }

    /**
     * (The no-location method exists for the convenience of components which will place their own
     * rendering locations, e.g., Button)
     */
    public static TextBlockDefinition textBlock(String fontId,
                                                float lineHeight,
                                                float maxLineLength,
                                                String paragraph) {
        return textBlock(
                fontId,
                lineHeight,
                maxLineLength,
                listOf(paragraph)
        );
    }

    public TextBlockDefinition withGlyphPadding(float glyphPadding) {
        this.glyphPadding = glyphPadding;

        return this;
    }

    public TextBlockDefinition withLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;

        return this;
    }

    public TextBlockDefinition withParagraphSpacing(float paragraphSpacing) {
        this.paragraphSpacing = paragraphSpacing;

        return this;
    }

    public TextBlockDefinition withHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;

        return this;
    }

    /**
     * Repeat calls of this method will not overwrite all prior data, only those objects in data
     * which share one of the new keys.
     */
    public TextBlockDefinition withData(Map<String, Object> data) {
        DATA.putAll(data);

        return this;
    }
}
