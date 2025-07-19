package inaugural.soliloquy.ui.definitions;

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

public class TriangleRenderableDefinitionReader {
    private final TriangleRenderableFactory FACTORY;
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;
    private final ProviderDefinitionReader PROVIDER_READER;
    @SuppressWarnings("rawtypes") private final StaticProvider NULL_PROVIDER;

    public TriangleRenderableDefinitionReader(TriangleRenderableFactory factory,
                                              @SuppressWarnings("rawtypes")
                                              Function<String, Action> getAction,
                                              ProviderDefinitionReader providerReader,
                                              @SuppressWarnings("rawtypes")
                                              StaticProvider nullProvider) {
        FACTORY = Check.ifNull(factory, "factory");
        GET_ACTION = Check.ifNull(getAction, "getAction");
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
    }

    public TriangleRenderable read(RenderableStack stack, TriangleRenderableDefinition definition) {
        var vector1 = PROVIDER_READER.read(definition.VERTEX_1_PROVIDER);
        var vector2 = PROVIDER_READER.read(definition.VERTEX_2_PROVIDER);
        var vector3 = PROVIDER_READER.read(definition.VERTEX_3_PROVIDER);

        @SuppressWarnings("unchecked") ProviderAtTime<Color> vector1Color =
                definition.vertex1ColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.vertex1ColorProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> vector2Color =
                definition.vertex2ColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.vertex2ColorProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> vector3Color =
                definition.vertex3ColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.vertex3ColorProvider);

        @SuppressWarnings("unchecked") ProviderAtTime<Integer> textureId =
                definition.textureIdProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureIdProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Float> textureTileWidth =
                definition.textureTileWidthProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureTileWidthProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Float> textureTileHeight =
                definition.textureTileHeightProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureTileHeightProvider);

        var onPress =
                Collections.<Integer, Action<RenderableWithMouseEvents.MouseEventInputs>>mapOf();
        if (definition.onPressIds != null) {
            //noinspection unchecked
            definition.onPressIds.forEach(
                    (button, id) -> onPress.put(button, GET_ACTION.apply(id)));
        }
        var onRelease =
                Collections.<Integer, Action<RenderableWithMouseEvents.MouseEventInputs>>mapOf();
        if (definition.onReleaseIds != null) {
            //noinspection unchecked
            definition.onReleaseIds.forEach(
                    (button, id) -> onRelease.put(button, GET_ACTION.apply(id)));
        }
        @SuppressWarnings("unchecked") Action<RenderableWithMouseEvents.MouseEventInputs>
                onMouseOver =
                definition.onMouseOverId == null ? null :
                        GET_ACTION.apply(definition.onMouseOverId);
        @SuppressWarnings("unchecked") Action<RenderableWithMouseEvents.MouseEventInputs>
                onMouseLeave =
                definition.onMouseLeaveId == null ? null :
                        GET_ACTION.apply(definition.onMouseLeaveId);

        return FACTORY.make(vector1, vector1Color, vector2, vector2Color, vector3, vector3Color,
                textureId, textureTileWidth,
                textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave,
                definition.Z, UUID.randomUUID(), stack);
    }
}
