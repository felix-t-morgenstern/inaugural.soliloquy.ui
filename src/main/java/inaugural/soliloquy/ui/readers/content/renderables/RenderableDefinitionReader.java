package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.AbstractContentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.Tools.falseIfNull;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static soliloquy.specs.io.input.keyboard.KeyBinding.keyBinding;

public class RenderableDefinitionReader extends AbstractContentDefinitionReader {
    private final RasterizedLineSegmentRenderableDefinitionReader RASTERIZED_LINE_READER;
    private final AntialiasedLineSegmentRenderableDefinitionReader ANTIALIASED_LINE_READER;
    private final RectangleRenderableDefinitionReader RECTANGLE_READER;
    private final TriangleRenderableDefinitionReader TRIANGLE_READER;
    private final SpriteRenderableDefinitionReader SPRITE_READER;
    private final ImageAssetSetRenderableDefinitionReader IMAGE_ASSET_SET_READER;
    private final FiniteAnimationRenderableDefinitionReader FINITE_ANIMATION_READER;
    private final TextLineRenderableDefinitionReader TEXT_LINE_READER;

    private final ComponentFactory COMPONENT_FACTORY;

    @SuppressWarnings("rawtypes") private final Function<String, Consumer> GET_CONSUMER;
    @SuppressWarnings("rawtypes")
    private final Map<Class, BiFunction<AbstractContentDefinition, Long, ComponentDefinition>>
            CUSTOM_READERS;
    private final ProviderAtTime<FloatBox> WHOLE_SCREEN_PROVIDER;
    private final int DEFAULT_KEY_EVENT_PRIORITY;

    public RenderableDefinitionReader(
            RasterizedLineSegmentRenderableDefinitionReader rasterizedLineReader,
            AntialiasedLineSegmentRenderableDefinitionReader antialiasedLineReader,
            RectangleRenderableDefinitionReader rectangleReader,
            TriangleRenderableDefinitionReader triangleReader,
            SpriteRenderableDefinitionReader spriteReader,
            ImageAssetSetRenderableDefinitionReader imageAssetSetReader,
            FiniteAnimationRenderableDefinitionReader finiteAnimationReader,
            TextLineRenderableDefinitionReader textLineReader,
            ComponentFactory componentFactory,
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") Function<String, Consumer> getConsumer,
            ProviderAtTime<FloatBox> wholeScreenProvider, int defaultKeyEventPriority) {
        super(providerReader);
        RASTERIZED_LINE_READER = Check.ifNull(rasterizedLineReader, "rasterizedLineReader");
        ANTIALIASED_LINE_READER = Check.ifNull(antialiasedLineReader, "antialiasedLineReader");
        RECTANGLE_READER = Check.ifNull(rectangleReader, "rectangleReader");
        TRIANGLE_READER = Check.ifNull(triangleReader, "triangleReader");
        SPRITE_READER = Check.ifNull(spriteReader, "spriteReader");
        IMAGE_ASSET_SET_READER = Check.ifNull(imageAssetSetReader, "imageAssetSetReader");
        FINITE_ANIMATION_READER = Check.ifNull(finiteAnimationReader, "finiteAnimationReader");
        TEXT_LINE_READER = Check.ifNull(textLineReader, "textLineReader");
        COMPONENT_FACTORY = Check.ifNull(componentFactory, "componentFactory");
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
        CUSTOM_READERS = mapOf();
        WHOLE_SCREEN_PROVIDER = Check.ifNull(wholeScreenProvider, "wholeScreenProvider");
        DEFAULT_KEY_EVENT_PRIORITY = defaultKeyEventPriority;
    }

