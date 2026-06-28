package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.test.integration.display.DisplayTestMethods.DisplayTest_onMousePress;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;

public class ButtonImageAssetSimpleDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button imageAsset simple display test",
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
                () -> DisplayTest.runThenClose("Button imageAsset simple", 16000),
                ButtonImageAssetSimpleDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var spriteDimens = floatBoxOf(0.45f, 0.4f, 0.55f, 0.6f);

        var def = button(0)
                .withImageAsset(sprite(SHIELD_SPRITE_ID, spriteDimens))
                .withImageAssetHover(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(0.1f, false)))
                .withImageAssetPressed(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(-0.1f, false)))
                .withPressSound(PRESS_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID)
                .onPress(DisplayTest_onMousePress);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
