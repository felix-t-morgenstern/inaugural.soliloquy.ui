package inaugural.soliloquy.ui.test.integration.display;

import inaugural.soliloquy.common.CommonModule;
import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.WindowResolution;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.gamestate.entities.Setting;
import soliloquy.specs.io.graphics.bootstrap.GraphicsCoreLoop;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.StaticProviderFactory;
import soliloquy.specs.io.graphics.rendering.FrameExecutor;
import soliloquy.specs.io.graphics.rendering.WindowDisplayMode;
import soliloquy.specs.io.graphics.rendering.timing.FrameTimer;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static inaugural.soliloquy.io.api.Constants.WHOLE_SCREEN;
import static inaugural.soliloquy.io.api.Settings.*;
import static inaugural.soliloquy.io.api.dto.AssetType.*;
import static inaugural.soliloquy.tools.CheckedExceptionWrapper.sleep;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class DisplayTest {
    private final static String SHADER_FILENAME_PREFIX =
            "./src/main/resources/shaders/defaultShader";

    @SuppressWarnings("rawtypes") private final Map<String, Action> ACTIONS;

    private static StaticProviderFactory StaticProviderFactory;

    public Component topLevelComponent;

    public DisplayTest() {
        this(setOf());
    }

    public DisplayTest(@SuppressWarnings("rawtypes") Set<Action> actions) {
        ACTIONS = mapOf();
        Check.ifNull(actions, "actions").forEach(action -> ACTIONS.put(action.id(), action));
    }

    public void runTest(
            String testName,
            AssetDefinitionsDTO assetDefinitionsDTO,
            Runnable displayTest,
            BiConsumer<UIModule, Component> populateTopLevelComponent
    ) {
        var commonModule = new CommonModule();

        var meshVerticesAndUvCoords = new float[]{0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f};

        // Many of these are dummy values which should be tweaked for performance
        @SuppressWarnings("rawtypes") var settings = Collections.<String, Setting>mapOf(
                AUDIO_FILETYPES_ID,
                setOf(),
                PERIODS_PER_FRAME_RATE_REPORT_AGGREGATE_ID,
                generateMockSetting(10),
                FRAME_TIMER_POLLING_INTERVAL_ID,
                generateMockSetting(100),
                FRAME_EXECUTOR_SEMAPHORE_PERMISSIONS_ID,
                generateMockSetting(3),
                SHADER_FILENAME_PREFIX_ID,
                generateMockSetting(SHADER_FILENAME_PREFIX),
                MESH_VERTICES_ID,
                generateMockSetting(meshVerticesAndUvCoords),
                MESH_UV_COORDS_ID,
                generateMockSetting(meshVerticesAndUvCoords),
                MOUSE_CAPTURE_ALPHA_THRESHOLD_ID,
                generateMockSetting(0.5f),
                GRAPHICS_PRELOADER_THREAD_POOL_SIZE_ID,
                generateMockSetting(4),
                GRAPHICS_PRELOADER_ASSET_TYPE_BATCH_SIZES_ID,
                generateMockSetting(mapOf(setOf(
                        IMAGE,
                        SPRITE,
                        ANIMATION,
                        GLOBAL_LOOPING_ANIMATION,
                        IMAGE_ASSET_SET,
                        FONT,
                        MOUSE_CURSOR_IMAGE,
                        ANIMATED_MOUSE_CURSOR_PROVIDER,
                        STATIC_MOUSE_CURSOR_PROVIDER
                ).stream().map(assetType -> pairOf(assetType, 10)))),
                STARTING_WINDOW_DISPLAY_MODE_ID,
                generateMockSetting(WindowDisplayMode.WINDOWED),
                STARTING_WINDOW_RESOLUTION_ID,
                generateMockSetting(WindowResolution.RES_1680x1050),
                DEFAULT_FONT_COLOR_ID,
                generateMockSetting(Color.WHITE)
        );

        var ioModule = new IOModule(
                commonModule,
                settings::get,
                ACTIONS::get,
                listOf(),
                testName,
                assetDefinitionsDTO
        );

        var uiModule = new UIModule(
                ioModule,
                ACTIONS::get
        );

        var coreLoop = ioModule.provide(GraphicsCoreLoop.class);

        var frameTimer = ioModule.provide(FrameTimer.class);
        frameTimer.setTargetFps(null);

        var frameExecutor = ioModule.provide(FrameExecutor.class);
        var componentFactory = ioModule.provide(ComponentFactory.class);
        StaticProviderFactory = ioModule.provide(StaticProviderFactory.class);
        var wholeScreenProvider = staticProvider(WHOLE_SCREEN);
        topLevelComponent = componentFactory.make(randomUUID(), 0, wholeScreenProvider, null);
        frameExecutor.setTopLevelComponent(topLevelComponent);

        coreLoop.startup(() -> {
            if (populateTopLevelComponent != null) {
                populateTopLevelComponent.accept(uiModule, topLevelComponent);
            }

            displayTest.run();
        });
    }

    protected static <T> StaticProvider<T> staticProvider(T val) {
        return StaticProviderFactory.make(randomUUID(), val);
    }

    protected static <T> StaticProvider<T> nullProvider() {
        return staticProvider(null);
    }

    protected static void runThenClose(String testName, int ms) {
        System.out.println(testName + " display test started");
        sleep(ms);
        System.out.println(testName + " display test ended");
    }

    private <T> Setting<T> generateMockSetting(T val) {
        @SuppressWarnings("unchecked") var mockSetting = (Setting<T>) mock(Setting.class);

        when(mockSetting.getValue()).thenReturn(val);

        return mockSetting;
    }
}
