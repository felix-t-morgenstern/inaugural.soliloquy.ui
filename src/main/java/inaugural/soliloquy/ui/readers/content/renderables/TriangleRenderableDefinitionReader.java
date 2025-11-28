package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TriangleRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public class TriangleRenderableDefinitionReader
        extends AbstractMouseEventsComponentDefinitionReader {
    private final TriangleRenderableFactory FACTORY;

    public TriangleRenderableDefinitionReader(TriangleRenderableFactory factory,
                                              @SuppressWarnings("rawtypes")
                                              Function<String, Consumer> getConsumer,
                                              ProviderDefinitionReader providerReader,
                                              @SuppressWarnings("rawtypes")
                                              ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getConsumer);
        FACTORY = Check.ifNull(factory, "factory");
    }

    public TriangleRenderable read(Component component,
                                   TriangleRenderableDefinition definition,
                                   long timestamp) {
        Check.ifNull(component, "component");
        Check.ifNull(definition, "definition");

        var vector1 = definition.VERTEX_1_PROVIDER != null ? definition.VERTEX_1_PROVIDER :
                provider(Check.ifNull(definition.VERTEX_1_PROVIDER_DEF,
                        "definition.VERTEX_1_PROVIDER_DEF"), timestamp);
        var vector2 = definition.VERTEX_2_PROVIDER != null ? definition.VERTEX_2_PROVIDER :
                provider(Check.ifNull(definition.VERTEX_2_PROVIDER_DEF,
                        "definition.VERTEX_2_PROVIDER_DEF"), timestamp);
        var vector3 = definition.VERTEX_3_PROVIDER != null ? definition.VERTEX_3_PROVIDER :
                provider(Check.ifNull(definition.VERTEX_3_PROVIDER_DEF,
                        "definition.VERTEX_3_PROVIDER_DEF"), timestamp);

        var vector1Color = defaultIfNull(definition.vertex1ColorProvider, provider(definition.vertex1ColorProviderDef, timestamp));
        var vector2Color = defaultIfNull(definition.vertex2ColorProvider, provider(definition.vertex2ColorProviderDef, timestamp));
        var vector3Color = defaultIfNull(definition.vertex3ColorProvider, provider(definition.vertex3ColorProviderDef, timestamp));

        var textureId = provider(definition.textureIdProvider, timestamp);
        var textureTileWidth = provider(definition.textureTileWidthProvider, timestamp);
        var textureTileHeight = provider(definition.textureTileHeightProvider, timestamp);

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var renderable =
                FACTORY.make(vector1, vector1Color, vector2, vector2Color, vector3, vector3Color,
                        textureId, textureTileWidth, textureTileHeight, onPress, onRelease,
                        onMouseOver, onMouseLeave, definition.Z, UUID.randomUUID(), component);

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
