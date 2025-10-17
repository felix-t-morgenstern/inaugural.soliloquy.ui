package inaugural.soliloquy.ui.test.integration.display.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonFromSpriteSimpleDisplayTest extends ButtonDisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from sprite simple display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(),
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
                () -> DisplayTest.runThenClose("Button definition from sprite simple", 8000),
                ButtonFromSpriteSimpleDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDef = testButtonFromSprite(
                SHIELD_SPRITE_ID,
                SPRITE_DIMENS
        );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, buttonDef, timestamp(uiModule));
    }
}
