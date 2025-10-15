package inaugural.soliloquy.ui.test.integration.display.button;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.test.integration.display.button.ButtonFromRectDimensWithTextDisplayTest.testButton;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonFromRectDimensWithTextAndTexDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition from rect dimens with text and texture display test",
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
                () -> DisplayTest.runThenClose("Button definition from rect dimens with text and texture", 8000),
                ButtonFromRectDimensWithTextAndTexDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDefLeft = testButton(
                floatBoxOf(0.05f, 0.4f, 0.25f, 0.6f),
                "Left",
                TextJustification.LEFT
        )
                .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION);

        var buttonDefCenter = testButton(
                floatBoxOf(0.4f, 0.4f, 0.6f, 0.6f),
                "Center",
                TextJustification.CENTER
        )
                .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION);

        var buttonDefRight = testButton(
                floatBoxOf(0.75f, 0.4f, 0.95f, 0.6f),
                "Right",
                TextJustification.RIGHT
        )
                .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        setOf(
                buttonDefLeft,
                buttonDefCenter,
                buttonDefRight
        ).forEach(d -> reader.read(topLevelComponent, d, timestamp(uiModule)));
    }
}
