package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.SpriteRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.SpriteRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class SpriteRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final SpriteRenderableFactory FACTORY;
    private final Function<String, Sprite> GET_SPRITE;

    public SpriteRenderableDefinitionReader(SpriteRenderableFactory factory,
                                            Function<String, Sprite> getSprite,
                                            @SuppressWarnings("rawtypes")
                                            Function<String, Action> getAction,
                                            ProviderDefinitionReader providerReader,
                                            ShiftDefinitionReader shiftReader,
                                            @SuppressWarnings("rawtypes")
                                            StaticProvider nullProvider) {
        super(providerReader, nullProvider, getAction, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
    }

    public SpriteRenderable read(Component component, SpriteRenderableDefinition definition) {
        var sprite = GET_SPRITE.apply(definition.SPRITE_ID);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER);

        var borderThickness = provider(definition.borderThicknessProvider);
        var borderColor = provider(definition.borderColorProvider);

        List<ColorShift> colorShifts = definition.colorShifts == null ? listOf() :
                Arrays.stream(definition.colorShifts).map(SHIFT_READER::read).toList();

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(sprite, borderThickness, borderColor, onPress, onRelease, onMouseOver,
                onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(), component);
    }
}
