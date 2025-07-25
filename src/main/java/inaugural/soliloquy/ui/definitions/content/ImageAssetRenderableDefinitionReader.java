package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.definitions.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.assets.ImageAssetSet;
import soliloquy.specs.io.graphics.renderables.ImageAssetSetRenderable;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.factories.ImageAssetSetRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.content.ImageAssetSetRenderableDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class ImageAssetRenderableDefinitionReader extends AbstractImageAssetDefinitionReader {
    private final ImageAssetSetRenderableFactory FACTORY;
    private final Function<String, ImageAssetSet> GET_IMAGE_ASSET_SET;

    public ImageAssetRenderableDefinitionReader(ImageAssetSetRenderableFactory factory,
                                                Function<String, ImageAssetSet> getImageAssetSet,
                                                @SuppressWarnings("rawtypes")
                                                Function<String, Action> getAction,
                                                ProviderDefinitionReader providerReader,
                                                ShiftDefinitionReader shiftReader,
                                                @SuppressWarnings("rawtypes")
                                                StaticProvider nullProvider) {
        super(providerReader, nullProvider, getAction, shiftReader);
        FACTORY = Check.ifNull(factory, "factory");
        GET_IMAGE_ASSET_SET = Check.ifNull(getImageAssetSet, "getImageAssetSet");
    }

    public ImageAssetSetRenderable read(RenderableStack stack,
                                        ImageAssetSetRenderableDefinition definition) {
        var imageAssetSet = GET_IMAGE_ASSET_SET.apply(definition.IMAGE_ASSET_SET_ID);

        var data = mapOf(definition.DISPLAY_PARAMS);

        var dimensions = PROVIDER_READER.read(definition.DIMENSIONS_PROVIDER);

        var borderThickness = provider(definition.borderThicknessProvider);
        var borderColor = provider(definition.borderColorProvider);

        List<ColorShift> colorShifts = definition.colorShiftProviders == null ? listOf() :
                Arrays.stream(definition.colorShiftProviders).map(SHIFT_READER::read).toList();

        var onPress = getActionPerButton(definition.onPressIds);
        var onRelease = getActionPerButton(definition.onReleaseIds);
        var onMouseOver = getAction(definition.onMouseOverId);
        var onMouseLeave = getAction(definition.onMouseLeaveId);

        return FACTORY.make(imageAssetSet, data, borderThickness, borderColor, onPress, onRelease,
                onMouseOver, onMouseLeave, colorShifts, dimensions, definition.Z, UUID.randomUUID(),
                stack);
    }
}
