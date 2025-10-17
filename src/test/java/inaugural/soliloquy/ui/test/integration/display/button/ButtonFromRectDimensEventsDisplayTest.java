package inaugural.soliloquy.ui.test.integration.display.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonFromRectDimensEventsDisplayTest extends ButtonDisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from rect dimens events display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Button definition from rect dimens events", 8000),
                ButtonFromRectDimensEventsDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDef = button(
                floatBoxOf(0.4f, 0.4f, 0.6f, 0.6f),
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
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, buttonDef, timestamp(uiModule));
    }
}
