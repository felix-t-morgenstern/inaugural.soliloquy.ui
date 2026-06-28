package inaugural.soliloquy.ui.test.integration.display.components.contentcolumn;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.test.integration.display.components.contentcolumn.ContentColumnLeftAlignDisplayTest.makeColumnWithContents;
import static inaugural.soliloquy.ui.test.integration.display.components.contentcolumn.ContentColumnLeftAlignDisplayTest.makeRectForCol;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class ContentColumnWithMovementDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content column with movement display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false),
                                new ImageDefinitionDTO(RPG_WEAPONS_RELATIVE_LOCATION, true)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(
                                new SpriteDefinitionDTO(SHIELD_SPRITE_ID, RPG_WEAPONS_RELATIVE_LOCATION,
                                        266, 271, 313, 343)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Content column with movement", 16000),
                ContentColumnWithMovementDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var renderingLoc = finiteSinusoidMoving(
                pairOf(1000, vertexOf(0.25f, 1f)),
                pairOf(3000, vertexOf(0.25f, 0f))
        );

        var rectDef = makeRectForCol();

        var colDef = makeColumnWithContents(renderingLoc, LEFT);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, rectDef, timestamp(uiModule));
        reader.read(topLevelComponent, colDef, timestamp(uiModule));
    }
}
