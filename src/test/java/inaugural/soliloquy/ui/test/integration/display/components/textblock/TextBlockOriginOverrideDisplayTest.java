package inaugural.soliloquy.ui.test.integration.display.components.textblock;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.ui.components.ComponentMethods.ORIGIN_OVERRIDE_ADJUST;
import static inaugural.soliloquy.ui.components.ComponentMethods.ORIGIN_OVERRIDE_PROVIDER;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextBlockOriginOverrideDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text block simple display test",
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
                () -> DisplayTest.runThenClose("Text block simple", 800000),
                TextBlockOriginOverrideDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {

        var paragraphs = listOf(
                """
                A *spectre* is haunting Europe - the spectre of **[color=red]communism[/color]**. All the powers of old Europe have entered into a [color=orange]holy alliance[/color] to exorcise this spectre: Pope and Tsar, Metternich and Guizot, French Radicals and German police-spies.""",
                """
                Where is the party in opposition that has not been decried as [color=red]communistic[/color] by its opponents in power? Where is the opposition that has not hurled back the branding reproach of *communism*, against the more advanced opposition parties, as well as against its [color=162,201,129]reactionary[/color] adversaries?""",
                "Two things result from this fact:",
                """
                I. **[color=red]Communism[/color]** is already acknowledged by all European powers to be itself a power.""",
                """
                [color=127,0,0]II. It is high time that Communists should openly, in the face of the *whole world*,[/color] publish their views, their aims, their tendencies, and meet this nursery tale of the Spectre of Communism with a manifesto of the party itself.""",
                """
                To this end, Communists of various nationalities have assembled in London and sketched the following manifesto, to be published in the [color=blue]E[color=white]n[color=red]g[color=white]l[color=red]i[color=white]s[color=blue]h[/color], [color=blue]Fr[color=white]en[color=red]ch[/color], [color=black]Ge[color=255,233,0]rm[color=red]an[/color], [color=green]It[color=white]ali[color=red]an[/color], [color=30,71,133]Fl[color=white]em[color=173,29,37]ish[/color] and [color=200,16,46]Da[color=white]ni[color=200,16,46]sh[/color] languages."""
        );
        var lineHeight = 0.025f;
        var lineSpacing = 0.005f;
        var paragraphSpacing = 0.02f;
        var glyphPadding = 0f;

        var timestamp = timestamp(uiModule);
        var reader = uiModule.provide(RenderableDefinitionReader.class);
        var providerReader = uiModule.provide(ProviderDefinitionReader.class);
        var originOverride = vertexOf(0.1f, 0.1f);
        var originOverrideProvider = providerReader.read(staticVal(originOverride), timestamp);

        Component textBlock = reader.read(topLevelComponent, textBlock(
                MERRIWEATHER_ID,
                lineHeight,
                0.5f,
                vertexOf(0.25f, 0f),
                glyphPadding,
                lineSpacing,
                paragraphSpacing,
                HorizontalAlignment.LEFT,
                paragraphs,
                1
        ), timestamp);
        textBlock.data().put(ORIGIN_OVERRIDE_PROVIDER, originOverrideProvider);

        reader.read(topLevelComponent, rectangle(floatBoxOf(0.25f, 0f, 0.75f, 1f), 0)
                .withColor(new Color(31, 31, 31)), timestamp);
    }
}
