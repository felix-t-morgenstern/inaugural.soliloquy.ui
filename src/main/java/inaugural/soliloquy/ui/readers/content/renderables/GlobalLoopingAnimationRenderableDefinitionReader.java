package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.assets.GlobalLoopingAnimation;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.GlobalLoopingAnimationRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.GlobalLoopingAnimationRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.GlobalLoopingAnimationRenderableDefinition;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNullElseTransform;
import static inaugural.soliloquy.tools.Tools.supplyIfNull;
import static inaugural.soliloquy.tools.collections.Collections.listOf;

public class GlobalLoopingAnimationRenderableDefinitionReader
        extends AbstractImageAssetDefinitionReader {
    private final GlobalLoopingAnimationRenderableFactory FACTORY;
    private final Function<String, GlobalLoopingAnimation> GET_GLOBAL_LOOPING_ANIMATION;

    public GlobalLoopingAnimationRenderableDefinitionReader(
            GlobalLoopingAnimationRenderableFactory factory,
            Function<String, GlobalLoopingAnimation> getGlobalLoopingAnimation,
            @SuppressWarnings("rawtypes") Function<String, Consumer> getConsumer,
            ProviderDefinitionReader providerReader,
            ColorShiftDefinitionReader shiftReader,
            @SuppressWarnings("rawtypes") ProviderAtTime nullProvider
    ) {
        super(providerReader, nullProvider, getConsumer, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_GLOBAL_LOOPING_ANIMATION =
                Check.ifNull(getGlobalLoopingAnimation, "getGlobalLoopingAnimation");
    }

    public GlobalLoopingAnimationRenderable read(Component component,
                                                 GlobalLoopingAnimationRenderableDefinition definition,
                                                 long timestamp) {
        var globalLoopingAnimation = GET_GLOBAL_LOOPING_ANIMATION.apply(definition.ASSET_ID);

        var dimensions = supplyIfNull(
                definition.dimensProvider,
                () -> PROVIDER_READER.read(definition.dimensProviderDef, timestamp)
        );

        var borderThickness = provider(definition.borderThicknessProviderDef, timestamp);
        var borderColor = provider(definition.borderColorProviderDef, timestamp);

        List<ColorShift> colorShifts = defaultIfNullElseTransform(
                definition.colorShifts,
                Collections::listOf,
                defaultIfNullElseTransform(
                        definition.colorShiftDefs,
                        c -> listOf(shiftDef -> SHIFT_READER.read(shiftDef, timestamp), c),
                        listOf()
                )
        );

        var onPress = getConsumerPerButton(definition.onPressIds);
        var onRelease = getConsumerPerButton(definition.onReleaseIds);
        var onWheelScroll = getConsumer(definition.onWheelScrollId);
        var onMouseOver = getConsumer(definition.onMouseOverId);
        var onMouseLeave = getConsumer(definition.onMouseLeaveId);

        var renderable = FACTORY.make(globalLoopingAnimation, borderThickness, borderColor, onPress,
                onRelease, onWheelScroll, onMouseOver, onMouseLeave, colorShifts, dimensions,
                definition.z, UUID.randomUUID(), component);

        setCaptures(definition, renderable);

        return renderable;
    }
}
