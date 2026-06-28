package inaugural.soliloquy.ui.components.button;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinitionReader;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.ImageAssetRenderable;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.*;
import soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.Tools.*;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.TEXT_BLOCK_HEIGHT;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ButtonDefinitionReader {
    public static final int RECT_Z = 0;
    public static final int IMAGE_ASSET_Z = 1;
    public static final int TEXT_BLOCK_Z = 2;

    // Go ahead and play with this value; perhaps it should be configurable, perhaps there
    // shouldn't be any discrepancy at all, but when it's this low, it feels VERY negligible
    private static final float TEXT_LINE_LENGTH_ROUNDING_ERROR = 1.0001f;

    private final ProviderDefinitionReader PROVIDER_DEF_READER;
    private final RenderableDefinitionReader RENDERABLE_DEF_READER;
    private final TextBlockDefinitionReader TEXT_BLOCK_DEF_READER;
    private final TextMarkupParser MARKUP_PARSER;
    private final TextLineRenderer TEXT_LINE_RENDERER;
    private final Function<String, Font> GET_FONT;
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> GET_CONSUMER;
    private final Supplier<Float> GET_WIDTH_TO_HEIGHT_RATIO;

    public ButtonDefinitionReader(ProviderDefinitionReader providerDefReader,
                                  RenderableDefinitionReader renderableDefReader,
                                  TextBlockDefinitionReader textBlockDefReader,
                                  TextMarkupParser markupParser,
                                  TextLineRenderer textLineRenderer,
                                  @SuppressWarnings("rawtypes")
                                  Function<String, Consumer> getConsumer,
                                  Function<String, Font> getFont,
                                  Supplier<Float> getWidthToHeightRatio) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
        RENDERABLE_DEF_READER = Check.ifNull(renderableDefReader, "renderableDefReader");
        TEXT_BLOCK_DEF_READER = Check.ifNull(textBlockDefReader, "textBlockDefReader");
        MARKUP_PARSER = Check.ifNull(markupParser, "markupParser");
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
        GET_FONT = Check.ifNull(getFont, "getFont");
        GET_WIDTH_TO_HEIGHT_RATIO = Check.ifNull(getWidthToHeightRatio, "getWidthToHeightRatio");
    }

    public ComponentDefinition read(ButtonDefinition definition, long timestamp) {
        Check.ifNull(definition, "definition");

        var defaultOptions = new Options();
        var hoverOptions = new Options();
        var pressedOptions = new Options();

        var data = Collections.<String, Object>mapOf(
                PRESS_CONSUMER,
                definition.onPressId != null ? GET_CONSUMER.apply(definition.onPressId) : null,
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
        );

        var content = makeContent(
                definition,
                data,
                defaultOptions,
                hoverOptions,
                pressedOptions,
                timestamp
        );
        var prereadContent = content.FIRST;
        var readTextBlockDef = content.SECOND;

        KeyBindingDefinition[] bindings =
                defaultIfNullElseTransform(
                        definition.keyCodepoints,
                        k -> arrayOf(binding(
                                Button_pressKey,
                                Button_releaseKey,
                                k
                        )),
                        arrayOf()
                );

        var buttonDef = component(
                definition.z,
                definition.UUID,
                prereadContent
        )
                .withDimensions(
                        functionalProvider(
                                Button_getDimens,
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
                .withData(data)
                .withData(definition.DATA);

        if (readTextBlockDef != null) {
            buttonDef.withContent(readTextBlockDef);
        }

        return buttonDef;
    }

    private Pair<Set<Renderable>, ComponentDefinition> makeContent(
            ButtonDefinition definition,
            Map<String, Object> data,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        var prereadContent = Collections.<Renderable>setOf();
        ComponentDefinition readTextBlockDef = null;

        ProviderAtTime<FloatBox> unadjDimensFromRectDef;
        RectangleRenderable rectDefault;
        var adjRectDimens =
                (definition.rectDefaultDef != null || definition.textBlockDef != null) ?
                        makeRectAdjDimensProvider(definition.UUID, timestamp) :
                        null;
        if (definition.rectDefaultDef != null) {
            unadjDimensFromRectDef = supplyIfNull(
                    definition.rectDefaultDef.dimensProvider,
                    () -> PROVIDER_DEF_READER.read(definition.rectDefaultDef.dimensProviderDef,
                            timestamp)
            );
            rectDefault = makeRectAndReadRectDefs(
                    definition,
                    adjRectDimens,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            );
            prereadContent.add(rectDefault);

            // The rect unadj dimens provider MAY be defined by the text block, see below; in
            // that case, the value written here will simply be overwritten.
            data.put(RECT_UNADJ_DIMENS_PROVIDER, unadjDimensFromRectDef);
        }

        if (definition.textBlockDef != null) {
            definition.textBlockDef.z = TEXT_BLOCK_Z;

            definition.textBlockYPadding = supplyIfNull(
                    definition.textBlockYPadding,
                    () -> definition.textBlockXPadding / GET_WIDTH_TO_HEIGHT_RATIO.get()
            );

            var firstParagraphLength = 0f;
            var dimensComeFromRect = dimensComeFromRect(definition);
            var dynamicMaxLineLength = definition.textBlockDef.maxLineLength <= 0f;

            if (dynamicMaxLineLength || dimensComeFromRect) {
                var font = GET_FONT.apply(definition.textBlockDef.FONT_ID);

                // If you pass in a TextBlockDefinition with a maxLineLength less than zero, and
                // do not ACTUALLY have any paragraphs around which to wrap the Button's
                // dimensions, then this is entirely on you.
                @SuppressWarnings("OptionalGetWithoutIsPresent") var firstLine =
                        definition.textBlockDef.PARAGRAPHS.getFirst().lines().findFirst().get();
                var firstLineFormatted =
                        MARKUP_PARSER.formatSingleLine(firstLine, definition.UUID, timestamp);
                firstParagraphLength = TEXT_LINE_RENDERER.textLineLength(
                        firstLineFormatted.text(),
                        font,
                        definition.textBlockDef.glyphPadding,
                        firstLineFormatted.italicIndices(),
                        firstLineFormatted.boldIndices(),
                        definition.textBlockDef.LINE_HEIGHT
                ) * TEXT_LINE_LENGTH_ROUNDING_ERROR;
            }

            if (dynamicMaxLineLength) {
                definition.textBlockDef.maxLineLength = firstParagraphLength;
            }

            readTextBlockDef = TEXT_BLOCK_DEF_READER.read(definition.textBlockDef, timestamp);

            float textBlockHeight = getFromData(readTextBlockDef.data, TEXT_BLOCK_HEIGHT);

            if (dimensComeFromRect) {
                // define the TextBlock rendering loc by rect dimens
                var textBlockUnadjRenderingLoc = makeUnadjTextBlockLocProviderFromUnadjRectDimens(
                        definition.UUID,
                        definition.textBlockHorizontalAlignment,
                        definition.textBlockXPadding,
                        textBlockHeight,
                        firstParagraphLength,
                        timestamp
                );
                data.put(TEXT_BLOCK_UNADJ_LOC_PROVIDER, textBlockUnadjRenderingLoc);
            }
            else {
                // define the rect dimens by TextBlock rendering loc
                ProviderAtTime<Vertex> textBlockUnadjUpperLeftProvider;
                if (dynamicMaxLineLength &&
                        (definition.textBlockCenterProvider != null ||
                        definition.textBlockCenterProviderDef != null)
                ) {
                    var textBlockUnadjCenterProvider = supplyIfNull(
                            definition.textBlockCenterProvider,
                            () -> PROVIDER_DEF_READER.read(definition.textBlockCenterProviderDef,
                                    timestamp)
                    );
                    textBlockUnadjUpperLeftProvider = PROVIDER_DEF_READER.read(
                            functionalProvider(
                                    Button_provideCenteredUnadjTextBlockLocFromRect,
                                    Vertex.class
                            )
                                    .withData(mapOf(
                                            Button_provideCenteredUnadjTextBlockLocFromRect_textBlockCenterProvider,
                                            textBlockUnadjCenterProvider,
                                            Button_provideCenteredUnadjTextBlockLocFromRect_textBlockDimens,
                                            vertexOf(firstParagraphLength, textBlockHeight)
                                    )),
                            timestamp
                    );
                }
                else {
                    textBlockUnadjUpperLeftProvider =
                            getFromData(readTextBlockDef.data, COMPONENT_ORIGIN_PROVIDER);
                }
                data.put(TEXT_BLOCK_UNADJ_LOC_PROVIDER, textBlockUnadjUpperLeftProvider);

                var rectUnadjDimensProviderFromTextBlock = makeUnadjRectDimensFromUnadjTextBlockLoc(
                        definition.UUID,
                        textBlockUnadjUpperLeftProvider,
                        definition.textBlockDef.maxLineLength,
                        textBlockHeight,
                        definition.textBlockXPadding,
                        definition.textBlockYPadding,
                        timestamp
                );

                if (definition.rectDefaultDef == null) {
                    prereadContent.add(makePlainDefaultRectForTextBlock(
                            adjRectDimens,
                            defaultOptions,
                            hoverOptions,
                            pressedOptions,
                            timestamp
                    ));
                }

                data.put(RECT_UNADJ_DIMENS_PROVIDER, rectUnadjDimensProviderFromTextBlock);
            }

            var adjTextBlockRenderingLoc = PROVIDER_DEF_READER.read(
                    functionalProvider(Button_textBlockLocWithAdj, FloatBox.class)
                            .withData(mapOf(
                                    COMPONENT_UUID,
                                    definition.UUID
                            )),
                    timestamp
            );
            readTextBlockDef.withData(mapOf(
                    COMPONENT_ORIGIN_PROVIDER,
                    adjTextBlockRenderingLoc
            ));
        }

        if (definition.imageAssetDefault != null) {
            var imageAssetDefault = makeImageAssets(
                    definition,
                    defaultOptions,
                    hoverOptions,
                    pressedOptions,
                    timestamp
            );
            prereadContent.add(imageAssetDefault);
        }

        return pairOf(prereadContent, readTextBlockDef);
    }

    private boolean dimensComeFromRect(ButtonDefinition definition) {
        boolean dimensComeFromRect;
        if (definition.rectDefaultDef != null) {
            if (definition.rectDefinesTextDimens == null) {
                throw new IllegalArgumentException(
                        "ButtonDefinitionReader#read: if a Button has both a Rectangle and a " +
                                "TextBlock, you must explicitly state whether the Rectangle or " +
                                "TextBlock define the Button's dimensions, via the methods " +
                                "rectDefinesTextDimens or textDefinesRectDimens.");
            }
            dimensComeFromRect = definition.rectDefinesTextDimens;
        }
        else {
            if (falseIfNull(definition.rectDefinesTextDimens)) {
                throw new IllegalArgumentException(
                        "ButtonDefinitionReader#read: If the rectangle defines the dimensions of " +
                                "the text, it must be defined");
            }
            dimensComeFromRect = false;
        }
        return dimensComeFromRect;
    }

    private RectangleRenderable makePlainDefaultRectForTextBlock(
            ProviderAtTime<FloatBox> adjRectDimens,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        var plainRect = rectangle(adjRectDimens, RECT_Z);
        addMouseEvents(plainRect);
        var plainRectRenderable =
                (RectangleRenderable) RENDERABLE_DEF_READER.read(null, plainRect, timestamp);

        defaultOptions.rect = hoverOptions.rect = pressedOptions.rect = plainRectRenderable;

        return plainRectRenderable;
    }

    private RectangleRenderable makeRectAndReadRectDefs(
            ButtonDefinition definition,
            ProviderAtTime<FloatBox> adjRectDimensProvider,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        setOf(
                definition.rectDefaultDef,
                definition.rectHoverDef,
                definition.rectPressedDef
        ).forEach(def -> {
            if (def != null) {
                addMouseEvents(def);
                def.z = RECT_Z;
                def.dimensProvider = adjRectDimensProvider;
            }
        });

        RectangleRenderable defaultRect =
                RENDERABLE_DEF_READER.read(null, definition.rectDefaultDef, timestamp);
        defaultOptions.rect = defaultRect;

        if (definition.rectHoverDef != null) {
            hoverOptions.rect =
                    RENDERABLE_DEF_READER.read(null, definition.rectHoverDef, timestamp);
        }
        else {
            hoverOptions.rect = defaultRect;
        }

        if (definition.rectPressedDef != null) {
            pressedOptions.rect =
                    RENDERABLE_DEF_READER.read(null, definition.rectPressedDef, timestamp);
        }
        else {
            pressedOptions.rect = defaultRect;
        }

        return defaultRect;
    }

    private void addMouseEvents(AbstractRenderableWithMouseEventsDefinition def) {
        def.onPress(mapOf(LEFT_MOUSE_BUTTON, Button_pressMouse))
                .onMouseOver(Button_mouseOver)
                .onMouseLeave(Button_mouseLeave);
    }

    private ProviderAtTime<FloatBox> makeRectAdjDimensProvider(UUID buttonUuid, long timestamp) {
        return PROVIDER_DEF_READER.read(functionalProvider(
                Button_rectDimensWithAdj,
                FloatBox.class
        ).withData(mapOf(COMPONENT_UUID, buttonUuid)), timestamp);
    }

    private ProviderAtTime<FloatBox> makeUnadjRectDimensFromUnadjTextBlockLoc(
            UUID buttonUuid,
            ProviderAtTime<Vertex> textBlockUnadjUpperLeftProvider,
            float maxLineLength,
            float lineHeight,
            float textPaddingHoriz,
            float textPaddingVert,
            long timestamp
    ) {
        return PROVIDER_DEF_READER.read(
                functionalProvider(Button_provideUnadjRectDimensFromTextBlock, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                Button_provideUnadjRectDimensFromTextBlock_unadjTextBlockUpperLeft,
                                textBlockUnadjUpperLeftProvider,
                                Button_provideUnadjRectDimensFromTextBlock_textBlockDimens,
                                vertexOf(maxLineLength, lineHeight),
                                Button_provideUnadjRectDimensFromTextBlock_textPaddingHoriz,
                                textPaddingHoriz,
                                Button_provideUnadjRectDimensFromTextBlock_textPaddingVert,
                                textPaddingVert
                        )), timestamp);
    }

    private ImageAssetRenderable makeImageAssets(
            ButtonDefinition definition,
            Options defaultOptions,
            Options hoverOptions,
            Options pressedOptions,
            long timestamp
    ) {
        var adjDimensProvider = PROVIDER_DEF_READER.read(
                functionalProvider(Button_imageAssetDimensWithAdj, FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                definition.UUID
                        )),
                timestamp
        );

        prepImageAssetDef(definition.imageAssetDefault);
        defaultOptions.imageAsset =
                RENDERABLE_DEF_READER.read(null, definition.imageAssetDefault, timestamp);
        defaultOptions.unadjImageAssetDimens = supplyIfNull(
                definition.imageAssetDefault.dimensProvider,
                () -> PROVIDER_DEF_READER.read(definition.imageAssetDefault.dimensProviderDef,
                        timestamp)
        );
        defaultOptions.imageAsset.setRenderingDimensionsProvider(adjDimensProvider);

        // Button DOES support image assets on hovering and pressing, even if one is absent for
        // the default state; e.g., a button that sparkles when the mouse cursor hovers

        if (definition.imageAssetHover != null) {
            prepImageAssetDef(definition.imageAssetHover);
            hoverOptions.imageAsset =
                    RENDERABLE_DEF_READER.read(null, definition.imageAssetHover, timestamp);
            hoverOptions.unadjImageAssetDimens = supplyIfNull(
                    definition.imageAssetHover.dimensProvider,
                    () -> defaultIfNullElseTransform(
                            definition.imageAssetHover.dimensProviderDef,
                            d -> PROVIDER_DEF_READER.read(d, timestamp),
                            defaultOptions.unadjImageAssetDimens
                    )
            );
            hoverOptions.imageAsset.setRenderingDimensionsProvider(adjDimensProvider);
        }

        if (definition.imageAssetPressed != null) {
            prepImageAssetDef(definition.imageAssetPressed);
            pressedOptions.imageAsset =
                    RENDERABLE_DEF_READER.read(null, definition.imageAssetPressed, timestamp);
            pressedOptions.unadjImageAssetDimens = supplyIfNull(
                    definition.imageAssetPressed.dimensProvider,
                    () -> PROVIDER_DEF_READER.read(definition.imageAssetPressed.dimensProviderDef,
                            timestamp)
            );
            pressedOptions.imageAsset.setRenderingDimensionsProvider(adjDimensProvider);
        }

        return defaultOptions.imageAsset;
    }

    private void prepImageAssetDef(AbstractImageAssetRenderableDefinition imageAssetDef) {
        imageAssetDef.z = IMAGE_ASSET_Z;
        addMouseEvents(imageAssetDef);
    }

    private ProviderAtTime<Vertex> makeUnadjTextBlockLocProviderFromUnadjRectDimens(
            UUID buttonUuid,
            HorizontalAlignment horizontalAlignment,
            float paddingHoriz,
            float textHeight,
            float lineLength,
            long timestamp
    ) {
        var providerDef =
                functionalProvider(Button_provideUnadjTextBlockLocFromRect, Vertex.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                Button_provideUnadjTextBlockLocFromRect_horizontalAlignment,
                                horizontalAlignment,
                                Button_provideUnadjTextBlockLocFromRect_paddingHoriz,
                                paddingHoriz,
                                Button_provideUnadjTextBlockLocFromRect_textBlockHeight,
                                textHeight,
                                Button_provideUnadjTextBlockLocFromRect_lineLength,
                                lineLength
                        ));

        return PROVIDER_DEF_READER.read(providerDef, timestamp);
    }
}
