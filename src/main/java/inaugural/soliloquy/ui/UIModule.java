package inaugural.soliloquy.ui;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.content.*;
import inaugural.soliloquy.ui.readers.providers.*;
import org.int4.dirk.api.Injector;
import org.int4.dirk.di.Injectors;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.game.Module;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.factories.*;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.*;
import soliloquy.specs.ui.definitions.providers.*;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static inaugural.soliloquy.io.api.Constants.NULL_PROVIDER;
import static inaugural.soliloquy.io.api.Constants.STATIC_PROVIDER_FACTORY;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class UIModule implements Module {
    private final Injector INJECTOR;

    public UIModule(
            IOModule ioModule,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction
    ) {
        // ====
        // Prep
        // ====

        INJECTOR = Injectors.manual();

        andRegister(ioModule);

        var graphics = ioModule.provide(Graphics.class);
        @SuppressWarnings("rawtypes") BiFunction<UUID, Object, ProviderAtTime>
                staticProviderFactory = ioModule.provide(STATIC_PROVIDER_FACTORY);
        @SuppressWarnings("rawtypes") ProviderAtTime nullProvider = ioModule.provide(NULL_PROVIDER);

        // ==================
        // Definition Readers
        // ==================

        // Provider Definition Reader

        var finiteLinearMovingColorProviderDefReader =
                new FiniteLinearMovingColorProviderDefinitionReader(
                        ioModule.provide(FiniteLinearMovingColorProviderFactory.class));
        var finiteLinearMovingProviderDefReader = new FiniteLinearMovingProviderDefinitionReader(
                ioModule.provide(FiniteLinearMovingProviderFactory.class));
        var finiteSinusoidMovingProviderDefReader =
                new FiniteSinusoidMovingProviderDefinitionReader(
                        ioModule.provide(FiniteSinusoidMovingProviderFactory.class));
        var functionalProviderDefReader = new FunctionalProviderDefinitionReader(
                ioModule.provide(FunctionalProviderFactory.class));
        var loopingLinearMovingColorProviderDefReader =
                new LoopingLinearMovingColorProviderDefinitionReader(
                        ioModule.provide(LoopingLinearMovingColorProviderFactory.class));
        var loopingLinearMovingProviderDefReader = new LoopingLinearMovingProviderDefinitionReader(
                ioModule.provide(LoopingLinearMovingProviderFactory.class));
        var staticProviderDefReader = new StaticProviderDefinitionReader(staticProviderFactory);

        @SuppressWarnings({"rawtypes", "unchecked"}) var providerReader =
                new ProviderDefinitionReader(Collections.mapOf(
                        pairOf(
                                FiniteLinearMovingColorProviderDefinition.class,
                                (d, t) -> finiteLinearMovingColorProviderDefReader.read(
                                        (FiniteLinearMovingColorProviderDefinition) d, t)
                        ),
                        pairOf(
                                FiniteLinearMovingProviderDefinition.class,
                                (d, t) -> finiteLinearMovingProviderDefReader.read(
                                        (FiniteLinearMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                FiniteSinusoidMovingProviderDefinition.class,
                                (d, t) -> finiteSinusoidMovingProviderDefReader.read(
                                        (FiniteSinusoidMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                FunctionalProviderDefinition.class,
                                (d, _) -> functionalProviderDefReader.read(
                                        (FunctionalProviderDefinition) d)
                        ),
                        pairOf(
                                LoopingLinearMovingColorProviderDefinition.class,
                                (d, t) -> loopingLinearMovingColorProviderDefReader.read(
                                        (LoopingLinearMovingColorProviderDefinition) d, t)
                        ),
                        pairOf(
                                LoopingLinearMovingProviderDefinition.class,
                                (d, t) -> loopingLinearMovingProviderDefReader.read(
                                        (LoopingLinearMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                StaticProviderDefinition.class,
                                (d, _) -> staticProviderDefReader.read((StaticProviderDefinition) d)
                        )
                ));

        // Color Shift Definition Reader

        var shiftDefinitionReader = new ShiftDefinitionReader(providerReader);

        // Renderable Definition Readers

        andRegister(new RenderableDefinitionReader(
                new RasterizedLineSegmentRenderableDefinitionReader(
                        ioModule.provide(RasterizedLineSegmentRenderableFactory.class),
                        providerReader,
                        (short) 1
                ),
                new AntialiasedLineSegmentRenderableDefinitionReader(
                        ioModule.provide(AntialiasedLineSegmentRenderableFactory.class),
                        providerReader
                ),
                new RectangleRenderableDefinitionReader(
                        ioModule.provide(RectangleRenderableFactory.class),
                        getAction,
                        providerReader,
                        nullProvider
                ),
                new TriangleRenderableDefinitionReader(
                        ioModule.provide(TriangleRenderableFactory.class),
                        getAction,
                        providerReader,
                        nullProvider
                ),
                new SpriteRenderableDefinitionReader(
                        ioModule.provide(SpriteRenderableFactory.class),
                        graphics::getSprite,
                        getAction,
                        providerReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new ImageAssetSetRenderableDefinitionReader(
                        ioModule.provide(ImageAssetSetRenderableFactory.class),
                        graphics::getImageAssetSet,
                        getAction,
                        providerReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new FiniteAnimationRenderableDefinitionReader(
                        ioModule.provide(FiniteAnimationRenderableFactory.class),
                        graphics::getAnimation,
                        getAction,
                        providerReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new TextLineRenderableDefinitionReader(
                        ioModule.provide(TextLineRenderableFactory.class),
                        graphics::getFont,
                        providerReader,
                        nullProvider
                ),
                ioModule.provide(ComponentFactory.class),
                providerReader
        ));

        andRegister(new RectangleRenderableDefinitionReader(
                ioModule.provide(RectangleRenderableFactory.class),
                getAction,
                providerReader,
                nullProvider
        ));
    }

    @Override
    public <T> T provide(Class<T> clazz) throws IllegalArgumentException {
        return INJECTOR.getInstance(clazz);
    }

    public <T> T provide(String instance) throws IllegalArgumentException {
        Check.ifNullOrEmpty(instance, "instance");
        throw new IllegalArgumentException("No named instances within UIModule");
    }

    private <T> T andRegister(T registrant) {
        INJECTOR.registerInstance(registrant);

        return registrant;
    }
}