    public <TDef extends AbstractContentDefinition, TRend extends Renderable> TRend read(
            Component containingComponent,
            TDef definition,
            long timestamp
    ) {
        Check.ifNull(containingComponent, "containingComponent");
        Check.ifNull(definition, "definition");
        TRend renderable = switch (definition) {
            case RasterizedLineSegmentRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) RASTERIZED_LINE_READER.read(containingComponent, d, timestamp);
            case AntialiasedLineSegmentRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) ANTIALIASED_LINE_READER.read(containingComponent, d, timestamp);
            case RectangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) RECTANGLE_READER.read(containingComponent, d, timestamp);
            case TriangleRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TRIANGLE_READER.read(containingComponent, d, timestamp);
            case SpriteRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) SPRITE_READER.read(containingComponent, d, timestamp);
            case ImageAssetSetRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) IMAGE_ASSET_SET_READER.read(containingComponent, d, timestamp);
            case FiniteAnimationRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) FINITE_ANIMATION_READER.read(containingComponent, d, timestamp);
            case TextLineRenderableDefinition d ->
                //noinspection unchecked
                    (TRend) TEXT_LINE_READER.read(containingComponent, d, timestamp);
            case ComponentDefinition d -> readComponentDef(d, containingComponent, timestamp);
            default -> //noinspection unchecked
                    (TRend) readCustomDef(definition, containingComponent, timestamp);
        };
        containingComponent.add(renderable);
        return renderable;
    }

    private <T extends AbstractContentDefinition> Component readCustomDef(
            T definition,
            Component containingComponent,
            long timestamp
    ) {
        var reader = CUSTOM_READERS.get(definition.getClass());
        if (reader == null) {
            throw new IllegalArgumentException(
                    "ContentDefinitionReader.read: Unexpected definition type (" +
                            definition.getClass().getCanonicalName() + ")");
        }
        var componentDef = reader.apply(definition, timestamp);
        return readComponentDef(componentDef, containingComponent, timestamp);
    }

    private <T extends Component> T readComponentDef(
            ComponentDefinition definition,
            Component containingComponent,
            long timestamp
    ) {
        @SuppressWarnings("unchecked") var readComponent = COMPONENT_FACTORY.make(
                definition.UUID,
                definition.Z,
                defaultIfNull(
                        definition.bindings,
                        bindingDefs -> Arrays.stream(bindingDefs)
                                .map(bindingDef -> keyBinding(
                                        bindingDef.KEY_CODEPOINTS,
                                        GET_CONSUMER.apply(bindingDef.PRESS_CONSUMER_ID),
                                        GET_CONSUMER.apply(bindingDef.RELEASE_CONSUMER_ID)))
                                .collect(Collectors.toSet()),
                        setOf()
                ),
                falseIfNull(definition.blocksLowerBindings),
                defaultIfNull(definition.keyBindingPriority, DEFAULT_KEY_EVENT_PRIORITY),
                defaultIfNull(
                        definition.dimensionsProvider,
                        defaultIfNull(
                                definition.dimensionsProviderDef,
                                d -> PROVIDER_READER.read(d, timestamp),
                                WHOLE_SCREEN_PROVIDER
                        )
                ),
                defaultIfNull(
                        definition.RENDERING_BOUNDARIES_PROVIDER,
                        defaultIfNull(
                                definition.RENDERING_BOUNDARIES_PROVIDER_DEF,
                                d -> PROVIDER_READER.read(d, timestamp),
                                WHOLE_SCREEN_PROVIDER
                        )
                ),
                definition.prerenderHookId,
                definition.addHookId,
                containingComponent,
                defaultIfNull(definition.data, Collections::mapOf, mapOf())
        );
        for (var contentDef : definition.CONTENT) {
            read(readComponent, contentDef, timestamp);
        }
        //noinspection unchecked
        return (T) readComponent;
    }

    public <T extends AbstractContentDefinition> void addCustomComponentReader(
            Class<T> aClass,
            BiFunction<AbstractContentDefinition, Long, ComponentDefinition> reader
    ) {
        CUSTOM_READERS.put(Check.ifNull(aClass, "aClass"), Check.ifNull(reader, "reader"));
    }
}
