package inaugural.soliloquy.ui.test.integration.display.components.contentrow;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment.TOP;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.row;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.components.beveledbutton.BeveledButtonDisplayTest.makeBeveledButton;
import static inaugural.soliloquy.ui.test.integration.display.components.button.ButtonFullSuiteDisplayTest.makeFullSuiteButton;
import static inaugural.soliloquy.ui.test.integration.display.components.contentrow.ContentRowTopAlignDisplayTest.makeRowTestRect;
import static inaugural.soliloquy.ui.test.integration.display.components.contentrow.ContentRowTopAlignDisplayTest.makeRowWithContents;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentRowWithMovementDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content row with movement display test",
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
                () -> DisplayTest.runThenClose("Content row with movement", 16000),
                ContentRowWithMovementDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var rect = makeRowTestRect();
        var locProviderDef = finiteSinusoidMoving(
                pairOf(1000, vertexOf(1f, 0.25f)),
                pairOf(3000, vertexOf(0f, 0.25f))
        );
        var def = makeRowWithContents(locProviderDef, TOP);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
        reader.read(topLevelComponent, rect, timestamp(uiModule));
    }
}
