package inaugural.soliloquy.ui.test.integration.display;

import inaugural.soliloquy.common.CommonModule;
import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.WindowResolution;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.FontDefinitionDTO;
import inaugural.soliloquy.io.api.dto.FontStyleDefinitionDTO;
import inaugural.soliloquy.io.api.dto.FontStyleDefinitionGlyphPropertyDTO;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.entities.Function;
import soliloquy.specs.gamestate.entities.Setting;
import soliloquy.specs.io.bootstrap.CoreLoop;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.FrameExecutor;
import soliloquy.specs.io.graphics.rendering.WindowDisplayMode;
import soliloquy.specs.io.graphics.rendering.timing.FrameTimer;
import soliloquy.specs.io.graphics.rendering.timing.GlobalClock;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static inaugural.soliloquy.io.api.Constants.*;
import static inaugural.soliloquy.io.api.Settings.*;
import static inaugural.soliloquy.io.api.dto.AssetType.*;
import static inaugural.soliloquy.tools.CheckedExceptionWrapper.sleep;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.reflection.Reflection.readMethods;
import static inaugural.soliloquy.ui.Constants.COLOR_PRESETS;
import static inaugural.soliloquy.ui.Settings.*;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class DisplayTest {
    protected static final String ON_MOUSE_OVER_ACTION_ID = "onMouseOver";
    protected static final String ON_MOUSE_LEAVE_ACTION_ID = "onMouseLeave";
    protected static final String ON_MOUSE_PRESS_ACTION_ID = "onMousePress";
    protected static final String ON_MOUSE_RELEASE_ACTION_ID = "onMouseRelease";

    protected final static WindowResolution DEFAULT_RES = WindowResolution.RES_1680x1050;
    private final static String SHADER_FILENAME_PREFIX =
            "./src/main/resources/shaders/defaultShader";

    @SuppressWarnings("rawtypes") private final Map<String, Action> ACTIONS;
    @SuppressWarnings("rawtypes") private final Map<String, Function> FUNCTIONS;

    private final static Set<String> AUDIO_DIR_RELATIVE_PATHS = setOf(
            "\\src\\test\\resources\\sounds\\ui\\button\\"
    );
    protected final static String PRESS_SOUND_ID = "pressSoundId";
    protected final static String RELEASE_SOUND_ID = "releaseSoundId";

    protected static final String BACKGROUND_TEXTURE_RELATIVE_LOCATION =
            "./src/test/resources/images/backgrounds/stone_tile_1.png";

    protected final static String SHIELD_SPRITE_ID = "shieldSpriteId";
    protected final static String RPG_WEAPONS_RELATIVE_LOCATION =
            "./src/test/resources/images/items/RPG_Weapons.png";

    protected final static String CINZEL_ID = "cinzel";
    protected final static String RELATIVE_LOCATION_CINZEL =
            "./src/test/resources/fonts/Cinzel-VariableFont_wght.ttf";
    protected final static float MAX_LOSSLESS_FONT_SIZE_CINZEL = 200f;
    protected final static float ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL = 0.25f;
    protected final static float ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL = 0.25f;
    protected final static float LEADING_ADJUSTMENT_CINZEL = 0f;
    protected final static FontStyleDefinitionGlyphPropertyDTO[] CINZEL_ITALIC_WIDTH_FACTORS =
            arrayOf(
                    new FontStyleDefinitionGlyphPropertyDTO('I', 0.965f),
                    new FontStyleDefinitionGlyphPropertyDTO('W', 0.975f),
                    new FontStyleDefinitionGlyphPropertyDTO('i', 0.965f),
                    new FontStyleDefinitionGlyphPropertyDTO('w', 0.975f),
                    new FontStyleDefinitionGlyphPropertyDTO('^', 0.975f)
            );
    protected final static FontDefinitionDTO CINZEL_DEFINITION_DTO =
            new FontDefinitionDTO(
                    CINZEL_ID,
                    RELATIVE_LOCATION_CINZEL,
                    MAX_LOSSLESS_FONT_SIZE_CINZEL,
                    LEADING_ADJUSTMENT_CINZEL,
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                            arrayOf(),
                            arrayOf(),
                            arrayOf(),
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                            arrayOf(),
                            arrayOf(),
                            CINZEL_ITALIC_WIDTH_FACTORS,
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                            arrayOf(),
                            arrayOf(),
                            arrayOf(),
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                            arrayOf(),
                            arrayOf(),
                            CINZEL_ITALIC_WIDTH_FACTORS,
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                    )
            );

    protected final static String MERRIWEATHER_ID = "merriweather";
    protected final static String RELATIVE_LOCATION_MERRIWEATHER =
            "./src/test/resources/fonts/Merriweather-Regular.ttf";
    protected final static float MAX_LOSSLESS_FONT_SIZE_MERRIWEATHER = 200f;
    protected final static float ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_MERRIWEATHER = 0.25f;
    protected final static float ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_MERRIWEATHER = 0.25f;
    protected final static float LEADING_ADJUSTMENT_MERRIWEATHER = 0f;
    protected final static FontStyleDefinitionGlyphPropertyDTO[] MERRIWEATHER_ITALIC_WIDTH_FACTORS =
            arrayOf();
    protected final static FontDefinitionDTO MERRIWEATHER_DEFINITION_DTO =
            new FontDefinitionDTO(
                    MERRIWEATHER_ID,
                    RELATIVE_LOCATION_MERRIWEATHER,
                    MAX_LOSSLESS_FONT_SIZE_MERRIWEATHER,
                    LEADING_ADJUSTMENT_MERRIWEATHER,
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_MERRIWEATHER,
                            arrayOf(),
                            arrayOf(),
                            arrayOf(),
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_MERRIWEATHER
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_MERRIWEATHER,
                            arrayOf(),
                            arrayOf(),
                            MERRIWEATHER_ITALIC_WIDTH_FACTORS,
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_MERRIWEATHER
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_MERRIWEATHER,
                            arrayOf(),
                            arrayOf(),
                            arrayOf(),
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_MERRIWEATHER
                    ),
                    new FontStyleDefinitionDTO(
                            ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_MERRIWEATHER,
                            arrayOf(),
                            arrayOf(),
                            MERRIWEATHER_ITALIC_WIDTH_FACTORS,
                            ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_MERRIWEATHER
                    )
            );

    public Component topLevelComponent;

    public DisplayTest() {
        ACTIONS = mapOf();
        FUNCTIONS = mapOf();

        var methods = readMethods(DisplayTestMethods.class);

        methods.FIRST.forEach(a -> ACTIONS.put(a.id(), a));
        methods.SECOND.forEach(f -> FUNCTIONS.put(f.id(), f));
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
                generateMockSetting(setOf("wav", "mp3")),
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
                generateMockSetting(DEFAULT_RES),
                DEFAULT_FONT_COLOR_ID,
                generateMockSetting(Color.WHITE),
                AUDIO_RELATIVE_DIRS_ID,
                generateMockSetting(AUDIO_DIR_RELATIVE_PATHS),
                DEFAULT_KEY_BINDING_PRIORITY_SETTING_ID,
                generateMockSetting(0),
                DEFAULT_TEXT_COLOR_SETTING_ID,
                generateMockSetting(Color.WHITE),
                COLOR_PRESETS_SETTING_ID,
                generateMockSetting(COLOR_PRESETS)
        );

        var ioModule = new IOModule(
                commonModule,
                settings::get,
                ACTIONS,
                FUNCTIONS,
                listOf(),
                testName,
                mapOf(
                        "JDSherbert - Ultimate UI SFX Pack - Cursor - 5.wav",
                        PRESS_SOUND_ID,
                        "JDSherbert - Ultimate UI SFX Pack - Select - 1.wav",
                        RELEASE_SOUND_ID
                ),
                mapOf(),
                mapOf(),
                assetDefinitionsDTO
        );

        var uiModule = new UIModule(
                ioModule,
                settings::get,
                ACTIONS,
                FUNCTIONS
        );

        var coreLoop = ioModule.provide(CoreLoop.class);

        var frameTimer = ioModule.provide(FrameTimer.class);
        frameTimer.setTargetFps(null);

        var frameExecutor = ioModule.provide(FrameExecutor.class);
        var componentFactory = ioModule.provide(ComponentFactory.class);
        @SuppressWarnings("rawtypes") BiFunction<UUID, Object, ProviderAtTime>
                staticProviderFactory = ioModule.provide(STATIC_PROVIDER_FACTORY);
        var wholeScreenProvider = staticProviderFactory.apply(randomUUID(), WHOLE_SCREEN);
        //noinspection unchecked
        topLevelComponent =
                componentFactory.make(randomUUID(), 0, setOf(), false, 0, wholeScreenProvider, null,
                        mapOf());
        frameExecutor.setTopLevelComponent(topLevelComponent);

        coreLoop.startup(() -> {
            if (populateTopLevelComponent != null) {
                populateTopLevelComponent.accept(uiModule, topLevelComponent);
            }

            displayTest.run();
        });
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

    protected static long timestamp(UIModule uiModule) {
        var ioModule = uiModule.provide(IOModule.class);
        var globalClock = ioModule.provide(GlobalClock.class);
        return globalClock.globalTimestamp();
    }

    @SuppressWarnings("SameParameterValue")
    protected static Map<Integer, AbstractProviderDefinition<Color>> rainbowGradient(
            String lineText) {
        var rainbowGradient = Collections.<Integer, AbstractProviderDefinition<Color>>mapOf();

        var degreePerLetter = 360f / lineText.length();
        for (var i = 0; i < lineText.length(); i++) {
            rainbowGradient.put(i, staticVal(colorAtDegree((float) i * degreePerLetter)));
        }
        return rainbowGradient;
    }

    private static Color colorAtDegree(float degree) {
        var red = getColorComponent(0f, degree);
        var green = getColorComponent(120f, degree);
        var blue = getColorComponent(240f, degree);

        return new Color(red, green, blue, 1f);
    }

    private static float getColorComponent(float componentCenter, float degree) {
        var degreesInCircle = 360f;
        var halfOfCircle = 180f;
        var sixthOfCircle = 60f;
        var degreeModulo = degree % degreesInCircle;
        var distance = componentCenter - degreeModulo;
        if (distance < -halfOfCircle) {
            distance += degreesInCircle;
        }
        var absVal = Math.abs(distance);
        if (absVal <= sixthOfCircle) {
            return 1f;
        }
        absVal -= sixthOfCircle;
        var absValWithCeiling = Math.min(sixthOfCircle, absVal);
        var amountOfSixthOfCircle = sixthOfCircle - absValWithCeiling;
        @SuppressWarnings("UnnecessaryLocalVariable")
        var colorComponent = amountOfSixthOfCircle / sixthOfCircle;
        return colorComponent;
    }
}
