package inaugural.soliloquy.ui;

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

public class Normalization {
    // The fields are left static rather than inserted as a dependency, so the static normalize
    // methods can take them as dependencies. The fields are internally-scoped, so they can only
    // be touched by classes within this package; they should only be touched by IOModule.
    static ProviderDefinitionReader ProviderDefReader;
    static Supplier<Float> GetWidthToHeightRatio;

    static final String DENORMALIZED_PROVIDER = "DENORMALIZED_PROVIDER";

    static final String provideWithNormalizedHeight = "provideWithNormalizedHeight";

    public FloatBox provideWithNormalizedHeight(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> normalizedProvider = getFromData(inputs, DENORMALIZED_PROVIDER);
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
    public static AbstractProviderDefinition<FloatBox> normalizeHeightDef(
            AbstractProviderDefinition<FloatBox> normalizedProviderDef,
            long timestamp
    ) {
        var normalizedProvider = ProviderDefReader.read(normalizedProviderDef, timestamp);
        return normalizeHeightDef(normalizedProvider);
    }

    /**
     * The dimensions provided by normalizedProvider will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> normalizeHeightDef(
            ProviderAtTime<FloatBox> normalizedProvider
    ) {
        return functionalProvider(
                provideWithNormalizedHeight,
                FloatBox.class
        )
                .withData(mapOf(
                        DENORMALIZED_PROVIDER,
                        normalizedProvider
                ));
    }

    /**
     * The denormalizedDimens will be transformed so that the height is treated as the same
     * on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> normalizeHeightDef(
            FloatBox denormalizedDimens) {
        return staticVal(normalizeHeight(denormalizedDimens));
    }

    @Reflection.DoNotReadMethod
    public static FloatBox normalizeHeight(FloatBox denormalizedDimens) {
        return floatBoxOf(
                denormalizedDimens.topLeft(),
                denormalizedDimens.width(),
                denormalizedDimens.height() * GetWidthToHeightRatio.get()
        );
    }

    static final String provideWithNormalizedWidth = "provideWithNormalizedWidth";

    public FloatBox provideWithNormalizedWidth(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> normalizedProvider = getFromData(inputs, DENORMALIZED_PROVIDER);
        var normalized = normalizedProvider.provide(inputs.timestamp());
        var widthToHeightRatio = GetWidthToHeightRatio.get();
        var denormalizedWidth = normalized.width() / widthToHeightRatio;

        return floatBoxOf(
                normalized.topLeft(),
                denormalizedWidth,
                normalized.width()
        );
    }

    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> normalizedWidthDef(
            AbstractProviderDefinition<FloatBox> denormalizedProviderDef,
            long timestamp
    ) {
        var normalizedProvider = ProviderDefReader.read(denormalizedProviderDef, timestamp);
        return normalizedWidthDef(normalizedProvider);
    }

    /**
     * The dimensions provided by denormalizedProvider will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> normalizedWidthDef(
            ProviderAtTime<FloatBox> denormalizedProvider
    ) {
        return functionalProvider(
                provideWithNormalizedWidth,
                FloatBox.class
        )
                .withData(mapOf(
                        DENORMALIZED_PROVIDER,
                        denormalizedProvider
                ));
    }

    /**
     * The denormalizedDimens will be transformed so that the height is treated as the same
     * on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> normalizedWidthDef(
            FloatBox denormalizedDimens
    ) {
        return staticVal(denormWidth(denormalizedDimens));
    }

    @Reflection.DoNotReadMethod
    public static FloatBox denormWidth(FloatBox denormalizedDimens) {
        return floatBoxOf(
                denormalizedDimens.topLeft(),
                denormalizedDimens.width() / GetWidthToHeightRatio.get(),
                denormalizedDimens.height()
        );
    }
}
