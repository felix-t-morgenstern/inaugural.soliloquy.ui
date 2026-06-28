package inaugural.soliloquy.ui.components.textblock;

import inaugural.soliloquy.ui.Constants;
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
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.textblock.TextBlockMethods.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class TextBlockMethodsTests extends AbstractComponentMethodsTest {
    private final Vertex LOC = randomVertex();
    private final Vertex TOP_OFFSET = randomVertex();

    @Mock private ProviderAtTime<Vertex> mockUpperLeftProvider;

    private TextBlockMethods methods;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockUpperLeftProvider.provide(anyLong())).thenReturn(LOC);

        methods = new TextBlockMethods(MOCK_GET_COMPONENT);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new TextBlockMethods(null));
    }

    @Test
    public void testTextBlock_provideTextLineRenderingLocWithNewTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockComponentData.get(COMPONENT_ORIGIN_PROVIDER)).thenReturn(mockUpperLeftProvider);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_UUID,
                COMPONENT_UUID,
                TEXT_BLOCK_LINE_OFFSET,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextLineRenderingLoc(inputs);

        assertEquals(translateVertex(LOC, TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockUpperLeftProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
        verify(mockComponentData, once()).put(COMPONENT_ORIGIN, LOC);
    }

    @Test
    public void testTextBlock_provideTextLineRenderingLocWithCurrentTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockComponentData.get(COMPONENT_ORIGIN)).thenReturn(LOC);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_UUID,
                COMPONENT_UUID,
                COMPONENT_ORIGIN_PROVIDER,
                mockUpperLeftProvider,
                TEXT_BLOCK_LINE_OFFSET,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextLineRenderingLoc(inputs);

        assertEquals(translateVertex(LOC, TOP_OFFSET), output);
        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, once()).get(COMPONENT_ORIGIN);
        verify(mockComponentData, never()).put(anyString(), any());
    }

    @Test
    public void testTextBlock_provideTextLineRenderingLoc_WithOverride() {
        var componentOrigin = randomVertex();
        @SuppressWarnings("unchecked") ProviderAtTime<Vertex> mockOriginOverrideProvider =
                mock(ProviderAtTime.class);
        when(mockOriginOverrideProvider.provide(anyLong())).thenReturn(componentOrigin);
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockComponentData.get(COMPONENT_ORIGIN_PROVIDER)).thenReturn(
                mockOriginOverrideProvider);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_UUID,
                COMPONENT_UUID,
                TEXT_BLOCK_LINE_OFFSET,
                TOP_OFFSET
        ));

        var output = methods.TextBlock_provideTextLineRenderingLoc(inputs);

        assertEquals(translateVertex(componentOrigin, TOP_OFFSET), output);
        verify(mockComponentData, once()).get(COMPONENT_ORIGIN_PROVIDER);
        verify(mockComponentData, never()).get(COMPONENT_ORIGIN);
        verify(mockOriginOverrideProvider, once()).provide(TIMESTAMP);
    }

    @Test
    public void testTextBlock_getDimens() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP - 1);
        when(mockComponentData.get(COMPONENT_ORIGIN_PROVIDER)).thenReturn(mockUpperLeftProvider);
        var textBlockWidth = randomFloat();
        when(mockComponentData.get(TEXT_BLOCK_WIDTH)).thenReturn(textBlockWidth);
        var textBlockHeight = randomFloat();
        when(mockComponentData.get(TEXT_BLOCK_HEIGHT)).thenReturn(textBlockHeight);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_UUID,
                COMPONENT_UUID
        ));

        var output = methods.TextBlock_getDimens(inputs);

        assertEquals(floatBoxOf(
                LOC,
                translateVertex(
                        LOC,
                        textBlockWidth,
                        textBlockHeight
                )
        ), output);

        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, once()).get(COMPONENT_ORIGIN_PROVIDER);
        verify(mockUpperLeftProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
        verify(mockComponentData, once()).put(COMPONENT_ORIGIN, LOC);
        verify(mockComponentData, once()).get(TEXT_BLOCK_WIDTH);
        verify(mockComponentData, once()).get(TEXT_BLOCK_HEIGHT);
    }

    @Test
    public void testTextBlock_getDimensSameTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockComponentData.get(COMPONENT_ORIGIN)).thenReturn(LOC);
        var textBlockWidth = randomFloat();
        when(mockComponentData.get(TEXT_BLOCK_WIDTH)).thenReturn(textBlockWidth);
        var textBlockHeight = randomFloat();
        when(mockComponentData.get(TEXT_BLOCK_HEIGHT)).thenReturn(textBlockHeight);

        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                Constants.COMPONENT_UUID,
                COMPONENT_UUID
        ));

        var output = methods.TextBlock_getDimens(inputs);

        assertEquals(floatBoxOf(
                LOC,
                translateVertex(
                        LOC,
                        textBlockWidth,
                        textBlockHeight
                )
        ), output);

        verify(MOCK_GET_COMPONENT, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, never()).get(COMPONENT_ORIGIN_PROVIDER);
        verify(mockUpperLeftProvider, never()).provide(anyLong());
        verify(mockComponentData, never()).put(eq(LAST_TIMESTAMP), anyLong());
        verify(mockComponentData, never()).put(eq(COMPONENT_ORIGIN), any());
        verify(mockComponentData, once()).get(TEXT_BLOCK_WIDTH);
        verify(mockComponentData, once()).get(TEXT_BLOCK_HEIGHT);
    }
}
