package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.test.unit.components.ComponentMethodsTest;
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
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class TextBlockMethodsTests extends ComponentMethodsTest {
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
    public void testProvideTextRenderingLoc_TextBlockWithNewTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockUpperLeftProvider.provide(anyLong())).thenReturn(LOC);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_ID,
                COMPONENT_ID,
                TextBlock_blockUpperLeftProvider,
                mockUpperLeftProvider,
                TextBlock_topOffset,
                TOP_OFFSET
        ));

        var output = methods.provideTextRenderingLoc_TextBlock(inputs);

        assertEquals(vertexOf(LOC.X, LOC.Y + TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_ID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockUpperLeftProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
        verify(mockComponentData, once()).put(BLOCK_UPPER_LEFT, LOC);
    }

    @Test
    public void testProvideTextRenderingLoc_TextBlockWithCurrentTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockComponentData.get(BLOCK_UPPER_LEFT)).thenReturn(LOC);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_ID,
                COMPONENT_ID,
                TextBlock_blockUpperLeftProvider,
                mockUpperLeftProvider,
                TextBlock_topOffset,
                TOP_OFFSET
        ));

        var output = methods.provideTextRenderingLoc_TextBlock(inputs);

        assertEquals(vertexOf(LOC.X, LOC.Y + TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_ID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, once()).get(BLOCK_UPPER_LEFT);
        verify(mockComponentData, never()).put(anyString(), any());
    }
}
