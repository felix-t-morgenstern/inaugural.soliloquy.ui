package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.definitions.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.SpriteRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.SpriteRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class SpriteRenderableDefinitionReader extends AbstractMouseEventsComponentDefinitionReader {
    private final SpriteRenderableFactory FACTORY;
    private final Function<String, Sprite> GET_SPRITE;
    private final ShiftDefinitionReader SHIFT_READER;

    public SpriteRenderableDefinitionReader(SpriteRenderableFactory factory,
                                            Function<String, Sprite> getSprite,
                                            @SuppressWarnings("rawtypes")
                                            Function<String, Action> getAction,
                                            ProviderDefinitionReader providerReader,
                                            ShiftDefinitionReader shiftReader,
                                            @SuppressWarnings("rawtypes")
                                            StaticProvider nullProvider) {
        super(providerReader, nullProvider, getAction);
        FACTORY = Check.ifNull(factory, "factory");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
        SHIFT_READER = Check.ifNull(shiftReader, "shiftReader");
    }

    public SpriteRenderable read(RenderableStack stack, SpriteRenderableDefinition definition) {
        var sprite = GET_SPRITE.apply(definition.SPRITE_ID);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER);

        var borderThickness = provider(definition.borderThicknessProvider);
        var borderColor = provider(definition.borderColorProvider);

        List<ColorShift> colorShifts = definition.colorShiftProviders == null ? listOf() :
                Arrays.stream(definition.colorShiftProviders).map(SHIFT_READER::read).toList();

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(sprite, borderThickness, borderColor, onPress, onRelease, onMouseOver,
                onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(), stack);
    }
}
