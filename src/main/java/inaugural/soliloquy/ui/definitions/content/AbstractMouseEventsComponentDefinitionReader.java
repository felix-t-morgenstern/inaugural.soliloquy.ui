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

public abstract class AbstractMouseEventsComponentDefinitionReader {
    protected final ProviderDefinitionReader PROVIDER_READER;
    @SuppressWarnings("rawtypes") private final StaticProvider NULL_PROVIDER;
    @SuppressWarnings("rawtypes") protected final Function<String, Action> GET_ACTION;

    protected AbstractMouseEventsComponentDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") StaticProvider nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction) {
        PROVIDER_READER = Check.ifNull(providerReader, "providerReader");
        NULL_PROVIDER = Check.ifNull(nullProvider, "nullProvider");
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

    protected <T> ProviderAtTime<T> provider(AbstractProviderDefinition<T> definition) {
        //noinspection unchecked
        return definition == null ? NULL_PROVIDER : PROVIDER_READER.read(definition);
    }
}
