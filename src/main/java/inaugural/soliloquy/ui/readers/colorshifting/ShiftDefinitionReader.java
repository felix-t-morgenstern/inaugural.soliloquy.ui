package inaugural.soliloquy.ui.readers.colorshifting;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;

import static soliloquy.specs.io.graphics.renderables.colorshifting.BrightnessShift.brightnessShift;
import static soliloquy.specs.io.graphics.renderables.colorshifting.ColorComponentIntensityShift.colorComponentShift;
import static soliloquy.specs.io.graphics.renderables.colorshifting.ColorRotationShift.rotationShift;

public class ShiftDefinitionReader {
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public ShiftDefinitionReader(ProviderDefinitionReader providerDefReader) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ColorShift read(ShiftDefinition definition, long timestamp) {
        Check.ifNull(definition, "definition");
        var shiftAmountProvider = PROVIDER_DEF_READER.read(
                Check.ifNull(definition.SHIFT_AMOUNT_PROVIDER, "definition.SHIFT_AMOUNT_PROVIDER"),
                timestamp);
        return switch (definition.SHIFT_TYPE) {
            case BRIGHTNESS -> brightnessShift(shiftAmountProvider,
                    definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);
            case COMPONENT_INTENSITY -> colorComponentShift(
                    shiftAmountProvider,
                    definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE,
                    Check.ifNull(definition.COMPONENT, "definition.COMPONENT")
            );
            case ROTATION -> rotationShift(shiftAmountProvider,
                    definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);
        };
    }
}
