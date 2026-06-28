package inaugural.soliloquy.ui.test.integration.display.components.textblock;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.test.integration.display.components.textblock.TextBlockSimpleDisplayTest.makeTestTextBlockDef;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class TextBlockMovingRenderingLocDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text block with moving rendering location display test",
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
                () -> DisplayTest.runThenClose("Text block with moving rendering location", 800000),
                TextBlockMovingRenderingLocDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var defs = Collections.<AbstractContentDefinition>setOf();

        defs.add(rectangle(floatBoxOf(0.25f, 0f, 0.75f, 1f), 0)
                .withColor(new Color(31, 31, 31)));

        var textBlockDef = makeTestTextBlockDef(finiteSinusoidMoving(
                pairOf(1000, vertexOf(0.25f, 1f)),
                pairOf(2000, vertexOf(0.25f, 0f))
        ));

        defs.add(textBlockDef);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        defs.forEach(d -> reader.read(topLevelComponent, d, timestamp(uiModule)));
    }
}
