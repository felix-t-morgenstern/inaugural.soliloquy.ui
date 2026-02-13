package inaugural.soliloquy.ui.readers.content.renderables;

import com.google.common.base.Strings;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.SpriteRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.SpriteRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class SpriteRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final SpriteRenderableFactory FACTORY;
    private final Function<String, Sprite> GET_SPRITE;

    public SpriteRenderableDefinitionReader(SpriteRenderableFactory factory,
                                            Function<String, Sprite> getSprite,
                                            @SuppressWarnings("rawtypes")
                                            Function<String, Consumer> getConsumer,
                                            ProviderDefinitionReader providerReader,
                                            ColorShiftDefinitionReader shiftReader,
                                            @SuppressWarnings("rawtypes")
                                            ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getConsumer, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
    }

    public SpriteRenderable read(Component component,
                                 SpriteRenderableDefinition definition,
                                 long timestamp) {
        var sprite = GET_SPRITE.apply(definition.SPRITE_ID);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER_DEF, timestamp);

        var borderThickness = provider(definition.borderThicknessProviderDef, timestamp);
        var borderColor = provider(definition.borderColorProviderDef, timestamp);

        List<ColorShift> colorShifts = defaultIfNull(
                definition.colorShifts,
                Collections::listOf,
                defaultIfNull(
                        definition.colorShiftDefs,
                        c -> listOf(shiftDef -> SHIFT_READER.read(shiftDef, timestamp), c),
                        listOf()
                )
        );

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var renderable =
                FACTORY.make(sprite, borderThickness, borderColor, onPress, onRelease, onMouseOver,
                        onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(),
                        component);
        if (
                (definition.onPressIds != null && !definition.onPressIds.isEmpty()) ||
                        (definition.onReleaseIds != null && !definition.onReleaseIds.isEmpty()) ||
                        !Strings.isNullOrEmpty(definition.onMouseOverId) ||
                        !Strings.isNullOrEmpty(definition.onMouseLeaveId)
        ) {
            renderable.setCapturesMouseEvents(true);
        }
        return renderable;
    }
}
