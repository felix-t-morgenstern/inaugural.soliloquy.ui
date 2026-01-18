package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.CENTER;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonDefinitionReader {
    private static final String PRESS_MOUSE_METHOD = "Button_pressMouse";
    private static final String MOUSE_OVER_METHOD = "Button_mouseOver";
    private static final String MOUSE_LEAVE_METHOD = "Button_mouseLeave";
    private static final String PRESS_KEY_METHOD = "Button_pressKey";
    private static final String RELEASE_KEY_METHOD = "Button_releaseKey";

    public static final int RECT_Z = 0;
    static final int SPRITE_Z = 1;
    static final int TEXT_Z = 2;

    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final ColorShiftDefinitionReader SHIFT_DEF_READER;
    @SuppressWarnings("rawtypes") private final ProviderAtTime NULL_PROVIDER;
    private final TextLineRenderer TEXT_LINE_RENDERER;
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> GET_CONSUMER;
    private final Function<String, Font> GET_FONT;
    private final Function<String, Integer> GET_TEX_ID;
    private final Supplier<Float> GET_WIDTH_TO_HEIGHT_RATIO;

    public ButtonDefinitionReader(ProviderDefinitionReader providerDefReader,
                                  ColorShiftDefinitionReader shiftDefReader,
                                  @SuppressWarnings("rawtypes") ProviderAtTime nullProvider,
                                  TextLineRenderer textLineRenderer,
                                  @SuppressWarnings("rawtypes")
                                  Function<String, Consumer> getConsumer,
                                  Function<String, Font> getFont,
                                  Function<String, Integer> getTexId,
                                  Supplier<Float> getWidthToHeightRatio) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
        SHIFT_DEF_READER = Check.ifNull(shiftDefReader, "shiftDefReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
        GET_FONT = Check.ifNull(getFont, "getFont");
        GET_TEX_ID = Check.ifNull(getTexId, "getTexId");
        GET_WIDTH_TO_HEIGHT_RATIO = Check.ifNull(getWidthToHeightRatio, "getWidthToHeightRatio");
    }

    public ComponentDefinition read(ButtonDefinition definition, long timestamp) {
        Check.ifNull(definition, "definition");

        var defaultOptions = new Options();
        var hoverOptions = new Options();
        var pressedOptions = new Options();

        var contentAndUuids = makeContent(
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
                contentAndUuids.content,
                definition.UUID
        )
                .withDimensions(
                        functionalProvider(
                                Component_setAndRetrieveDimensForComponentAndContentForProvider,
                                FloatBox.class
                        )
                                .withData(mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))
                )
                .withBindings(
                        false,
                        definition.keyEventPriority,
                        bindings
                )
                .withPrerenderHook(Button_setDimensForComponentAndContent)
                .withData(mapOf(
                        PRESS_ACTION,
                        definition.onPressId != null ? GET_CONSUMER.apply(definition.onPressId) :
                                null,
                        PRESS_SOUND_ID,
                        definition.pressSoundId,
                        MOUSE_OVER_SOUND_ID,
                        definition.Button_mouseOverSoundId,
                        MOUSE_LEAVE_SOUND_ID,
                        definition.mouseLeaveSoundId,
                        RELEASE_SOUND_ID,
                        definition.releaseSoundId,
                        RENDERABLE_OPTIONS_DEFAULT,
                        defaultOptions,
                        RENDERABLE_OPTIONS_HOVER,
                        hoverOptions,
                        RENDERABLE_OPTIONS_PRESSED,
                        pressedOptions
                ));
    }

    private record ContentAndUuids(Set<AbstractContentDefinition> content,
                                   UUID rectUuid,
                                   UUID spriteUuid,
                                   UUID textUuid) {
    }

    private ContentAndUuids makeContent(
            ButtonDefinition definition,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        System.out.println(">>> in makeContent");
        var content = Collections.<AbstractContentDefinition>setOf();
        UUID rectUuid = null;
        UUID spriteUuid = null;
        UUID textUuid = null;

        var makeRect = false;
        var unadjRectDimensFromDef = defaultIfNull(definition.RECT_DIMENS_DEF, null,
                d -> PROVIDER_DEF_READER.read(d, timestamp));
        System.out.println("unadjRectDimensFromDef = " + unadjRectDimensFromDef);
        if (definition.text != null) {
            System.out.println("(there is text...)");
            makeRect = true;
            // it has both a rect and text, and loc is defined by rect dimens XOR text rendering loc
            ProviderAtTime<Vertex> textUnadjRenderingLoc;
            var font = GET_FONT.apply(definition.fontId);
            var paddingHoriz = definition.textPaddingVertical / GET_WIDTH_TO_HEIGHT_RATIO.get();
            List<Integer> textItalicIndicesDefault =
                    defaultIfNull(definition.textItalicIndicesDefault, listOf());
            List<Integer> textBoldIndicesDefault =
                    defaultIfNull(definition.textBoldIndicesDefault, listOf());
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
                                    defaultIfNull(definition.textItalicIndicesHover,
                                            textItalicIndicesDefault),
                                    defaultIfNull(definition.textBoldIndicesHover,
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
                                    defaultIfNull(definition.textItalicIndicesPressed,
                                            textItalicIndicesDefault),
                                    defaultIfNull(definition.textBoldIndicesPressed,
                                            textBoldIndicesDefault),
                                    definition.textHeight
                            )
                            : textLineLengthDefault;
            if (unadjRectDimensFromDef != null) {
                // define the locs by rect dimens
                setAdjTextLocFromUnadjRectDimens(
                        definition.horizontalAlignment,
                        paddingHoriz,
                        definition.textHeight,
                        defaultOptions,
                        hoverOptions,
                        pressedOptions,
                        timestamp
                );
                setUnadjRectDimensFromDef(
                        unadjRectDimensFromDef,
                        defaultOptions,
                        hoverOptions,
                        pressedOptions
                );
            }
            else {
                // define the rect dimens by text rendering loc
                textUnadjRenderingLoc =
                        PROVIDER_DEF_READER.read(definition.TEXT_RENDERING_LOC_DEF, timestamp);
                setUnadjRectDimensFromUnadjTextLoc(
                        definition.UUID,
                        textUnadjRenderingLoc,
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
            var textLineDef = makeTextLineDef(
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            );
            content.add(textLineDef);
            textUuid = textLineDef.UUID;
        }
        else if (definition.RECT_DIMENS_DEF != null) {
            makeRect = true;
        }

        if (makeRect) {
            var rectDef = makeRectDef(
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            );
            content.add(rectDef);
            rectUuid = rectDef.UUID;
        }

        if (definition.spriteIdDefault != null) {
            var spriteDef = makeSpriteDef(
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            );
            content.add(spriteDef);
            spriteUuid = spriteDef.UUID;
        }

        return new ContentAndUuids(content, rectUuid, spriteUuid, textUuid);
    }

    private RectangleRenderableDefinition makeRectDef(
            ButtonDefinition definition,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
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
            var texDimensProviders = makeTexDimensProviders(definition.UUID, timestamp);
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

        return rectangle(functionalProvider(Button_rectDimensWithAdj, FloatBox.class)
                .withData(mapOf(
                        COMPONENT_UUID,
                        definition.UUID
                )), RECT_Z)
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

    private void setUnadjRectDimensFromUnadjTextLoc(
            UUID buttonUuid,
            ProviderAtTime<Vertex> unadjTextRenderingLocProvider,
            float lineLengthDefault,
            float lineLengthHover,
            float lineLengthPressed,
            float lineHeight,
            float textPaddingVert,
            float textPaddingHoriz,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        defaultOptions.unadjRectDimens = PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideUnadjRectDimensFromText, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                unadjTextRenderingLocProvider,
                                Button_provideUnadjRectDimensFromText_lineLength,
                                lineLengthDefault,
                                Button_provideUnadjRectDimensFromText_textHeight,
                                lineHeight,
                                Button_provideUnadjRectDimensFromText_textPaddingVert,
                                textPaddingVert,
                                Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                textPaddingHoriz
                        )), timestamp);
        hoverOptions.unadjRectDimens = PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideUnadjRectDimensFromText, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                unadjTextRenderingLocProvider,
                                Button_provideUnadjRectDimensFromText_lineLength,
                                lineLengthHover,
                                Button_provideUnadjRectDimensFromText_textHeight,
                                lineHeight,
                                Button_provideUnadjRectDimensFromText_textPaddingVert,
                                textPaddingVert,
                                Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                textPaddingHoriz
                        )), timestamp);
        pressedOptions.unadjRectDimens = PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideUnadjRectDimensFromText, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                Button_provideUnadjRectDimensFromText_unadjTextLoc,
                                unadjTextRenderingLocProvider,
                                Button_provideUnadjRectDimensFromText_lineLength,
                                lineLengthPressed,
                                Button_provideUnadjRectDimensFromText_textHeight,
                                lineHeight,
                                Button_provideUnadjRectDimensFromText_textPaddingVert,
                                textPaddingVert,
                                Button_provideUnadjRectDimensFromText_textPaddingHoriz,
                                textPaddingHoriz
                        )), timestamp);

        defaultOptions.unadjTextLoc =
                hoverOptions.unadjTextLoc =
                        pressedOptions.unadjTextLoc = unadjTextRenderingLocProvider;
    }

    private Pair<ProviderAtTime<Float>, ProviderAtTime<Float>> makeTexDimensProviders(
            UUID buttonUuid,
            long timestamp
    ) {
        var data = Collections.<String, Object>mapOf(
                COMPONENT_UUID,
                buttonUuid
        );

        var texWidth = PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideTexTileWidth, Float.class)
                        .withData(data),
                timestamp);
        var texHeight = PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideTexTileHeight, Float.class)
                        .withData(data),
                timestamp);

        return pairOf(texWidth, texHeight);
    }

    private SpriteRenderableDefinition makeSpriteDef(
            ButtonDefinition definition,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        var spriteShiftDefault = defaultIfNull(definition.spriteShiftDefaultDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));
        var spriteShiftHover = defaultIfNull(definition.spriteShiftHoverDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));
        var spriteShiftPressed = defaultIfNull(definition.spriteShiftPressedDef, null,
                d -> SHIFT_DEF_READER.read(d, timestamp));

        defaultOptions.spriteId = definition.spriteIdDefault;
        defaultOptions.unadjSpriteDimens =
                PROVIDER_DEF_READER.read(definition.spriteDimensDefaultDef, timestamp);
        defaultOptions.spriteShift = spriteShiftDefault;

        hoverOptions.spriteId = defaultIfNull(definition.spriteIdHover, null);
        hoverOptions.unadjSpriteDimens = defaultIfNull(defaultOptions.unadjSpriteDimens,
                defaultIfNull(definition.spriteDimensHoverDef, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp)));
        hoverOptions.spriteShift = spriteShiftHover;

        pressedOptions.spriteId = defaultIfNull(definition.spriteIdPressed, null);
        pressedOptions.unadjSpriteDimens = defaultIfNull(defaultOptions.unadjSpriteDimens,
                defaultIfNull(definition.spriteDimensPressedDef, null,
                        d -> PROVIDER_DEF_READER.read(d, timestamp)));
        pressedOptions.spriteShift = spriteShiftPressed;

        return sprite(
                definition.spriteIdDefault,
                functionalProvider(Button_spriteDimensWithAdj, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )),
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
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        var colorsDefault = getColorIndices(definition.textColorIndicesDefault, timestamp);
        var colorsHover = defaultIfNull(definition.textColorIndicesHover, null,
                d -> getColorIndices(d, timestamp));
        var colorsPressed = defaultIfNull(definition.textColorIndicesPressed, null,
                d -> getColorIndices(d, timestamp));

        List<Integer> italicsDefault = defaultIfNull(definition.textItalicIndicesDefault, listOf());
        var italicsHover =
                defaultIfNull(definition.textItalicIndicesHover, null);
        var italicsPressed =
                defaultIfNull(definition.textItalicIndicesPressed, null);

        List<Integer> boldsDefault = defaultIfNull(definition.textBoldIndicesDefault, listOf());
        var boldsHover =
                defaultIfNull(definition.textBoldIndicesHover, null);
        var boldsPressed =
                defaultIfNull(definition.textBoldIndicesPressed, null);

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
                PROVIDER_DEF_READER.read(functionalProvider(
                        Button_textLocWithAdj, Vertex.class
                ).withData(mapOf(
                        COMPONENT_UUID,
                        definition.UUID
                )), timestamp),
                staticVal(definition.textHeight),
                defaultIfNull(definition.horizontalAlignment, CENTER),
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

    private void setAdjTextLocFromUnadjRectDimens(
            HorizontalAlignment horizontalAlignment,
            float paddingHoriz,
            float textHeight,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        System.out.println(">>> in setUnadjTextLocProvidersFromUnadjRectDimensProvider");
        var providerDef =
                functionalProvider(Button_provideUnadjTextLocFromRect, Vertex.class)
                        .withData(mapOf(
                                Button_provideUnadjTextLocFromRect_horizontalAlignment,
                                horizontalAlignment,
                                Button_provideUnadjTextLocFromRect_paddingHoriz,
                                paddingHoriz,
                                Button_provideUnadjTextLocFromRect_textHeight,
                                textHeight
                        ));

        var provider = PROVIDER_DEF_READER.read(providerDef, timestamp);

        defaultOptions.unadjTextLoc =
                hoverOptions.unadjTextLoc =
                        pressedOptions.unadjTextLoc = provider;
    }

    private void setUnadjRectDimensFromDef(
            ProviderAtTime<FloatBox> unadjRectDimensFromDef,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions
    ) {
        defaultOptions.unadjRectDimens =
                hoverOptions.unadjRectDimens =
                        pressedOptions.unadjRectDimens = unadjRectDimensFromDef;
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
