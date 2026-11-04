package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.api.dto.*;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.centerFloatBox;
import static inaugural.soliloquy.ui.Normalization.normalizeHeight;
import static inaugural.soliloquy.ui.Normalization.normalizedWidthDef;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.GlobalLoopingAnimationRenderableDefinition.globalLooping;

public class GlobalLoopingAnimationRenderableDefinitionReaderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Global looping animation renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(FIRE_PARTICLE_EFFECT_REL_LOC, false)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(
                                new AnimationDefinitionDTO(
                                        FLAME_PARTICLE_EFFECT_ID,
                                        1000,
                                        FIRE_PARTICLE_EFFECT_FRAMES
                                )
                        ),
                        arrayOf(
                                new GlobalLoopingAnimationDefinitionDTO(
                                        LOOPING_FLAME_PARTICLE_EFFECT_ID,
                                        FLAME_PARTICLE_EFFECT_ID,
                                        0
                                )
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose(
                        "Global looping animation renderable definition reader", 16000),
                GlobalLoopingAnimationRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var normDimens = normalizeHeight(floatBoxOf(0.25f, 0.25f));

        var definition = globalLooping(
                LOOPING_FLAME_PARTICLE_EFFECT_ID,
                centerFloatBox(normDimens),
                0
        );

        var reader = uiModule.provide(RenderableDefinitionReader.class);
        reader.read(topLevelComponent, definition, timestamp(uiModule));
    }
}
