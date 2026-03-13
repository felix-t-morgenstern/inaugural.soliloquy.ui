package inaugural.soliloquy.ui.test.integration.display.components.contentcolumn;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;

import java.awt.*;

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
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.FiniteLinearMovingProviderDefinition.finiteLinearMoving;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentColumnScrollingThroughBoundariesDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content column scrolling through boundaries display test",
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
                () -> DisplayTest.runThenClose("Content column scrolling through boundaries", 16000),
                ContentColumnScrollingThroughBoundariesDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var def = column(
                finiteLinearMoving(
                        pairOf(0, vertexOf(0.25f, 1f)),
                        pairOf(16000, vertexOf(0.25f, -1f))
                ),
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
                                        HorizontalAlignment.LEFT,
                                        0f,
                                        0
                                ),
                                spacingAfter
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
                                spacingAfter
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
                                spacingAfter
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
                                spacingAfter
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
                                spacingAfter
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
                                0f
                        )
                );

        var componentWithBoundaries = component(
                0,
                renderingBoundaries
        )
                .withContent(
                        rectangle(
                                renderingBoundaries,
                                -1
                        )
                                .withColor(Color.GRAY),
                        def
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, componentWithBoundaries, timestamp(uiModule));
    }
}
