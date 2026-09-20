package inaugural.soliloquy.ui.components.dialogbox;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.EventInputs;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class DialogBoxMethods {
    final static String BOX_UNADJ_DIMENS_PROVIDER = "BOX_UNADJ_DIMENS_PROVIDER";
    final static String CONTENT_REL_ORIGIN_PROVIDER = "CONTENT_REL_ORIGIN_PROVIDER";
    final static String BUTTONS_SPAN_REL_ORIGIN_PROVIDER = "BUTTONS_SPAN_REL_ORIGIN_PROVIDER";
    final static String BUTTON_HOOKS_AND_ACTIONS = "BUTTON_HOOKS_AND_ACTIONS";

    private final Function<UUID, Component> GET_COMPONENT;
    @SuppressWarnings("rawtypes") private final Function<String, Consumer> GET_CONSUMER;

    public DialogBoxMethods(Function<UUID, Component> getComponent,
                            @SuppressWarnings("rawtypes") Function<String, Consumer> getConsumer) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
    }

    final static String DialogBox_boxDimens = "DialogBox_boxDimens";

    public FloatBox DialogBox_boxDimens(FunctionalProvider.Inputs inputs) {
        var dialogBox = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        ProviderAtTime<FloatBox> boxUnadjDimensProvider =
                getFromData(dialogBox, BOX_UNADJ_DIMENS_PROVIDER);
        var boxUnadjDimens = boxUnadjDimensProvider.provide(inputs.timestamp());

        ProviderAtTime<Vertex> componentOriginProvider =
                getFromData(dialogBox, COMPONENT_ORIGIN_PROVIDER);
        var componentOrigin = componentOriginProvider.provide(inputs.timestamp());

        return floatBoxOf(
                componentOrigin,
                boxUnadjDimens.width(),
                boxUnadjDimens.height()
        );
    }

    final static String DialogBox_boxUnadjDimens = "DialogBox_boxUnadjDimens";

    public FloatBox DialogBox_boxUnadjDimens(FunctionalProvider.Inputs inputs) {
        var dialogBox = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));
        ProviderAtTime<FloatBox> boxUnadjDimensProvider =
                getFromData(dialogBox, BOX_UNADJ_DIMENS_PROVIDER);
        var boxUnadjDimens = boxUnadjDimensProvider.provide(inputs.timestamp());

        return floatBoxOf(
                boxUnadjDimens.width(),
                boxUnadjDimens.height()
        );
    }

    final static String DialogBox_contentOrigin = "DialogBox_contentOrigin";

    public Vertex DialogBox_contentOrigin(FunctionalProvider.Inputs inputs) {
        return getAdjOrigin(inputs, CONTENT_REL_ORIGIN_PROVIDER);
    }

    final static String DialogBox_buttonsSpanOrigin = "DialogBox_buttonsSpanOrigin";

    public Vertex DialogBox_buttonsSpanOrigin(FunctionalProvider.Inputs inputs) {
        return getAdjOrigin(inputs, BUTTONS_SPAN_REL_ORIGIN_PROVIDER);
    }

    private Vertex getAdjOrigin(FunctionalProvider.Inputs inputs, String relOriginProviderKey) {
        var dialogBox = GET_COMPONENT.apply(getFromData(inputs, COMPONENT_UUID));

        ProviderAtTime<Vertex> componentOriginProvider =
                getFromData(dialogBox, COMPONENT_ORIGIN_PROVIDER);
        var componentOrigin = componentOriginProvider.provide(inputs.timestamp());

        ProviderAtTime<Vertex> relOriginProvider = getFromData(dialogBox, relOriginProviderKey);
        var contentRelOrigin = relOriginProvider.provide(inputs.timestamp());

        return translateVertex(componentOrigin, contentRelOrigin);
    }

    final static String DialogBox_callHookForButtonAction = "DialogBox_callHookForButtonAction";

    public void DialogBox_callHookForButtonAction(EventInputs e) {
        var button = e.component;
        var dialogBox = getDialogBox(button);

        Map<UUID, String[]> buttonHooksAndActions = getFromData(dialogBox, BUTTON_HOOKS_AND_ACTIONS);
        var hookAndAction = buttonHooksAndActions.get(button.uuid());

        var preHookId = hookAndAction[0];
        var buttonActionId = hookAndAction[1];
        var postHookId = hookAndAction[2];

        runAction(preHookId, e);
        runAction(buttonActionId, e);
        runAction(postHookId, e);
    }

    private void runAction(String id, EventInputs e) {
        if (id != null) {
            var consumer = GET_CONSUMER.apply(id);
            //noinspection unchecked
            consumer.accept(e);
        }
    }

    public static final String DialogBox_close = "DialogBox_close";

    public void DialogBox_close(EventInputs e) {
        var button = e.component;
        var dialogBox = getDialogBox(button);

        dialogBox.delete();
    }

    private Component getDialogBox(Component button) {
        var buttonSpan = button.getContainingComponent();
        var buttonScrollableSpan = buttonSpan.getContainingComponent();
        return buttonScrollableSpan.getContainingComponent();
    }
}
