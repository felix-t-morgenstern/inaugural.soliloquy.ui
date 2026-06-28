package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.*;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static inaugural.soliloquy.ui.test.integration.display.DisplayTestMethods.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class RectangleRenderableDefinitionReaderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Rectangle renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Rectangle renderable definition reader", 4000),
                RectangleRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);
        var image = graphics.getImage(BACKGROUND_TEXTURE_RELATIVE_LOCATION);
        var textureOffsetProviderDef = loopingLinearMoving(
                2000,
                0,
                pairOf(0, 0f),
                pairOf(2000, 1f)
        );
        var rectDef = rectangle(
                staticVal(floatBoxOf(0.25f, 0.25f, 0.75f, 0.75f)),
                0
        )
                .withTexture(
                        staticVal(image.textureId())
                )
                .withTextureTilingDefs(
                        staticVal(0.5f),
                        staticVal(0.5f)
                )
                .withTextureTilingOffsetDefs(
                        textureOffsetProviderDef,
                        textureOffsetProviderDef
                )
                .withColors(
                        staticVal(randomColor()),
                        staticVal(randomColor()),
                        staticVal(randomColor()),
                        staticVal(randomColor())
                )
                .onPress(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        DisplayTest_onMousePress
                ))
                .onRelease(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        DisplayTest_onMouseRelease
                ))
                .onMouseOver(DisplayTest_onMouseOver)
                .onMouseLeave(DisplayTest_onMouseLeave);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, rectDef, timestamp(uiModule));
    }
}
