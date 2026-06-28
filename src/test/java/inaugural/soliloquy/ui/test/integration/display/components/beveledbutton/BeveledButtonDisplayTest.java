package inaugural.soliloquy.ui.test.integration.display.components.beveledbutton;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.Constants.NO_MAX_LINE_LENGTH;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinition.beveledButton;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

public class BeveledButtonDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Beveled button display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Beveled button", 16000),
                BeveledButtonDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var def = makeBeveledButton();

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }

    public static ButtonDefinition makeBeveledButton() {
        var lineHeight = 0.075f;

        return beveledButton(
                0,
                rectangle()
                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                        .withColor(randomHighSaturationColor()),
                0.05f,
                0.125f
        )
                .withTextBlockDef(
                        textBlock(
                                MERRIWEATHER_ID,
                                lineHeight,
                                NO_MAX_LINE_LENGTH,
                                "Button"
                        )
                )
                .withTextBlockPadding(0.025f)
                .textBlockDefinesRectDimens();
    }
}