package inaugural.soliloquy.ui.test.unit.components;

import inaugural.soliloquy.tools.testing.Mock;
import inaugural.soliloquy.ui.Constants;
import soliloquy.specs.io.graphics.renderables.Component;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithUuid;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.lenient;

public abstract class AbstractComponentMethodsTest {
    protected final long TIMESTAMP = randomLong();

    protected final UUID COMPONENT_UUID = randomUUID();
    protected final Mock.LookupAndEntitiesWithUuid<Component> MOCK_COMPONENT_AND_LOOKUP =
            generateMockLookupFunctionWithUuid(Component.class, COMPONENT_UUID);
    protected final Component MOCK_COMPONENT = MOCK_COMPONENT_AND_LOOKUP.entities.getFirst();
    protected final Function<UUID, Component> MOCK_GET_COMPONENT = MOCK_COMPONENT_AND_LOOKUP.lookup;

    @org.mockito.Mock protected Map<String, Object> mockComponentData;

    protected void setUp() {
        lenient().when(mockComponentData.get(Constants.COMPONENT_UUID)).thenReturn(
                COMPONENT_UUID);
        lenient().when(MOCK_COMPONENT.data()).thenReturn(mockComponentData);
    }
}
