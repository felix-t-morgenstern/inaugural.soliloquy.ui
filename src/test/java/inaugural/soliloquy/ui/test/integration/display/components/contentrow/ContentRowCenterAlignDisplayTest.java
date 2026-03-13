package inaugural.soliloquy.ui.test.integration.display.components.contentrow;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition;
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
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment.CENTER;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.row;
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

public class ContentRowCenterAlignDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content row center align display test",
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
                () -> DisplayTest.runThenClose("Content row center align", 16000),
                ContentRowCenterAlignDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var def = row(
                staticVal(vertexOf(0f, 0.25f)),
                0.5f,
                0
        )
                .withItems(
                        itemOf(
                                textLine(
                                        MERRIWEATHER_ID,
                                        "Text line!",
                                        ORIGIN,
                                        lineHeight * 1.5f,
                                        HorizontalAlignment.LEFT,
                                        0f,
                                        0
                                ),
                                CENTER,
                                spacingAfter
                        ),
                        itemOf(
                                rectangle(
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, floatBoxOf(divWidth, 0f)),
                                                pairOf(
                                                        divCycle / 2,
                                                        floatBoxOf(divWidth, 0.5f)
                                                ),
                                                pairOf(
                                                        divCycle,
                                                        floatBoxOf(divWidth, 0f)
                                                )
                                        ),
                                        0
                                ).withColor(
                                        randomHighSaturationColor()
                                ),
                                CENTER,
                                spacingAfter
                        ),
                        space(spacingAfter),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        0.125f,
                                        vertexOf(0f, 0f),
                                        glyphPadding,
                                        lineSpacing,
                                        paragraphSpacing,
                                        HorizontalAlignment.LEFT,
                                        listOf("Lorem ipsum yada yada. This is a text block which" +
                                                " takes up more than one line."),
                                        1
                                ),
                                CENTER,
                                spacingAfter
                        ),
                        itemOf(
                                indent,
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
                                CENTER,
                                spacingAfter
                        ),
                        itemOf(
                                triangle(
                                        staticVal(vertexOf(0f, 0f)),
                                        staticVal(vertexOf(0f, 0.5f)),
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, vertexOf(divWidth, 0.25f)),
                                                pairOf(divCycle / 4, vertexOf(divWidth, 0.5f)),
                                                pairOf(divCycle * 3 / 4, vertexOf(divWidth, 0f)),
                                                pairOf(divCycle, vertexOf(divWidth, 0.25f))
                                        ),
                                        0
                                ).withColor(
                                        randomHighSaturationColor()
                                ),
                                CENTER,
                                spacingAfter
                        ),
                        itemOf(
                                beveledButton(
                                        "Button",
                                        MERRIWEATHER_ID,
                                        beveledButtonLineHeight,
                                        SCREEN_CENTER,
                                        0.05f,
                                        0.125f,
                                        0
                                )
                                        .withTextPadding(0.025f)
                                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                                        .withBgColor(randomHighSaturationColor()),
                                CENTER
                        )
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
