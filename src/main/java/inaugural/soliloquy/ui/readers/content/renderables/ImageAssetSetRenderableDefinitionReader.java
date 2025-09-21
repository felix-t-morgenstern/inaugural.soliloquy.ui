package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
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

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class ImageAssetSetRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final ImageAssetSetRenderableFactory FACTORY;
    private final Function<String, ImageAssetSet> GET_IMAGE_ASSET_SET;

    public ImageAssetSetRenderableDefinitionReader(ImageAssetSetRenderableFactory factory,
                                                   Function<String, ImageAssetSet> getImageAssetSet,
                                                   @SuppressWarnings("rawtypes")
                                                   Function<String, Action> getAction,
                                                   ProviderDefinitionReader providerReader,
                                                   ShiftDefinitionReader shiftReader,
                                                   @SuppressWarnings("rawtypes")
                                                   ProviderAtTime nullProvider) {
        super(providerReader, nullProvider, getAction, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_IMAGE_ASSET_SET = Check.ifNull(getImageAssetSet, "getImageAssetSet");
    }

    public ImageAssetSetRenderable read(Component component,
                                        ImageAssetSetRenderableDefinition definition,
                                        long timestamp) {
        var imageAssetSet = GET_IMAGE_ASSET_SET.apply(definition.IMAGE_ASSET_SET_ID);

        var data = mapOf(definition.DISPLAY_PARAMS);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER, timestamp);

        var borderThickness = provider(definition.borderThicknessProvider, timestamp);
        var borderColor = provider(definition.borderColorProvider, timestamp);

        List<ColorShift> colorShifts = definition.colorShifts == null ? listOf() :
                Arrays.stream(definition.colorShifts)
                        .map(shiftDef -> SHIFT_READER.read(shiftDef, timestamp)).toList();

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(imageAssetSet, data, borderThickness, borderColor, onPress, onRelease,
                onMouseOver, onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(),
                component);
    }
}
