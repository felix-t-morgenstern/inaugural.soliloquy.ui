package inaugural.soliloquy.ui;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.tools.module.AbstractModule;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.components.button.ButtonDefinitionReader;
import inaugural.soliloquy.ui.components.button.ButtonMethods;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.content.renderables.*;
import inaugural.soliloquy.ui.readers.providers.*;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.entities.Function;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.factories.*;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.*;
import soliloquy.specs.io.graphics.rendering.WindowResolutionManager;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.io.graphics.rendering.timing.GlobalClock;
import soliloquy.specs.io.input.mouse.MouseEventHandler;
import soliloquy.specs.ui.definitions.providers.*;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static inaugural.soliloquy.io.api.Constants.*;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.reflection.Reflection.readMethods;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class UIModule extends AbstractModule {
    public UIModule(
            IOModule ioModule,
            @SuppressWarnings("rawtypes") Map<String, Action> actions,
            @SuppressWarnings("rawtypes") Map<String, Function> functions
    ) {
        // ====
        // Prep
        // ====

        andRegister(ioModule);

        var graphics = ioModule.provide(Graphics.class);
        @SuppressWarnings("rawtypes") BiFunction<UUID, Object, ProviderAtTime>
                staticProviderFactory = ioModule.provide(STATIC_PROVIDER_FACTORY);
        @SuppressWarnings("rawtypes") ProviderAtTime nullProvider = ioModule.provide(NULL_PROVIDER);
        ProviderAtTime<FloatBox> wholeScreenProvider = ioModule.provide(WHOLE_SCREEN_PROVIDER);
        var clock = ioModule.provide(GlobalClock.class);
        TextLineRenderer textLineRenderer = ioModule.provide(TEXT_LINE_RENDERER);
        var resManager = ioModule.provide(WindowResolutionManager.class);
        TriConsumer<Integer, MouseEventHandler.EventType, Runnable> subscribeToNextMouseEvent =
                ioModule.provide(SUBSCRIBE_TO_NEXT_MOUSE_EVENT);

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

        @SuppressWarnings({"rawtypes", "unchecked"}) var providerDefinitionReader =
                new ProviderDefinitionReader(mapOf(
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

        var shiftDefinitionReader = new ShiftDefinitionReader(providerDefinitionReader);

        // Renderable Definition Readers

        var renderableDefinitionReader = andRegister(new RenderableDefinitionReader(
                new RasterizedLineSegmentRenderableDefinitionReader(
                        ioModule.provide(RasterizedLineSegmentRenderableFactory.class),
                        providerDefinitionReader,
                        (short) 1
                ),
                new AntialiasedLineSegmentRenderableDefinitionReader(
                        ioModule.provide(AntialiasedLineSegmentRenderableFactory.class),
                        providerDefinitionReader
                ),
                new RectangleRenderableDefinitionReader(
                        ioModule.provide(RectangleRenderableFactory.class),
                        actions::get,
                        providerDefinitionReader,
                        nullProvider
                ),
                new TriangleRenderableDefinitionReader(
                        ioModule.provide(TriangleRenderableFactory.class),
                        actions::get,
                        providerDefinitionReader,
                        nullProvider
                ),
                new SpriteRenderableDefinitionReader(
                        ioModule.provide(SpriteRenderableFactory.class),
                        graphics::getSprite,
                        actions::get,
                        providerDefinitionReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new ImageAssetSetRenderableDefinitionReader(
                        ioModule.provide(ImageAssetSetRenderableFactory.class),
                        graphics::getImageAssetSet,
                        actions::get,
                        providerDefinitionReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new FiniteAnimationRenderableDefinitionReader(
                        ioModule.provide(FiniteAnimationRenderableFactory.class),
                        graphics::getAnimation,
                        actions::get,
                        providerDefinitionReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new TextLineRenderableDefinitionReader(
                        ioModule.provide(TextLineRenderableFactory.class),
                        graphics::getFont,
                        providerDefinitionReader,
                        nullProvider
                ),
                ioModule.provide(ComponentFactory.class),
                providerDefinitionReader,
                actions::get,
                wholeScreenProvider
        ));

        var customComponentMethods = Collections.setOf();

        var buttonReader = new ButtonDefinitionReader(
                providerDefinitionReader,
                shiftDefinitionReader,
                nullProvider,
                clock::globalTimestamp,
                textLineRenderer,
                actions::get,
                graphics::getFont,
                imgRelLoc -> graphics.getImage(imgRelLoc).textureId(),
                resManager::windowWidthToHeightRatio
        );
        //noinspection unchecked
        customComponentMethods.add(new ButtonMethods(
                id -> functions.get(PLAY_SOUND_METHOD_NAME).apply(id),
                subscribeToNextMouseEvent,
                graphics::getSprite
        ));

        renderableDefinitionReader.addCustomComponentReader(
                ButtonDefinition.class,
                d -> buttonReader.read((ButtonDefinition) d)
        );

        customComponentMethods.forEach(methods -> {
            var fromMethods = readMethods(methods);
            fromMethods.FIRST.forEach(a -> actions.put(a.id(), a));
            fromMethods.SECOND.forEach(f -> functions.put(f.id(), f));
        });
    }
}
