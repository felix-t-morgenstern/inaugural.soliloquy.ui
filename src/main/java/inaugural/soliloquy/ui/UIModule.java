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

import java.util.function.BiFunction;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static java.util.UUID.randomUUID;
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
        var staticProviderFactory = ioModule.provide(StaticProviderFactory.class);
        var nullProvider = staticProviderFactory.make(randomUUID(), null);

        // ==================
        // Definition Readers
        // ==================

        // Provider Definition Reader

        @SuppressWarnings({"rawtypes", "unchecked"}) var providerReader =
                new ProviderDefinitionReader(Collections.mapOf(
                        pairOf(
                                FiniteLinearMovingColorProviderDefinition.class,
                                (d, t) -> new FiniteLinearMovingColorProviderDefinitionReader(
                                        ioModule.provide(
                                                FiniteLinearMovingColorProviderFactory.class)
                                ).read((FiniteLinearMovingColorProviderDefinition) d, t)
                        ),
                        pairOf(
                                FiniteLinearMovingProviderDefinition.class,
                                (d, t) -> new FiniteLinearMovingProviderDefinitionReader(
                                        ioModule.provide(FiniteLinearMovingProviderFactory.class)
                                ).read((FiniteLinearMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                FiniteSinusoidMovingProviderDefinition.class,
                                (d, t) -> new FiniteSinusoidMovingProviderDefinitionReader(
                                        ioModule.provide(FiniteSinusoidMovingProviderFactory.class)
                                ).read((FiniteSinusoidMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                LoopingLinearMovingColorProviderDefinition.class,
                                (d, t) -> new LoopingLinearMovingColorProviderDefinitionReader(
                                        ioModule.provide(
                                                LoopingLinearMovingColorProviderFactory.class)
                                ).read((LoopingLinearMovingColorProviderDefinition) d, t)
                        ),
                        pairOf(
                                LoopingLinearMovingProviderDefinition.class,
                                (d, t) -> new LoopingLinearMovingProviderDefinitionReader(
                                        ioModule.provide(LoopingLinearMovingProviderFactory.class)
                                ).read((LoopingLinearMovingProviderDefinition) d, t)
                        ),
                        pairOf(
                                StaticProviderDefinition.class,
                                (d, _) -> new StaticProviderDefinitionReader(
                                        ioModule.provide(StaticProviderFactory.class)
                                ).read((StaticProviderDefinition) d)
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
