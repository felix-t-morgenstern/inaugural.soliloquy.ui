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
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.Constants.ORIGIN;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinition.beveledButton;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.components.button.ButtonDisplayTest.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentColumnCenterAlignDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content column center align display test",
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
                () -> DisplayTest.runThenClose("Content column center align", 16000),
                ContentColumnCenterAlignDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var spacingAfter = 0.0125f;

        var lineHeight = 0.02f;
        var lineSpacing = 0.005f;
        var paragraphSpacing = 0.02f;
        var glyphPadding = 0f;

        var paragraphs1 = listOf(
                """
                A *spectre* is haunting Europe - the spectre of **[color=red]communism[/color]**. All the powers of old Europe have entered into a [color=orange]holy alliance[/color] to exorcise this spectre: Pope and Tsar, Metternich and Guizot, French Radicals and German police-spies.""",
                """
                Where is the party in opposition that has not been decried as [color=red]communistic[/color] by its opponents in power? Where is the opposition that has not hurled back the branding reproach of *communism*, against the more advanced opposition parties, as well as against its [color=162,201,129]reactionary[/color] adversaries?"""
        );
        var paragraphs2 = listOf(
                """
                Two things result from this fact:""",
                """
                I. **[color=red]Communism[/color]** is already acknowledged by all European powers to be itself a power.""",
                """
                [color=127,0,0]II. It is high time that Communists should openly, in the face of the *whole world*,[/color] publish their views, their aims, their tendencies, and meet this nursery tale of the Spectre of Communism with a manifesto of the party itself."""
        );

        var divHeight = 0.00625f;
        var divCycle = 3000;

        var beveledButtonLineHeight = 0.075f;

        var def = column(
                staticVal(vertexOf(0.25f, 0f)),
                0.5f,
                0
        )
                .withItems(
                        itemOf(
                                textLine(
                                        MERRIWEATHER_ID,
                                        "This is a text line!",
                                        ORIGIN,
                                        lineHeight * 1.5f,
                                        HorizontalAlignment.CENTER,
                                        0f,
                                        0
                                ),
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
                        itemOf(
                                rectangle(
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, floatBoxOf(0f, divHeight)),
                                                pairOf(
                                                        divCycle/2,
                                                        floatBoxOf(0.5f, divHeight)
                                                ),
                                                pairOf(
                                                        divCycle,
                                                        floatBoxOf(0f, divHeight)
                                                )
                                        ),
                                        0
                                ).withColor(
                                        randomHighSaturationColor()
                                ),
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
                        space(spacingAfter),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        0.5f,
                                        vertexOf(0f, 0f),
                                        glyphPadding,
                                        lineSpacing,
                                        paragraphSpacing,
                                        HorizontalAlignment.LEFT,
                                        paragraphs1,
                                        1
                                ),
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
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
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        0.5f,
                                        vertexOf(0f, 0f),
                                        glyphPadding,
                                        lineSpacing,
                                        paragraphSpacing,
                                        HorizontalAlignment.LEFT,
                                        paragraphs2,
                                        1
                                ),
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
                        itemOf(
                                triangle(
                                        staticVal(vertexOf(0f,0f)),
                                        staticVal(vertexOf(0.5f,0f)),
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, vertexOf(0.25f, divHeight)),
                                                pairOf(divCycle/4, vertexOf(0.5f, divHeight)),
                                                pairOf(divCycle*3/4, vertexOf(0f, divHeight)),
                                                pairOf(divCycle, vertexOf(0.25f, divHeight))
                                        ),
                                        0
                                ).withColor(
                                        randomHighSaturationColor()
                                ),
                                spacingAfter,
                                HorizontalAlignment.CENTER,
                                0f
                        ),
                        itemOf(
                                beveledButton(
                                        "Button",
                                        MERRIWEATHER_ID,
                                        beveledButtonLineHeight,
                                        vertexOf(0.5f, 0.5f - (lineHeight/2f)),
                                        0.05f,
                                        0.125f,
                                        0
                                )
                                        .withTextPadding(0.025f)
                                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                                        .withBgColor(randomHighSaturationColor()),
                                HorizontalAlignment.CENTER,
                                0f
                        )
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
