package inaugural.soliloquy.ui;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.assets.FontStyleInfo;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

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

    private final Color DEFAULT_COLOR;
    private final Map<Integer, Color> DEFAULT_COLORS;
    private final Map<String, Color> COLOR_PRESETS;
    private final TextLineRenderer TEXT_LINE_RENDERER;

    public TextMarkupParserImpl(Color defaultColor,
                                Map<String, Color> colorPresets,
                                TextLineRenderer textLineRenderer) {
        DEFAULT_COLOR = Check.ifNull(defaultColor, "defaultColor");
        DEFAULT_COLORS = mapOf(0, DEFAULT_COLOR);
        COLOR_PRESETS = mapOf(Check.ifNull(colorPresets, "colorPresets").entrySet().stream()
                .map(e -> pairOf(e.getKey().toLowerCase(), e.getValue())));
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
    }

    @Override
    public LineFormatting formatSingleLine(String rawText) {
        return processRawText(rawText, false, null, 0f, 0f, 0f, "formatSingleLine")[0];
    }

    @Override
    public LineFormatting[] formatMultiline(String rawText,
                                            Font font,
                                            float paddingBetweenGlyphs,
                                            float lineHeight,
                                            float maxLength) throws IllegalArgumentException {
        if (Strings.isNullOrEmpty(rawText)) {
            return arrayOf(new LineFormatting("", mapOf(DEFAULT_COLORS), listOf(), listOf()));
        }

        return processRawText(rawText, true, font, maxLength, paddingBetweenGlyphs, lineHeight,
                "formatMultiline");
    }

    // Join me on this journey.
    private LineFormatting[] processRawText(String rawText,
                                            boolean isMultiline,
                                            Font font,
                                            float maxLength,
                                            float paddingBetweenGlyphs,
                                            float lineHeight,
                                            String methodName) {
        var colors = mapOf(DEFAULT_COLORS);

        if (Strings.isNullOrEmpty(rawText)) {
            return arrayOf(new LineFormatting("", colors, listOf(), listOf()));
        }

        var results = Collections.<LineFormatting>listOf();
        var lineLength = 0f;
        var baseGlyphPadding = 0f;
        if (isMultiline) {
            baseGlyphPadding = paddingBetweenGlyphs * lineHeight;
        }
        var mostRecentSpaceIndex = 0;
        var colorAtMostRecentSpaceIndex = DEFAULT_COLOR;
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
        Color customColorApplied = null;
        var isItalic = false;
        var isBold = false;
        for (var i = 0; i < rawText.length(); i++) {
            var aChar = rawText.charAt(i);
            if (withinTag) {
                if (aChar == TAG_END) {
                    withinTag = false;
                    var tagContents = tagContentsBuilder.toString().toLowerCase();
                    if (String.format("%s%s", COMMAND_END, COLOR_COMMAND).equals(tagContents)) {
                        if (customColorApplied != null) {
                            colors.put(i - indexAdjustment, DEFAULT_COLOR);
                            customColorApplied = null;
                        }
                    }
                    else {
                        var assignmentOperator = tagContents.indexOf(ASSIGNMENT_OPERATOR);
                        if (assignmentOperator > 0) {
                            var command = tagContents.substring(0, assignmentOperator);
                            if (COLOR_COMMAND.equals(command)) {
                                var colorVal = tagContents.substring(assignmentOperator + 1);
                                var preset = COLOR_PRESETS.get(colorVal);
                                if (preset != null) {
                                    colors.put(i - indexAdjustment, preset);
                                    customColorApplied = preset;
                                }
                                else {
                                    customColorApplied =
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
                                lineLength += baseGlyphPadding;
                            }
                            var fontStyle = getStyle(font, isItalic, isBold);
                            var glyphWidth =
                                    TEXT_LINE_RENDERER.getGlyphWidth(aChar, fontStyle, lineHeight);
                            lineLength += glyphWidth;
                            makeNewLine = lineLength > maxLength;
                        }
                        if (makeNewLine) {
                            String prevLineText;
                            String newLineText;
                            var newColors = mapOf(pairOf(0, aChar == CARRIAGE_RETURN ?
                                    defaultIfNull(customColorApplied, DEFAULT_COLOR) :
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
                                        listOf(boldIndices.stream().filter(j -> j < prevLineEnd))
                                ));

                                var newLineIndex = mostRecentSpaceIndex + 1;
                                newLineText = textBuilder.substring(newLineIndex);
                                var newLineTextTrimmed = newLineText.trim();
                                lineLength = TEXT_LINE_RENDERER.textLineLength(
                                        newLineTextTrimmed,
                                        font,
                                        paddingBetweenGlyphs,
                                        listOf(),
                                        listOf(),
                                        lineHeight
                                );
                                textBuilder = new StringBuilder(newLineText);
                                var charsTrimmed =
                                        newLineText.length() - newLineTextTrimmed.length();
                                var newLineStartInPrevLine = newLineIndex + charsTrimmed;

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
                                newLineText = "";
                                lineLength = 0f;
                                textBuilder = new StringBuilder();

                                results.add(new LineFormatting(
                                        prevLineText,
                                        colors,
                                        italicIndices,
                                        boldIndices
                                ));
                            }
                            colors = newColors;
                            italicIndices = newItalicIndices;
                            boldIndices = newBoldIndices;

                            mostRecentSpaceIndex = 0;
                            colorAtMostRecentSpaceIndex =
                                    defaultIfNull(customColorApplied, DEFAULT_COLOR);
                            italicAtMostRecentSpaceIndex = isItalic;
                            boldAtMostRecentSpaceIndex = isBold;
                            indexAdjustment = i - newLineText.length();
                        }
                        if (aChar != CARRIAGE_RETURN) {
                            textBuilder.append(aChar);
                        }
                        else {
                            indexAdjustment++;
                        }
                    }
                    else {
                        textBuilder.append(aChar);
                    }
                    if (aChar == SPACE) {
                        mostRecentSpaceIndex = i - indexAdjustment;
                        colorAtMostRecentSpaceIndex =
                                defaultIfNull(customColorApplied, DEFAULT_COLOR);
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
                boldIndices
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

    private Color processColorValFromChannels(String colorVal,
                                              Map<Integer, Color> colorIndices,
                                              int index,
                                              String methodName) {
        try {
            var channels = colorVal.split(SEPARATOR);
            if (channels.length == RGB_CHANNELS || channels.length == RGBA_CHANNELS) {
                Color color;
                var red = Integer.parseInt(channels[0]);
                var green = Integer.parseInt(channels[1]);
                var blue = Integer.parseInt(channels[2]);
                if (channels.length == RGB_CHANNELS) {
                    color = new Color(red, green, blue);
                }
                else {
                    var alpha = Integer.parseInt(channels[3]);
                    color = new Color(red, green, blue, alpha);
                }
                colorIndices.put(index, color);
                return color;
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
}
