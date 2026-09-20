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

public class Denormalization {
    // The fields are left static rather than inserted as a dependency, so the static denorm
    // methods can take them as dependencies. The fields are internally-scoped, so they can only
    // be touched by classes within this package; they should only be touched by IOModule.
    static ProviderDefinitionReader ProviderDefReader;
    static Supplier<Float> GetWidthToHeightRatio;

    static final String NORMALIZED_PROVIDER = "NORMALIZED_PROVIDER";

    static final String provideWithDenormalizedHeight = "provideWithDenormalizedHeight";

    public FloatBox provideWithDenormalizedHeight(FunctionalProvider.Inputs inputs) {
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
    public static AbstractProviderDefinition<FloatBox> denormHeightDef(
            AbstractProviderDefinition<FloatBox> normalizedProviderDef,
            long timestamp
    ) {
        var normalizedProvider = ProviderDefReader.read(normalizedProviderDef, timestamp);
        return denormHeightDef(normalizedProvider);
    }

    /**
     * The dimensions provided by normalizedProvider will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> denormHeightDef(
            ProviderAtTime<FloatBox> normalizedProvider
    ) {
        return functionalProvider(
                provideWithDenormalizedHeight,
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
    public static AbstractProviderDefinition<FloatBox> denormHeightDef(FloatBox normalizedDimens) {
        return staticVal(denormHeight(normalizedDimens));
    }

    @Reflection.DoNotReadMethod
    public static FloatBox denormHeight(FloatBox normalizedDimens) {
        return floatBoxOf(
                normalizedDimens.topLeft(),
                normalizedDimens.width(),
                normalizedDimens.height() * GetWidthToHeightRatio.get()
        );
    }

    static final String provideWithDenormalizedWidth = "provideWithDenormalizedWidth";

    public FloatBox provideWithDenormalizedWidth(FunctionalProvider.Inputs inputs) {
        ProviderAtTime<FloatBox> normalizedProvider = getFromData(inputs, NORMALIZED_PROVIDER);
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
    public static AbstractProviderDefinition<FloatBox> denormWidthDef(
            AbstractProviderDefinition<FloatBox> normalizedProviderDef,
            long timestamp
    ) {
        var normalizedProvider = ProviderDefReader.read(normalizedProviderDef, timestamp);
        return denormWidthDef(normalizedProvider);
    }

    /**
     * The dimensions provided by normalizedProvider will be transformed so that the height is
     * treated as the same on-screen distance as the equivalent width.
     */
    @Reflection.DoNotReadMethod
    public static AbstractProviderDefinition<FloatBox> denormWidthDef(
            ProviderAtTime<FloatBox> normalizedProvider
    ) {
        return functionalProvider(
                provideWithDenormalizedWidth,
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
    public static AbstractProviderDefinition<FloatBox> denormWidthDef(FloatBox normalizedDimens) {
        return staticVal(denormWidth(normalizedDimens));
    }

    @Reflection.DoNotReadMethod
    public static FloatBox denormWidth(FloatBox normalizedDimens) {
        return floatBoxOf(
                normalizedDimens.topLeft(),
                normalizedDimens.width() / GetWidthToHeightRatio.get(),
                normalizedDimens.height()
        );
    }
}
