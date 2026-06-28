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

import static inaugural.soliloquy.tools.Tools.*;
import static java.util.UUID.randomUUID;

public class RectangleRenderableDefinitionReader
        extends AbstractMouseEventsComponentDefinitionReader {
    private final RectangleRenderableFactory FACTORY;
    private final Function<String, ProviderAtTime<Integer>> GET_TEX_ID_PROVIDER_FROM_REL_LOC;

    public RectangleRenderableDefinitionReader(
            RectangleRenderableFactory factory,
            @SuppressWarnings("rawtypes")
            Function<String, Consumer> getConsumer,
            ProviderDefinitionReader providerReader,
            Function<String, ProviderAtTime<Integer>> getTexIdProviderFromRelLoc,
            @SuppressWarnings("rawtypes")
            ProviderAtTime nullProvider
    ) {
        super(providerReader, nullProvider, getConsumer);
        FACTORY = Check.ifNull(factory, "factory");
        GET_TEX_ID_PROVIDER_FROM_REL_LOC =
                Check.ifNull(getTexIdProviderFromRelLoc, "getTexIdProviderFromRelLoc");
    }

    public RectangleRenderable read(Component component,
                                    RectangleRenderableDefinition definition,
                                    long timestamp) {
        Check.ifNull(definition, "definition");

        var dimens = definition.dimensProvider != null ? definition.dimensProvider :
                PROVIDER_READER.read(
                        Check.ifNull(definition.dimensProviderDef, "definition.dimensProviderDef"),
                        timestamp
                );

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

        @SuppressWarnings("unchecked") ProviderAtTime<Integer> textureId = supplyIfNull(
                definition.textureIdProvider,
                () -> defaultIfNullElseTransform(
                        definition.textureIdProviderDef,
                        d -> provider(d, timestamp),
                        defaultIfNullElseTransform(
                                definition.textureRelativeLoc,
                                GET_TEX_ID_PROVIDER_FROM_REL_LOC::apply,
                                NULL_PROVIDER
                        )
                ));
        var textureTilesPerWidth = supplyIfNull(definition.textureTilesPerWidthProvider,
                () -> providerOrNull(definition.textureTilesPerWidthProviderDef, timestamp));
        var textureXOffset = supplyIfNull(definition.textureXOffsetProvider,
                () -> providerOrNull(definition.textureXOffsetProviderDef, timestamp));
        var textureTilesPerHeight = supplyIfNull(definition.textureTilesPerHeightProvider,
                () -> providerOrNull(definition.textureTilesPerHeightProviderDef, timestamp));
        var textureYOffset = supplyIfNull(definition.textureYOffsetProvider,
                () -> providerOrNull(definition.textureYOffsetProviderDef, timestamp));

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var renderable = FACTORY.make(
                topLeft,
                topRight,
                bottomRight,
                bottomLeft,
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
                definition.z,
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
