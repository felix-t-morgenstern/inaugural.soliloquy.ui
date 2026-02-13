package inaugural.soliloquy.ui.test.integration.display.components.button;

import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;

import java.awt.*;

import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.components.button.ButtonDefinition.button;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ButtonDisplayTest extends DisplayTest {
    public static float BUTTON_TEXT_HEIGHT = 0.075f;
    public static float SPRITE_PRESS_SHADING = 0.2f;
    public static FloatBox SPRITE_DIMENS = floatBoxOf(0.45f, 0.4f, 0.55f, 0.6f);

    protected static ButtonDefinition testButtonFromRectDimens(
            FloatBox rectDimens,
            String text,
            HorizontalAlignment alignment
    ) {
        return withMaxTestArgs(button(
                rectDimens,
                0
        ))
                .withText(
                        text,
                        MERRIWEATHER_ID,
                        0.075f
                )
                .withHorizontalAlignment(alignment);
    }

    protected static ButtonDefinition testButtonFromText(
            String text,
            Vertex renderingLoc
    ) {
        return withMaxTestArgs(button(
                text,
                MERRIWEATHER_ID,
                BUTTON_TEXT_HEIGHT,
                renderingLoc,
                0
        ))
                .withTextPadding(0.025f);
    }

    protected static ButtonDefinition withMaxTestArgs(ButtonDefinition definition) {
        return definition
                .withBgColors(
                        new Color(255, 0, 127),
                        Color.RED,
                        Color.RED,
                        new Color(255, 127, 0)
                )
                .withBgColorsHover(
                        new Color(127, 255, 0),
                        Color.GREEN,
                        Color.GREEN,
                        new Color(0, 255, 127)
                )
                .withBgColorsPressed(
                        new Color(0, 127, 255),
                        Color.BLUE,
                        Color.BLUE,
                        new Color(127, 0, 255)
                )
                .withTextColor(Color.CYAN)
                .withTextColorHover(Color.MAGENTA)
                .withTextColorPressed(Color.YELLOW)
                .withTextPadding(0.01f);
    }

    public static ButtonDefinition testFullDefFromText(
            String text,
            Vertex center
    ) {
        return testButtonFromText(
                text,
                translateVertex(center, 0, -(BUTTON_TEXT_HEIGHT / 2f))
        )
                .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION)
                .withPressSound(PRESS_SOUND_ID)
                .withReleaseSound(RELEASE_SOUND_ID);
    }

    protected static ButtonDefinition testButtonFromSprite(
            FloatBox dimens
    ) {
        return button(DisplayTest.SHIELD_SPRITE_ID, dimens, 0);
    }
}
