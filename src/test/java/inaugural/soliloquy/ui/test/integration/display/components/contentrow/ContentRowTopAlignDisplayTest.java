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
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition.triangle;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingProviderDefinition.loopingLinearMoving;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ContentRowTopAlignDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Content row top align display test",
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
                () -> DisplayTest.runThenClose("Content row top align", 16000),
                ContentRowTopAlignDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var rect = makeRowTestRect();
        var def = makeRowWithContents(staticVal(vertexOf(0f, 0.25f)), TOP);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
        reader.read(topLevelComponent, rect, timestamp(uiModule));
    }

    public static RectangleRenderableDefinition makeRowTestRect() {
        return rectangle(
                floatBoxOf(0f, 0.25f, 1f, 0.75f),
                -1
        )
                .withColor(Color.GRAY);
    }

    public static ContentRowDefinition makeRowWithContents(
            AbstractProviderDefinition<Vertex> renderingLoc,
            VerticalAlignment align
    ) {
        return row(
                renderingLoc,
                0.5f,
                0
        )
                .withItems(
                        itemOf(
                                indent,
                                textLine(
                                        MERRIWEATHER_ID,
                                        "Text line!",
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
                                align,
                                spacingAfter
                        ),
                        space(spacingAfter),
                        itemOf(
                                textBlock(
                                        MERRIWEATHER_ID,
                                        lineHeight,
                                        0.125f,
                                        listOf("Lorem ipsum yada yada. This is a text block which" +
                                                " takes up more than one line."),
                                        1
                                )
                                        .withGlyphPadding(
                                                glyphPadding)
                                        .withLineSpacing(lineSpacing)
                                        .withParagraphSpacing(paragraphSpacing)
                                        .withHorizontalAlignment(HorizontalAlignment.LEFT),
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
