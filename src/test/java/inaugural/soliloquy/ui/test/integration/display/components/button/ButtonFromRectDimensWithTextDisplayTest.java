package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonFromRectDimensWithTextDisplayTest extends ButtonDisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from rect dimens with text display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
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
        var buttonDefLeft = testButtonFromRectDimens(
                floatBoxOf(0.05f, 0.4f, 0.25f, 0.6f),
                "Left",
                TextJustification.LEFT
        );

        var buttonDefCenter = testButtonFromRectDimens(
                floatBoxOf(0.4f, 0.4f, 0.6f, 0.6f),
                "Center",
                TextJustification.CENTER
        );

        var buttonDefRight = testButtonFromRectDimens(
                floatBoxOf(0.75f, 0.4f, 0.95f, 0.6f),
                "Right",
                TextJustification.RIGHT
        );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        setOf(
                buttonDefLeft,
                buttonDefCenter,
                buttonDefRight
        ).forEach(d -> reader.read(topLevelComponent, d, timestamp(uiModule)));
    }
}
