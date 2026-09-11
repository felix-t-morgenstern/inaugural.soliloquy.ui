package inaugural.soliloquy.ui.test.integration.display;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.Denormalization.denorm;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class DenormalizationDisplayTest  extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Denormalization display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Denormalization", 4000),
                DenormalizationDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var rectDef = rectangle(
                denorm(floatBoxOf(vertexOf(0.25f, 0.25f), 0.25f, 0.25f)),
                0
        )
                .withColors(
                        staticVal(randomHighSaturationColor()),
                        staticVal(randomHighSaturationColor()),
                        staticVal(randomHighSaturationColor()),
                        staticVal(randomHighSaturationColor())
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, rectDef, timestamp(uiModule));
    }
}
