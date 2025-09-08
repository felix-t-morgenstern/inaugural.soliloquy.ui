package inaugural.soliloquy.ui.test.integration.display.readers;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.*;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.rendering.timing.GlobalClock;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static soliloquy.specs.common.entities.Action.action;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class RectangleRenderableDefinitionReaderDisplayTest extends DisplayTest {
    private static final String BACKGROUND_TEXTURE_RELATIVE_LOCATION =
            "./src/test/resources/images/backgrounds/stone_tile_1.png";
    private static final String ON_MOUSE_OVER_ACTION_ID = "onMouseOver";
    private static final String ON_MOUSE_LEAVE_ACTION_ID = "onMouseLeave";
    private static final String ON_MOUSE_PRESS_ACTION_ID = "onMousePress";
    private static final String ON_MOUSE_RELEASE_ACTION_ID = "onMouseRelease";

    public static void main(String[] args) {
        var displayTest = new DisplayTest(
                setOf(
                        action(ON_MOUSE_OVER_ACTION_ID, _ -> System.out.println("MOUSE OVER")),
                        action(ON_MOUSE_LEAVE_ACTION_ID, _ -> System.out.println("MOUSE LEAVE")),
                        action(ON_MOUSE_PRESS_ACTION_ID, _ -> System.out.println("MOUSE PRESS")),
                        action(ON_MOUSE_RELEASE_ACTION_ID, _ -> System.out.println("MOUSE RELEASE"))
                )
        );
        displayTest.runTest(
                "Simple Rectangle definition reader display test",
                new AssetDefinitionsDTO(
                        new ImageDefinitionDTO[]{
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false)
                        },
                        new FontDefinitionDTO[]{},
                        new SpriteDefinitionDTO[]{},
                        new AnimationDefinitionDTO[]{},
                        new GlobalLoopingAnimationDefinitionDTO[]{},
                        new ImageAssetSetDefinitionDTO[]{},
                        new MouseCursorImageDefinitionDTO[]{},
                        new AnimatedMouseCursorDefinitionDTO[]{},
                        new StaticMouseCursorDefinitionDTO[]{}
                ),
                () -> DisplayTest.runThenClose("Simple Rectangle definition reader", 4000),
                RectangleRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);
        var image = graphics.getImage(BACKGROUND_TEXTURE_RELATIVE_LOCATION);
        var globalClock = ioModule.provide(GlobalClock.class);
        var rectDef = rectangle(
                staticVal(floatBoxOf(0.25f, 0.25f, 0.75f, 0.75f)),
                0
        )
                .withTexture(
                        staticVal(image.textureId()),
                        staticVal(0.5f),
                        staticVal(0.5f)
                )
                .withColors(
                        staticVal(randomColor()),
                        staticVal(randomColor()),
                        staticVal(randomColor()),
                        staticVal(randomColor())
                )
                .onPress(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        ON_MOUSE_PRESS_ACTION_ID
                ))
                .onRelease(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        ON_MOUSE_RELEASE_ACTION_ID
                ))
                .onMouseOver(ON_MOUSE_OVER_ACTION_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ACTION_ID);

        var renderableDefReader = uiModule.provide(RenderableDefinitionReader.class);

        renderableDefReader.read(topLevelComponent, rectDef, globalClock.globalTimestamp());
    }
}
