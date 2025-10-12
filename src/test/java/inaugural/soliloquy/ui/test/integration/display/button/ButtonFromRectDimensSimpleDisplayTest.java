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

public class ButtonFromRectDimensSimpleDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from rect dimens simple display test",
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
                () -> DisplayTest.runThenClose("Button definition from rect dimens simple", 4000),
                ButtonFromRectDimensSimpleDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDef = button(
                floatBoxOf(0.45f, 0.45f, 0.55f, 0.55f),
                0
        )
                .withBgColor(Color.RED);

        var renderableDefReader = uiModule.provide(RenderableDefinitionReader.class);

        renderableDefReader.read(topLevelComponent, buttonDef, timestamp(uiModule));
    }
}
