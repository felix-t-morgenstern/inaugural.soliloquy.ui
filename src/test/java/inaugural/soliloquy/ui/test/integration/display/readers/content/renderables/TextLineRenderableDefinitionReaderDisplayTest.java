package inaugural.soliloquy.ui.test.integration.display.readers.content.renderables;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.FontDefinitionDTO;
import inaugural.soliloquy.io.api.dto.FontStyleDefinitionDTO;
import inaugural.soliloquy.io.api.dto.FontStyleDefinitionGlyphPropertyDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextJustification;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition.textLine;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class TextLineRenderableDefinitionReaderDisplayTest extends DisplayTest {
    protected final static String CINZEL_ID = "cinzel";
    protected final static String RELATIVE_LOCATION_CINZEL =
            "./src/test/resources/fonts/Cinzel-VariableFont_wght.ttf";
    protected final static float MAX_LOSSLESS_FONT_SIZE_CINZEL = 200f;
    protected final static float ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL = 0.25f;
    protected final static float ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL = 0.25f;
    protected final static float LEADING_ADJUSTMENT_CINZEL = 0f;
    protected final static FontStyleDefinitionGlyphPropertyDTO[] CINZEL_ITALIC_WIDTH_FACTORS =
            arrayOf(
                    new FontStyleDefinitionGlyphPropertyDTO('I', 0.965f),
                    new FontStyleDefinitionGlyphPropertyDTO('W', 0.975f),
                    new FontStyleDefinitionGlyphPropertyDTO('i', 0.965f),
                    new FontStyleDefinitionGlyphPropertyDTO('w', 0.975f),
                    new FontStyleDefinitionGlyphPropertyDTO('^', 0.975f)
            );

    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Text line renderable definition reader display test",
                new AssetDefinitionsDTO(
                        arrayOf(),
                        arrayOf(
                                new FontDefinitionDTO(
                                        CINZEL_ID,
                                        RELATIVE_LOCATION_CINZEL,
                                        MAX_LOSSLESS_FONT_SIZE_CINZEL,
                                        LEADING_ADJUSTMENT_CINZEL,
                                        new FontStyleDefinitionDTO(
                                                ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                                                arrayOf(),
                                                arrayOf(),
                                                arrayOf(),
                                                ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                                        ),
                                        new FontStyleDefinitionDTO(
                                                ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                                                arrayOf(),
                                                arrayOf(),
                                                CINZEL_ITALIC_WIDTH_FACTORS,
                                                ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                                        ),
                                        new FontStyleDefinitionDTO(
                                                ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                                                arrayOf(),
                                                arrayOf(),
                                                arrayOf(),
                                                ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                                        ),
                                        new FontStyleDefinitionDTO(
                                                ADDITIONAL_GLYPH_HORIZONTAL_TEXTURE_SPACING_CINZEL,
                                                arrayOf(),
                                                arrayOf(),
                                                CINZEL_ITALIC_WIDTH_FACTORS,
                                                ADDITIONAL_GLYPH_VERTICAL_TEXTURE_SPACING_CINZEL
                                        )
                                )
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Text line renderable definition reader", 4000),
                TextLineRenderableDefinitionReaderDisplayTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var text = "This is the text!";
        var def = textLine(
                CINZEL_ID,
                staticVal(text),
                staticVal(vertexOf(0.5f, 0.475f)),
                staticVal(0.05f),
                TextJustification.CENTER,
                0f,
                0
        )
                .withColors(rainbowGradient(text))
                .withItalics(5, 7, 12)
                .withBold(0, 4, 12)
                .withBorder(
                        staticVal(0.001f),
                        staticVal(Color.WHITE)
                )
                .withDropShadow(
                        staticVal(0.05f),
                        staticVal(vertexOf(0.0025f, 0.0025f)),
                        staticVal(Color.getHSBColor(0f, 0f, 0.5f))
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, def, timestamp(uiModule));
    }

    @SuppressWarnings("SameParameterValue")
    private static Pair<Integer, AbstractProviderDefinition<Color>>[] rainbowGradient(
            String lineText) {
        var rainbowGradient = listOf();

        var degreePerLetter = 360f / lineText.length();
        for (var i = 0; i < lineText.length(); i++) {
            rainbowGradient.add(pairOf(i, staticVal(colorAtDegree((float) i * degreePerLetter))));
        }
        //noinspection SuspiciousToArrayCall,unchecked
        return rainbowGradient.toArray(Pair[]::new);
    }

    private static Color colorAtDegree(float degree) {
        var red = getColorComponent(0f, degree);
        var green = getColorComponent(120f, degree);
        var blue = getColorComponent(240f, degree);

        return new Color(red, green, blue, 1f);
    }

    private static float getColorComponent(float componentCenter, float degree) {
        var degreesInCircle = 360f;
        var halfOfCircle = 180f;
        var sixthOfCircle = 60f;
        var degreeModulo = degree % degreesInCircle;
        var distance = componentCenter - degreeModulo;
        if (distance < -halfOfCircle) {
            distance += degreesInCircle;
        }
        var absVal = Math.abs(distance);
        if (absVal <= sixthOfCircle) {
            return 1f;
        }
        absVal -= sixthOfCircle;
        var absValWithCeiling = Math.min(sixthOfCircle, absVal);
        var amountOfSixthOfCircle = sixthOfCircle - absValWithCeiling;
        @SuppressWarnings("UnnecessaryLocalVariable")
        var colorComponent = amountOfSixthOfCircle / sixthOfCircle;
        return colorComponent;
    }
}
