package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;

import java.util.UUID;

import static java.util.UUID.randomUUID;

/**
 * (This Component was principally made early as a demonstration of how decorative elements can be
 * festooned onto functional Components, but feel free to use to your heart's content!)
 */
public class BeveledButtonDefinition extends ButtonDefinition {
    public final float BEVEL_DIMENS_PERCENT;
    public final float BEVEL_INTENSITY;

    private BeveledButtonDefinition(int z,
                                    RectangleRenderableDefinition rectDefault,
                                    float bevelDimensPercent,
                                    float bevelIntensity,
                                    UUID uuid) {
        super(z, uuid);
        BEVEL_DIMENS_PERCENT = bevelDimensPercent;
        BEVEL_INTENSITY = bevelIntensity;
        this.rectDefaultDef = rectDefault;
    }

    public static BeveledButtonDefinition beveledButton(int z,
                                                        RectangleRenderableDefinition rectDefault,
                                                        float bevelDimensPercent,
                                                        float bevelIntensity,
                                                        UUID uuid) {
        return new BeveledButtonDefinition(z, rectDefault, bevelDimensPercent, bevelIntensity,
                uuid);
    }

    public static BeveledButtonDefinition beveledButton(int z,
                                                        RectangleRenderableDefinition rectDefault,
                                                        float bevelDimensPercent,
                                                        float bevelIntensity) {
        return new BeveledButtonDefinition(z, rectDefault, bevelDimensPercent, bevelIntensity,
                randomUUID());
    }
}
