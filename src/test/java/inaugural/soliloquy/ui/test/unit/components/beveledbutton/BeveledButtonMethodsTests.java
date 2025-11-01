package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.ui.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.Component;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithUuid;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithUuid;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods.BEVEL_LAST_TIMESTAMP;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class BeveledButtonMethodsTests {
    private final long TIMESTAMP = randomLong();

    private final UUID COMPONENT_ID = randomUUID();
    private final LookupAndEntitiesWithUuid<Component> MOCK_COMPONENT_AND_LOOKUP =
            generateMockLookupFunctionWithUuid(Component.class, COMPONENT_ID);
    private final Component MOCK_COMPONENT = MOCK_COMPONENT_AND_LOOKUP.entities.getFirst();
    private final Function<UUID, Component> MOCK_GET_COMPONENT = MOCK_COMPONENT_AND_LOOKUP.lookup;

    @Mock private Map<String, Object> mockComponentData;

    private BeveledButtonMethods methods;

    @BeforeEach
    public void setUp() {
        lenient().when(mockComponentData.get(BEVEL_LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        lenient().when(mockComponentData.get(Constants.COMPONENT_ID)).thenReturn(COMPONENT_ID);
        lenient().when(MOCK_COMPONENT.data()).thenReturn(mockComponentData);

        methods = new BeveledButtonMethods(MOCK_GET_COMPONENT);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new BeveledButtonMethods(null));
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp() {
        var inputs = providerInputs(TIMESTAMP + 1, null, mapOf(

        ));
    }
}
