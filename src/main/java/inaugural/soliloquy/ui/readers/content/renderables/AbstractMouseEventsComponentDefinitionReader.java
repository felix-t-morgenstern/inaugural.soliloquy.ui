package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.AbstractContentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.EventInputs;

import java.util.Map;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public abstract class AbstractMouseEventsComponentDefinitionReader extends
        AbstractContentDefinitionReader {
    @SuppressWarnings("rawtypes") protected final Function<String, Action> GET_ACTION;

    protected AbstractMouseEventsComponentDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") ProviderAtTime nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction) {
        super(providerReader, nullProvider);
        GET_ACTION = Check.ifNull(getAction, "getAction");
    }

    protected Action<EventInputs> getAction(String id) {
        //noinspection unchecked
        return defaultIfNull(id, null, GET_ACTION);
    }

    protected Map<Integer, Action<EventInputs>> getActionPerButton(Map<Integer, String> ids) {
        var actionPerButton = Collections.<Integer, Action<EventInputs>>mapOf();
        if (ids != null) {
            //noinspection unchecked
            ids.forEach((button, id) -> actionPerButton.put(button, GET_ACTION.apply(id)));
        }
        return actionPerButton;
    }
}
