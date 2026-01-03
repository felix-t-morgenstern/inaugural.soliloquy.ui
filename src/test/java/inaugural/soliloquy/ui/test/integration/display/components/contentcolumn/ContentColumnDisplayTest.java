package inaugural.soliloquy.ui.test.integration.display.components.contentcolumn;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;

import static inaugural.soliloquy.io.api.Constants.SCREEN_CENTER;
import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.components.button.ButtonDisplayTest.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentColumnDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content column display test",
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
                () -> DisplayTest.runThenClose("Content column", 16000),
                ContentColumnDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var lineHeight = 0.025f;
        var lineSpacing = 0.005f;
        var paragraphSpacing = 0.02f;
        var glyphPadding = 0f;

        var paragraphs = listOf(
                """
                A *spectre* is haunting Europe - the spectre of **[color=red]communism[/color]**. All the powers of old Europe have entered into a [color=orange]holy alliance[/color] to exorcise this spectre: Pope and Tsar, Metternich and Guizot, French Radicals and German police-spies.""",
                """
                Where is the party in opposition that has not been decried as [color=red]communistic[/color] by its opponents in power? Where is the opposition that has not hurled back the branding reproach of *communism*, against the more advanced opposition parties, as well as against its [color=162,201,129]reactionary[/color] adversaries?"""
        );

        var def = column(
                staticVal(vertexOf(0.25f, 0f)),
                0.5f,
                0
        )
                .withItems(
//                        itemOf(
//                                textBlock(
//                                        MERRIWEATHER_ID,
//                                        lineHeight,
//                                        0.5f,
//                                        vertexOf(0f, 0f),
//                                        glyphPadding,
//                                        lineSpacing,
//                                        paragraphSpacing,
//                                        HorizontalAlignment.LEFT,
//                                        paragraphs,
//                                        1
//                                ),
//                                0.1f
//                        ),
                        itemOf(
                                testFullDefFromText("Button", SCREEN_CENTER)
                                        .withTextItalicIndices(listOf(listOf(0, 1)))
                                        .withKey(GLFW_KEY_B, 0)
                                        .withSprite(
                                                SHIELD_SPRITE_ID,
                                                SPRITE_DIMENS
                                        )
                                        .withSpriteColorShiftHover(
                                                brightness(SPRITE_PRESS_SHADING, false))
                                        .withSpriteColorShiftPressed(
                                                brightness(-SPRITE_PRESS_SHADING, false)),
                                0f
                        )
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
