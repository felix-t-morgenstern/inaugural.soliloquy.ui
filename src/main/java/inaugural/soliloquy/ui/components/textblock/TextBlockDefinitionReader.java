package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.Tools.supplyIfNull;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockDefinitionReader {
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
        // TODO: Test null UL def and provider (e.g. Button)
        var blockUpperLeft = supplyIfNull(
                definition.upperLeftProvider,
                () -> PROVIDER_DEF_READER.read(
                        defaultIfNull(definition.upperLeftProviderDef, staticVal(WINDOW_ORIGIN)),
                        timestamp
                )
        );

        // TODO: Test default alignment assignment
        definition.horizontalAlignment = defaultIfNull(definition.horizontalAlignment, LEFT);

        var componentDef = component(definition.z, definition.UUID)
                .withDimensions(
                        functionalProvider(
                                TextBlock_getDimens,
                                FloatBox.class
                        )
                                .withData(mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))
                );

        var font = GET_FONT.apply(definition.FONT_ID);
        if (font == null) {
            throw new IllegalArgumentException(
                    "TextBlockDefinitionReader#read: definition contains illegal font id (" +
                            definition.FONT_ID + ")");
        }

        var parsedParagraphs = Collections.<TextMarkupParser.LineFormatting[]>listOf();
        for (var p : definition.PARAGRAPHS) {
            parsedParagraphs.add(PARSER.formatMultiline(p, font, definition.glyphPadding,
                    definition.LINE_HEIGHT, definition.maxLineLength, definition.UUID, timestamp));
        }

        var totalHeightThusFar = 0f;
        var firstParagraph = true;
        for (var paragraph : parsedParagraphs) {
            if (firstParagraph) {
                firstParagraph = false;
            }
            else {
                totalHeightThusFar += definition.paragraphSpacing;
            }
            var firstLineInParagraph = true;
            for (var line : paragraph) {
                if (firstLineInParagraph) {
                    firstLineInParagraph = false;
                }
                else {
                    totalHeightThusFar += definition.lineSpacing;
                }
                componentDef.withContent(textLine(
                        font,
                        line.text(),
                        makeLineVertexProvider(
                                componentDef.UUID,
                                definition.maxLineLength,
                                definition.horizontalAlignment,
                                totalHeightThusFar,
                                timestamp
                        ),
                        definition.LINE_HEIGHT,
                        definition.horizontalAlignment,
                        definition.glyphPadding,
                        0
                )
                        .withBold(line.boldIndices())
                        .withItalics(line.italicIndices())
                        .withColorProviders(line.colorIndices()));
                totalHeightThusFar += definition.LINE_HEIGHT;
            }
        }

        var dataFromReadingDef = Collections.<String, Object>mapOf(
                COMPONENT_ORIGIN_PROVIDER,
                blockUpperLeft,
                TEXT_BLOCK_WIDTH,
                definition.maxLineLength,
                TEXT_BLOCK_HEIGHT,
                totalHeightThusFar
        );

        dataFromReadingDef.putAll(definition.DATA);

        componentDef.withData(dataFromReadingDef);

        return componentDef;
    }

    private ProviderAtTime<Vertex> makeLineVertexProvider(UUID textBlockId,
                                                          float maxLineLength,
                                                          HorizontalAlignment horizontalAlignment,
                                                          float topOffset,
                                                          long timestamp) {
        var leftOffset = switch (horizontalAlignment) {
            case LEFT -> 0f;
            case CENTER -> maxLineLength / 2f;
            case RIGHT -> maxLineLength;
        };

        return PROVIDER_DEF_READER.read(functionalProvider(
                TextBlock_provideTextLineRenderingLoc, Vertex.class
        ).withData(mapOf(
                COMPONENT_UUID,
                textBlockId,
                TEXT_BLOCK_LINE_OFFSET,
                vertexOf(leftOffset, topOffset)
        )), timestamp);
    }
}
