package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class CombinationTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Combination definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false),
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(
                                CINZEL_DEFINITION_DTO
                        ),
                        arrayOf(
                                new SpriteDefinitionDTO(SHIELD_SPRITE_ID, RPG_WEAPONS_RELATIVE_LOCATION,
                                        266, 271, 313, 343)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Combination definition reader", 4000),
                CombinationTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);

        var image = graphics.getImage(BACKGROUND_TEXTURE_RELATIVE_LOCATION);
        var rectDef = rectangle(
                staticVal(floatBoxOf(0.2f, 0.2f, 0.8f, 0.8f)),
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
                        ON_MOUSE_PRESS_CONSUMER_ID
                ))
                .onRelease(mapOf(
                        GLFW_MOUSE_BUTTON_LEFT,
                        ON_MOUSE_RELEASE_CONSUMER_ID
                ))
                .onMouseOver(ON_MOUSE_OVER_CONSUMER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_CONSUMER_ID);

        var spriteDef = sprite(SHIELD_SPRITE_ID, floatBoxOf(0.25f, 0.125f, 0.75f, 0.875f), 1)
                .withBorder(
                        staticVal(0.01f),
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

        var text = "This is the text!";
        var textLineDef = textLine(
                CINZEL_ID,
                staticVal(text),
                staticVal(vertexOf(0.5f, 0.475f)),
                staticVal(0.05f),
                HorizontalAlignment.CENTER,
                0f,
                2
        )
                .withColorDefs(rainbowGradient(text))
                .withItalics(listOf(5, 7, 12))
                .withBold(listOf(0, 4, 12))
                .withBorder(
                        staticVal(0.001f),
                        staticVal(Color.WHITE)
                )
                .withDropShadow(
                        staticVal(0.05f),
                        staticVal(vertexOf(0.0025f, 0.0025f)),
                        staticVal(Color.getHSBColor(0f, 0f, 0.5f))
                );


        var reader = uiModule.provide(RenderableDefinitionReader.class);
        reader.read(topLevelComponent, spriteDef, timestamp(uiModule));
        reader.read(topLevelComponent, rectDef, timestamp(uiModule));
        reader.read(topLevelComponent, textLineDef, timestamp(uiModule));
    }
}
