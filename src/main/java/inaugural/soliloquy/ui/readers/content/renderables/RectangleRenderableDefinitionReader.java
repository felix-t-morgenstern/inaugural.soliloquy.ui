package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.Tools.provideIfNull;
import static java.util.UUID.randomUUID;

public class RectangleRenderableDefinitionReader
        extends AbstractMouseEventsComponentDefinitionReader {
    private final RectangleRenderableFactory FACTORY;

    public RectangleRenderableDefinitionReader(RectangleRenderableFactory factory,
                                               @SuppressWarnings("rawtypes")
                                               Function<String, Consumer> getConsumer,
                                               ProviderDefinitionReader providerReader,
                                               @SuppressWarnings("rawtypes")
                                               ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getConsumer);
        FACTORY = Check.ifNull(factory, "factory");
    }

    public RectangleRenderable read(Component component,
                                    RectangleRenderableDefinition definition,
                                    long timestamp) {
        Check.ifNull(component, "component");
        Check.ifNull(definition, "definition");

        var dimens = definition.DIMENS_PROVIDER != null ? definition.DIMENS_PROVIDER :
                PROVIDER_READER.read(
                        Check.ifNull(definition.DIMENS_PROVIDER_DEF, "definition.DIMENS_PROVIDER"),
                        timestamp);

        var topLeft = provider(
                definition.topLeftColorProvider,
                definition.topLeftColorProviderDef,
                timestamp);
        var topRight = provider(
                definition.topRightColorProvider,
                definition.topRightColorProviderDef,
                timestamp);
        var bottomLeft = provider(
                definition.bottomLeftColorProvider,
                definition.bottomLeftColorProviderDef,
                timestamp);
        var bottomRight = provider(
                definition.bottomRightColorProvider,
                definition.bottomRightColorProviderDef,
                timestamp);

        var textureId = provideIfNull(definition.textureIdProvider,
                () -> provider(definition.textureIdProviderDef, timestamp));
        var textureTilesPerWidth = provideIfNull(definition.textureTilesPerWidthProvider,
                () -> providerOrNull(definition.textureTilesPerWidthProviderDef, timestamp));
        var textureXOffset = provideIfNull(definition.textureXOffsetProvider,
                () -> providerOrNull(definition.textureXOffsetProviderDef, timestamp));
        var textureTilesPerHeight = provideIfNull(definition.textureTilesPerHeightProvider,
                () -> providerOrNull(definition.textureTilesPerHeightProviderDef, timestamp));
        var textureYOffset = provideIfNull(definition.textureYOffsetProvider,
                () -> providerOrNull(definition.textureYOffsetProviderDef, timestamp));

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var renderable = FACTORY.make(
                topLeft,
                topRight,
                bottomLeft,
                bottomRight,
                textureId,
                textureTilesPerWidth,
                textureXOffset,
                textureTilesPerHeight,
                textureYOffset,
                onPress,
                onRelease,
                onMouseOver,
                onMouseLeave,
                dimens,
                definition.Z,
                defaultIfNull(definition.UUID, randomUUID()),
                component
        );

        if (definition.onPressIds != null ||
                definition.onReleaseIds != null ||
                definition.onMouseOverId != null ||
                definition.onMouseLeaveId != null
        ) {
            renderable.setCapturesMouseEvents(true);
        }

        return renderable;
    }
}
