package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextLineRenderableDefinitionReaderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text line renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
                        arrayOf(
                                CINZEL_DEFINITION_DTO
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Text line renderable definition reader", 4000),
                TextLineRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var text = "This is the text!";
        var def = textLine(
                CINZEL_ID,
                staticVal(text),
                staticVal(vertexOf(0.5f, 0.475f)),
                staticVal(0.05f),
                TextJustification.CENTER,
                0f,
                0
        )
                .withColorDefs(rainbowGradient(text))
                .withItalics(listOf(5, 7, 12))
                .withBold(listOf(0, 4, 12))
                .withBorder(
                        staticVal(0.001f),
                        staticVal(Color.WHITE)
                )
                .withDropShadow(
                        staticVal(0.05f),
                        staticVal(vertexOf(0.0025f, 0.0025f)),
                        staticVal(Color.getHSBColor(0f, 0f, 0.5f))
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
