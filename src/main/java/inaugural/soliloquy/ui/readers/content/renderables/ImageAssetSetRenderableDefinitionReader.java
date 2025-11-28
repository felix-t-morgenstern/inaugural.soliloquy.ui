package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.assets.ImageAssetSet;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.ImageAssetSetRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.ImageAssetSetRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.ImageAssetSetRenderableDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class ImageAssetSetRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final ImageAssetSetRenderableFactory FACTORY;
    private final Function<String, ImageAssetSet> GET_IMAGE_ASSET_SET;

    public ImageAssetSetRenderableDefinitionReader(ImageAssetSetRenderableFactory factory,
                                                   Function<String, ImageAssetSet> getImageAssetSet,
                                                   @SuppressWarnings("rawtypes")
                                                   Function<String, Consumer> getConsumer,
                                                   ProviderDefinitionReader providerReader,
                                                   ColorShiftDefinitionReader shiftReader,
                                                   @SuppressWarnings("rawtypes")
                                                   ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getConsumer, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_IMAGE_ASSET_SET = Check.ifNull(getImageAssetSet, "getImageAssetSet");
    }

    public ImageAssetSetRenderable read(Component component,
                                        ImageAssetSetRenderableDefinition definition,
                                        long timestamp) {
        var imageAssetSet = GET_IMAGE_ASSET_SET.apply(definition.IMAGE_ASSET_SET_ID);

        var data = mapOf(definition.DISPLAY_PARAMS);

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

        return FACTORY.make(imageAssetSet, data, borderThickness, borderColor, onPress, onRelease,
                onMouseOver, onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(),
                component);
    }
}
