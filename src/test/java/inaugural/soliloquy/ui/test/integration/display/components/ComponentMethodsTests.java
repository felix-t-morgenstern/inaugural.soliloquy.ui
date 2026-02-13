package inaugural.soliloquy.ui.test.integration.display.components;

import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.components.ComponentMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockMap;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class ComponentMethodsTests {
    private final long TIMESTAMP = randomLong();
    private final UUID COMPONENT_UUID = randomUUID();
    private final UUID CONTENT_UUID = randomUUID();
    private final FloatBox CONTENT_UNADJ_DIMENS = randomFloatBox();
    private final Vertex CONTENT_ORIGIN = randomVertex();
    private final List<Vertex> CONTENT_UNADJUSTED_VERTICES =
            IntStream.range(0, randomIntInRange(10, 40)).mapToObj(_ -> randomVertex()).toList();
    private final int VERTICES_INDEX = randomIntInRange(0, CONTENT_UNADJUSTED_VERTICES.size() - 1);
    private final Vertex CONTENT_POLYGON_OFFSET = randomVertex();

    @Mock private Component mockComponent;
    @Mock private Function<UUID, Component> mockGetComponent;

    private Map<String, Object> mockComponentData;
    private Map<UUID, FloatBox> mockContentUnadjDimens;
    private Map<UUID, Vertex> mockContentSpecificOrigins;
    private Map<UUID, Vertex> mockContentPolygonOffsets;
    private Map<UUID, List<Vertex>> mockContentUnadjVertices;

    private ComponentMethods methods;

    @BeforeEach
    public void setUp() {
        mockContentSpecificOrigins = generateMockMap(
                pairOf(CONTENT_UUID, CONTENT_ORIGIN)
        );

        mockContentUnadjVertices = generateMockMap(
                pairOf(CONTENT_UUID, CONTENT_UNADJUSTED_VERTICES)
        );

        mockContentUnadjDimens = generateMockMap(
                pairOf(CONTENT_UUID, CONTENT_UNADJ_DIMENS)
        );

        mockContentPolygonOffsets = generateMockMap(
                pairOf(CONTENT_UUID, CONTENT_POLYGON_OFFSET)
        );

        mockComponentData = generateMockMap(
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentUnadjDimens),
                pairOf(Constants.CONTENT_UNADJUSTED_VERTICES, mockContentUnadjVertices)
        );
        lenient().when(mockComponent.data()).thenReturn(mockComponentData);

        lenient().when(mockComponent.uuid()).thenReturn(COMPONENT_UUID);

        lenient().when(mockGetComponent.apply(any())).thenReturn(mockComponent);

        methods = new ComponentMethods(mockGetComponent);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ComponentMethods(null));
    }

    @Test
    public void testComponent_innerContentDimensWithContentSpecificOverrideNoOverride() {
        var inputs = providerInputs(TIMESTAMP, mapOf(
                CONTAINING_COMPONENT_UUID,
                COMPONENT_UUID,
                Constants.CONTENT_UUID,
                CONTENT_UUID
        ));

        var output = methods.Component_innerContentDimensWithContentSpecificOverride(inputs);

        assertEquals(CONTENT_UNADJ_DIMENS, output);

        verify(mockGetComponent, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_UNADJUSTED_DIMENS);
        verify(mockContentUnadjDimens, once()).get(CONTENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_SPECIFIC_ORIGINS);
        //noinspection SuspiciousMethodCalls
        verify(mockContentSpecificOrigins, never()).get(any());
    }

    @Test
    public void testComponent_innerContentDimensWithContentSpecificOverrideWithOverride() {
        when(mockComponentData.get(CONTENT_SPECIFIC_ORIGINS))
                .thenReturn(mockContentSpecificOrigins);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                CONTAINING_COMPONENT_UUID,
                COMPONENT_UUID,
                Constants.CONTENT_UUID,
                CONTENT_UUID
        ));

        var output = methods.Component_innerContentDimensWithContentSpecificOverride(inputs);

        assertEquals(floatBoxOf(
                CONTENT_ORIGIN,
                CONTENT_UNADJ_DIMENS.width(),
                CONTENT_UNADJ_DIMENS.height()
        ), output);

        verify(mockGetComponent, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_UNADJUSTED_DIMENS);
        verify(mockContentUnadjDimens, once()).get(CONTENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_SPECIFIC_ORIGINS);
        verify(mockContentSpecificOrigins, once()).get(CONTENT_UUID);
    }

    @Test
    public void testComponent_innerContentSpecificRenderingLoc() {
        when(mockComponentData.get(CONTENT_SPECIFIC_ORIGINS))
                .thenReturn(mockContentSpecificOrigins);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                CONTAINING_COMPONENT_UUID,
                COMPONENT_UUID,
                Constants.CONTENT_UUID,
                CONTENT_UUID
        ));

        var output = methods.Component_innerContentSpecificRenderingLoc(inputs);

        assertEquals(CONTENT_ORIGIN, output);

        verify(mockGetComponent, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_SPECIFIC_ORIGINS);
        verify(mockContentSpecificOrigins, once()).get(CONTENT_UUID);
    }

    @Test
    public void testComponent_innerContentPolygonVertexWithContentSpecificOverrideNoOverride() {
        var inputs = providerInputs(TIMESTAMP, mapOf(
                CONTAINING_COMPONENT_UUID,
                COMPONENT_UUID,
                Constants.CONTENT_UUID,
                CONTENT_UUID,
                Constants.VERTICES_INDEX,
                VERTICES_INDEX
        ));

        var output = methods.Component_innerContentPolygonVertexWithContentSpecificOverride(inputs);

        assertEquals(CONTENT_UNADJUSTED_VERTICES.get(VERTICES_INDEX), output);

        verify(mockGetComponent, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(Constants.CONTENT_UNADJUSTED_VERTICES);
        verify(mockContentUnadjVertices, once()).get(CONTENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_POLYGON_OFFSETS);
        verify(mockContentPolygonOffsets, never()).get(CONTENT_UUID);
    }

    @Test
    public void testComponent_innerContentPolygonVertexWithContentSpecificOverrideWithOverride() {
        when(mockComponentData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                CONTAINING_COMPONENT_UUID,
                COMPONENT_UUID,
                Constants.CONTENT_UUID,
                CONTENT_UUID,
                Constants.VERTICES_INDEX,
                VERTICES_INDEX
        ));

        var output = methods.Component_innerContentPolygonVertexWithContentSpecificOverride(inputs);

        assertEquals(
                translateVertex(
                        CONTENT_UNADJUSTED_VERTICES.get(VERTICES_INDEX),
                        CONTENT_POLYGON_OFFSET
                ),
                output
        );

        verify(mockGetComponent, once()).apply(COMPONENT_UUID);
        verify(mockComponentData, once()).get(Constants.CONTENT_UNADJUSTED_VERTICES);
        verify(mockContentUnadjVertices, once()).get(CONTENT_UUID);
        verify(mockComponentData, once()).get(CONTENT_POLYGON_OFFSETS);
        verify(mockContentPolygonOffsets, once()).get(CONTENT_UUID);
    }
}
