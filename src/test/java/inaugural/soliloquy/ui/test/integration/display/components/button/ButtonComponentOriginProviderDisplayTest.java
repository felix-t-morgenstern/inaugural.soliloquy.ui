package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.io.api.dto.SpriteDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.TextMarkupParserMethods.coloredText;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.DisplayTestMethods.*;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.CENTER;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.brightness;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class ButtonComponentOriginProviderDisplayTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Button component origin provider display test",
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
                () -> DisplayTest.runThenClose("Button component origin provider", 16000),
                ButtonComponentOriginProviderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var providerDefReader = uiModule.provide(ProviderDefinitionReader.class);

        var buttonUuid = randomUUID();
        var lineHeight = 0.05f;
        var spriteDimens = floatBoxOf(0.475f, 0.45f, 0.525f, 0.55f);
        var brightnessAdj = 0.2f;

        var movingCenterOriginProvider = providerDefReader.read(
                functionalProvider(
                        DisplayTest_buttonSinusoidMovingOriginProvider,
                        Vertex.class
                )
                        .withData(mapOf(
                                COMPONENT_UUID,
                                buttonUuid,
                                DisplayTest_buttonSinusoidMovingOriginProvider_p1,
                                vertexOf(0.5f, 1.1f),
                                DisplayTest_buttonSinusoidMovingOriginProvider_p2,
                                vertexOf(0.5f, 0.5f),
                                DisplayTest_buttonSinusoidMovingOriginProvider_t1,
                                1000,
                                DisplayTest_buttonSinusoidMovingOriginProvider_t2,
                                1500
                        )),
                timestamp(uiModule)
        );

        var def = button(0, buttonUuid)
                .withRectDefault(rectangle()
                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                        .withColors(
                                RED,
                                ORANGE,
                                YELLOW,
                                ORANGE
                        )
                )
                .withRectHover(rectangle()
                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                        .withColors(
                                GREEN,
                                new Color(0, 255, 127),
                                CYAN,
                                new Color(0, 255, 127)
                        )
                )
                .withRectPressed(rectangle()
                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                        .withColors(
                                BLUE,
                                new Color(127, 0, 255),
                                MAGENTA,
                                new Color(127, 0, 255)
                        )
                )
                .withTextBlockDef(
                        textBlock(
                                MERRIWEATHER_ID,
                                lineHeight,
                                NO_MAX_LINE_LENGTH,
                                coloredText(WHITE, "Button")
                        )
                                .withHorizontalAlignment(CENTER)
                )
                .textBlockDefinesRectDimens()
                .withTextBlockCenterProviderDef(staticVal(WINDOW_CENTER))
                .withTextBlockPadding(0.01f)
                .withImageAsset(sprite(SHIELD_SPRITE_ID, spriteDimens))
                .withImageAssetHover(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(brightnessAdj, false)))
                .withImageAssetPressed(sprite(SHIELD_SPRITE_ID, spriteDimens)
                        .withColorShifts(brightness(-brightnessAdj, false)))
                .withPressSound(PRESS_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID)
                .onPress(DisplayTest_onMousePress)
                .withData(mapOf(
                        COMPONENT_ORIGIN_PROVIDER,
                        movingCenterOriginProvider
                ));

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }
}
