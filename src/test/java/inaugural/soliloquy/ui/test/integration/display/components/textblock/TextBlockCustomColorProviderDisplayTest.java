package inaugural.soliloquy.ui.test.integration.display.components.textblock;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.textblock.TextBlockDefinition;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.LoopingLinearMovingColorProviderDefinition.loopingColor;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockCustomColorProviderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text block custom color provider display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Text block custom color provider", 800000),
                TextBlockCustomColorProviderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var timestamp = timestamp(uiModule);

        var defs = Collections.<AbstractContentDefinition>setOf();

        defs.add(rectangle(floatBoxOf(0.25f, 0f, 0.75f, 1f), 0)
                .withColor(new Color(31, 31, 31)));

        var textBlockDef = makeTestTextBlockDefWithCustomProvider(
                staticVal(vertexOf(0.25f, 0f)),
                uiModule.provide(ProviderDefinitionReader.class),
                timestamp
        );
        defs.add(textBlockDef);

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        defs.forEach(d -> reader.read(topLevelComponent, d, timestamp));
    }

    public static TextBlockDefinition makeTestTextBlockDefWithCustomProvider(
            AbstractProviderDefinition<Vertex> upperLeftRenderingLocProviderDef,
            ProviderDefinitionReader providerDefinitionReader,
            long timestamp
    ) {
        var paragraphs = listOf(
                """
                [color=p:rainbow]A *spectre* is haunting Europe - the spectre of **communism**. All the powers of old Europe have entered into a holy alliance to exorcise this spectre: Pope and Tsar, Metternich and Guizot, French Radicals and German police-spies.[/color]"""
        );
        var lineHeight = 0.025f;
        var lineSpacing = 0.005f;
        var paragraphSpacing = 0.02f;
        var glyphPadding = 0f;
        var rainbowPeriod = 2000;

        return textBlock(
                MERRIWEATHER_ID,
                lineHeight,
                0.5f,
                upperLeftRenderingLocProviderDef,
                paragraphs,
                1
        )
                .withGlyphPadding(glyphPadding)
                .withLineSpacing(lineSpacing)
                .withParagraphSpacing(paragraphSpacing)
                .withData(mapOf(
                        "rainbow",
                        providerDefinitionReader.read(
                                loopingColor(
                                        rainbowPeriod,
                                        0,
                                        listOf(true, true),
                                        pairOf(0, Color.RED),
                                        pairOf(rainbowPeriod/2, Color.CYAN),
                                        pairOf(rainbowPeriod, Color.RED)
                                ),
                                timestamp
                        )
                ));
    }
}
