package inaugural.soliloquy.ui;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.tools.timing.TimestampValidator;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.assets.FontStyleInfo;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.TextMarkupParserImpl.TextMarkupParserMethods.TextMarkupParserMethods_provideCustomColor;
import static inaugural.soliloquy.ui.TextMarkupParserImpl.TextMarkupParserMethods.TextMarkupParserMethods_provideCustomColor_dataKey;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class TextMarkupParserImpl implements TextMarkupParser {
    private final static char ESC_CHAR = '\\';
    private final static char SPACE = ' ';
    private final static char CARRIAGE_RETURN = '\n';
    private final static char STYLE_MARKER = '*';
    private final static char TAG_START = '[';
    private final static char TAG_END = ']';
    private final static char COMMAND_END = '/';
    private final static char ASSIGNMENT_OPERATOR = '=';
    private final static String COLOR_COMMAND = "color";
    private final static int RGB_CHANNELS = 3;
    private final static int RGBA_CHANNELS = 4;
    private final static String SEPARATOR = ",";

    private final ProviderAtTime<Color> DEFAULT_COLOR_PROVIDER;
    private final Map<Integer, ProviderAtTime<Color>> DEFAULT_COLOR_INDICES;
    private final Map<String, Color> COLOR_PRESETS;
    private final TextLineRenderer TEXT_LINE_RENDERER;
    private final ProviderDefinitionReader PROVIDER_DEFINITION_READER;
    private final Function<Color, ProviderAtTime<Color>> MAKE_STATIC_PROVIDER;
    private final TimestampValidator TIMESTAMP_VALIDATOR;

    public TextMarkupParserImpl(Color defaultColor,
                                Map<Set<String>, Color> colorPresets,
                                TextLineRenderer textLineRenderer,
                                ProviderDefinitionReader providerDefinitionReader,
                                Function<Color, ProviderAtTime<Color>> makeStaticProvider,
                                TimestampValidator timestampValidator) {
        MAKE_STATIC_PROVIDER = Check.ifNull(makeStaticProvider, "makeStaticProvider");
        DEFAULT_COLOR_PROVIDER =
                MAKE_STATIC_PROVIDER.apply(Check.ifNull(defaultColor, "defaultColor"));
        DEFAULT_COLOR_INDICES = mapOf(0, DEFAULT_COLOR_PROVIDER);
        COLOR_PRESETS = mapOf();
        Check.ifNull(colorPresets, "colorPresets").forEach(
                (key, value) -> Check.ifNull(key, "key within colorPresets")
                        .forEach(s -> addColorPreset(s, value)));
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
        PROVIDER_DEFINITION_READER =
                Check.ifNull(providerDefinitionReader, "providerDefinitionReader");
        TIMESTAMP_VALIDATOR = Check.ifNull(timestampValidator, "timestampValidator");
    }

    @Override
    public LineFormatting formatSingleLine(String rawText,
                                           UUID containingComponentUuid,
                                           long timestamp) {
        return processRawText(rawText, false, null, 0f, 0f, 0f, containingComponentUuid, timestamp,
                "formatSingleLine")[0];
    }

    @Override
    public LineFormatting[] formatMultiline(
            String rawText,
            Font font,
            float paddingBetweenGlyphs,
            float lineHeight,
            float maxLength,
            UUID containingComponentUuid,
            long timestamp
    ) throws IllegalArgumentException {
        if (Strings.isNullOrEmpty(rawText)) {
            return arrayOf(
                    new LineFormatting("", mapOf(DEFAULT_COLOR_INDICES), listOf(), listOf(), null));
        }

        return processRawText(rawText, true, font, maxLength, paddingBetweenGlyphs, lineHeight,
                containingComponentUuid, timestamp, "formatMultiline");
    }

    // Join me on this journey.
    private LineFormatting[] processRawText(String rawText,
                                            boolean isMultiline,
                                            Font font,
                                            float maxLength,
                                            float paddingBetweenGlyphs,
                                            float lineHeight,
                                            UUID containingComponentUuid,
                                            long timestamp,
                                            String methodName) {
        // TODO: Test whether validator was called
        TIMESTAMP_VALIDATOR.validateTimestamp(timestamp);

        var colors = mapOf(DEFAULT_COLOR_INDICES);

        if (Strings.isNullOrEmpty(rawText)) {
            return arrayOf(new LineFormatting("", colors, listOf(), listOf(), null));
        }

        var results = Collections.<LineFormatting>listOf();
        var lineLength = 0f;
        var paddingToRender = paddingBetweenGlyphs * lineHeight;
        var mostRecentSpaceIndex = 0;
        var colorAtMostRecentSpaceIndex = DEFAULT_COLOR_PROVIDER;
        var italicAtMostRecentSpaceIndex = false;
        var boldAtMostRecentSpaceIndex = false;

        var textBuilder = new StringBuilder();
        var italicIndices = Collections.<Integer>listOf();
        var boldIndices = Collections.<Integer>listOf();

        var indexAdjustment = 0;
        var escapeNextChar = false;
        var escapeThisChar = false;
        var nextStyleMarkerShouldBoldface = false;
        Integer italicIndexToAdd = null;
        var withinTag = false;
        StringBuilder tagContentsBuilder = null;
        ProviderAtTime<Color> customColorProviderApplied = null;
        var isItalic = false;
        var isBold = false;
        for (var i = 0; i < rawText.length(); i++) {
            var aChar = rawText.charAt(i);
            if (withinTag) {
                if (aChar == TAG_END) {
                    withinTag = false;
                    var tagContents = tagContentsBuilder.toString().toLowerCase();
                    if (String.format("%s%s", COMMAND_END, COLOR_COMMAND).equals(tagContents)) {
                        if (customColorProviderApplied != null) {
                            colors.put(i - indexAdjustment, DEFAULT_COLOR_PROVIDER);
                            customColorProviderApplied = null;
                        }
                    }
                    else {
                        var assignmentOperator = tagContents.indexOf(ASSIGNMENT_OPERATOR);
                        if (assignmentOperator > 0) {
                            var command = tagContents.substring(0, assignmentOperator);
                            if (COLOR_COMMAND.equals(command)) {
                                var colorVal = tagContents.substring(assignmentOperator + 1);
                                var presetColor = COLOR_PRESETS.get(colorVal);
                                int colonIndex;
                                if (presetColor != null) {
                                    var presetProvider = MAKE_STATIC_PROVIDER.apply(presetColor);
                                    colors.put(i - indexAdjustment, presetProvider);
                                    customColorProviderApplied = presetProvider;
                                }
                                else if ((colonIndex = colorVal.indexOf(':')) >= 0 &&
                                        colorVal.subSequence(0, colonIndex).equals("p")) {
                                    var providerKey = colorVal.substring(colonIndex + 1);
                                    customColorProviderApplied =
                                            makeColorProviderFromCustomProvider(
                                                    providerKey,
                                                    containingComponentUuid,
                                                    timestamp
                                            );
                                    colors.put(i - indexAdjustment, customColorProviderApplied);
                                }
                                else {
                                    customColorProviderApplied =
                                            processColorValFromChannels(colorVal, colors,
                                                    i - indexAdjustment, methodName);
                                }
                            }
                        }
                    }
                }
                else {
                    tagContentsBuilder.append(aChar);
                }
                indexAdjustment++;
            }
            else {
                if (escapeNextChar) {
                    escapeThisChar = true;
                    escapeNextChar = false;
                }
                if (aChar == ESC_CHAR && !escapeThisChar) {
                    escapeNextChar = true;
                    indexAdjustment++;
                }
                else if (aChar == TAG_START && !escapeThisChar) {
                    withinTag = true;
                    tagContentsBuilder = new StringBuilder();
                    indexAdjustment++;
                }
                else if (aChar == STYLE_MARKER && !escapeThisChar) {
                    if (nextStyleMarkerShouldBoldface) {
                        boldIndices.add(i - indexAdjustment++);
                        italicIndexToAdd = null;
                        nextStyleMarkerShouldBoldface = false;
                        isBold = !isBold;
                    }
                    else {
                        italicIndexToAdd = i - indexAdjustment++;
                        nextStyleMarkerShouldBoldface = true;
                    }
                    isItalic = !isItalic;
                }
                else {
                    if (italicIndexToAdd != null) {
                        italicIndices.add(italicIndexToAdd);
                    }
                    italicIndexToAdd = null;
                    nextStyleMarkerShouldBoldface = false;
                    if (isMultiline) {
                        boolean makeNewLine;
                        if (aChar == CARRIAGE_RETURN) {
                            makeNewLine = true;
                        }
                        else {
                            if (!textBuilder.isEmpty()) {
                                lineLength += paddingToRender;
                            }
                            var fontStyle = getStyle(font, isItalic, isBold);
                            var glyphWidth =
                                    TEXT_LINE_RENDERER.getGlyphWidth(aChar, fontStyle, lineHeight);
                            lineLength += glyphWidth;
                            makeNewLine = lineLength >= maxLength;
                        }
                        if (makeNewLine) {
                            String prevLineText;
                            String newLineText;
                            int trimAdjustment;
                            var newColors = mapOf(pairOf(0, aChar == CARRIAGE_RETURN ?
                                    defaultIfNull(customColorProviderApplied,
                                            DEFAULT_COLOR_PROVIDER) :
                                    colorAtMostRecentSpaceIndex));
                            List<Integer> newItalicIndices = (aChar == CARRIAGE_RETURN ? isItalic :
                                    italicAtMostRecentSpaceIndex) ? listOf(0) : listOf();
                            List<Integer> newBoldIndices = (aChar == CARRIAGE_RETURN ? isBold :
                                    boldAtMostRecentSpaceIndex) ? listOf(0) : listOf();
                            if (mostRecentSpaceIndex > 0 && aChar != CARRIAGE_RETURN) {
                                prevLineText = textBuilder.substring(0, mostRecentSpaceIndex);

                                final int prevLineEnd = mostRecentSpaceIndex;
                                results.add(new LineFormatting(
                                        prevLineText,
                                        mapOfStream(colors.entrySet().stream()
                                                .filter(e -> e.getKey() < prevLineEnd)),
                                        listOf(italicIndices.stream().filter(j -> j < prevLineEnd)),
                                        listOf(boldIndices.stream().filter(j -> j < prevLineEnd)),
                                        null
                                ));

                                newLineText = textBuilder.substring(
                                        Math.min(mostRecentSpaceIndex, textBuilder.length()));
                                newLineText += aChar;
                                var trimNewLine = newLineText.charAt(0) == SPACE;
                                trimAdjustment = (trimNewLine ? 1 : 0);
                                newLineText = newLineText.substring(trimAdjustment);
                                lineLength = TEXT_LINE_RENDERER.textLineLength(
                                        newLineText,
                                        font,
                                        paddingBetweenGlyphs,
                                        listOf(),
                                        listOf(),
                                        lineHeight
                                );
                                textBuilder = new StringBuilder(newLineText);
                                var newLineStartInPrevLine = mostRecentSpaceIndex + trimAdjustment;

                                newColors.putAll(mapOf(colors.entrySet().stream()
                                        .filter(e -> e.getKey() >= prevLineEnd).map(e -> pairOf(
                                                Math.max(0, e.getKey() - newLineStartInPrevLine),
                                                e.getValue()))));

                                newItalicIndices.addAll(
                                        listOf(italicIndices.stream().filter(j -> j >= prevLineEnd)
                                                .map(j -> Math.max(0,
                                                        j - newLineStartInPrevLine))));

                                newBoldIndices.addAll(
                                        listOf(boldIndices.stream().filter(j -> j >= prevLineEnd)
                                                .map(j -> Math.max(0,
                                                        j - newLineStartInPrevLine))));
                            }
                            else {
                                prevLineText = textBuilder.toString();
                                newLineText = "" + (aChar == CARRIAGE_RETURN ? "" : aChar);
                                lineLength = 0f;
                                textBuilder = new StringBuilder(newLineText);
                                trimAdjustment = 1;

                                results.add(new LineFormatting(
                                        prevLineText,
                                        colors,
                                        italicIndices,
                                        boldIndices,
                                        null
                                ));
                            }
                            colors = newColors;
                            italicIndices = newItalicIndices;
                            boldIndices = newBoldIndices;

                            mostRecentSpaceIndex = 0;
                            colorAtMostRecentSpaceIndex =
                                    defaultIfNull(customColorProviderApplied,
                                            DEFAULT_COLOR_PROVIDER);
                            italicAtMostRecentSpaceIndex = isItalic;
                            boldAtMostRecentSpaceIndex = isBold;
                            indexAdjustment = i - newLineText.length() + trimAdjustment;
                        }
                        else {
                            textBuilder.append(aChar);
                        }
                    }
                    else {
                        textBuilder.append(aChar);
                    }
                    if (aChar == SPACE) {
                        mostRecentSpaceIndex = i - indexAdjustment;
                        colorAtMostRecentSpaceIndex =
                                defaultIfNull(customColorProviderApplied, DEFAULT_COLOR_PROVIDER);
                        italicAtMostRecentSpaceIndex = isItalic;
                        boldAtMostRecentSpaceIndex = isBold;
                    }
                }
                escapeThisChar = false;
            }
        }
        if (italicIndexToAdd != null) {
            italicIndices.add(italicIndexToAdd);
        }

        results.add(new LineFormatting(
                textBuilder.toString(),
                colors,
                italicIndices,
                boldIndices,
                null
        ));

        return results.toArray(LineFormatting[]::new);
    }
    // It just works.

    private FontStyleInfo getStyle(Font font, boolean isItalic, boolean isBold) {
        if (isItalic) {
            if (isBold) {
                return font.boldItalic();
            }
            else {
                return font.italic();
            }
        }
        else {
            if (isBold) {
                return font.bold();
            }
            else {
                return font.plain();
            }
        }
    }

    private ProviderAtTime<Color> processColorValFromChannels(
            String colorVal,
            Map<Integer, ProviderAtTime<Color>> colorProviderIndices,
            int index,
            String methodName
    ) {
        try {
            var channels = colorVal.split(SEPARATOR);
            if (channels.length == RGB_CHANNELS || channels.length == RGBA_CHANNELS) {
                ProviderAtTime<Color> colorProvider;
                var red = Integer.parseInt(channels[0]);
                var green = Integer.parseInt(channels[1]);
                var blue = Integer.parseInt(channels[2]);
                if (channels.length == RGB_CHANNELS) {
                    colorProvider = MAKE_STATIC_PROVIDER.apply(new Color(red, green, blue));
                }
                else {
                    var alpha = Integer.parseInt(channels[3]);
                    colorProvider = MAKE_STATIC_PROVIDER.apply(new Color(red, green, blue, alpha));
                }
                colorProviderIndices.put(index, colorProvider);
                return colorProvider;
            }
            else {
                throw new Exception();
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                    "TextMarkupParserImpl." + methodName + ": invalid color val at index " + index +
                            " (\"" + colorVal + "\")");
        }
    }

    @Override
    public void addColorPreset(String name, Color color) throws IllegalArgumentException {
        COLOR_PRESETS.put(
                Check.ifNullOrEmpty(name, name).toLowerCase(),
                Check.ifNull(color, "color")
        );
    }

    private ProviderAtTime<Color> makeColorProviderFromCustomProvider(String dataKey,
                                                                      UUID containingComponentUuid,
                                                                      long timestamp) {
        return PROVIDER_DEFINITION_READER.read(
                functionalProvider(TextMarkupParserMethods_provideCustomColor, Color.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                containingComponentUuid,
                                TextMarkupParserMethods_provideCustomColor_dataKey,
                                dataKey
                        )),
                timestamp
        );
    }

    public static class TextMarkupParserMethods {
        private final Function<UUID, Component> GET_COMPONENT;

        public TextMarkupParserMethods(Function<UUID, Component> getComponent) {
            GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        }

        public final static String TextMarkupParserMethods_provideCustomColor = "TextMarkupParserMethods_provideCustomColor";
        public final static String TextMarkupParserMethods_provideCustomColor_dataKey = "TextMarkupParserMethods_provideCustomColor_dataKey";

        public Color TextMarkupParserMethods_provideCustomColor(FunctionalProvider.Inputs inputs) {
            var containingComponent = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
            ProviderAtTime<Color> customProvider = getFromData(containingComponent,
                    getFromData(inputs, TextMarkupParserMethods_provideCustomColor_dataKey));
            return customProvider.provide(inputs.timestamp());
        }
    }
}
