package inaugural.soliloquy.ui.test.integration.display.components.contentcolumn;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.components.beveledbutton.BeveledButtonDisplayTest.makeBeveledButton;
import static inaugural.soliloquy.ui.test.integration.display.components.button.ButtonFullSuiteDisplayTest.makeFullSuiteButton;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentColumnLeftAlignDisplayTest extends DisplayTest {
    public static final AbstractProviderDefinition<Vertex> DEFAULT_COL_RENDERING_LOC =
            staticVal(vertexOf(0.25f, 0f));

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content column left align display test",
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
                () -> DisplayTest.runThenClose("Content column left align", 16000),
                ContentColumnLeftAlignDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var rectDef = makeRectForCol();

        var colDef = makeColumnWithContents(DEFAULT_COL_RENDERING_LOC, LEFT);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, rectDef, timestamp(uiModule));
        reader.read(topLevelComponent, colDef, timestamp(uiModule));
    }

    public static RectangleRenderableDefinition makeRectForCol() {
        return rectangle(
                floatBoxOf(0.25f, 0f, 0.75f, 1f),
                -1
        )
                .withColor(Color.GRAY);
    }

    public static ContentColumnDefinition makeColumnWithContents(
            AbstractProviderDefinition<Vertex> renderingLoc,
            HorizontalAlignment align
    ) {
        var colWidth = 0.5f;
        var textBlockWidth = 0.4f;

        return column(
                renderingLoc,
                colWidth,
                0
        )
                .withItems(
                        itemOf(
                                indent,
                                textLine(
                                        MERRIWEATHER_ID,
                                        "This is an indented text line!",
                                        lineHeight * 1.5f,
                                        HorizontalAlignment.LEFT,
                                        0f,
                                        0
                                ),
                                align,
                                spacingAfter
                        ),
                        itemOf(
                                rectangle(
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, floatBoxOf(0f, divHeight)),
                                                pairOf(
                                                        divCycle / 2,
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
                                align,
                                spacingAfter
                        ),
                        space(spacingAfter),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        textBlockWidth,
                                        paragraphs1
                                )
                                        .withGlyphPadding(glyphPadding)
                                        .withLineSpacing(lineSpacing)
                                        .withParagraphSpacing(paragraphSpacing),
                                align,
                                spacingAfter
                        ),
                        itemOf(
                                indent,
                                makeFullSuiteButton(),
                                align,
                                spacingAfter
                        ),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        textBlockWidth,
                                        paragraphs2
                                )
                                        .withGlyphPadding(glyphPadding)
                                        .withLineSpacing(lineSpacing)
                                        .withParagraphSpacing(paragraphSpacing),
                                align,
                                spacingAfter
                        ),
                        itemOf(
                                triangle(
                                        staticVal(vertexOf(0f, 0f)),
                                        staticVal(vertexOf(0.5f, 0f)),
                                        loopingLinearMoving(
                                                divCycle,
                                                0,
                                                pairOf(0, vertexOf(0.25f, divHeight)),
                                                pairOf(divCycle / 4, vertexOf(0.5f, divHeight)),
                                                pairOf(divCycle * 3 / 4, vertexOf(0f, divHeight)),
                                                pairOf(divCycle, vertexOf(0.25f, divHeight))
                                        ),
                                        0
                                ).withColor(
                                        randomHighSaturationColor()
                                ),
                                align,
                                spacingAfter
                        ),
                        itemOf(
                                makeBeveledButton(),
                                align
                        )
                );
    }
}
