package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.*;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;

public class ImageAssetSetRenderableDefinitionReaderDisplayTest extends DisplayTest {
    private final static String IMAGE_ASSET_SET_ID = "imageAssetSetId";
    private final static String RPG_WEAPONS_RELATIVE_LOCATION =
            "./src/test/resources/images/items/RPG_Weapons.png";

    public static void main(String[] args) {
        var displayTest = new DisplayTest(MOUSE_ACTIONS);
        displayTest.runTest(
                "Image asset set renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(
//                                new ImageAssetSetDefinitionDTO(IMAGE_ASSET_SET_ID, arrayOf(
//                                        new ImageAssetSetAssetDefinitionDTO(
//                                                AssetType.SPRITE.getValue()
//                                        )
//                                ))
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Image asset set renderable definition reader", 4000),
                ImageAssetSetRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);
    }
}
