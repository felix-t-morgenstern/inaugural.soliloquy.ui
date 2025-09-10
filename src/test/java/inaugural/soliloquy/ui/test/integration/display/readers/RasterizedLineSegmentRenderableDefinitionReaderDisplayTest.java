package inaugural.soliloquy.ui.test.integration.display.readers;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RasterizedLineSegmentRenderable;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomColor;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RasterizedLineSegmentRenderableDefinition.rasterizedLineSegment;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class RasterizedLineSegmentRenderableDefinitionReaderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        var displayTest = new DisplayTest(MOUSE_ACTIONS);
        displayTest.runTest(
                "Rasterized line segment renderable definition reader display test",
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
                () -> DisplayTest.runThenClose("Rasterized line segment renderable definition reader", 4000),
                RasterizedLineSegmentRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var def = rasterizedLineSegment(
                staticVal(vertexOf(0.1f, 0.4f)),
                staticVal(vertexOf(0.9f, 0.6f)),
                staticVal(10f),
                staticVal(randomColor()),
                0
        );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
