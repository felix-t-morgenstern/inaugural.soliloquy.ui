package inaugural.soliloquy.ui.components.textbox;

import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;

import java.util.List;

public class TextBoxDefinition {
    public final String FONT_ID;
    public final float LINE_HEIGHT;
    public final float MAX_LINE_LENGTH;
    public final float PADDING_BETWEEN_GLYPHS;
    public final float LINE_SPACING;
    public final float PARAGRAPH_SPACING;
    public final HorizontalAlignment ALIGNMENT;
    public final List<String> PARAGRAPHS;

    private TextBoxDefinition(String fontId,
                              float lineHeight,
                              float maxLineLength,
                              float paddingBetweenGlyphs,
                              float lineSpacing,
                              float paragraphSpacing,
                              HorizontalAlignment alignment,
                              List<String> paragraphs) {
        FONT_ID = fontId;
        LINE_HEIGHT = lineHeight;
        MAX_LINE_LENGTH = maxLineLength;
        PADDING_BETWEEN_GLYPHS = paddingBetweenGlyphs;
        LINE_SPACING = lineSpacing;
        PARAGRAPH_SPACING = paragraphSpacing;
        ALIGNMENT = alignment;
        PARAGRAPHS = paragraphs;
    }

    public static TextBoxDefinition textBox(String fontId,
                                            float lineHeight,
                                            float maxLineLength,
                                            float paddingBetweenGlyphs,
                                            float lineSpacing,
                                            float paragraphSpacing,
                                            HorizontalAlignment alignment,
                                            List<String> paragraphs) {
        return new TextBoxDefinition(
                fontId,
                lineHeight,
                maxLineLength,
                paddingBetweenGlyphs,
                lineSpacing,
                paragraphSpacing,
                alignment,
                paragraphs
        );
    }
}
