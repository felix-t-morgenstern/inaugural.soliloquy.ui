package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.io.api.Constants.SCREEN_CENTER;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.ORIGIN_OVERRIDE_PROVIDER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonOriginOverrideDisplayTest extends ButtonDisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button definition with origin override display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false),
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(
                                new SpriteDefinitionDTO(SHIELD_SPRITE_ID,
                                        RPG_WEAPONS_RELATIVE_LOCATION,
                                        266, 271, 313, 343)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Button definition with origin override", 800000),
                ButtonOriginOverrideDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var buttonDef = testFullDefFromText("Button", SCREEN_CENTER)
                .withTextItalicIndices(listOf(listOf(0, 1)))
                .withKey(GLFW_KEY_B, 0)
                .withSprite(
                        SHIELD_SPRITE_ID,
                        SPRITE_DIMENS
                )
                .withSpriteColorShiftHover(brightness(SPRITE_PRESS_SHADING, false))
                .withSpriteColorShiftPressed(brightness(-SPRITE_PRESS_SHADING, false))
                .onPress("printComponentDimens");

        var timestamp = timestamp(uiModule);
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        var providerReader = uiModule.provide(ProviderDefinitionReader.class);
        var originOverride = vertexOf(0.1f, 0.1f);
        var originOverrideProvider = providerReader.read(staticVal(originOverride), timestamp);

        Component button = reader.read(topLevelComponent, buttonDef, timestamp(uiModule));
        button.data().put(ORIGIN_OVERRIDE_PROVIDER, originOverrideProvider);
    }
}
