package inaugural.soliloquy.ui.test.integration.display.readers;

import inaugural.soliloquy.io.api.dto.*;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.AntialiasedLineSegmentRenderableDefinition.antialiasedLine;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class AntialiasedLineSegmentRenderableDefinitionReaderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        var displayTest = new DisplayTest();
        displayTest.runTest(
                "AntialiasedRenderable definition reader display test",
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
                () -> DisplayTest.runThenClose("AntialiasedRenderable definition reader", 4000),
                AntialiasedLineSegmentRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var lineDef = antialiasedLine(
                staticVal(vertexOf(0.1f, 0.3f)),
                staticVal(vertexOf(0.9f, 0.7f)),
                staticVal(0.002f),
                staticVal(randomColor()),
                staticVal(.1f),
                staticVal(.005f),
                0
        );
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        reader.read(topLevelComponent, lineDef, timestamp(uiModule));
    }
}
