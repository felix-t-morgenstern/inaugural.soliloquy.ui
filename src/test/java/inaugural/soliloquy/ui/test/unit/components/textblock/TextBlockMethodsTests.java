package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.test.unit.components.AbstractComponentMethodsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomFloat;
import static inaugural.soliloquy.tools.random.Random.randomVertex;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class TextBlockMethodsTests extends AbstractComponentMethodsTest {
    private final Vertex LOC = randomVertex();
    private final float TOP_OFFSET = randomFloat();

    @Mock private ProviderAtTime<Vertex> mockUpperLeftProvider;

    private TextBlockMethods methods;

    @BeforeEach
    public void setUp() {
        super.setUp();

        methods = new TextBlockMethods(MOCK_GET_COMPONENT);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new TextBlockMethods(null));
    }

    @Test
    public void testTextBlock_provideTextRenderingLocWithNewTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockUpperLeftProvider.provide(anyLong())).thenReturn(LOC);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                ComponentMethods.COMPONENT_UUID,
                COMPONENT_UUID,
                TextBlock_blockUpperLeftProvider,
                mockUpperLeftProvider,
                TextBlock_topOffset,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextRenderingLoc(inputs);

        assertEquals(vertexOf(LOC.X, LOC.Y + TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockUpperLeftProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
        verify(mockComponentData, once()).put(ORIGIN_OVERRIDE, LOC);
    }

    @Test
    public void testTextBlock_provideTextRenderingLocWithCurrentTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockComponentData.get(ORIGIN_OVERRIDE)).thenReturn(LOC);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                ComponentMethods.COMPONENT_UUID,
                COMPONENT_UUID,
                TextBlock_blockUpperLeftProvider,
                mockUpperLeftProvider,
                TextBlock_topOffset,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextRenderingLoc(inputs);

        assertEquals(vertexOf(LOC.X, LOC.Y + TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, once()).get(ORIGIN_OVERRIDE);
        verify(mockComponentData, never()).put(anyString(), any());
    }

    @Test
    public void testTextBlock_provideTextRenderingLoc_WithOverride() {
        var originOverride = randomVertex();
        @SuppressWarnings("unchecked") ProviderAtTime<Vertex> mockOriginOverrideProvider = mock(ProviderAtTime.class);
        when(mockOriginOverrideProvider.provide(anyLong())).thenReturn(originOverride);
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockComponentData.get(ORIGIN_OVERRIDE_PROVIDER)).thenReturn(mockOriginOverrideProvider);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                ComponentMethods.COMPONENT_UUID,
                COMPONENT_UUID,
                TextBlock_topOffset,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextRenderingLoc(inputs);

        assertEquals(vertexOf(originOverride.X, originOverride.Y + TOP_OFFSET), output);
        verify(mockComponentData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockComponentData, never()).get(TextBlock_blockUpperLeftProvider);
        verify(mockOriginOverrideProvider, once()).provide(TIMESTAMP);
    }
}
