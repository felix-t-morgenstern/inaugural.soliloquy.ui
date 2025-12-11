package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.provideIfNull;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.mapVals;
import static inaugural.soliloquy.ui.components.ComponentMethods.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.ComponentMethods.LAST_TIMESTAMP;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class TextBlockDefinitionReader {
    private final static String HEIGHT = "HEIGHT";

    private final TextMarkupParser PARSER;
    private final Function<String, Font> GET_FONT;
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public TextBlockDefinitionReader(TextMarkupParser parser,
                                     Function<String, Font> getFont,
                                     ProviderDefinitionReader providerDefReader) {
        PARSER = Check.ifNull(parser, "parser");
        GET_FONT = Check.ifNull(getFont, "getFont");
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ComponentDefinition read(TextBlockDefinition definition, long timestamp) {
        var componentDef = component(definition.Z);

        var font = GET_FONT.apply(definition.FONT_ID);
        if (font == null) {
            throw new IllegalArgumentException(
                    "TextBlockDefinitionReader#read: definition contains illegal font id (" +
                            definition.FONT_ID + ")");
        }

        var blockUpperLeft = provideIfNull(definition.UPPER_LEFT_PROVIDER,
                () -> PROVIDER_DEF_READER.read(Check.ifNull(definition.UPPER_LEFT_PROVIDER_DEF,
                        "definition.UPPER_LEFT_PROVIDER_DEF"), timestamp));

        var parsedParagraphs = definition.PARAGRAPHS.stream()
                .map(p -> PARSER.formatMultiline(p, font, definition.GLYPH_PADDING,
                        definition.LINE_HEIGHT, definition.MAX_LINE_LENGTH)).toList();

        var yOffset = 0f;
        var firstParagraph = true;
        for (var paragraph : parsedParagraphs) {
            if (firstParagraph) {
                firstParagraph = false;
            }
            else {
                yOffset += definition.PARAGRAPH_SPACING;
            }
            var firstLineInParagraph = true;
            for (var line : paragraph) {
                if (firstLineInParagraph) {
                    firstLineInParagraph = false;
                }
                else {
                    yOffset += definition.LINE_SPACING;
                }
                componentDef.withContent(textLine(
                        font,
                        line.text(),
                        makeLineVertexProvider(componentDef.UUID, blockUpperLeft, yOffset,
                                timestamp),
                        definition.LINE_HEIGHT,
                        definition.ALIGNMENT,
                        definition.GLYPH_PADDING,
                        0
                )
                        .withBold(line.boldIndices())
                        .withItalics(line.italicIndices())
                        .withColorDefs(
                                mapVals(line.colorIndices(), StaticProviderDefinition::staticVal)));
                yOffset += definition.LINE_HEIGHT;
            }
        }

        componentDef.withData(mapOf(
                HEIGHT,
                yOffset,
                LAST_TIMESTAMP,
                timestamp - 1
        ));

        return componentDef;
    }

    private ProviderAtTime<Vertex> makeLineVertexProvider(UUID componentId,
                                                          ProviderAtTime<Vertex> blockUpperLeft,
                                                          float topOffset,
                                                          long timestamp) {
        return PROVIDER_DEF_READER.read(functionalProvider(
                TextBlock_provideTextRenderingLoc, Vertex.class
        ).withData(mapOf(
                COMPONENT_UUID,
                componentId,
                TextBlock_blockUpperLeftProvider,
                blockUpperLeft,
                TextBlock_topOffset,
                topOffset
        )), timestamp);
    }
}
