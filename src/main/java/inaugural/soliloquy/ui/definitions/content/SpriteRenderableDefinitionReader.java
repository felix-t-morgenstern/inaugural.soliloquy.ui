package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.definitions.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents;
import soliloquy.specs.io.graphics.renderables.SpriteRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.SpriteRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class SpriteRenderableDefinitionReader {
    private final SpriteRenderableFactory FACTORY;
    private final Function<String, Sprite> GET_SPRITE;
    @SuppressWarnings("rawtypes") private final Function<String, Action> GET_ACTION;
    private final ProviderDefinitionReader PROVIDER_READER;
    private final ShiftDefinitionReader SHIFT_READER;
    @SuppressWarnings("rawtypes") private final StaticProvider NULL_PROVIDER;

    public SpriteRenderableDefinitionReader(SpriteRenderableFactory factory,
                                            Function<String, Sprite> getSprite,
                                            @SuppressWarnings("rawtypes")
                                            Function<String, Action> getAction,
                                            ProviderDefinitionReader providerReader,
                                            ShiftDefinitionReader shiftReader,
                                            @SuppressWarnings("rawtypes")
                                            StaticProvider nullProvider) {
        FACTORY = Check.ifNull(factory, "factory");
        GET_SPRITE = Check.ifNull(getSprite, "getSprite");
        GET_ACTION = Check.ifNull(getAction, "getAction");
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        SHIFT_READER = Check.ifNull(shiftReader, "shiftReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
    }

    public SpriteRenderable read(RenderableStack stack, SpriteRenderableDefinition definition) {
        var sprite = GET_SPRITE.apply(definition.SPRITE_ID);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER);

        @SuppressWarnings("unchecked") ProviderAtTime<Float> borderThickness =
                definition.borderThicknessProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.borderThicknessProvider);
        @SuppressWarnings("unchecked") ProviderAtTime<Color> borderColor =
                definition.borderColorProvider == null ? NULL_PROVIDER :
                        PROVIDER_READER.read(definition.borderColorProvider);

        List<ColorShift> colorShifts = definition.colorShiftProviders == null ? listOf() :
                Arrays.stream(definition.colorShiftProviders).map(SHIFT_READER::read).toList();

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

        return FACTORY.make(sprite, borderThickness, borderColor, onPress, onRelease, onMouseOver,
                onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(), stack);
    }
}
