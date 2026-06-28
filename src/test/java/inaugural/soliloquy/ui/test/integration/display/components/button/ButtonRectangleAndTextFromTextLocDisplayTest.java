package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.TextMarkupParserMethods.coloredText;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.CENTER;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

public class ButtonRectangleAndTextFromTextLocDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button rectangle and text from text loc display test",
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
                () -> DisplayTest.runThenClose("Button rectangle and text from text loc", 16000),
                ButtonRectangleAndTextFromTextLocDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var lineHeight = 0.04f;

        var def = button(0)
                .withRectDefault(
                        rectangle()
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
                .textBlockDefinesRectDimens()
                .withTextBlockCenter(WINDOW_CENTER)
                .withTextBlockPadding(0.01f);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
