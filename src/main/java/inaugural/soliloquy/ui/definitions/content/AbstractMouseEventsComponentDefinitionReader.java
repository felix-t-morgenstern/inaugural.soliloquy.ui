package inaugural.soliloquy.ui.definitions.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents.MouseEventInputs;

import java.util.Map;
import java.util.function.Function;

public abstract class AbstractMouseEventsComponentDefinitionReader extends AbstractContentDefinitionReader {
    @SuppressWarnings("rawtypes") protected final Function<String, Action> GET_ACTION;

    protected AbstractMouseEventsComponentDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") StaticProvider nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction) {
        super(providerReader, nullProvider);
        GET_ACTION = Check.ifNull(getAction, "getAction");
    }

    protected Action<MouseEventInputs> getAction(String id) {
        //noinspection unchecked
        return id == null ? null : GET_ACTION.apply(id);
    }

    protected Map<Integer, Action<MouseEventInputs>> getActionPerButton(Map<Integer, String> ids) {
        var actionPerButton = Collections.<Integer, Action<MouseEventInputs>>mapOf();
        if (ids != null) {
            //noinspection unchecked
            ids.forEach((button, id) -> actionPerButton.put(button, GET_ACTION.apply(id)));
        }
        return actionPerButton;
    }
}
