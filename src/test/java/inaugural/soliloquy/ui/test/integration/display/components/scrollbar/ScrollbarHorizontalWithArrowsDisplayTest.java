package inaugural.soliloquy.ui.test.integration.display.components.scrollbar;

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
import static inaugural.soliloquy.ui.components.scrollbar.ScrollbarDefinition.Orientation.HORIZONTAL;
import static inaugural.soliloquy.ui.components.scrollbar.ScrollbarDefinition.scrollbar;
import static inaugural.soliloquy.ui.components.scrollbar.ScrollbarMethods.THUMB_LOC_IN_SCROLLABLE_RANGE;
import static java.awt.Color.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ScrollbarHorizontalWithArrowsDisplayTest extends DisplayTest {
    public static final Vertex DEFAULT_RENDERING_LOC = vertexOf(0.25f, 0.25f);
    public static final float SCROLLBAR_HEIGHT = 0.05f;

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Scrollbar horizontal with arrows display test",
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
                () -> DisplayTest.runThenClose("Scrollbar horizontal with arrows", 60000),
                ScrollbarHorizontalWithArrowsDisplayTest::populateTopLevelComponent
        );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var scrollMoveDur = 100;
        var arrowHoldStartThreshold = 500;
        var minDurBetweenMovesWhileArrowHeld = scrollMoveDur;
        var arrowHeldRepeatedTimeExponent = 3f;
        var arrowHeldRepeatedTimeExponentFactor = 100f;
        var scrollbarDef = scrollbar(
                HORIZONTAL,
                DEFAULT_RENDERING_LOC,
                rectangle(floatBoxOf(0.4f, SCROLLBAR_HEIGHT), 0)
                        .withColor(BLUE),
                button()
                        .withRectDefault(
                                rectangle(
                                        floatBoxOf(SCROLLBAR_HEIGHT * 1.2f, SCROLLBAR_HEIGHT * 1.25f),
                                        0)
                                        .withColor(YELLOW)
                        )
                        .withRectDefault(
                                rectangle(
                                        floatBoxOf(SCROLLBAR_HEIGHT * 1.2f, SCROLLBAR_HEIGHT * 1.25f),
                                        0)
                                        .withColor(ORANGE)
                        )
                        .withPressSound(PRESS_SOUND_ID)
                        .withReleaseSound(RELEASE_SOUND_ID),
                staticVal(0.1f),
                scrollMoveDur
        )
                .withArrowButtons(
                        button(0)
                                .withRectDefault(
                                        rectangle(floatBoxOf(SCROLLBAR_HEIGHT, SCROLLBAR_HEIGHT), 0)
                                                .withColor(GREEN)
                                )
                                .withRectPressed(
                                        rectangle(floatBoxOf(SCROLLBAR_HEIGHT, SCROLLBAR_HEIGHT), 0)
                                                .withColor(RED)
                                )
                                .withPressSound(PRESS_SOUND_ID)
                                .withReleaseSound(RELEASE_SOUND_ID),
                        button(0)
                                .withRectDefault(
                                        rectangle(floatBoxOf(SCROLLBAR_HEIGHT, SCROLLBAR_HEIGHT), 0)
                                                .withColor(GREEN)
                                )
                                .withRectPressed(
                                        rectangle(floatBoxOf(SCROLLBAR_HEIGHT, SCROLLBAR_HEIGHT), 0)
                                                .withColor(RED)
                                )
                                .withPressSound(PRESS_SOUND_ID)
                                .withReleaseSound(RELEASE_SOUND_ID),
                        arrowHoldStartThreshold,
                        minDurBetweenMovesWhileArrowHeld,
                        arrowHeldRepeatedTimeExponent,
                        arrowHeldRepeatedTimeExponentFactor
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
