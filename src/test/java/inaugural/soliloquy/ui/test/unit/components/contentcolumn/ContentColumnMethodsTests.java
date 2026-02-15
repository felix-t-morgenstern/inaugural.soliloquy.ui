package inaugural.soliloquy.ui.test.unit.components.contentcolumn;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.contentcolumn.ContentColumnMethods;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockList;
import static inaugural.soliloquy.tools.testing.Mock.generateMockMap;
import static inaugural.soliloquy.tools.valueobjects.Vertex.polygonEncompassingDimens;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.Component.Addend.addend;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class ContentColumnMethodsTests {
    private final UUID COLUMN_UUID = randomUUID();
    private final Vertex COLUMN_RENDERING_LOC = randomVertex();
    private final float COLUMN_WIDTH = randomFloat();

    private final UUID CONTENT_COMPONENT_UUID = randomUUID();
    private final FloatBox CONTENT_COMPONENT_UNADJ_DIMENS = randomFloatBox();
    private final float CONTENT_COMPONENT_SPACING_AFTER = randomFloat();
    private final float CONTENT_COMPONENT_INDENT = randomFloat();

    private final UUID CONTENT_TEXT_LINE_UUID = randomUUID();
    private final float CONTENT_TEXT_LINE_HEIGHT = randomFloat();
    private final float CONTENT_TEXT_LINE_SPACING_AFTER = randomFloat();
    private final float CONTENT_TEXT_LINE_INDENT = randomFloat();

    private final UUID CONTENT_WITH_DIMENS_UUID = randomUUID();
    private final FloatBox CONTENT_WITH_DIMENS_UNADJ_DIMENS = randomFloatBox();
    private final float CONTENT_WITH_DIMENS_SPACING_AFTER = randomFloat();
    private final float CONTENT_WITH_DIMENS_INDENT = randomFloat();

    private final UUID CONTENT_TRIANGLE_UUID = randomUUID();
    private final Vertex CONTENT_TRIANGLE_UNADJ_VERTEX_1 = randomVertex();
    private final Vertex CONTENT_TRIANGLE_UNADJ_VERTEX_2 = randomVertex();
    private final Vertex CONTENT_TRIANGLE_UNADJ_VERTEX_3 = randomVertex();
    private final float CONTENT_TRIANGLE_SPACING_AFTER = randomFloat();
    private final FloatBox CONTENT_TRIANGLE_POLYGON_DIMENS = polygonEncompassingDimens(
            CONTENT_TRIANGLE_UNADJ_VERTEX_1,
            CONTENT_TRIANGLE_UNADJ_VERTEX_2,
            CONTENT_TRIANGLE_UNADJ_VERTEX_3
    );
    private final float CONTENT_TRIANGLE_INDENT = randomFloat();

    private final float EXPECTED_HEIGHT =
            CONTENT_COMPONENT_UNADJ_DIMENS.height() +
                    CONTENT_COMPONENT_SPACING_AFTER +
                    CONTENT_TEXT_LINE_HEIGHT +
                    CONTENT_TEXT_LINE_SPACING_AFTER +
                    CONTENT_WITH_DIMENS_UNADJ_DIMENS.height() +
                    CONTENT_WITH_DIMENS_SPACING_AFTER +
                    CONTENT_TRIANGLE_POLYGON_DIMENS.height() +
                    CONTENT_TRIANGLE_SPACING_AFTER;
    private final float EXPECTED_HEIGHT_TO_TEXT_LINE =
            CONTENT_COMPONENT_UNADJ_DIMENS.height() + CONTENT_COMPONENT_SPACING_AFTER;
    private final float EXPECTED_HEIGHT_TO_WITH_DIMENS =
            EXPECTED_HEIGHT_TO_TEXT_LINE + CONTENT_TEXT_LINE_HEIGHT +
                    CONTENT_TEXT_LINE_SPACING_AFTER;
    private final float EXPECTED_HEIGHT_TO_TRIANGLE =
            EXPECTED_HEIGHT_TO_WITH_DIMENS + CONTENT_WITH_DIMENS_UNADJ_DIMENS.height() +
                    CONTENT_WITH_DIMENS_SPACING_AFTER;
    private final FloatBox EXPECTED_OUTPUT = floatBoxOf(
            COLUMN_RENDERING_LOC,
            COLUMN_WIDTH,
            EXPECTED_HEIGHT
    );

    private final long TIMESTAMP = randomLong();

    @Mock private Set<UUID> mockRegisteredComponents;

    @Mock private Component mockContentComponent;
    @Mock private ProviderAtTime<FloatBox> mockContentComponentDimens;
    @Mock private ProviderAtTime<Vertex> mockContentComponentOriginOverrideProvider;
    @Mock private Map<String, Object> mockContentComponentData;

    @Mock private TextLineRenderable mockContentTextLine;
    @Mock private ProviderAtTime<Float> mockContentTextLineHeightProvider;
    @Mock private ProviderAtTime<Vertex> mockContentTextLineOriginProvider;

    @Mock private RenderableWithMutableDimensions mockContentWithDimens;
    @Mock private ProviderAtTime<FloatBox> mockContentWithDimensUnadjDimens;
    @Mock private ProviderAtTime<FloatBox> mockContentWithDimensAdjDimens;

    @Mock private TriangleRenderable mockContentTriangle;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleUnadjVertex1;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleUnadjVertex2;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleUnadjVertex3;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleAdjVertex1;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleAdjVertex2;
    @Mock private ProviderAtTime<Vertex> mockContentTriangleAdjVertex3;

    @Mock private Component mockColumn;

    @SuppressWarnings("rawtypes") @Mock
    private Function<FunctionalProviderDefinition, ProviderAtTime> mockFunctionalProviderDefReader;
    @Mock private Function<UUID, Component> mockGetComponent;

    @Mock private ProviderAtTime<Vertex> mockColumnRenderingLocProvider;
    @Mock private Map<UUID, Vertex> mockContentPolygonOffsets;

    private Map<String, Object> mockColumnData;

    private ContentColumnMethods methods;

    @BeforeEach
    public void setUp() {
        lenient().when(mockColumnRenderingLocProvider.provide(anyLong()))
                .thenReturn(COLUMN_RENDERING_LOC);

        mockColumnData = generateMockMap(
                pairOf(COMPONENT_RENDERING_LOC, mockColumnRenderingLocProvider),
                pairOf(COMPONENT_WIDTH, COLUMN_WIDTH),
                pairOf(REGISTERED_CONTENTS, mockRegisteredComponents)
        );

        lenient().when(mockColumn.uuid()).thenReturn(COLUMN_UUID);
        lenient().when(mockColumn.data()).thenReturn(mockColumnData);

        lenient().when(mockGetComponent.apply(any())).thenReturn(mockColumn);

        lenient().when(mockContentComponent.uuid()).thenReturn(CONTENT_COMPONENT_UUID);
        lenient().when(mockContentComponentDimens.provide(anyLong()))
                .thenReturn(CONTENT_COMPONENT_UNADJ_DIMENS);
        lenient().when(mockContentComponent.getDimensionsProvider())
                .thenReturn(mockContentComponentDimens);
        lenient().when(mockContentComponent.data()).thenReturn(mockContentComponentData);

        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_COMPONENT_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )))).thenReturn(mockContentComponentOriginOverrideProvider);

        lenient().when(mockContentTextLine.uuid()).thenReturn(CONTENT_TEXT_LINE_UUID);
        lenient().when(mockContentTextLine.lineHeightProvider())
                .thenReturn(mockContentTextLineHeightProvider);
        lenient().when(mockContentTextLineHeightProvider.provide(anyLong()))
                .thenReturn(CONTENT_TEXT_LINE_HEIGHT);

        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TEXT_LINE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )))).thenReturn(mockContentTextLineOriginProvider);

        lenient().when(mockContentWithDimens.uuid()).thenReturn(CONTENT_WITH_DIMENS_UUID);
        lenient().when(mockContentWithDimens.getRenderingDimensionsProvider())
                .thenReturn(mockContentWithDimensUnadjDimens);
        lenient().when(mockContentWithDimensUnadjDimens.provide(anyLong()))
                .thenReturn(CONTENT_WITH_DIMENS_UNADJ_DIMENS);

        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentDimensWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_WITH_DIMENS_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )))).thenReturn(mockContentWithDimensAdjDimens);

        lenient().when(mockContentTriangle.uuid()).thenReturn(CONTENT_TRIANGLE_UUID);
        lenient().when(mockContentTriangle.containingComponent()).thenReturn(mockColumn);
        lenient().when(mockContentTriangle.getVertex1Provider())
                .thenReturn(mockContentTriangleUnadjVertex1);
        lenient().when(mockContentTriangle.getVertex2Provider())
                .thenReturn(mockContentTriangleUnadjVertex2);
        lenient().when(mockContentTriangle.getVertex3Provider())
                .thenReturn(mockContentTriangleUnadjVertex3);
        lenient().when(mockContentTriangleUnadjVertex1.provide(anyLong()))
                .thenReturn(CONTENT_TRIANGLE_UNADJ_VERTEX_1);
        lenient().when(mockContentTriangleUnadjVertex2.provide(anyLong()))
                .thenReturn(CONTENT_TRIANGLE_UNADJ_VERTEX_2);
        lenient().when(mockContentTriangleUnadjVertex3.provide(anyLong()))
                .thenReturn(CONTENT_TRIANGLE_UNADJ_VERTEX_3);

        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentPolygonVertexWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TRIANGLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID,
                                VERTICES_INDEX,
                                0
                        )
                )))).thenReturn(mockContentTriangleAdjVertex1);
        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentPolygonVertexWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TRIANGLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID,
                                VERTICES_INDEX,
                                1
                        )
                )))).thenReturn(mockContentTriangleAdjVertex2);
        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentPolygonVertexWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TRIANGLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID,
                                VERTICES_INDEX,
                                2
                        )
                )))).thenReturn(mockContentTriangleAdjVertex3);

        methods = new ContentColumnMethods(mockGetComponent, mockFunctionalProviderDefReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContentColumnMethods(null, mockFunctionalProviderDefReader));
        assertThrows(IllegalArgumentException.class,
                () -> new ContentColumnMethods(mockGetComponent, null));
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentWithSameTimestampAsPrev() {
        var columnDimens = randomFloatBox();
        when(mockColumnData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockColumnData.get(COMPONENT_DIMENS)).thenReturn(columnDimens);

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(columnDimens, output);

        verify(mockColumnData, once()).get(LAST_TIMESTAMP);
        verify(mockColumnData, once()).get(COMPONENT_DIMENS);
        verify(mockColumnData, never()).get(CONTENT_UNADJUSTED_DIMENS_PROVIDERS);
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentWithNewTimestampAndNoContent() {
        when(mockColumnData.get(CONTENTS)).thenReturn(listOf());
        var expectedOutput = floatBoxOf(COLUMN_RENDERING_LOC, COLUMN_WIDTH, 0f);

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(expectedOutput, output);

        verify(mockColumnData, once()).get(LAST_TIMESTAMP);
        verify(mockColumnData, once()).get(CONTENT_UNADJUSTED_DIMENS_PROVIDERS);
        verify(mockColumnData, once()).put(CONTENT_UNADJUSTED_DIMENS_PROVIDERS,
                Collections.<String, Object>mapOf());
        verify(mockColumnData, once()).get(CONTENT_UNADJUSTED_DIMENS);
        verify(mockColumnData, once()).put(CONTENT_UNADJUSTED_DIMENS,
                Collections.<String, Object>mapOf());
        verify(mockColumnData, once()).get(CONTENT_UNADJUSTED_VERTICES_PROVIDERS);
        verify(mockColumnData, once()).put(CONTENT_UNADJUSTED_VERTICES_PROVIDERS,
                Collections.<String, Object>mapOf());
        verify(mockColumnData, once()).get(CONTENT_UNADJUSTED_VERTICES);
        verify(mockColumnData, once()).put(CONTENT_UNADJUSTED_VERTICES,
                Collections.<String, Object>mapOf());
        verify(mockColumnData, once()).get(CONTENT_POLYGON_OFFSETS);
        verify(mockColumnData, once()).put(CONTENT_POLYGON_OFFSETS,
                Collections.<UUID, Vertex>mapOf());
        verify(mockColumnData, once()).get(COMPONENT_RENDERING_LOC);
        verify(mockColumnRenderingLocProvider, once()).provide(TIMESTAMP);
        verify(mockColumnData, once()).get(COMPONENT_WIDTH);
        verify(mockColumnData, once()).get(CONTENTS);
        verify(mockColumnData, once()).get(REGISTERED_CONTENTS);
        verify(mockColumnData, once()).put(COMPONENT_DIMENS, expectedOutput);
        verify(mockColumnData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentNewTimestampAndNewContentLeftAlignment() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentWithDimens,
                mockContentTriangle
        ));
        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                )
        ));
        when(mockColumnData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockContentComponent, once()).getDimensionsProvider();
        verify(mockContentComponentDimens, once()).provide(TIMESTAMP);
        verify(mockFunctionalProviderDefReader, once())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_COMPONENT_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockRegisteredComponents, once()).add(CONTENT_COMPONENT_UUID);
        verify(mockContentComponent, once()).data();
        verify(mockContentComponentData, once())
                .put(ORIGIN_OVERRIDE_PROVIDER, mockContentComponentOriginOverrideProvider);

        verify(mockFunctionalProviderDefReader, once())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TEXT_LINE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockRegisteredComponents, once()).add(CONTENT_TEXT_LINE_UUID);
        verify(mockContentTextLine, once())
                .setRenderingLocationProvider(mockContentTextLineOriginProvider);
        verify(mockContentTextLine, once()).setAlignment(HorizontalAlignment.LEFT);
        verify(mockContentTextLineHeightProvider, once()).provide(TIMESTAMP);

        verify(mockContentWithDimens, once()).getRenderingDimensionsProvider();
        verify(mockFunctionalProviderDefReader, once())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentDimensWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_WITH_DIMENS_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockContentWithDimens, once())
                .setRenderingDimensionsProvider(mockContentWithDimensAdjDimens);
        verify(mockContentWithDimensUnadjDimens, once()).provide(TIMESTAMP);

        verify(mockContentTriangle, atLeastOnce()).uuid();
        verify(mockContentTriangle, once()).getVertex1Provider();
        verify(mockContentTriangle, once()).getVertex2Provider();
        verify(mockContentTriangle, once()).getVertex3Provider();
        IntStream.range(0, 3).forEach(i -> verify(mockFunctionalProviderDefReader, once())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentPolygonVertexWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TRIANGLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID,
                                VERTICES_INDEX,
                                i
                        )
                ))));
        verify(mockContentTriangle, once()).setVertex1Provider(mockContentTriangleAdjVertex1);
        verify(mockContentTriangle, once()).setVertex2Provider(mockContentTriangleAdjVertex2);
        verify(mockContentTriangle, once()).setVertex3Provider(mockContentTriangleAdjVertex3);

        verify(mockColumnData, once()).put(COMPONENT_DIMENS, output);

        verify(mockColumnData, once()).put(CONTENT_SPECIFIC_ORIGINS, mapOf(
                CONTENT_COMPONENT_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X,
                        COLUMN_RENDERING_LOC.Y
                ),
                CONTENT_TEXT_LINE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TEXT_LINE
                ),
                CONTENT_WITH_DIMENS_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_WITH_DIMENS
                )
        ));

        verify(mockContentPolygonOffsets, once()).clear();
        verify(mockContentPolygonOffsets, once()).put(
                CONTENT_TRIANGLE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TRIANGLE
                )
        );

        verify(mockColumnData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentNewTimestampAndExistingContent() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentTriangle,
                mockContentWithDimens
        ));
        Map<UUID, List<Vertex>> mockContentUnadjVertices = generateMockMap();
        when(mockColumnData.get(CONTENT_UNADJUSTED_DIMENS)).thenReturn(mockContentUnadjVertices);
        Map<UUID, FloatBox> mockContentUnadjDimens = generateMockMap();
        when(mockColumnData.get(CONTENT_UNADJUSTED_VERTICES)).thenReturn(mockContentUnadjDimens);
        when(mockColumnData.get(CONTENT_UNADJUSTED_DIMENS_PROVIDERS)).thenReturn(mapOf(
                CONTENT_WITH_DIMENS_UUID, mockContentWithDimensUnadjDimens
        ));
        when(mockColumnData.get(CONTENT_UNADJUSTED_VERTICES_PROVIDERS)).thenReturn(mapOf(
                CONTENT_TRIANGLE_UUID, listOf(
                        mockContentTriangleUnadjVertex1,
                        mockContentTriangleUnadjVertex2,
                        mockContentTriangleUnadjVertex3
                )
        ));
        when(mockColumnData.get(REGISTERED_CONTENTS)).thenReturn(setOf(
                CONTENT_COMPONENT_UUID,
                CONTENT_TEXT_LINE_UUID
        ));
        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, 0f
                )
        ));

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockRegisteredComponents, never()).add(any());

        verify(mockContentUnadjVertices, once()).clear();
        verify(mockContentUnadjDimens, once()).clear();

        verify(mockContentComponent, once()).getDimensionsProvider();
        verify(mockContentComponentDimens, once()).provide(TIMESTAMP);
        verify(mockFunctionalProviderDefReader, never())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_COMPONENT_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockContentComponent, never()).data();
        verify(mockContentComponentData, never()).put(eq(ORIGIN_OVERRIDE_PROVIDER), any());

        verify(mockFunctionalProviderDefReader, never())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentSpecificRenderingLoc,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TEXT_LINE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockContentTextLine, never()).setRenderingLocationProvider(any());
        verify(mockContentTextLineHeightProvider, once()).provide(TIMESTAMP);

        verify(mockContentWithDimens, never()).getRenderingDimensionsProvider();
        verify(mockFunctionalProviderDefReader, never())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentDimensWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_WITH_DIMENS_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )));
        verify(mockContentWithDimens, never()).setRenderingDimensionsProvider(any());
        verify(mockContentWithDimensUnadjDimens, once()).provide(TIMESTAMP);

        verify(mockContentTriangle, atLeastOnce()).uuid();
        verify(mockContentTriangle, never()).getVertex1Provider();
        verify(mockContentTriangle, never()).getVertex2Provider();
        verify(mockContentTriangle, never()).getVertex3Provider();
        IntStream.range(0, 3).forEach(i -> verify(mockFunctionalProviderDefReader, never())
                .apply(argThat(new FunctionalProviderDefMatcher<>(
                        Component_innerContentPolygonVertexWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TRIANGLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID,
                                VERTICES_INDEX,
                                i
                        )
                ))));
        verify(mockContentTriangle, never()).setVertex1Provider(mockContentTriangleAdjVertex1);
        verify(mockContentTriangle, never()).setVertex2Provider(mockContentTriangleAdjVertex2);
        verify(mockContentTriangle, never()).setVertex3Provider(mockContentTriangleAdjVertex3);
    }

    @Test
    public void ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider() {
        var columnDimens = randomFloatBox();
        when(mockColumnData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockColumnData.get(COMPONENT_DIMENS)).thenReturn(columnDimens);

        Map<String, Object> mockInputsData = generateMockMap(pairOf(COMPONENT_UUID, COLUMN_UUID));

        var output = methods.ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider(
                providerInputs(TIMESTAMP, mockInputsData)
        );

        assertEquals(columnDimens, output);

        verify(mockInputsData, once()).get(COMPONENT_UUID);
        verify(mockGetComponent, once()).apply(COLUMN_UUID);

        verify(mockColumnData, once()).get(LAST_TIMESTAMP);
        verify(mockColumnData, once()).get(COMPONENT_DIMENS);
        verify(mockColumnData, never()).get(CONTENT_UNADJUSTED_DIMENS_PROVIDERS);
    }

    @Test
    public void testContentColumn_addItem() {
        var alignment = HorizontalAlignment.fromValue(randomIntInRange(1, 3));
        var contentComponentIndent = randomFloat();
        Map<String, Object> mockAddendData = generateMockMap(
                pairOf(SPACING_AFTER, CONTENT_COMPONENT_SPACING_AFTER),
                pairOf(ALIGNMENT, alignment),
                pairOf(INDENT, contentComponentIndent)
        );
        List<ContentColumnMethods.Content> mockContents = generateMockList();
        when(mockColumnData.get(CONTENTS)).thenReturn(mockContents);

        methods.ContentColumn_add(mockColumn, addend(mockContentComponent, mockAddendData));

        verify(mockColumn, once()).data();
        verify(mockColumnData, once()).get(CONTENTS);
        verify(mockContentComponent, once()).uuid();
        verify(mockAddendData, once()).get(SPACING_AFTER);
        verify(mockAddendData, once()).get(ALIGNMENT);
        verify(mockAddendData, once()).get(INDENT);
        verify(mockContents, once()).add(eq(new ContentColumnMethods.Content(
                CONTENT_COMPONENT_UUID,
                CONTENT_COMPONENT_SPACING_AFTER,
                alignment,
                contentComponentIndent
        )));
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentRightAlignment() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentTriangle,
                mockContentWithDimens
        ));

        when(mockColumnData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, 0f
                )
        ));

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockContentTextLine, once()).setAlignment(HorizontalAlignment.RIGHT);

        verify(mockColumnData, once()).put(CONTENT_SPECIFIC_ORIGINS, mapOf(
                CONTENT_COMPONENT_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + COLUMN_WIDTH -
                                CONTENT_COMPONENT_UNADJ_DIMENS.width(),
                        COLUMN_RENDERING_LOC.Y
                ),
                CONTENT_TEXT_LINE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + COLUMN_WIDTH,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TEXT_LINE
                ),
                CONTENT_WITH_DIMENS_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + COLUMN_WIDTH -
                                CONTENT_WITH_DIMENS_UNADJ_DIMENS.width(),
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_WITH_DIMENS
                )
        ));

        verify(mockContentPolygonOffsets, once()).put(
                CONTENT_TRIANGLE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + COLUMN_WIDTH -
                                CONTENT_TRIANGLE_POLYGON_DIMENS.width(),
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TRIANGLE
                )
        );
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentCenterAlignment() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentTriangle,
                mockContentWithDimens
        ));

        when(mockColumnData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.CENTER, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.CENTER, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.CENTER, 0f
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.CENTER, 0f
                )
        ));

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockContentTextLine, once()).setAlignment(HorizontalAlignment.CENTER);

        verify(mockColumnData, once()).put(CONTENT_SPECIFIC_ORIGINS, mapOf(
                CONTENT_COMPONENT_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                ((COLUMN_WIDTH - CONTENT_COMPONENT_UNADJ_DIMENS.width()) / 2f),
                        COLUMN_RENDERING_LOC.Y
                ),
                CONTENT_TEXT_LINE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + (COLUMN_WIDTH / 2f),
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TEXT_LINE
                ),
                CONTENT_WITH_DIMENS_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                ((COLUMN_WIDTH - CONTENT_WITH_DIMENS_UNADJ_DIMENS.width()) / 2f),
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_WITH_DIMENS
                )
        ));

        verify(mockContentPolygonOffsets, once()).put(
                CONTENT_TRIANGLE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                ((COLUMN_WIDTH - CONTENT_TRIANGLE_POLYGON_DIMENS.width()) / 2),
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TRIANGLE
                )
        );
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentLeftAlignmentWithIndent() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentTriangle,
                mockContentWithDimens
        ));

        when(mockColumnData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.LEFT, CONTENT_COMPONENT_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, CONTENT_TEXT_LINE_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.LEFT, CONTENT_WITH_DIMENS_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.LEFT, CONTENT_TRIANGLE_INDENT
                )
        ));

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockContentTextLine, once()).setAlignment(HorizontalAlignment.LEFT);

        verify(mockColumnData, once()).put(CONTENT_SPECIFIC_ORIGINS, mapOf(
                CONTENT_COMPONENT_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + CONTENT_COMPONENT_INDENT,
                        COLUMN_RENDERING_LOC.Y
                ),
                CONTENT_TEXT_LINE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + CONTENT_TEXT_LINE_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TEXT_LINE
                ),
                CONTENT_WITH_DIMENS_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + CONTENT_WITH_DIMENS_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_WITH_DIMENS
                )
        ));

        verify(mockContentPolygonOffsets, once()).put(
                CONTENT_TRIANGLE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + CONTENT_TRIANGLE_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TRIANGLE
                )
        );
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentRightAlignmentWithIndent() {
        when(mockColumn.contentsRepresentation()).thenReturn(setOf(
                mockContentComponent,
                mockContentTextLine,
                mockContentTriangle,
                mockContentWithDimens
        ));

        when(mockColumnData.get(CONTENT_POLYGON_OFFSETS)).thenReturn(mockContentPolygonOffsets);

        when(mockColumnData.get(CONTENTS)).thenReturn(listOf(
                new ContentColumnMethods.Content(
                        CONTENT_COMPONENT_UUID, CONTENT_COMPONENT_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, CONTENT_COMPONENT_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TEXT_LINE_UUID, CONTENT_TEXT_LINE_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, CONTENT_TEXT_LINE_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_WITH_DIMENS_UUID, CONTENT_WITH_DIMENS_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, CONTENT_WITH_DIMENS_INDENT
                ),
                new ContentColumnMethods.Content(
                        CONTENT_TRIANGLE_UUID, CONTENT_TRIANGLE_SPACING_AFTER,
                        HorizontalAlignment.RIGHT, CONTENT_TRIANGLE_INDENT
                )
        ));

        var output = methods.ContentColumn_setDimensForComponentAndContent(mockColumn, TIMESTAMP);

        assertEquals(EXPECTED_OUTPUT, output);

        verify(mockContentTextLine, once()).setAlignment(HorizontalAlignment.RIGHT);

        verify(mockColumnData, once()).put(CONTENT_SPECIFIC_ORIGINS, mapOf(
                CONTENT_COMPONENT_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                (COLUMN_WIDTH - CONTENT_COMPONENT_UNADJ_DIMENS.width()) -
                                CONTENT_COMPONENT_INDENT,
                        COLUMN_RENDERING_LOC.Y
                ),
                CONTENT_TEXT_LINE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X + COLUMN_WIDTH - CONTENT_TEXT_LINE_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TEXT_LINE
                ),
                CONTENT_WITH_DIMENS_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                (COLUMN_WIDTH - CONTENT_WITH_DIMENS_UNADJ_DIMENS.width()) -
                                CONTENT_WITH_DIMENS_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_WITH_DIMENS
                )
        ));

        verify(mockContentPolygonOffsets, once()).put(
                CONTENT_TRIANGLE_UUID,
                vertexOf(
                        COLUMN_RENDERING_LOC.X +
                                (COLUMN_WIDTH - CONTENT_TRIANGLE_POLYGON_DIMENS.width()) -
                                CONTENT_TRIANGLE_INDENT,
                        COLUMN_RENDERING_LOC.Y + EXPECTED_HEIGHT_TO_TRIANGLE
                )
        );
    }

    @Test
    public void testContentColumn_addSpace() {
        var spacingUuid = randomUUID();
        List<ContentColumnMethods.Content> mockContents = generateMockList();
        when(mockColumnData.get(CONTENTS)).thenReturn(mockContents);
        var space = randomFloat();

        methods.ContentColumn_add(mockColumn, addend(null, mapOf(
                SPACING_UUID,
                spacingUuid,
                SPACING_AFTER,
                space
        )));

        verify(mockContents, once()).add(eq(new ContentColumnMethods.Content(
                spacingUuid,
                space,
                null,
                0f
        )));
    }
}
