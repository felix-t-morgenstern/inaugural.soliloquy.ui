package inaugural.soliloquy.ui.test.unit.components;

import inaugural.soliloquy.tools.testing.Mock;
import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;
import soliloquy.specs.ui.definitions.providers.StaticProviderDefinition;

import java.util.Map;
import java.util.function.Function;

import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
public class ComponentDefinitionReaderTest {
    protected final String FONT_ID = randomString();
    protected final Mock.LookupAndEntitiesWithId<Font> MOCK_FONT_AND_LOOKUP =
            generateMockLookupFunctionWithId(Font.class, FONT_ID);
    protected final Font MOCK_FONT = MOCK_FONT_AND_LOOKUP.entities.getFirst();
    protected final Function<String, Font> MOCK_GET_FONT = MOCK_FONT_AND_LOOKUP.lookup;

    protected final int Z = randomInt();

    protected final long TIMESTAMP = randomLong();

    @org.mockito.Mock protected ProviderDefinitionReader mockProviderDefReader;
    @org.mockito.Mock protected ColorShiftDefinitionReader mockShiftReader;

    protected <T> T extractStaticVal(AbstractProviderDefinition<T> provider) {
        return ((StaticProviderDefinition<T>) provider).VALUE;
    }

    protected <T> void assertIsFunctionalProviderWithData(AbstractProviderDefinition<T> providerDef,
                                                          String expectedMethod,
                                                          Map<Object, Object> expectedData) {
        assertInstanceOf(FunctionalProviderDefinition.class, providerDef);
        var funcProviderDef = (FunctionalProviderDefinition<T>) providerDef;
        assertEquals(expectedMethod, funcProviderDef.PROVIDE_FUNCTION_ID);
        assertEquals(expectedData, funcProviderDef.data);
    }
}
