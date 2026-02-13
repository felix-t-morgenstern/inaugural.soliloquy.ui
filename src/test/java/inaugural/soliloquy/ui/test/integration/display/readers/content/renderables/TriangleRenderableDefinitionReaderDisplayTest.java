package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TriangleRenderableDefinitionReaderDisplayTest extends DisplayTest {
    private static final String BACKGROUND_TEXTURE_RELATIVE_LOCATION =
            "./src/test/resources/images/backgrounds/stone_tile_1.png";

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Triangle renderable definition reader display test",
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
                () -> DisplayTest.runThenClose("Triangle renderable definition reader", 4000),
                TriangleRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);
        var image = graphics.getImage(BACKGROUND_TEXTURE_RELATIVE_LOCATION);
        var def = triangle(
                staticVal(vertexOf(0.2f, 0.2f)),
                staticVal(vertexOf(0.8f, 0.4f)),
                staticVal(vertexOf(0.5f, 0.8f)),
                0
        )
                .withTexture(
                        staticVal(image.textureId()),
                        staticVal(0.6f),
                        staticVal(0.6f)
                )
                .withColors(
                        staticVal(randomColor()),
                        staticVal(randomColor()),
                        staticVal(randomColor())
                )
                .onPress(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        ON_MOUSE_PRESS_CONSUMER_ID
                ))
                .onRelease(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        ON_MOUSE_RELEASE_CONSUMER_ID
                ))
                .onMouseOver(ON_MOUSE_OVER_CONSUMER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_CONSUMER_ID);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
