package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public class RectangleRenderableDefinitionReader
        extends AbstractMouseEventsComponentDefinitionReader {
    private final RectangleRenderableFactory FACTORY;

    public RectangleRenderableDefinitionReader(RectangleRenderableFactory factory,
                                               @SuppressWarnings("rawtypes")
                                               Function<String, Action> getAction,
                                               ProviderDefinitionReader providerReader,
                                               @SuppressWarnings("rawtypes")
                                               ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getAction);
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

        var textureId = defaultIfNull(definition.textureIdProvider,
                provider(definition.textureIdProviderDef, timestamp));
        var textureTileWidth = provider(definition.textureTileWidthProvider, timestamp);
        var textureTileHeight = provider(definition.textureTileHeightProvider, timestamp);

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        var renderable = FACTORY.make(topLeft, topRight, bottomLeft, bottomRight, textureId,
                textureTileWidth, textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave,
                dimens, definition.Z, UUID.randomUUID(), component);

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
