package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.function.Function;

public class AbstractImageAssetDefinitionReader extends AbstractMouseEventsComponentDefinitionReader {
    protected final ColorShiftDefinitionReader SHIFT_READER;

    protected AbstractImageAssetDefinitionReader(
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") ProviderAtTime nullProvider,
            @SuppressWarnings("rawtypes") Function<String, Action> getAction,
            ColorShiftDefinitionReader shiftReader) {
        super(providerReader, nullProvider, getAction);
        SHIFT_READER = Check.ifNull(shiftReader, "shiftReader");
    }
}
