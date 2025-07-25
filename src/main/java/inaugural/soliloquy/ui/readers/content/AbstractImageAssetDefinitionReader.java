package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;

import java.util.function.Function;

public class AbstractImageAssetDefinitionReader extends AbstractMouseEventsComponentDefinitionReader {
    protected final ShiftDefinitionReader SHIFT_READER;

    protected AbstractImageAssetDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") StaticProvider nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction,
            ShiftDefinitionReader shiftReader) {
        super(providerReader, nullProvider, getAction);
        SHIFT_READER = Check.ifNull(shiftReader, "shiftReader");
    }
}
