package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.definitions.content.*;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.io.graphics.renderables.TextJustification.CENTER;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonDefinitionReader {
    private static final String PRESS_MOUSE_METHOD = "pressMouse_Button";
    private static final String MOUSE_OVER_METHOD = "mouseOver_Button";
    private static final String MOUSE_LEAVE_METHOD = "mouseLeave_Button";
    private static final String PRESS_KEY_METHOD = "pressKey_Button";
    private static final String RELEASE_KEY_METHOD = "releaseKey_Button";

    static final int RECT_Z = 0;
    static final int SPRITE_Z = 1;
    static final int TEXT_Z = 2;

    private static final String TEXT_LOC_FROM_RECT_DIMENS_METHOD =
            "provideTextRenderingLocFromRect_Button";
    private static final String RECT_DIMENS_FROM_TEXT_LOC_METHOD =
            "provideRectDimensFromText_Button";
    private static final String TEX_WIDTH_FROM_RECT_DIMENS_METHOD = "provideTexTileWidth_Button";
    private static final String TEX_HEIGHT_FROM_RECT_DIMENS_METHOD = "provideTexTileHeight_Button";

    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final ShiftDefinitionReader SHIFT_DEF_READER;
    @SuppressWarnings("rawtypes") private final ProviderAtTime NULL_PROVIDER;
    private final Supplier<Long> GET_TIMESTAMP;
    private final TextLineRenderer TEXT_LINE_RENDERER;
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;
    private final Function<String, Font> GET_FONT;
    private final Function<String, Integer> GET_TEX_ID;
    private final Supplier<Float> GET_WIDTH_TO_HEIGHT_RATIO;

    public ButtonDefinitionReader(ProviderDefinitionReader providerDefReader,
                                  ShiftDefinitionReader shiftDefReader,
                                  @SuppressWarnings("rawtypes") ProviderAtTime nullProvider,
                                  Supplier<Long> getTimestamp,
                                  TextLineRenderer textLineRenderer,
                                  @SuppressWarnings("rawtypes") Function<String, Action> getAction,
                                  Function<String, Font> getFont,
                                  Function<String, Integer> getTexId,
                                  Supplier<Float> getWidthToHeightRatio) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
        SHIFT_DEF_READER = Check.ifNull(shiftDefReader, "shiftDefReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
        GET_TIMESTAMP = Check.ifNull(getTimestamp, "getTimestamp");
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
        GET_ACTION = Check.ifNull(getAction, "getAction");
        GET_FONT = Check.ifNull(getFont, "getFont");
        GET_TEX_ID = Check.ifNull(getTexId, "getTexId");
        GET_WIDTH_TO_HEIGHT_RATIO = Check.ifNull(getWidthToHeightRatio, "getWidthToHeightRatio");
    }

    public ComponentDefinition read(ButtonDefinition definition) {
        Check.ifNull(definition, "definition");

        var timestamp = GET_TIMESTAMP.get();

        var defaultOptions = new RenderableOptions();
        var hoverOptions = new RenderableOptions();
        var pressedOptions = new RenderableOptions();

        var content = makeContent(
                definition,
                defaultOptions,
                hoverOptions,
                pressedOptions,
                timestamp
        );

        KeyBindingDefinition[] bindings =
                defaultIfNull(definition.keyCodepoints, arrayOf(), k -> arrayOf(binding(
                        PRESS_KEY_METHOD,
                        RELEASE_KEY_METHOD,
                        k
                )));

        return component(
                definition.Z,
                content
        )
                .withBindings(
                        false,
                        definition.keyEventPriority,
                        bindings
                )
                .withData(mapOf(
                        PRESS_ACTION,
                        definition.onPressId != null ? GET_ACTION.apply(definition.onPressId) :
                                null,
                        PRESS_SOUND_ID,
                        definition.pressSoundId,
                        MOUSE_OVER_SOUND_ID,
                        definition.mouseOverSoundId,
                        MOUSE_LEAVE_SOUND_ID,
                        definition.mouseLeaveSoundId,
                        RELEASE_SOUND_ID,
                        definition.releaseSoundId,
                        DEFAULT_RENDERABLE_OPTIONS,
                        defaultOptions,
                        HOVER_RENDERABLE_OPTIONS,
                        hoverOptions,
                        PRESSED_RENDERABLE_OPTIONS,
                        pressedOptions
                ));
    }

    private Set<AbstractContentDefinition> makeContent(
            ButtonDefinition definition,
            RenderableOptions defaultOptions,
            RenderableOptions hoverOptions,
            RenderableOptions pressedOptions,
            long timestamp
    ) {
        var content = Collections.<AbstractContentDefinition>setOf();

        ProviderAtTime<FloatBox> rectDimensProvider = null;
        var rectDimensFromDef = defaultIfNull(definition.RECT_DIMENS_DEF, null,
                d -> PROVIDER_DEF_READER.read(d, timestamp));
        if (definition.text != null) {
            // it has both a rect and text, and loc is defined by rect dimens XOR text rendering loc
            ProviderAtTime<Vertex> textRenderingLoc;
            var font = GET_FONT.apply(definition.fontId);
            var paddingHoriz = definition.textPaddingVertical / GET_WIDTH_TO_HEIGHT_RATIO.get();
            List<Integer> textItalicIndicesDefault =
                    indicesOrDefault(definition.textItalicIndicesDefault, listOf());
            List<Integer> textBoldIndicesDefault =
                    indicesOrDefault(definition.textBoldIndicesDefault, listOf());
            var textLineLengthDefault = TEXT_LINE_RENDERER.textLineLength(
                    definition.text,
                    font,
                    definition.textGlyphPadding,
                    textItalicIndicesDefault,
                    textBoldIndicesDefault,
                    definition.textHeight
            );
            var textLineLengthHover =
                    (definition.textItalicIndicesHover != null ||
                            definition.textBoldIndicesHover != null) ?
                            TEXT_LINE_RENDERER.textLineLength(
                                    definition.text,
                                    font,
                                    definition.textGlyphPadding,
                                    indicesOrDefault(definition.textItalicIndicesHover,
                                            textItalicIndicesDefault),
                                    indicesOrDefault(definition.textBoldIndicesHover,
                                            textBoldIndicesDefault),
                                    definition.textHeight
                            )
                            : textLineLengthDefault;
            var textLineLengthPressed =
                    (definition.textItalicIndicesPressed != null ||
                            definition.textBoldIndicesPressed != null) ?
                            TEXT_LINE_RENDERER.textLineLength(
                                    definition.text,
                                    font,
                                    definition.textGlyphPadding,
                                    indicesOrDefault(definition.textItalicIndicesPressed,
                                            textItalicIndicesDefault),
                                    indicesOrDefault(definition.textBoldIndicesPressed,
                                            textBoldIndicesDefault),
                                    definition.textHeight
                            )
                            : textLineLengthDefault;
            if (rectDimensFromDef != null) {
                // define the locs by rect dimens
                rectDimensProvider = rectDimensFromDef;
                textRenderingLoc = textRenderingLocProviderFromRectDimensProvider(
                        rectDimensProvider,
                        definition.textJustification,
                        paddingHoriz,
                        definition.textHeight,
                        timestamp
                );
            }
            else {
                // define the rect dimens by text rendering loc
                textRenderingLoc =
                        PROVIDER_DEF_READER.read(definition.TEXT_RENDERING_LOC_DEF, timestamp);
                rectDimensProvider = rectDimensProviderFromTextRenderingLocProvider(
                        textRenderingLoc,
                        textLineLengthDefault,
                        textLineLengthHover,
                        textLineLengthPressed,
                        definition.textHeight,
                        definition.textPaddingVertical,
                        paddingHoriz,
                        defaultOptions,
                        hoverOptions,
                        pressedOptions,
                        timestamp
                );
            }
            content.add(makeTextLineDef(
                    definition,
                    textRenderingLoc,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            ));
        }
        if (rectDimensFromDef != null) {
            // it has only a rect, no text
            rectDimensProvider = rectDimensFromDef;
        }

        if (rectDimensProvider != null) {
            content.add(makeRectDef(
                    rectDimensProvider,
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            ));
        }

        if (definition.spriteIdDefault != null) {
            content.add(makeSpriteDef(
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            ));
        }

        return content;
    }

    private List<Integer> indicesOrDefault(int[] providedIndices, List<Integer> defaultIndices) {
        return defaultIfNull(providedIndices, defaultIndices, Collections::listInts);
    }

    private RectangleRenderableDefinition makeRectDef(
            ProviderAtTime<FloatBox> rectDimensProvider,
            ButtonDefinition definition,
            RenderableOptions defaultOptions,
            RenderableOptions hoverOptions,
            RenderableOptions pressedOptions,
            long timestamp
    ) {
        var bgColorTopLeftDefault =
                getNullProviderIfNull(definition.bgColorTopLeftDefault, timestamp);
        var bgColorTopRightDefault =
                getNullProviderIfNull(definition.bgColorTopRightDefault, timestamp);
        var bgColorBottomLeftDefault =
                getNullProviderIfNull(definition.bgColorBottomLeftDefault, timestamp);
        var bgColorBottomRightDefault =
                getNullProviderIfNull(definition.bgColorBottomRightDefault, timestamp);
        var bgTexProviderDefault = getNullProviderIfNull(
                getBgTexProviderDef(definition.bgTexProviderDefault, definition.bgTexRelLocDefault),
                timestamp);
        ProviderAtTime<Float> texWidthProvider;
        ProviderAtTime<Float> texHeightProvider;
        if (bgTexProviderDefault != NULL_PROVIDER) {
            var texDimensProviders = makeTexDimensProviders(rectDimensProvider, timestamp);
            texWidthProvider = texDimensProviders.FIRST;
            texHeightProvider = texDimensProviders.SECOND;
        }
        else {
            //noinspection unchecked
            texWidthProvider = texHeightProvider = NULL_PROVIDER;
        }

        var bgTexProviderHover = defaultIfNull(
                getBgTexProviderDef(definition.bgTexProviderHover, definition.bgTexRelLocHover),
                null, d -> PROVIDER_DEF_READER.read(d, timestamp));
        var bgTexProviderPressed = defaultIfNull(
                getBgTexProviderDef(definition.bgTexProviderPressed, definition.bgTexRelLocHover),
                null, d -> PROVIDER_DEF_READER.read(d, timestamp));

        defaultOptions.rectDimens = rectDimensProvider;
        defaultOptions.bgColorTopLeft = bgColorTopLeftDefault;
        defaultOptions.bgColorTopRight = bgColorTopRightDefault;
        defaultOptions.bgColorBottomLeft = bgColorBottomLeftDefault;
        defaultOptions.bgColorBottomRight = bgColorBottomRightDefault;
        defaultOptions.bgTexProvider = bgTexProviderDefault;

        hoverOptions.bgColorTopLeft =
                defaultIfNull(definition.bgColorTopLeftHover, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        hoverOptions.bgColorTopRight =
                defaultIfNull(definition.bgColorTopRightHover, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        hoverOptions.bgColorBottomLeft =
                defaultIfNull(definition.bgColorBottomLeftHover, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        hoverOptions.bgColorBottomRight =
                defaultIfNull(definition.bgColorBottomRightHover, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        hoverOptions.bgTexProvider = bgTexProviderHover;

        pressedOptions.bgColorTopLeft =
                defaultIfNull(definition.bgColorTopLeftPressed, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        pressedOptions.bgColorTopRight =
                defaultIfNull(definition.bgColorTopRightPressed, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        pressedOptions.bgColorBottomLeft =
                defaultIfNull(definition.bgColorBottomLeftPressed, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        pressedOptions.bgColorBottomRight =
                defaultIfNull(definition.bgColorBottomRightPressed, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        pressedOptions.bgTexProvider = bgTexProviderPressed;

        return rectangle(rectDimensProvider, RECT_Z)
                .withColors(
                        bgColorTopLeftDefault,
                        bgColorTopRightDefault,
                        bgColorBottomLeftDefault,
                        bgColorBottomRightDefault
                )
                .withTexture(
                        bgTexProviderDefault,
                        texWidthProvider,
                        texHeightProvider
                )
                .onPress(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD))
                .onMouseOver(MOUSE_OVER_METHOD)
                .onMouseLeave(MOUSE_LEAVE_METHOD);
    }

    private ProviderAtTime<FloatBox> rectDimensProviderFromTextRenderingLocProvider(
            ProviderAtTime<Vertex> textRenderingLocProvider,
            float lineLengthDefault,
            float lineLengthHover,
            float lineLengthPressed,
            float lineHeight,
            float textPaddingVert,
            float textPaddingHoriz,
            RenderableOptions defaultOptions,
            RenderableOptions hoverOptions,
            RenderableOptions pressedOptions,
            long timestamp
    ) {
        var providerDefault = PROVIDER_DEF_READER.read(
                functionalProvider(RECT_DIMENS_FROM_TEXT_LOC_METHOD, FloatBox.class)
                        .withData(mapOf(
                                provideRectDimensFromText_Button_textRenderingLocProvider,
                                textRenderingLocProvider,
                                provideRectDimensFromText_Button_lineLength,
                                lineLengthDefault,
                                provideRectDimensFromText_Button_textHeight,
                                lineHeight,
                                provideRectDimensFromText_Button_textPaddingVert,
                                textPaddingVert,
                                provideRectDimensFromText_Button_textPaddingHoriz,
                                textPaddingHoriz
                        )), timestamp);
        var providerHover =
                lineLengthHover == lineLengthDefault ? null : PROVIDER_DEF_READER.read(
                        functionalProvider(RECT_DIMENS_FROM_TEXT_LOC_METHOD, FloatBox.class)
                                .withData(mapOf(
                                        provideRectDimensFromText_Button_textRenderingLocProvider,
                                        textRenderingLocProvider,
                                        provideRectDimensFromText_Button_lineLength,
                                        lineLengthHover,
                                        provideRectDimensFromText_Button_textHeight,
                                        lineHeight,
                                        provideRectDimensFromText_Button_textPaddingVert,
                                        textPaddingVert,
                                        provideRectDimensFromText_Button_textPaddingHoriz,
                                        textPaddingHoriz
                                )), timestamp);
        var providerPressed =
                lineLengthPressed == lineLengthDefault ? null : PROVIDER_DEF_READER.read(
                        functionalProvider(RECT_DIMENS_FROM_TEXT_LOC_METHOD, FloatBox.class)
                                .withData(mapOf(
                                        provideRectDimensFromText_Button_textRenderingLocProvider,
                                        textRenderingLocProvider,
                                        provideRectDimensFromText_Button_lineLength,
                                        lineLengthPressed,
                                        provideRectDimensFromText_Button_textHeight,
                                        lineHeight,
                                        provideRectDimensFromText_Button_textPaddingVert,
                                        textPaddingVert,
                                        provideRectDimensFromText_Button_textPaddingHoriz,
                                        textPaddingHoriz
                                )), timestamp);

        defaultOptions.rectDimens = providerDefault;
        hoverOptions.rectDimens = providerHover;
        pressedOptions.rectDimens = providerPressed;

        return providerDefault;
    }

    private Pair<ProviderAtTime<Float>, ProviderAtTime<Float>> makeTexDimensProviders(
            ProviderAtTime<FloatBox> rectDimensProvider,
            long timestamp
    ) {
        var data = Collections.<String, Object>mapOf(
                provideTexTileDimens_Button_rectDimensProvider,
                rectDimensProvider
        );

        var texWidth = PROVIDER_DEF_READER.read(
                functionalProvider(TEX_WIDTH_FROM_RECT_DIMENS_METHOD, Float.class)
                        .withData(data),
                timestamp);
        var texHeight = PROVIDER_DEF_READER.read(
                functionalProvider(TEX_HEIGHT_FROM_RECT_DIMENS_METHOD, Float.class)
                        .withData(data),
                timestamp);

        return pairOf(texWidth, texHeight);
    }

    private SpriteRenderableDefinition makeSpriteDef(
            ButtonDefinition definition,
            RenderableOptions defaultOptions,
            RenderableOptions hoverOptions,
            RenderableOptions pressedOptions,
            long timestamp
    ) {
        var spriteDimensDefault =
                PROVIDER_DEF_READER.read(definition.spriteDimensDefaultDef, timestamp);
        var spriteDimensHover =
                defaultIfNull(definition.spriteDimensHoverDef, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));
        var spriteDimensPressed =
                defaultIfNull(definition.spriteDimensPressedDef, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp));

        var spriteShiftDefault = defaultIfNull(definition.spriteShiftDefaultDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));
        var spriteShiftHover = defaultIfNull(definition.spriteShiftHoverDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));
        var spriteShiftPressed = defaultIfNull(definition.spriteShiftPressedDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));

        defaultOptions.spriteId = definition.spriteIdDefault;
        defaultOptions.spriteDimens = spriteDimensDefault;
        defaultOptions.spriteShift = spriteShiftDefault;

        hoverOptions.spriteId = defaultIfNull(definition.spriteIdHover, null);
        hoverOptions.spriteDimens = spriteDimensHover;
        hoverOptions.spriteShift = spriteShiftHover;

        pressedOptions.spriteId = defaultIfNull(definition.spriteIdPressed, null);
        pressedOptions.spriteDimens = spriteDimensPressed;
        pressedOptions.spriteShift = spriteShiftPressed;

        return sprite(
                definition.spriteIdDefault,
                definition.spriteDimensDefaultDef,
                SPRITE_Z
        )
                .onPress(mapOf(LEFT_MOUSE_BUTTON, PRESS_MOUSE_METHOD))
                .onMouseOver(MOUSE_OVER_METHOD)
                .onMouseLeave(MOUSE_LEAVE_METHOD)
                .withColorShifts(
                        defaultIfNull(
                                spriteShiftDefault,
                                Collections.<ColorShift>arrayOf(),
                                Collections::arrayOf
                        )
                );
    }

    private TextLineRenderableDefinition makeTextLineDef(
            ButtonDefinition definition,
            ProviderAtTime<Vertex> textRenderingLoc,
            RenderableOptions defaultOptions,
            RenderableOptions hoverOptions,
            RenderableOptions pressedOptions,
            long timestamp
    ) {
        var colorsDefault = getColorIndices(definition.textColorIndicesDefault, timestamp);
        var colorsHover = defaultIfNull(definition.textColorIndicesHover, null,
                d -> getColorIndices(d, timestamp));
        var colorsPressed = defaultIfNull(definition.textColorIndicesPressed, null,
                d -> getColorIndices(d, timestamp));

        var italicsDefault = listInts(definition.textItalicIndicesDefault);
        var italicsHover =
                defaultIfNull(definition.textItalicIndicesHover, null, Collections::listInts);
        var italicsPressed =
                defaultIfNull(definition.textItalicIndicesPressed, null, Collections::listInts);

        var boldsDefault = listInts(definition.textBoldIndicesDefault);
        var boldsHover =
                defaultIfNull(definition.textBoldIndicesHover, null, Collections::listInts);
        var boldsPressed =
                defaultIfNull(definition.textBoldIndicesPressed, null, Collections::listInts);

        defaultOptions.textColors = colorsDefault;
        defaultOptions.italics = italicsDefault;
        defaultOptions.bolds = boldsDefault;

        hoverOptions.textColors = colorsHover;
        hoverOptions.italics = italicsHover;
        hoverOptions.bolds = boldsHover;

        pressedOptions.textColors = colorsPressed;
        pressedOptions.italics = italicsPressed;
        pressedOptions.bolds = boldsPressed;

        return textLine(
                definition.fontId,
                staticVal(definition.text),
                textRenderingLoc,
                staticVal(definition.textHeight),
                defaultIfNull(definition.textJustification, CENTER),
                definition.textGlyphPadding,
                TEXT_Z
        )
                .withColorProviders(colorsDefault)
                .withItalics(definition.textItalicIndicesDefault)
                .withBold(definition.textBoldIndicesDefault);
    }

    private Map<Integer, ProviderAtTime<Color>> getColorIndices(
            Map<Integer, AbstractProviderDefinition<Color>> colorDefs,
            long timestamp
    ) {
        return defaultIfNull(colorDefs, mapOf(), c -> c.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey,
                        def -> PROVIDER_DEF_READER.read(def.getValue(), timestamp))));
    }

    private ProviderAtTime<Vertex> textRenderingLocProviderFromRectDimensProvider(
            ProviderAtTime<FloatBox> rectDimensProvider,
            TextJustification textJustification,
            float paddingHoriz,
            float textHeight,
            long timestamp
    ) {
        var providerDef =
                functionalProvider(TEXT_LOC_FROM_RECT_DIMENS_METHOD, Vertex.class)
                        .withData(mapOf(
                                provideTextRenderingLocFromRect_Button_textJustification,
                                textJustification,
                                provideTextRenderingLocFromRect_Button_rectDimensProvider,
                                rectDimensProvider,
                                provideTextRenderingLocFromRect_Button_paddingHoriz,
                                paddingHoriz,
                                provideTextRenderingLocFromRect_Button_textHeight,
                                textHeight
                        ));

        return PROVIDER_DEF_READER.read(providerDef, timestamp);
    }

    private <T> ProviderAtTime<T> getNullProviderIfNull(AbstractProviderDefinition<T> def,
                                                        long timestamp) {
        if (def == null) {
            //noinspection unchecked
            return NULL_PROVIDER;
        }
        return PROVIDER_DEF_READER.read(def, timestamp);
    }

    private AbstractProviderDefinition<Integer> getBgTexProviderDef(
            AbstractProviderDefinition<Integer> def,
            String relLoc
    ) {
        if (def != null) {
            return def;
        }

        if (relLoc != null && !relLoc.isEmpty()) {
            var texId = GET_TEX_ID.apply(relLoc);
            return staticVal(texId);
        }

        return null;
    }
}
