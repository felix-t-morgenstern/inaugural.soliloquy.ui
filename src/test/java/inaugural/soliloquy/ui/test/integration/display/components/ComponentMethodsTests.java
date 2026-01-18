package inaugural.soliloquy.ui.test.integration.display.components;

import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import inaugural.soliloquy.ui.test.unit.components.AbstractComponentMethodsTest;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.RenderableWithMutableDimensions;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.TriangleRenderable;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.randomFloatBox;
import static inaugural.soliloquy.tools.random.Random.randomVertex;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.translate;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.tools.valueobjects.Vertex.polygonDimens;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class ComponentMethodsTests extends AbstractComponentMethodsTest {
    private final UUID TEXT_LINE_RENDERABLE_UUID = randomUUID();
    private final Vertex TEXT_LINE_ORIG_LOC = randomVertex();
    private final UUID TRIANGLE_RENDERABLE_UUID = randomUUID();
    private final Vertex TRIANGLE_ORIG_VERTEX_1 = randomVertex();
    private final Vertex TRIANGLE_ORIG_VERTEX_2 = randomVertex();
    private final Vertex TRIANGLE_ORIG_VERTEX_3 = randomVertex();
    private final UUID RENDERABLE_WITH_DIMENS_UUID = randomUUID();
    private final FloatBox RENDERABLE_WITH_DIMENS_ORIG_DIMENS = randomFloatBox();
    private final UUID INNER_CONTENT_UUID = randomUUID();
    private final FloatBox INNER_COMPONENT_ORIG_DIMENS = randomFloatBox();
    private final FloatBox EXPECTED_NET_ORIG_DIMENS = encompassing(
            polygonDimens(TRIANGLE_ORIG_VERTEX_1, TRIANGLE_ORIG_VERTEX_2, TRIANGLE_ORIG_VERTEX_3),
            encompassing(RENDERABLE_WITH_DIMENS_ORIG_DIMENS, INNER_COMPONENT_ORIG_DIMENS));

    private final Vertex ORIGIN_OVERRIDE = randomVertex();
    private final Vertex EXPECTED_ORIGIN_ADJUST =
            difference(EXPECTED_NET_ORIG_DIMENS.topLeft(), ORIGIN_OVERRIDE);

    @Mock private Map<UUID, ProviderAtTime<FloatBox>> mockOrigContentDimensProviders;
    @Mock private Map<UUID, FloatBox> mockContentDimens;
    @Mock private Map<UUID, ProviderAtTime<Vertex>> mockOrigContentLocProviders;
    @Mock private Map<UUID, Vertex> mockContentLocs;
    @Mock private Map<UUID, List<ProviderAtTime<Vertex>>> mockOrigContentVerticesProviders;
    @Mock private Map<UUID, List<Vertex>> mockContentVertices;

    @Mock private ProviderAtTime<Vertex> mockTextLineOriginalLocProvider;
    @Mock private FunctionalProvider<Vertex> mockTextLineNewLocProvider;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private ProviderAtTime<Vertex> mockTriangleOriginalVertex1Provider;
    @Mock private ProviderAtTime<Vertex> mockTriangleOriginalVertex2Provider;
    @Mock private ProviderAtTime<Vertex> mockTriangleOriginalVertex3Provider;
    @Mock private FunctionalProvider<Vertex> mockTriangleNewVertex1Provider;
    @Mock private FunctionalProvider<Vertex> mockTriangleNewVertex2Provider;
    @Mock private FunctionalProvider<Vertex> mockTriangleNewVertex3Provider;
    @Mock private TriangleRenderable mockTriangleRenderable;
    @Mock private ProviderAtTime<FloatBox> mockRenderableWithDimensOriginalDimensProvider;
    @Mock private FunctionalProvider<FloatBox> mockRenderableWithDimensNewDimensProvider;
    @Mock private RenderableWithMutableDimensions mockRenderableWithDimens;
    @Mock private ProviderAtTime<FloatBox> mockContentComponentOriginalDimensProvider;
    @Mock private FunctionalProvider<FloatBox> mockContentComponentNewDimensProvider;
    @Mock private Component mockContentComponent;
    @Mock private FunctionalProviderDefinitionReader mockFunctionalProviderDefReader;

    @Mock private ProviderAtTime<Vertex> mockOriginOverrideProvider;

    private ComponentMethods componentMethods;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockTextLineOriginalLocProvider.provide(anyLong())).thenReturn(
                TEXT_LINE_ORIG_LOC);
        lenient().when(mockTextLineRenderable.uuid()).thenReturn(TEXT_LINE_RENDERABLE_UUID);
        lenient().when(mockTextLineRenderable.containingComponent()).thenReturn(MOCK_COMPONENT);
        lenient().when(mockTextLineRenderable.getRenderingLocationProvider()).thenReturn(
                mockTextLineOriginalLocProvider);

        lenient().when(mockTriangleOriginalVertex1Provider.provide(anyLong()))
                .thenReturn(TRIANGLE_ORIG_VERTEX_1);
        lenient().when(mockTriangleOriginalVertex2Provider.provide(anyLong()))
                .thenReturn(TRIANGLE_ORIG_VERTEX_2);
        lenient().when(mockTriangleOriginalVertex3Provider.provide(anyLong()))
                .thenReturn(TRIANGLE_ORIG_VERTEX_3);
        lenient().when(mockTriangleRenderable.uuid()).thenReturn(TRIANGLE_RENDERABLE_UUID);
        lenient().when(mockTriangleRenderable.containingComponent()).thenReturn(MOCK_COMPONENT);
        lenient().when(mockTriangleRenderable.getVertex1Provider())
                .thenReturn(mockTriangleOriginalVertex1Provider);
        lenient().when(mockTriangleRenderable.getVertex2Provider())
                .thenReturn(mockTriangleOriginalVertex2Provider);
        lenient().when(mockTriangleRenderable.getVertex3Provider())
                .thenReturn(mockTriangleOriginalVertex3Provider);

        lenient().when(mockRenderableWithDimensOriginalDimensProvider.provide(anyLong()))
                .thenReturn(RENDERABLE_WITH_DIMENS_ORIG_DIMENS);
        lenient().when(mockRenderableWithDimens.uuid())
                .thenReturn(RENDERABLE_WITH_DIMENS_UUID);
        lenient().when(mockRenderableWithDimens.containingComponent()).thenReturn(MOCK_COMPONENT);
        lenient().when(mockRenderableWithDimens.getRenderingDimensionsProvider())
                .thenReturn(mockRenderableWithDimensOriginalDimensProvider);

        lenient().when(mockContentComponentOriginalDimensProvider.provide(anyLong()))
                .thenReturn(INNER_COMPONENT_ORIG_DIMENS);
        lenient().when(mockContentComponent.getDimensionsProvider())
                .thenReturn(mockContentComponentOriginalDimensProvider);
        lenient().when(mockContentComponent.uuid()).thenReturn(INNER_CONTENT_UUID);
        lenient().when(mockContentComponent.containingComponent()).thenReturn(MOCK_COMPONENT);

        lenient().when(mockOriginOverrideProvider.provide(anyLong())).thenReturn(ORIGIN_OVERRIDE);

        lenient().when(MOCK_COMPONENT.contentsRepresentation())
                .thenReturn(setOf(
                        mockTextLineRenderable,
                        mockTriangleRenderable,
                        mockRenderableWithDimens,
                        mockContentComponent));

        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentRenderingLocWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                TEXT_LINE_RENDERABLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID
                        ))))).thenReturn(mockTextLineNewLocProvider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentVertexWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                TRIANGLE_RENDERABLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID,
                                VERTICES_INDEX,
                                0
                        ))))).thenReturn(mockTriangleNewVertex1Provider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentVertexWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                TRIANGLE_RENDERABLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID,
                                VERTICES_INDEX,
                                1
                        ))))).thenReturn(mockTriangleNewVertex2Provider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentVertexWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                TRIANGLE_RENDERABLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID,
                                VERTICES_INDEX,
                                2
                        ))))).thenReturn(mockTriangleNewVertex3Provider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                        Component_innerContentDimensWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                RENDERABLE_WITH_DIMENS_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID
                        ))))).thenReturn(mockRenderableWithDimensNewDimensProvider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                        Component_innerContentDimensWithWholeComponentOverrideCalculation,
                        mapOf(
                                CONTENT_UUID,
                                INNER_CONTENT_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID
                        ))))).thenReturn(mockContentComponentNewDimensProvider);

        componentMethods =
                new ComponentMethods(MOCK_GET_COMPONENT, mockFunctionalProviderDefReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentMethods(null, mockFunctionalProviderDefReader));
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentMethods(MOCK_GET_COMPONENT, null));
    }

    @Test
    public void testComponent_setDimensForComponentAndContent() {
        setOf(
                pairOf(UNADJUSTED_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(UNADJUSTED_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(UNADJUSTED_CONTENT_VERTICES_PROVIDERS, mockOrigContentVerticesProviders),
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ORIGIN_OVERRIDE_PROVIDER, mockOriginOverrideProvider)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_setDimensForComponentAndContent(MOCK_COMPONENT,
                TIMESTAMP);

        assertEquals(translate(EXPECTED_NET_ORIG_DIMENS, EXPECTED_ORIGIN_ADJUST), output);

        verifyCalledSetDimensForComponentAndContent();
    }

    @Test
    public void testComponent_setAndRetrieveDimensForComponentAndContentForProvider() {
        setOf(
                pairOf(UNADJUSTED_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(UNADJUSTED_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(UNADJUSTED_CONTENT_VERTICES_PROVIDERS, mockOrigContentVerticesProviders),
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ORIGIN_OVERRIDE_PROVIDER, mockOriginOverrideProvider)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_setAndRetrieveDimensForComponentAndContentForProvider(
                        providerInputs(TIMESTAMP,
                                mapOf(ComponentMethods.COMPONENT_UUID, super.COMPONENT_UUID)));

        assertEquals(translate(EXPECTED_NET_ORIG_DIMENS, EXPECTED_ORIGIN_ADJUST), output);

        verifyCalledSetDimensForComponentAndContent();
    }

    private void verifyCalledSetDimensForComponentAndContent() {
        verify(MOCK_COMPONENT, atLeastOnce()).data();
        verify(MOCK_COMPONENT, atLeastOnce()).uuid();
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, never()).get(COMPONENT_DIMENS);
        verify(mockComponentData, once()).get(UNADJUSTED_CONTENT_DIMENS_PROVIDERS);
        verify(mockComponentData, once()).get(CONTENT_UNADJUSTED_DIMENS);
        verify(mockComponentData, once()).get(UNADJUSTED_CONTENT_LOC_PROVIDERS);
        verify(mockComponentData, once()).get(CONTENT_UNADJUSTED_LOCS);
        verify(mockContentDimens, once()).clear();
        verify(MOCK_COMPONENT, once()).contentsRepresentation();

        verify(mockTextLineRenderable, atLeastOnce()).uuid();
        verify(mockOrigContentLocProviders, once()).get(TEXT_LINE_RENDERABLE_UUID);
        verify(mockTextLineRenderable, once()).getRenderingLocationProvider();
        var functionalDefCaptor = ArgumentCaptor.forClass(FunctionalProviderDefinition.class);
        //noinspection unchecked
        verify(mockFunctionalProviderDefReader, times(6)).read(functionalDefCaptor.capture());
        verify(mockTextLineRenderable, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                                Component_innerContentRenderingLocWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        TEXT_LINE_RENDERABLE_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID
                                ))));
        verify(mockTextLineRenderable, once()).setRenderingLocationProvider(
                mockTextLineNewLocProvider);
        verify(mockOrigContentLocProviders, once()).put(TEXT_LINE_RENDERABLE_UUID,
                mockTextLineOriginalLocProvider);
        verify(mockTextLineOriginalLocProvider, once()).provide(TIMESTAMP);
        verify(mockContentLocs, once()).put(TEXT_LINE_RENDERABLE_UUID, TEXT_LINE_ORIG_LOC);

        verify(mockTriangleRenderable, atLeastOnce()).uuid();
        verify(mockOrigContentVerticesProviders, once()).get(TRIANGLE_RENDERABLE_UUID);
        verify(mockTriangleRenderable, once()).getVertex1Provider();
        verify(mockTriangleRenderable, once()).getVertex2Provider();
        verify(mockTriangleRenderable, once()).getVertex3Provider();
        verify(mockTriangleRenderable, atLeastOnce()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                                Component_innerContentVertexWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        TRIANGLE_RENDERABLE_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID,
                                        VERTICES_INDEX,
                                        0
                                ))));
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                                Component_innerContentVertexWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        TRIANGLE_RENDERABLE_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID,
                                        VERTICES_INDEX,
                                        1
                                ))));
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                                Component_innerContentVertexWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        TRIANGLE_RENDERABLE_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID,
                                        VERTICES_INDEX,
                                        2
                                ))));
        verify(mockTriangleRenderable, once()).setVertex1Provider(mockTriangleNewVertex1Provider);
        verify(mockTriangleRenderable, once()).setVertex2Provider(mockTriangleNewVertex2Provider);
        verify(mockTriangleRenderable, once()).setVertex3Provider(mockTriangleNewVertex3Provider);
        verify(mockOrigContentVerticesProviders, once()).put(TRIANGLE_RENDERABLE_UUID,
                listOf(mockTriangleOriginalVertex1Provider,
                        mockTriangleOriginalVertex2Provider,
                        mockTriangleOriginalVertex3Provider));
        verify(mockTriangleOriginalVertex1Provider, once()).provide(TIMESTAMP);
        verify(mockTriangleOriginalVertex2Provider, once()).provide(TIMESTAMP);
        verify(mockTriangleOriginalVertex3Provider, once()).provide(TIMESTAMP);
        verify(mockContentVertices, once()).put(TRIANGLE_RENDERABLE_UUID, listOf(
                TRIANGLE_ORIG_VERTEX_1,
                TRIANGLE_ORIG_VERTEX_2,
                TRIANGLE_ORIG_VERTEX_3
        ));

        verify(mockRenderableWithDimens, atLeastOnce()).uuid();
        verify(mockOrigContentDimensProviders, once()).get(RENDERABLE_WITH_DIMENS_UUID);
        verify(mockRenderableWithDimens, once()).getRenderingDimensionsProvider();
        verify(mockRenderableWithDimens, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                                Component_innerContentDimensWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        RENDERABLE_WITH_DIMENS_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID
                                ))));
        verify(mockRenderableWithDimens, once()).setRenderingDimensionsProvider(
                mockRenderableWithDimensNewDimensProvider);
        verify(mockOrigContentDimensProviders, once()).put(RENDERABLE_WITH_DIMENS_UUID,
                mockRenderableWithDimensOriginalDimensProvider);
        verify(mockRenderableWithDimensOriginalDimensProvider, once()).provide(TIMESTAMP);
        verify(mockContentDimens, once()).put(RENDERABLE_WITH_DIMENS_UUID,
                RENDERABLE_WITH_DIMENS_ORIG_DIMENS);

        verify(mockContentComponent, atLeastOnce()).uuid();
        verify(mockOrigContentDimensProviders, once()).get(INNER_CONTENT_UUID);
        verify(mockContentComponent, once()).getDimensionsProvider();
        verify(mockContentComponent, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                                Component_innerContentDimensWithWholeComponentOverrideCalculation,
                                mapOf(
                                        CONTENT_UUID,
                                        INNER_CONTENT_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID
                                ))));
        verify(mockContentComponent, once()).setDimensionsProvider(
                mockContentComponentNewDimensProvider);
        verify(mockOrigContentDimensProviders, once()).put(INNER_CONTENT_UUID,
                mockContentComponentOriginalDimensProvider);
        verify(mockContentComponentOriginalDimensProvider, once()).provide(TIMESTAMP);
        verify(mockContentDimens, once()).put(INNER_CONTENT_UUID, INNER_COMPONENT_ORIG_DIMENS);

        verify(mockComponentData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(ComponentMethods.ORIGIN_OVERRIDE, ORIGIN_OVERRIDE);
        verify(mockComponentData, once()).put(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft());
        verify(mockComponentData, once()).put(COMPONENT_DIMENS, floatBoxOf(
                ORIGIN_OVERRIDE.X,
                ORIGIN_OVERRIDE.Y,
                ORIGIN_OVERRIDE.X + EXPECTED_NET_ORIG_DIMENS.width(),
                ORIGIN_OVERRIDE.Y + EXPECTED_NET_ORIG_DIMENS.height()
        ));
        verify(mockComponentData, once()).put(LAST_TIMESTAMP, TIMESTAMP);
    }

    @Test
    public void testComponent_setDimensForComponentAndContent_SameTimestamp() {
        when(mockComponentData.get(LAST_TIMESTAMP)).thenReturn(TIMESTAMP);
        when(mockComponentData.get(COMPONENT_DIMENS)).thenReturn(EXPECTED_NET_ORIG_DIMENS);

        var output = componentMethods.Component_setDimensForComponentAndContent(MOCK_COMPONENT,
                TIMESTAMP);

        assertEquals(EXPECTED_NET_ORIG_DIMENS, output);
        verify(mockComponentData, once()).get(LAST_TIMESTAMP);
        verify(mockComponentData, once()).get(COMPONENT_DIMENS);
        verify(mockComponentData, never()).get(UNADJUSTED_CONTENT_DIMENS_PROVIDERS);
    }

    @Test
    public void testComponent_setDimensForComponentAndContent_AlreadyTornOutAndReplaced() {
        setOf(
                pairOf(UNADJUSTED_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(UNADJUSTED_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(UNADJUSTED_CONTENT_VERTICES_PROVIDERS, mockOrigContentVerticesProviders),
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ORIGIN_OVERRIDE_PROVIDER, mockOriginOverrideProvider)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));
        //noinspection SuspiciousMethodCalls
        when(mockOrigContentDimensProviders.get(any())).thenReturn(
                mockRenderableWithDimensOriginalDimensProvider);
        //noinspection SuspiciousMethodCalls
        when(mockOrigContentLocProviders.get(any())).thenReturn(mockTextLineOriginalLocProvider);

        componentMethods.Component_setDimensForComponentAndContent(MOCK_COMPONENT,
                TIMESTAMP);

        verify(mockRenderableWithDimens, never()).getRenderingDimensionsProvider();
        verify(mockTextLineRenderable, never()).getRenderingLocationProvider();
    }

    @Test
    public void testComponent_setDimensForComponentAndContent_NoOriginOverride() {
        setOf(
                pairOf(UNADJUSTED_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(UNADJUSTED_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(UNADJUSTED_CONTENT_VERTICES_PROVIDERS, mockOrigContentVerticesProviders),
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ORIGIN_OVERRIDE_PROVIDER, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_setDimensForComponentAndContent(MOCK_COMPONENT,
                TIMESTAMP);

        assertEquals(EXPECTED_NET_ORIG_DIMENS, output);
    }

    @Test
    public void testComponent_innerContentDimensWithWholeComponentOverrideCalculation() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        setOf(
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(ComponentMethods.ORIGIN_OVERRIDE, ORIGIN_OVERRIDE),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentDimensWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(translate(contentDimens, EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentDimensWithWholeComponentOverrideCalculationWithNoOriginOverride() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        //noinspection SuspiciousMethodCalls
        setOf(
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(ORIGIN_OVERRIDE, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentDimensWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(contentDimens, output);
    }

    @Test
    public void testComponent_innerContentDimensWithContentSpecificOverride() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        setOf(
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf(INNER_CONTENT_UUID, ORIGIN_OVERRIDE)),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentDimensWithContentSpecificOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(translate(contentDimens, EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentDimensWithContentSpecificOverrideWithNoOriginOverride() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        setOf(
                pairOf(CONTENT_UNADJUSTED_DIMENS, mockContentDimens),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentDimensWithContentSpecificOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(contentDimens, output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithWholeComponentOverrideCalculation() {
        var contentLocs = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLocs);
        setOf(
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(ComponentMethods.ORIGIN_OVERRIDE, ORIGIN_OVERRIDE),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentRenderingLocWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(inaugural.soliloquy.tools.valueobjects.Vertex.translate(contentLocs,
                EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithWholeComponentOverrideCalculationWithNoOriginOverride() {
        var contentLocs = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLocs);
        setOf(
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(ComponentMethods.ORIGIN_OVERRIDE, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentRenderingLocWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(contentLocs, output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithContentSpecificOverride() {
        var contentLoc = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLoc);
        setOf(
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf(INNER_CONTENT_UUID, ORIGIN_OVERRIDE)),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentRenderingLocWithContentSpecificOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(inaugural.soliloquy.tools.valueobjects.Vertex.translate(contentLoc,
                EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithContentSpecificOverrideWithNoOriginOverride() {
        var contentLocs = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLocs);
        setOf(
                pairOf(CONTENT_UNADJUSTED_LOCS, mockContentLocs),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentRenderingLocWithContentSpecificOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID)));

        assertEquals(contentLocs, output);
    }

    @Test
    public void testComponent_innerContentVertexWithWholeComponentOverrideCalculation() {
        var vertex = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentVertices.get(any())).thenReturn(
                listOf(randomVertex(), vertex, randomVertex()));
        setOf(
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ComponentMethods.ORIGIN_OVERRIDE, ORIGIN_OVERRIDE),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentVertexWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID,
                                VERTICES_INDEX,
                                1)));

        assertEquals(inaugural.soliloquy.tools.valueobjects.Vertex.translate(vertex,
                EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentVertexWithWholeComponentOverrideCalculationWithNoOriginOverride() {
        var vertex = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentVertices.get(any())).thenReturn(
                listOf(randomVertex(), vertex, randomVertex()));
        setOf(
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(ComponentMethods.ORIGIN_OVERRIDE, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentVertexWithWholeComponentOverrideCalculation(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                ComponentMethods.COMPONENT_UUID,
                                INNER_CONTENT_UUID,
                                VERTICES_INDEX,
                                1)));

        assertEquals(vertex, output);
    }

    @Test
    public void testComponent_innerContentVertexWithSpecificContentOverride() {
        var vertex = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentVertices.get(any())).thenReturn(
                listOf(randomVertex(), vertex, randomVertex()));
        setOf(
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf(INNER_CONTENT_UUID, ORIGIN_OVERRIDE)),
                pairOf(UNADJUSTED_ORIGIN, EXPECTED_NET_ORIG_DIMENS.topLeft())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentVertexWithSpecificContentOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID,
                                VERTICES_INDEX,
                                1)));

        assertEquals(inaugural.soliloquy.tools.valueobjects.Vertex.translate(vertex,
                EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentVertexWithSpecificContentOverrideWithNoOriginOverride() {
        var vertex = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentVertices.get(any())).thenReturn(
                listOf(randomVertex(), vertex, randomVertex()));
        setOf(
                pairOf(CONTENT_UNADJUSTED_VERTICES, mockContentVertices),
                pairOf(CONTENTS_TOP_LEFT_LOCS, mapOf())
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output =
                componentMethods.Component_innerContentVertexWithSpecificContentOverride(
                        providerInputs(TIMESTAMP, mapOf(
                                CONTAINING_COMPONENT_UUID,
                                super.COMPONENT_UUID,
                                CONTENT_UUID,
                                INNER_CONTENT_UUID,
                                VERTICES_INDEX,
                                1)));

        assertEquals(vertex, output);
    }
}
