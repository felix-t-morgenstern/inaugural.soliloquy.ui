package inaugural.soliloquy.ui.test.integration.display.components.textblock;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.test.integration.display.components.textblock.TextBlockSimpleDisplayTest.makeTestTextBlockDef;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockRenderingLocOverrideDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text block rendering loc override display test",
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
                () -> DisplayTest.runThenClose("Text block rendering loc override", 800000),
                TextBlockRenderingLocOverrideDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var timestamp = timestamp(uiModule);
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        var providerReader = uiModule.provide(ProviderDefinitionReader.class);
        var componentOrigin = vertexOf(0.1f, 0.1f);
        var componentOriginProvider = providerReader.read(staticVal(componentOrigin), timestamp);

        reader.read(
                topLevelComponent,
                makeTestTextBlockDef(staticVal(vertexOf(0.25f, 0f)))
                        .withData(mapOf(COMPONENT_ORIGIN_PROVIDER, componentOriginProvider)),
                timestamp(uiModule)
        );

        reader.read(topLevelComponent, rectangle(floatBoxOf(0.25f, 0f, 0.75f, 1f), 0)
                .withColor(new Color(31, 31, 31)), timestamp(uiModule));
    }
}
