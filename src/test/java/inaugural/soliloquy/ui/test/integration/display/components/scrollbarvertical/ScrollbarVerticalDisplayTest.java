package inaugural.soliloquy.ui.test.integration.display.components.scrollbarvertical;

import inaugural.soliloquy.io.IOModule;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.Graphics;
import soliloquy.specs.io.graphics.renderables.Component;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.exception.CheckedExceptionWrapper.sleep;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.scrollbarvertical.ScrollbarVerticalDefinition.scrollbarVertical;
import static inaugural.soliloquy.ui.components.scrollbarvertical.ScrollbarVerticalMethods.THUMB_LOC_IN_SCROLLABLE_RANGE;
import static java.awt.Color.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollbarVerticalDisplayTest extends DisplayTest {
    public static final Vertex DEFAULT_RENDERING_LOC = vertexOf(0.25f, 0.25f);
    public static final float SCROLLBAR_WIDTH = 0.05f;

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Scrollbar vertical display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Scrollbar vertical", 32000),
                ScrollbarVerticalDisplayTest::populateTopLevelComponent
        );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var scrollbarDef = scrollbarVertical(
                DEFAULT_RENDERING_LOC,
                rectangle(floatBoxOf(SCROLLBAR_WIDTH, 0.4f), 0)
                        .withColor(BLUE),
                button()
                        .withRectDefault(
                                rectangle(
                                        floatBoxOf(SCROLLBAR_WIDTH * 1.1f, SCROLLBAR_WIDTH * 1.25f),
                                        0)
                                        .withColor(YELLOW)
                        )
                        .withRectDefault(
                                rectangle(
                                        floatBoxOf(SCROLLBAR_WIDTH * 1.1f, SCROLLBAR_WIDTH * 1.25f),
                                        0)
                                        .withColor(ORANGE)
                        )
                        .withPressSound(PRESS_SOUND_ID)
                        .withReleaseSound(RELEASE_SOUND_ID),
                1000
        )
                .withAnchors(
                        button(0)
                                .withRectDefault(
                                        rectangle(floatBoxOf(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH), 0)
                                                .withColor(GREEN)
                                )
                                .withRectPressed(
                                        rectangle(floatBoxOf(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH), 0)
                                                .withColor(RED)
                                )
                                .withPressSound(PRESS_SOUND_ID)
                                .withReleaseSound(RELEASE_SOUND_ID),
                        button(0)
                                .withRectDefault(
                                        rectangle(floatBoxOf(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH), 0)
                                                .withColor(GREEN)
                                )
                                .withRectPressed(
                                        rectangle(floatBoxOf(SCROLLBAR_WIDTH, SCROLLBAR_WIDTH), 0)
                                                .withColor(RED)
                                )
                                .withPressSound(PRESS_SOUND_ID)
                                .withReleaseSound(RELEASE_SOUND_ID),
                        staticVal(0.1f),
                        500,
                        500
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, scrollbarDef, timestamp(uiModule));

        Function<UUID, Component> getComponent =
                uiModule.provide(IOModule.class).provide(Graphics.class)::getComponent;
        var scrollbar = getComponent.apply(scrollbarDef.UUID);

        new Thread(() -> {
            while (testIsRunning) {
                Float thumbLocInScrollableRange =
                        getFromData(scrollbar, THUMB_LOC_IN_SCROLLABLE_RANGE);
                System.out.println("THUMB_LOC_IN_SCROLLABLE_RANGE = " + thumbLocInScrollableRange);
                sleep(100);
            }
        }).start();
    }
}
