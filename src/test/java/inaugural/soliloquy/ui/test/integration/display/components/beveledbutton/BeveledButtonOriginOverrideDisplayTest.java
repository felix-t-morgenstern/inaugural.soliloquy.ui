package inaugural.soliloquy.ui.test.integration.display.components.beveledbutton;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import org.lwjgl.glfw.GLFW;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.test.integration.display.components.beveledbutton.BeveledButtonDisplayTest.makeBeveledButton;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class BeveledButtonOriginOverrideDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Beveled button display test",
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
                () -> DisplayTest.runThenClose("Beveled button", 16000),
                BeveledButtonOriginOverrideDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var def = makeBeveledButton()
                .withKey(GLFW.GLFW_KEY_B, 1);

        var timestamp = timestamp(uiModule);
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        var providerReader = uiModule.provide(ProviderDefinitionReader.class);
        var componentOrigin = vertexOf(0.1f, 0.1f);
        var componentOriginProvider = providerReader.read(staticVal(componentOrigin), timestamp);

        Component beveledButton = reader.read(topLevelComponent, def, timestamp);
        beveledButton.data().put(COMPONENT_ORIGIN_PROVIDER, componentOriginProvider);
    }
}
