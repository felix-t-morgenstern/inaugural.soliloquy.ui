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
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RenderableWithMutableDimensions;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockMap;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnMethods.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

@ExtendWith(MockitoExtension.class)
public class ContentColumnMethodsTests {
    private final UUID COLUMN_UUID = randomUUID();
    private final Vertex COLUMN_RENDERING_LOC = randomVertex();
    private final float COLUMN_WIDTH = randomFloat();

    private final UUID CONTENT_COMPONENT_UUID = randomUUID();
    private final FloatBox CONTENT_COMPONENT_DIMENS = randomFloatBox();

    private final UUID CONTENT_TEXT_LINE_UUID = randomUUID();

    private final UUID CONTENT_TRIANGLE_UUID = randomUUID();

    private final UUID CONTENT_WITH_DIMENS_UUID = randomUUID();

    private final long TIMESTAMP = randomLong();

    @Mock private Component mockContentComponent;
    @Mock private ProviderAtTime<FloatBox> mockContentComponentDimens;
    @Mock private ProviderAtTime<Vertex> mockContentComponentOriginOverrideProvider;

    @Mock private TextLineRenderable mockContentTextLine;
    @Mock private ProviderAtTime<Vertex> mockContentTextLineOriginProvider;

    @Mock private TriangleRenderable mockContentTriangle;

    @Mock private RenderableWithMutableDimensions mockContentWithDimens;

    @Mock private Component mockColumn;

    @SuppressWarnings("rawtypes") @Mock
    private Function<FunctionalProviderDefinition, ProviderAtTime> mockFunctionalProviderDefReader;
    @Mock private Function<UUID, Component> mockGetComponent;

    @Mock private ProviderAtTime<Vertex> mockColumnRenderingLocProvider;

    private Map<String, Object> mockColumnData;

    private ContentColumnMethods methods;

    @BeforeEach
    public void setUp() {
        lenient().when(mockColumnRenderingLocProvider.provide(anyLong()))
                .thenReturn(COLUMN_RENDERING_LOC);

        mockColumnData = generateMockMap(
                pairOf(RENDERING_LOC, mockColumnRenderingLocProvider),
                pairOf(WIDTH, COLUMN_WIDTH),
                pairOf(CONTENTS_TOP_LEFT_LOCS, Collections.<UUID, Vertex>mapOf())
        );

        lenient().when(mockColumn.uuid()).thenReturn(COLUMN_UUID);
        lenient().when(mockColumn.data()).thenReturn(mockColumnData);

        lenient().when(mockGetComponent.apply(any())).thenReturn(mockColumn);

        lenient().when(mockContentComponent.uuid()).thenReturn(CONTENT_COMPONENT_UUID);
        lenient().when(mockContentComponentDimens.provide(anyLong()))
                .thenReturn(CONTENT_COMPONENT_DIMENS);
        lenient().when(mockContentComponent.getDimensionsProvider())
                .thenReturn(mockContentComponentDimens);
        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentRenderingLocWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_COMPONENT_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )))).thenReturn(mockContentComponentOriginOverrideProvider);

        lenient().when(mockContentTextLine.uuid()).thenReturn(CONTENT_TEXT_LINE_UUID);
        lenient().when(mockFunctionalProviderDefReader.apply(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentRenderingLocWithContentSpecificOverride,
                        mapOf(
                                CONTENT_UUID,
                                CONTENT_TEXT_LINE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COLUMN_UUID
                        )
                )))).thenReturn(mockContentTextLineOriginProvider);

        lenient().when(mockContentTriangle.uuid()).thenReturn(CONTENT_TRIANGLE_UUID);

        lenient().when(mockContentWithDimens.uuid()).thenReturn(CONTENT_WITH_DIMENS_UUID);

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
    public void testContentColumn_setDimensForComponentAndContentWithNewTimestampAndNoContentAndNoOverride() {
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
        verify(mockColumnData, once()).get(RENDERING_LOC);
        verify(mockColumnRenderingLocProvider, once()).provide(TIMESTAMP);
        verify(mockColumnData, once()).get(WIDTH);
        verify(mockColumnData, once()).get(CONTENTS);
        verify(mockColumnData, once()).get(CONTENTS_TOP_LEFT_LOCS);
        verify(mockColumnData, once()).put(COMPONENT_DIMENS, expectedOutput);
        verify(mockColumnData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
    }

    @Test
    public void testContentColumn_setDimensForComponentAndContentWithNewTimestampAndNewContentAndNoOverride() {
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
        verify(mockColumnData, once()).get(RENDERING_LOC);
        verify(mockColumnRenderingLocProvider, once()).provide(TIMESTAMP);
        verify(mockColumnData, once()).get(WIDTH);
        verify(mockColumnData, once()).get(CONTENTS);
        verify(mockColumnData, once()).get(CONTENTS_TOP_LEFT_LOCS);
        verify(mockColumnData, once()).put(COMPONENT_DIMENS, expectedOutput);
        verify(mockColumnData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
    }
}
