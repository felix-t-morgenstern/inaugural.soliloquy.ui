package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.Constants.NO_MAX_LINE_LENGTH;
import static inaugural.soliloquy.ui.Constants.WINDOW_CENTER;
import static inaugural.soliloquy.ui.TextMarkupParserMethods.coloredText;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.DisplayTestMethods.DisplayTest_onMousePress;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.CENTER;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;

public class ButtonAutomaticRectangleAroundTextDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button automatic rectangle around text display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false),
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(
                                new SpriteDefinitionDTO(SHIELD_SPRITE_ID,
                                        RPG_WEAPONS_RELATIVE_LOCATION,
                                        266, 271, 313, 343)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Button automatic rectangle around text", 16000),
                ButtonAutomaticRectangleAroundTextDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var lineHeight = 0.05f;
        var spriteDimens = floatBoxOf(0.475f, 0.45f, 0.525f, 0.55f);
        var brightnessAdj = 0.2f;

        var def = button(0)
                .withTextBlockDef(
                        textBlock(
                                MERRIWEATHER_ID,
                                lineHeight,
                                NO_MAX_LINE_LENGTH,
                                coloredText(Constants.WHITE, "Button")
                        )
                                .withHorizontalAlignment(CENTER)
                )
                .withTextBlockCenter(WINDOW_CENTER)
                .withTextBlockPadding(0.01f)
                .withImageAsset(sprite(SHIELD_SPRITE_ID, spriteDimens))
                .withImageAssetHover(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(brightnessAdj, false)))
                .withImageAssetPressed(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(-brightnessAdj, false)))
                .withPressSound(PRESS_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID)
                .onPress(DisplayTest_onMousePress);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
