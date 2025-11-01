package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class BeveledButtonDefinition extends ButtonDefinition {
    public final float BEVEL_DIMENS_PERCENT;
    public final float BEVEL_INTENSITY;

    private BeveledButtonDefinition(int z,
                                    AbstractProviderDefinition<FloatBox> rectDimensDef,
                                    AbstractProviderDefinition<Vertex> textRenderingLocDef,
                                    float bevelDimensPercent,
                                    float bevelIntensity) {
        super(z, rectDimensDef, textRenderingLocDef);
        BEVEL_DIMENS_PERCENT = bevelDimensPercent;
        BEVEL_INTENSITY = bevelIntensity;
    }

    public static BeveledButtonDefinition beveledButton(
            AbstractProviderDefinition<FloatBox> dimens,
            float bevelDimensPercent,
            float bevelIntensity,
            int z
    ) {
        return new BeveledButtonDefinition(
                z,
                dimens,
                null,
                bevelDimensPercent,
                bevelIntensity
        );
    }

    public static BeveledButtonDefinition beveledButton(
            FloatBox dimens,
            float bevelDimensPercent,
            float bevelIntensity,
            int z
    ) {
        return beveledButton(
                staticVal(dimens),
                bevelDimensPercent,
                bevelIntensity,
                z
        );
    }

    public static BeveledButtonDefinition beveledButton(
            String text,
            String fontId,
            float textHeight,
            AbstractProviderDefinition<Vertex> textRenderingLoc,
            float bevelDimensPercent,
            float bevelIntensity,
            int z
    ) {
        return (BeveledButtonDefinition) new BeveledButtonDefinition(
                z,
                null,
                textRenderingLoc,
                bevelDimensPercent,
                bevelIntensity
        )
                .withText(text, fontId, textHeight);
    }

    public static BeveledButtonDefinition beveledButton(
            String text,
            String fontId,
            float textHeight,
            Vertex textRenderingLoc,
            float bevelDimensPercent,
            float bevelIntensity,
            int z
    ) {
        return beveledButton(
                text,
                fontId,
                textHeight,
                staticVal(textRenderingLoc),
                bevelDimensPercent,
                bevelIntensity,
                z
        );
    }
}
