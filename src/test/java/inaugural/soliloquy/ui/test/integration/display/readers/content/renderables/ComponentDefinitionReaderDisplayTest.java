package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.io.api.Constants.WHOLE_SCREEN;
import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;

public class ComponentDefinitionReaderDisplayTest extends SpriteRenderableDefinitionReaderDisplayTest {
    private final static String SWORD_SPRITE_ID = "swordSpriteId";

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Component definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(),
                        arrayOf(
                                new SpriteDefinitionDTO(SHIELD_SPRITE_ID, RPG_WEAPONS_RELATIVE_LOCATION,
                                        266, 271, 313, 343),
                                new SpriteDefinitionDTO(SWORD_SPRITE_ID, RPG_WEAPONS_RELATIVE_LOCATION,
                                        208, 32, 227, 105)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Component definition reader", 4000),
                ComponentDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var shieldSpriteDef = sprite(SHIELD_SPRITE_ID, floatBoxOf(0.35f, 0.25f, 0.65f, 0.75f), 1);
        var swordSpriteDef = sprite(SWORD_SPRITE_ID, floatBoxOf(0.45f, 0f, 0.55f, 1f), 0);
        var definition = component(0, WHOLE_SCREEN, shieldSpriteDef, swordSpriteDef);
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        reader.read(topLevelComponent, definition, timestamp(uiModule));
    }
}
