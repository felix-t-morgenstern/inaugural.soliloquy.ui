package inaugural.soliloquy.ui.test.integration.display.readers;

import inaugural.soliloquy.io.api.dto.AnimationDefinitionDTO;
import inaugural.soliloquy.io.api.dto.AnimationFrameDefinitionDTO;
import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.content.FiniteAnimationRenderableDefinition.finiteAnimation;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class FiniteAnimationRenderableDefinitionReaderTest extends DisplayTest {
    private final static String EXPLOSION_ANIMATION_ID = "explosion";
    private final static String EXPLOSION_RELATIVE_LOCATION =
            "./src/test/resources/images/effects/Explosion.png";

    public static void main(String[] args) {
        var animationDef = new AnimationDefinitionDTO(
                EXPLOSION_ANIMATION_ID,
                480,
                IntStream.range(0,11).mapToObj(i -> new AnimationFrameDefinitionDTO(
                        EXPLOSION_RELATIVE_LOCATION,
                        i * 40,
                        i * 96,
                        0,
                        (i + 1) * 96,
                        96,
                        0,
                        0
                )).toArray(AnimationFrameDefinitionDTO[]::new)
        );

        var displayTest = new DisplayTest();
        displayTest.runTest(
                "Finite animation renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(EXPLOSION_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(
                                animationDef
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Finite animation renderable definition reader",
                        4000),
                FiniteAnimationRenderableDefinitionReaderTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var height = 0.5f;
        var halfHeight = height / 2f;
        var width = height / DEFAULT_RES.widthToHeightRatio();
        var halfWidth = width / 2f;
        var renderableDef = finiteAnimation(
                EXPLOSION_ANIMATION_ID,
                staticVal(floatBoxOf(
                        0.5f - halfWidth,
                        0.5f - halfHeight,
                        0.5f + halfWidth,
                        0.5f + halfHeight
                )),
                0
        );

        var reader = uiModule.provide(RenderableDefinitionReader.class);
        reader.read(topLevelComponent, renderableDef, timestamp(uiModule));
    }
}
