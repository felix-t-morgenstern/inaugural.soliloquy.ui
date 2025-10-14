package inaugural.soliloquy.ui.test.integration.display.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonFromRectDimensWithTextDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from rect dimens with text display test",
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
                () -> DisplayTest.runThenClose("Button definition from rect dimens with text", 8000),
                ButtonFromRectDimensWithTextDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDefLeft = button(
                floatBoxOf(0.05f, 0.4f, 0.25f, 0.6f),
                0
        )
                .withBgColors(
                        new Color(255, 0, 127),
                        Color.RED,
                        Color.RED,
                        new Color(255, 127, 0)
                )
                .withBgColorsHover(
                        new Color(127, 255, 0),
                        Color.GREEN,
                        Color.GREEN,
                        new Color(0, 255, 127)
                )
                .withBgColorsPressed(
                        new Color(0, 127, 255),
                        Color.BLUE,
                        Color.BLUE,
                        new Color(127, 0, 255)
                )
                .withText(
                        "Left",
                        MERRIWEATHER_ID,
                        0.1f
                )
                .withTextColor(Color.CYAN)
                .withTextColorHover(Color.MAGENTA)
                .withTextColorPressed(Color.YELLOW)
                .withTextJustification(TextJustification.LEFT);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, buttonDefLeft, timestamp(uiModule));
    }
}
