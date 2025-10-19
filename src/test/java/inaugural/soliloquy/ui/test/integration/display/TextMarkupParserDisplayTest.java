package inaugural.soliloquy.ui.test.integration.display;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.ui.TextMarkupParser;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.awt.*;
import java.util.Arrays;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.mapVals;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;

public class TextMarkupParserDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text markup parser display test",
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
                () -> DisplayTest.runThenClose("Text markup parser", 800000),
                TextMarkupParserDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var defs = Collections.<AbstractContentDefinition>setOf();

        defs.add(rectangle(floatBoxOf(0.25f, 0f, 0.75f, 1f), 0)
                .withColor(new Color(31, 31, 31)));

        var ioModule = uiModule.provide(IOModule.class);
        var graphics = ioModule.provide(Graphics.class);
        var font = graphics.getFont(MERRIWEATHER_ID);

        var parser = uiModule.provide(TextMarkupParser.class);
        var rawText = """
                      A *spectre* is haunting Europe - the spectre of **[color=red]communism[/color]**. All the powers of old Europe have entered into a holy alliance to exorcise this spectre: Pope and Tsar, Metternich and Guizot, French Radicals and German police-spies.
                      
                      Where is the party in opposition that has not been decried as [color=red]communistic[/color] by its opponents in power? Where is the opposition that has not hurled back the branding reproach of *communism*, against the more advanced opposition parties, as well as against its [color=162,201,129]reactionary[/color] adversaries?
                      
                      Two things result from this fact:
                      
                      I. **[color=red]Communism[/color]** is already acknowledged by all European powers to be itself a power.
                      II. It is high time that Communists should openly, in the face of the *whole world*, publish their views, their aims, their tendencies, and meet this nursery tale of the Spectre of Communism with a manifesto of the party itself.
                      
                      To this end, Communists of various nationalities have assembled in London and sketched the following manifesto, to be published in the [color=blue]E[color=white]n[color=red]g[color=white]l[color=red]i[color=white]s[color=blue]h[/color], [color=blue]Fr[color=white]en[color=red]ch[/color], [color=black]Ge[color=255,233,0]rm[color=red]an[/color], [color=green]It[color=white]ali[color=red]an[/color], [color=30,71,133]Fl[color=white]em[color=173,29,37]ish[/color] and [color=200,16,46]Da[color=white]ni[color=200,16,46]sh[/color] languages.""";
        var lineHeight = 0.025f;
        var formattedText = parser.formatMultiline(
                rawText,
                font,
                0f,
                lineHeight,
                0.5f
        );
        System.out.println("formattedText = " + Arrays.toString(formattedText));
        System.out.println("formattedText.length = " + formattedText.length);
        Arrays.stream(formattedText).forEach(f -> {
            System.out.println(f.text());
        });

        var linePadding = 0.005f;
        for (var i = 0; i < formattedText.length; i++) {
            defs.add(
                    textLine(
                            MERRIWEATHER_ID,
                            formattedText[i].text(),
                            vertexOf(0.25f, (lineHeight * i) + (linePadding * (i + 1))),
                            lineHeight,
                            TextJustification.LEFT,
                            0f,
                            1
                    )
                            .withItalics(formattedText[i].italicIndices())
                            .withBold(formattedText[i].boldIndices())
                            .withColorDefs(mapVals(formattedText[i].colorIndices(),
                                    StaticProviderDefinition::staticVal))
                            .withBorder(
                                    0.000625f,
                                    Color.BLACK
                            )
            );
        }

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        defs.forEach(d -> reader.read(topLevelComponent, d, timestamp(uiModule)));
    }
}
