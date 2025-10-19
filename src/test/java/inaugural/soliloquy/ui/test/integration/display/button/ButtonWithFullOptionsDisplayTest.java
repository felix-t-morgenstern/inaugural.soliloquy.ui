package inaugural.soliloquy.ui.test.integration.display.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.io.api.Constants.SCREEN_CENTER;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;

public class ButtonWithFullOptionsDisplayTest extends ButtonDisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition with key binding display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false),
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
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
                () -> DisplayTest.runThenClose("Button definition with key binding", 800000),
                ButtonWithFullOptionsDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDef = testFullDefFromText("Button", SCREEN_CENTER)
                .withTextItalicIndices(listOf(listOf(0, 1)))
                .withKey(GLFW_KEY_B, 0)
                .withSprite(
                        SHIELD_SPRITE_ID,
                        SPRITE_DIMENS
                )
                .withSpriteColorShiftHover(brightness(SPRITE_PRESS_SHADING, false))
                .withSpriteColorShiftPressed(brightness(-SPRITE_PRESS_SHADING, false));

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        setOf(
                buttonDef
        ).forEach(d -> reader.read(topLevelComponent, d, timestamp(uiModule)));
    }
}
