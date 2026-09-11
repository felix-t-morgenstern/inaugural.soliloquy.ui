package inaugural.soliloquy.ui;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.reflection.Reflection;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.function.Supplier;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class Denormalization {
    // The fields are left static rather than inserted as a dependency, so the static denorm
    // methods can take them as dependencies. The fields are internally-scoped, so they can only
    // be touched by classes within this package; they should only be touched by IOModule.
    static ProviderDefinitionReader ProviderDefReader;
    static Supplier<Float> GetWidthToHeightRatio;

    static final String NORMALIZED_PROVIDER = "NORMALIZED_PROVIDER";

    static final String provideDenormalized = "provideDenormalized";

    public FloatBox provideDenormalized(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> normalizedProvider = getFromData(inputs, NORMALIZED_PROVIDER);
        var normalized = normalizedProvider.provide(inputs.timestamp());
        var widthToHeightRatio = GetWidthToHeightRatio.get();
        var denormalizedHeight = normalized.height() * widthToHeightRatio;

        return floatBoxOf(
                normalized.topLeft(),
                normalized.width(),
                denormalizedHeight
        );
    }

    /**
     * The dimensions provided by normalizedProviderDef will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> denorm(
            AbstractProviderDefinition<FloatBox> normalizedProviderDef,
            long timestamp
    ) {
        var normalizedProvider = ProviderDefReader.read(normalizedProviderDef, timestamp);
        return denorm(normalizedProvider);
    }

    /**
     * The dimensions provided by normalizedProvider will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> denorm(
            ProviderAtTime<FloatBox> normalizedProvider
    ) {
        return functionalProvider(
                provideDenormalized,
                FloatBox.class
        )
                .withData(mapOf(
                        NORMALIZED_PROVIDER,
                        normalizedProvider
                ));
    }

    /**
     * The normalizedDimens will be transformed so that the height is treated as the same on-screen
     * distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> denorm(FloatBox normalizedDimens) {
        return staticVal(
                floatBoxOf(
                        normalizedDimens.topLeft(),
                        normalizedDimens.width(),
                        normalizedDimens.height() * GetWidthToHeightRatio.get()
                )
        );
    }
}
