package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.assets.Animation;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.FiniteAnimationRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.FiniteAnimationRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.FiniteAnimationRenderableDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class FiniteAnimationRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final FiniteAnimationRenderableFactory FACTORY;
    private final Function<String, Animation> GET_ANIMATION;

    public FiniteAnimationRenderableDefinitionReader(FiniteAnimationRenderableFactory factory,
                                                     Function<String, Animation> getAnimation,
                                                     @SuppressWarnings("rawtypes")
                                                     Function<String, Consumer> getConsumer,
                                                     ProviderDefinitionReader providerReader,
                                                     ColorShiftDefinitionReader shiftReader,
                                                     @SuppressWarnings("rawtypes")
                                                     ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getConsumer, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_ANIMATION = Check.ifNull(getAnimation, "getAnimation");
    }

    public FiniteAnimationRenderable read(Component component,
                                          FiniteAnimationRenderableDefinition definition,
                                          long timestamp) {
        var animation = GET_ANIMATION.apply(definition.ANIMATION_ID);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER_DEF, timestamp);

        var borderThickness = provider(definition.borderThicknessProviderDef, timestamp);
        var borderColor = provider(definition.borderColorProviderDef, timestamp);

        List<ColorShift> colorShifts = defaultIfNull(definition.colorShifts,
                defaultIfNull(definition.colorShiftDefs, listOf(), c -> Arrays.stream(c)
                        .map(shiftDef -> SHIFT_READER.read(shiftDef, timestamp)).toList()),
                d -> Arrays.stream(d).toList());

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var startTimestamp = timestamp + definition.startTimestampOffset;

        return FACTORY.make(
                animation,
                borderThickness,
                borderColor,
                onPress,
                onRelease,
                onMouseOver,
                onMouseLeave,
                colorShifts,
                dimensions,
                definition.Z,
                UUID.randomUUID(),
                component,
                startTimestamp,
                null
        );
    }
}
