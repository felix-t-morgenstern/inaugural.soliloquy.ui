package inaugural.soliloquy.ui.test.integration.display;

import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.EventInputs;

import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.BUTTON_UNADJ_DIMENS;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.providers.FiniteSinusoidMovingProviderDefinition.finiteSinusoidMoving;

public class DisplayTestMethods {
    private final static String MOVING_CENTER_SINUSOID_PROVIDER = "MOVING_CENTER_SINUSOID_PROVIDER";

    private final Function<UUID, Component> GET_COMPONENT;
    private final ProviderDefinitionReader PROVIDER_DEFINITION_READER;

    public DisplayTestMethods(Function<UUID, Component> getComponent,
                              ProviderDefinitionReader providerDefinitionReader) {
        GET_COMPONENT = getComponent;
        PROVIDER_DEFINITION_READER = providerDefinitionReader;
    }

    public final static String DisplayTest_onMouseOver = "DisplayTest_onMouseOver";

    public void DisplayTest_onMouseOver(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE OVER");
    }

    public final static String DisplayTest_onMouseLeave = "DisplayTest_onMouseLeave";

    public void DisplayTest_onMouseLeave(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE LEAVE");
    }

    public final static String DisplayTest_onMousePress = "DisplayTest_onMousePress";

    public void DisplayTest_onMousePress(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE PRESS");
    }

    public final static String DisplayTest_onMouseRelease = "DisplayTest_onMouseRelease";

    public void DisplayTest_onMouseRelease(@SuppressWarnings("unused") EventInputs e) {
        System.out.println("MOUSE RELEASE");
    }

    public final static String DisplayTest_buttonSinusoidMovingOriginProvider =
            "DisplayTest_buttonSinusoidMovingOriginProvider";
    public final static String DisplayTest_buttonSinusoidMovingOriginProvider_p1 =
            "DisplayTest_buttonSinusoidMovingOriginProvider_p1";
    public final static String DisplayTest_buttonSinusoidMovingOriginProvider_p2 =
            "DisplayTest_buttonSinusoidMovingOriginProvider_p2";
    public final static String DisplayTest_buttonSinusoidMovingOriginProvider_t1 =
            "DisplayTest_buttonSinusoidMovingOriginProvider_t1";
    public final static String DisplayTest_buttonSinusoidMovingOriginProvider_t2 =
            "DisplayTest_buttonSinusoidMovingOriginProvider_t2";

    public Vertex DisplayTest_buttonSinusoidMovingOriginProvider(FunctionalProvider.Inputs inputs) {
        var button = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        FloatBox buttonUnadjDimens = getFromData(button, BUTTON_UNADJ_DIMENS);
        ProviderAtTime<Vertex> movingCenterSinusoidProvider =
                getFromData(button, MOVING_CENTER_SINUSOID_PROVIDER);
        if (movingCenterSinusoidProvider == null) {
            Vertex p1 = getFromData(inputs, DisplayTest_buttonSinusoidMovingOriginProvider_p1);
            Vertex p2 = getFromData(inputs, DisplayTest_buttonSinusoidMovingOriginProvider_p2);
            int t1 = getFromData(inputs, DisplayTest_buttonSinusoidMovingOriginProvider_t1);
            int t2 = getFromData(inputs, DisplayTest_buttonSinusoidMovingOriginProvider_t2);
            var providerDef = finiteSinusoidMoving(
                    pairOf(t1, p1),
                    pairOf(t2, p2)
            );
            movingCenterSinusoidProvider =
                    PROVIDER_DEFINITION_READER.read(providerDef, inputs.timestamp());
            button.data().put(MOVING_CENTER_SINUSOID_PROVIDER, movingCenterSinusoidProvider);
        }
        Vertex currentCenter = movingCenterSinusoidProvider.provide(inputs.timestamp());

        return vertexOf(
                currentCenter.X - buttonUnadjDimens.width() / 2f,
                currentCenter.Y - buttonUnadjDimens.height() / 2f
        );
    }
}
