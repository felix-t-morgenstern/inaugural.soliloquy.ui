package inaugural.soliloquy.ui;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.tools.module.AbstractModule;
import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinition;
import inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinitionReader;
import inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.components.button.ButtonDefinitionReader;
import inaugural.soliloquy.ui.components.button.ButtonMethods;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinition;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinitionReader;
import inaugural.soliloquy.ui.components.textblock.TextBlockMethods;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.content.renderables.*;
import inaugural.soliloquy.ui.readers.providers.*;
import org.apache.commons.lang3.function.TriConsumer;
import soliloquy.specs.common.entities.Methods;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.gamestate.entities.Setting;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.factories.*;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.factories.*;
import soliloquy.specs.io.graphics.rendering.WindowResolutionManager;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.io.graphics.rendering.timing.GlobalClock;
import soliloquy.specs.io.input.mouse.MouseEventHandler;
import soliloquy.specs.ui.definitions.providers.*;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import static inaugural.soliloquy.io.api.Constants.*;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.reflection.Reflection.readMethods;
import static inaugural.soliloquy.ui.Settings.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class UIModule extends AbstractModule {
    public UIModule(
            IOModule ioModule,
            @SuppressWarnings("rawtypes")
            java.util.function.Function<String, Setting> getSetting,
            Methods methods
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

        // >>> Provider Definition Reader

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

        // >>> Color Shift Definition Reader

        var shiftDefinitionReader = new ColorShiftDefinitionReader(providerDefinitionReader);

        // >>> Renderable Definition Readers

        var defaultKeyBindingPriority =
                (int) (getSetting.apply(DEFAULT_KEY_BINDING_PRIORITY_SETTING_ID).getValue());
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
                        methods.CONSUMERS::get,
                        providerDefinitionReader,
                        nullProvider
                ),
                new TriangleRenderableDefinitionReader(
                        ioModule.provide(TriangleRenderableFactory.class),
                        methods.CONSUMERS::get,
                        providerDefinitionReader,
                        nullProvider
                ),
                new SpriteRenderableDefinitionReader(
                        ioModule.provide(SpriteRenderableFactory.class),
                        graphics::getSprite,
                        methods.CONSUMERS::get,
                        providerDefinitionReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new ImageAssetSetRenderableDefinitionReader(
                        ioModule.provide(ImageAssetSetRenderableFactory.class),
                        graphics::getImageAssetSet,
                        methods.CONSUMERS::get,
                        providerDefinitionReader,
                        shiftDefinitionReader,
                        nullProvider
                ),
                new FiniteAnimationRenderableDefinitionReader(
                        ioModule.provide(FiniteAnimationRenderableFactory.class),
                        graphics::getAnimation,
                        methods.CONSUMERS::get,
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
                methods.CONSUMERS::get,
                wholeScreenProvider,
                defaultKeyBindingPriority
        ));

        // >>> Custom component readers

        var defaultTextColor = (Color) (getSetting.apply(DEFAULT_TEXT_COLOR_SETTING_ID).getValue());
        @SuppressWarnings("unchecked") var defaultColorPresets =
                (Map<Set<String>, Color>) (getSetting.apply(COLOR_PRESETS_SETTING_ID).getValue());
        var markupParser = andRegister(new TextMarkupParserImpl(
                defaultTextColor,
                defaultColorPresets,
                textLineRenderer
        ));

        var customComponentMethods = Collections.setOf();

        // Component general methods
        var componentMethods = new ComponentMethods(graphics::getComponent, functionalProviderDefReader);
        customComponentMethods.add(componentMethods);

        // Button
        var buttonReader = new ButtonDefinitionReader(
                providerDefinitionReader,
                shiftDefinitionReader,
                nullProvider,
                textLineRenderer,
                methods.CONSUMERS::get,
                graphics::getFont,
                imgRelLoc -> graphics.getImage(imgRelLoc).textureId(),
                resManager::windowWidthToHeightRatio
        );
        //noinspection unchecked
        customComponentMethods.add(new ButtonMethods(
                id -> methods.FUNCTIONS.get(PLAY_SOUND_METHOD_NAME).apply(id),
                subscribeToNextMouseEvent,
                graphics::getSprite,
                componentMethods
        ));
        renderableDefinitionReader.addCustomComponentReader(
                ButtonDefinition.class,
                (d, t) -> buttonReader.read((ButtonDefinition) d, t)
        );

        // Beveled Button
        var beveledButtonReader =
                new BeveledButtonDefinitionReader(buttonReader, providerDefinitionReader);
        customComponentMethods.add(new BeveledButtonMethods(graphics::getComponent));
        renderableDefinitionReader.addCustomComponentReader(
                BeveledButtonDefinition.class,
                (d, t) -> beveledButtonReader.read((BeveledButtonDefinition) d, t)
        );

        // Text Block
        var textBlockReader = new TextBlockDefinitionReader(markupParser, graphics::getFont,
                providerDefinitionReader);
        customComponentMethods.add(new TextBlockMethods(graphics::getComponent));
        renderableDefinitionReader.addCustomComponentReader(TextBlockDefinition.class,
                (d, t) -> textBlockReader.read((TextBlockDefinition) d, t));

        customComponentMethods.forEach(m -> methods.concatenate(readMethods(m)));
    }
}
