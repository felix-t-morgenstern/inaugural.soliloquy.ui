package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.RectangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.RectangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.awt.*;
import java.util.UUID;
import java.util.function.Function;

import static soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents.MouseEventInputs;

public class RectangleRenderableDefinitionReader extends AbstractMouseEventsComponentDefinitionReader {
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

    public RectangleRenderable read(RenderableStack stack,
                                    RectangleRenderableDefinition definition) {
        Check.ifNull(stack, "stack");
        Check.ifNull(definition, "definition");

        var area = PROVIDER_READER.read(
                Check.ifNull(definition.AREA_PROVIDER, "definition.AREA_PROVIDER"));

        var topLeft = provider(definition.topLeftColorProvider);
        var topRight = provider(definition.topRightColorProvider);
        var bottomLeft = provider(definition.bottomLeftColorProvider);
        var bottomRight = provider(definition.bottomRightColorProvider);

        var textureId = provider(definition.textureIdProvider);
        var textureTileWidth = provider(definition.textureTileWidthProvider);
        var textureTileHeight = provider(definition.textureTileHeightProvider);

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(topLeft, topRight, bottomLeft, bottomRight, textureId, textureTileWidth,
                textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave, area,
                definition.Z, UUID.randomUUID(), stack);
    }
}
