package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.util.UUID;
import java.util.function.Function;

public class RectangleRenderableDefinitionReader
        extends AbstractMouseEventsComponentDefinitionReader {
    private final RectangleRenderableFactory FACTORY;

    public RectangleRenderableDefinitionReader(RectangleRenderableFactory factory,
                                               @SuppressWarnings("rawtypes")
                                               Function<String, Action> getAction,
                                               ProviderDefinitionReader providerReader,
                                               @SuppressWarnings("rawtypes")
                                               StaticProvider nullProvider) {
        super(providerReader, nullProvider, getAction);
        FACTORY = Check.ifNull(factory, "factory");
    }

    public RectangleRenderable read(Component component,
                                    RectangleRenderableDefinition definition,
                                    long timestamp) {
        Check.ifNull(component, "component");
        Check.ifNull(definition, "definition");

        var area = PROVIDER_READER.read(
                Check.ifNull(definition.AREA_PROVIDER, "definition.AREA_PROVIDER"), timestamp);

        var topLeft = provider(definition.topLeftColorProvider, timestamp);
        var topRight = provider(definition.topRightColorProvider, timestamp);
        var bottomLeft = provider(definition.bottomLeftColorProvider, timestamp);
        var bottomRight = provider(definition.bottomRightColorProvider, timestamp);

        var textureId = provider(definition.textureIdProvider, timestamp);
        var textureTileWidth = provider(definition.textureTileWidthProvider, timestamp);
        var textureTileHeight = provider(definition.textureTileHeightProvider, timestamp);

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        var renderable = FACTORY.make(topLeft, topRight, bottomLeft, bottomRight, textureId,
                textureTileWidth, textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave,
                area, definition.Z, UUID.randomUUID(), component);

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
