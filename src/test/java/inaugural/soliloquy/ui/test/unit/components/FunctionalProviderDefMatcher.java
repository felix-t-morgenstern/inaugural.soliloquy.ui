package inaugural.soliloquy.ui.test.unit.components;


import org.mockito.ArgumentMatcher;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.Map;
import java.util.Objects;

// NB: This matcher doesn't verify whether the same type parameters were provided to
// functionalProvider(), but if incorrect type parameters were provided in the
// implementation, it would fail to compile, so this doesn't need testing
@SuppressWarnings("rawtypes")
public class FunctionalProviderDefMatcher<T extends AbstractProviderDefinition> implements ArgumentMatcher<T> {
    private final String METHOD;
    private final Map<String, Object> DATA;

    public FunctionalProviderDefMatcher(String method, Map<String, Object> data) {
        METHOD = method;
        DATA = data;
    }

    @Override
    public boolean matches(T definition) {
        if (!(definition instanceof FunctionalProviderDefinition<?>)) {
            return false;
        }

        var functionalDef = (FunctionalProviderDefinition) definition;

        if (METHOD != null || DATA != null) {
            return Objects.equals(METHOD, functionalDef.PROVIDE_FUNCTION_ID) &&
                    Objects.equals(DATA, functionalDef.data);
        }
        else {
            return true;
        }
    }
}
