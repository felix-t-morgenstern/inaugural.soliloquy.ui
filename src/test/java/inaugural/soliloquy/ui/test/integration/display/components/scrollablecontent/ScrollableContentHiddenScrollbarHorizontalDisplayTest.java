package inaugural.soliloquy.ui.test.integration.display.components.scrollablecontent;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.ui.components.content.row.ContentRowDefinition.VerticalAlignment.TOP;
import static inaugural.soliloquy.ui.components.scrollablecontent.ScrollableContentDefinition.scrollableContent;
import static inaugural.soliloquy.ui.test.integration.display.components.content.row.ContentRowTopAlignDisplayTest.makeSmallRowWithContents;
import static inaugural.soliloquy.ui.test.integration.display.components.scrollbar.ScrollbarHorizontalWithArrowsDisplayTest.makeHorizontalScrollbarDef;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollableContentHiddenScrollbarHorizontalDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Scrollable content horizontal display test",
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
                () -> DisplayTest.runThenClose("Scrollable content horizontal", 16000),
                ScrollableContentHiddenScrollbarHorizontalDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var origin = staticVal(vertexOf(0.25f, 0.25f));
        var rowDef = makeSmallRowWithContents(origin, TOP);
        var indicatorRect = rectangle(
                floatBoxOf(
                        vertexOf(0.25f, 0.25f),
                        0.5f, 0.5f
                ),
                0
        )
                .withColor(Color.GRAY);

        var scrollableContent = scrollableContent(
                rowDef,
                0.5f,
                makeHorizontalScrollbarDef(),
                1,
                randomUUID()
        )
                .withScrollbarPadding(0.01f)
                .hidesScrollbarWhenContentFits();

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, indicatorRect, timestamp(uiModule));
        reader.read(topLevelComponent, scrollableContent, timestamp(uiModule));
    }
}
