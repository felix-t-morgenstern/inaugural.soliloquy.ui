package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TriangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition;

import java.awt.*;
import java.util.UUID;
import java.util.function.Function;

public class TriangleRenderableDefinitionReader extends AbstractMouseEventsComponentDefinitionReader {
    private final TriangleRenderableFactory FACTORY;

    public TriangleRenderableDefinitionReader(TriangleRenderableFactory factory,
                                              @SuppressWarnings("rawtypes")
                                              Function<String, Action> getAction,
                                              ProviderDefinitionReader providerReader,
                                              @SuppressWarnings("rawtypes")
                                              StaticProvider nullProvider) {
        super(providerReader, nullProvider, getAction);
        FACTORY = Check.ifNull(factory, "factory");
    }

    public TriangleRenderable read(RenderableStack stack, TriangleRenderableDefinition definition) {
        Check.ifNull(stack, "stack");
        Check.ifNull(definition, "definition");

        var vector1 = provider(Check.ifNull(definition.VERTEX_1_PROVIDER, "definition.VERTEX_1_PROVIDER"));
        var vector2 = provider(Check.ifNull(definition.VERTEX_2_PROVIDER, "definition.VERTEX_2_PROVIDER"));
        var vector3 = provider(Check.ifNull(definition.VERTEX_3_PROVIDER, "definition.VERTEX_3_PROVIDER"));

        var vector1Color = provider(definition.vertex1ColorProvider);
        var vector2Color = provider(definition.vertex2ColorProvider);
        var vector3Color = provider(definition.vertex3ColorProvider);

        var textureId = provider(definition.textureIdProvider);
        var textureTileWidth = provider(definition.textureTileWidthProvider);
        var textureTileHeight = provider(definition.textureTileHeightProvider);

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(vector1, vector1Color, vector2, vector2Color, vector3, vector3Color,
                textureId, textureTileWidth,
                textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave,
                definition.Z, UUID.randomUUID(), stack);
    }
}
