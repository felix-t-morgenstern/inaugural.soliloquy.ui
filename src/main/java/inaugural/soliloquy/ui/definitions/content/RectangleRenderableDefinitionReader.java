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

public class RectangleRenderableDefinitionReader {
    private final RectangleRenderableFactory FACTORY;
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;
    private final ProviderDefinitionReader PROVIDER_READER;
    @SuppressWarnings("rawtypes") private final StaticProvider NULL_PROVIDER;

    public RectangleRenderableDefinitionReader(RectangleRenderableFactory factory,
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

    public RectangleRenderable read(RenderableStack stack,
                                    RectangleRenderableDefinition definition) {
        Check.ifNull(stack, "stack");
        Check.ifNull(definition, "definition");

        var area = PROVIDER_READER.read(
                Check.ifNull(definition.AREA_PROVIDER, "definition.AREA_PROVIDER"));

        @SuppressWarnings("unchecked") ProviderAtTime<Color> topLeft =
                definition.topLeftColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.topLeftColorProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> topRight =
                definition.topRightColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.topRightColorProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> bottomLeft =
                definition.bottomLeftColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.bottomLeftColorProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> bottomRight =
                definition.bottomRightColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.bottomRightColorProvider);

        @SuppressWarnings("unchecked") ProviderAtTime<Integer> textureId =
                definition.textureIdProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureIdProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Float> textureTileWidth =
                definition.textureTileWidthProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureTileWidthProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Float> textureTileHeight =
                definition.textureTileHeightProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.textureTileHeightProvider);

        var onPress = Collections.<Integer, Action<MouseEventInputs>>mapOf();
        if (definition.onPressIds != null) {
            //noinspection unchecked
            definition.onPressIds.forEach(
                    (button, id) -> onPress.put(button, GET_ACTION.apply(id)));
        }
        var onRelease = Collections.<Integer, Action<MouseEventInputs>>mapOf();
        if (definition.onReleaseIds != null) {
            //noinspection unchecked
            definition.onReleaseIds.forEach(
                    (button, id) -> onRelease.put(button, GET_ACTION.apply(id)));
        }
        @SuppressWarnings("unchecked") Action<MouseEventInputs> onMouseOver =
                definition.onMouseOverId == null ? null :
                        GET_ACTION.apply(definition.onMouseOverId);
        @SuppressWarnings("unchecked") Action<MouseEventInputs> onMouseLeave =
                definition.onMouseLeaveId == null ? null :
                        GET_ACTION.apply(definition.onMouseLeaveId);

        return FACTORY.make(topLeft, topRight, bottomLeft, bottomRight, textureId, textureTileWidth,
                textureTileHeight, onPress, onRelease, onMouseOver, onMouseLeave, area,
                definition.Z, UUID.randomUUID(), stack);
    }
}
