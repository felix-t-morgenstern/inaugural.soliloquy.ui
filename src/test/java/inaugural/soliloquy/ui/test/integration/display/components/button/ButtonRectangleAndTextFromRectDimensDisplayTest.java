package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.Constants.NO_MAX_LINE_LENGTH;
import static inaugural.soliloquy.ui.Constants.WHITE;
import static inaugural.soliloquy.ui.TextMarkupParserMethods.coloredText;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.*;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

public class ButtonRectangleAndTextFromRectDimensDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button rectangle and text from rect dimens display test",
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
                () -> DisplayTest.runThenClose("Button rectangle and text from rect dimens", 16000),
                ButtonRectangleAndTextFromRectDimensDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var lineHeight = 0.04f;

        var def = button(0)
                .withRectDefault(
                        rectangle(
                                floatBoxOf(0.4f, 0.45f, 0.6f, 0.55f),
                                0
                        )
                                .withColor(Color.RED)
                )
                .withTextBlockDef(
                        textBlock(
                                MERRIWEATHER_ID,
                                lineHeight,
                                NO_MAX_LINE_LENGTH,
                                coloredText(WHITE,"Button")
                        )
                                .withHorizontalAlignment(CENTER)
                )
                .rectDefinesTextDimens(LEFT)
                .withTextBlockPadding(0.01f);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
