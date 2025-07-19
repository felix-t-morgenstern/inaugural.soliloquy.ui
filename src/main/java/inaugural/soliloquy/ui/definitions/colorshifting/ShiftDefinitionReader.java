package inaugural.soliloquy.ui.definitions.colorshifting;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.renderables.colorshifting.*;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;

public class ShiftDefinitionReader {
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public ShiftDefinitionReader(ProviderDefinitionReader providerDefReader) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ColorShift read(ShiftDefinition definition) {
        Check.ifNull(definition, "definition");
        var shiftAmountProvider = PROVIDER_DEF_READER.read(
                Check.ifNull(definition.SHIFT_AMOUNT_PROVIDER, "definition.SHIFT_AMOUNT_PROVIDER"));
        return switch (definition.SHIFT_TYPE) {
            case BRIGHTNESS -> new BrightnessShift() {
                @Override
                public ProviderAtTime<Float> shiftAmountProvider() {
                    return shiftAmountProvider;
                }

                @Override
                public boolean overridesPriorShiftsOfSameType() {
                    return definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE;
                }
            };
            case COMPONENT_INTENSITY -> {
                var component = Check.ifNull(definition.COMPONENT, "definition.COMPONENT");
                yield new ColorComponentIntensityShift() {
                    @Override
                    public ColorComponent colorComponent() {
                        return component;
                    }

                    @Override
                    public ProviderAtTime<Float> shiftAmountProvider() {
                        return shiftAmountProvider;
                    }

                    @Override
                    public boolean overridesPriorShiftsOfSameType() {
                        return definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE;
                    }
                };
            }
            case ROTATION -> new ColorRotationShift() {
                @Override
                public ProviderAtTime<Float> shiftAmountProvider() {
                    return shiftAmountProvider;
                }

                @Override
                public boolean overridesPriorShiftsOfSameType() {
                    return definition.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE;
                }
            };
        };
    }
}
