package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.AbstractContentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Consumer;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.EventInputs;

import java.util.Map;
import java.util.function.Function;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;

public abstract class AbstractMouseEventsComponentDefinitionReader extends
        AbstractContentDefinitionReader {
    @SuppressWarnings("rawtypes") protected final Function<String, Consumer> GET_CONSUMER;

    protected AbstractMouseEventsComponentDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") ProviderAtTime nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Consumer> getConsumer) {
        super(providerReader, nullProvider);
        GET_CONSUMER = Check.ifNull(getConsumer, "getConsumer");
    }

    protected Consumer<EventInputs> getConsumer(String id) {
        //noinspection unchecked
        return defaultIfNull(id, null, GET_CONSUMER);
    }

    protected Map<Integer, Consumer<EventInputs>> getConsumerPerButton(Map<Integer, String> ids) {
        var actionPerButton = Collections.<Integer, Consumer<EventInputs>>mapOf();
        if (ids != null) {
            //noinspection unchecked
            ids.forEach((button, id) -> actionPerButton.put(button, GET_CONSUMER.apply(id)));
        }
        return actionPerButton;
    }
}
