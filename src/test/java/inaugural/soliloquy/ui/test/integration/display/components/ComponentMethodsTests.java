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
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.Map;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.randomFloatBox;
import static inaugural.soliloquy.tools.random.Random.randomVertex;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.translate;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class ComponentMethodsTests extends AbstractComponentMethodsTest {
    private final UUID TEXT_LINE_RENDERABLE_UUID = randomUUID();
    private final Vertex TEXT_LINE_ORIG_LOC = randomVertex();
    private final UUID RENDERABLE_WITH_DIMENS_UUID = randomUUID();
    private final FloatBox RENDERABLE_WITH_DIMENS_ORIG_DIMENS = floatBoxOf(1f, 2f);
    //randomFloatBox();
    private final UUID INNER_COMPONENT_UUID = randomUUID();
    private final FloatBox INNER_COMPONENT_ORIG_DIMENS = floatBoxOf(5f, 8f);//randomFloatBox();
    private final FloatBox EXPECTED_NET_ORIG_DIMENS =
            encompassing(RENDERABLE_WITH_DIMENS_ORIG_DIMENS, INNER_COMPONENT_ORIG_DIMENS);

    private final Vertex ORIGIN_OVERRIDE = vertexOf(10f, 100f);//randomVertex();
    private final Vertex EXPECTED_ORIGIN_ADJUST =
            difference(EXPECTED_NET_ORIG_DIMENS.topLeft(), ORIGIN_OVERRIDE);

    @Mock private Map<UUID, ProviderAtTime<FloatBox>> mockOrigContentDimensProviders;
    @Mock private Map<UUID, FloatBox> mockContentDimens;
    @Mock private Map<UUID, ProviderAtTime<Vertex>> mockOrigContentLocProviders;
    @Mock private Map<UUID, Vertex> mockContentLocs;

    @Mock private ProviderAtTime<Vertex> mockTextLineOriginalLocProvider;
    @Mock private FunctionalProvider<Vertex> mockTextLineNewLocProvider;
    @Mock private TextLineRenderable mockTextLineRenderable;
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
        lenient().when(mockContentComponent.uuid()).thenReturn(INNER_COMPONENT_UUID);
        lenient().when(mockContentComponent.containingComponent()).thenReturn(MOCK_COMPONENT);

        lenient().when(mockOriginOverrideProvider.provide(anyLong())).thenReturn(ORIGIN_OVERRIDE);

        lenient().when(MOCK_COMPONENT.contentsRepresentation())
                .thenReturn(setOf(
                        mockTextLineRenderable,
                        mockRenderableWithDimens,
                        mockContentComponent));

        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                        Component_innerContentRenderingLocWithOverrideCalculation,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                TEXT_LINE_RENDERABLE_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID
                        ))))).thenReturn(mockTextLineNewLocProvider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                        Component_innerContentDimensWithOverrideCalculation,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                RENDERABLE_WITH_DIMENS_UUID,
                                CONTAINING_COMPONENT_UUID,
                                COMPONENT_UUID
                        ))))).thenReturn(mockRenderableWithDimensNewDimensProvider);
        lenient().when(mockFunctionalProviderDefReader.read(argThat(
                new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                        Component_innerContentDimensWithOverrideCalculation,
                        mapOf(
                                ComponentMethods.COMPONENT_UUID,
                                INNER_COMPONENT_UUID,
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
                pairOf(ORIG_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIG_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_LOCS, mockContentLocs),
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
                pairOf(ORIG_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIG_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_LOCS, mockContentLocs),
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
        verify(mockComponentData, once()).get(ORIG_CONTENT_DIMENS_PROVIDERS);
        verify(mockComponentData, once()).get(CONTENT_DIMENS);
        verify(mockComponentData, once()).get(ORIG_CONTENT_LOC_PROVIDERS);
        verify(mockComponentData, once()).get(CONTENT_LOCS);
        verify(mockContentDimens, once()).clear();
        verify(MOCK_COMPONENT, once()).contentsRepresentation();

        verify(mockTextLineRenderable, atLeastOnce()).uuid();
        verify(mockOrigContentLocProviders, once()).get(TEXT_LINE_RENDERABLE_UUID);
        verify(mockTextLineRenderable, once()).getRenderingLocationProvider();
        var functionalDefCaptor = ArgumentCaptor.forClass(FunctionalProviderDefinition.class);
        //noinspection unchecked
        verify(mockFunctionalProviderDefReader, times(3)).read(functionalDefCaptor.capture());
        verify(mockTextLineRenderable, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<Vertex>>(
                                Component_innerContentRenderingLocWithOverrideCalculation,
                                mapOf(
                                        ComponentMethods.COMPONENT_UUID,
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

        verify(mockRenderableWithDimens, atLeastOnce()).uuid();
        verify(mockOrigContentDimensProviders, once()).get(RENDERABLE_WITH_DIMENS_UUID);
        verify(mockRenderableWithDimens, once()).getRenderingDimensionsProvider();
        verify(mockRenderableWithDimens, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                                Component_innerContentDimensWithOverrideCalculation,
                                mapOf(
                                        ComponentMethods.COMPONENT_UUID,
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
        verify(mockOrigContentDimensProviders, once()).get(INNER_COMPONENT_UUID);
        verify(mockContentComponent, once()).getDimensionsProvider();
        verify(mockContentComponent, once()).containingComponent();
        verify(mockFunctionalProviderDefReader, once()).read(
                argThat(
                        new FunctionalProviderDefMatcher<FunctionalProviderDefinition<FloatBox>>(
                                Component_innerContentDimensWithOverrideCalculation,
                                mapOf(
                                        ComponentMethods.COMPONENT_UUID,
                                        INNER_COMPONENT_UUID,
                                        CONTAINING_COMPONENT_UUID,
                                        COMPONENT_UUID
                                ))));
        verify(mockContentComponent, once()).setDimensionsProvider(
                mockContentComponentNewDimensProvider);
        verify(mockOrigContentDimensProviders, once()).put(INNER_COMPONENT_UUID,
                mockContentComponentOriginalDimensProvider);
        verify(mockContentComponentOriginalDimensProvider, once()).provide(TIMESTAMP);
        verify(mockContentDimens, once()).put(INNER_COMPONENT_UUID, INNER_COMPONENT_ORIG_DIMENS);

        verify(mockComponentData, once()).get(ORIGIN_OVERRIDE_PROVIDER);
        verify(mockOriginOverrideProvider, once()).provide(TIMESTAMP);
        verify(mockComponentData, once()).put(ORIGIN_OVERRIDE_ADJUST, EXPECTED_ORIGIN_ADJUST);
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
        verify(mockComponentData, never()).get(ORIG_CONTENT_DIMENS_PROVIDERS);
    }

    @Test
    public void testComponent_setDimensForComponentAndContent_AlreadyTornOutAndReplaced() {
        setOf(
                pairOf(ORIG_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIG_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_LOCS, mockContentLocs),
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
                pairOf(ORIG_CONTENT_DIMENS_PROVIDERS, mockOrigContentDimensProviders),
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIG_CONTENT_LOC_PROVIDERS, mockOrigContentLocProviders),
                pairOf(CONTENT_LOCS, mockContentLocs),
                pairOf(ORIGIN_OVERRIDE_PROVIDER, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_setDimensForComponentAndContent(MOCK_COMPONENT,
                TIMESTAMP);

        assertEquals(EXPECTED_NET_ORIG_DIMENS, output);
    }

    @Test
    public void testComponent_innerContentDimensWithOverrideCalculation() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        setOf(
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIGIN_OVERRIDE_ADJUST, EXPECTED_ORIGIN_ADJUST)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_innerContentDimensWithOverrideCalculation(
                providerInputs(TIMESTAMP, mapOf(
                        CONTAINING_COMPONENT_UUID,
                        super.COMPONENT_UUID,
                        ComponentMethods.COMPONENT_UUID,
                        INNER_COMPONENT_UUID)));

        assertEquals(translate(contentDimens, EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentDimensWithOverrideCalculationWithNoOriginOverride() {
        var contentDimens = randomFloatBox();
        //noinspection SuspiciousMethodCalls
        when(mockContentDimens.get(any())).thenReturn(contentDimens);
        setOf(
                pairOf(CONTENT_DIMENS, mockContentDimens),
                pairOf(ORIGIN_OVERRIDE_ADJUST, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_innerContentDimensWithOverrideCalculation(
                providerInputs(TIMESTAMP, mapOf(
                        CONTAINING_COMPONENT_UUID,
                        super.COMPONENT_UUID,
                        ComponentMethods.COMPONENT_UUID,
                        INNER_COMPONENT_UUID)));

        assertEquals(contentDimens, output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithOverrideCalculation() {
        var contentLocs = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLocs);
        setOf(
                pairOf(CONTENT_LOCS, mockContentLocs),
                pairOf(ORIGIN_OVERRIDE_ADJUST, EXPECTED_ORIGIN_ADJUST)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_innerContentRenderingLocWithOverrideCalculation(
                providerInputs(TIMESTAMP, mapOf(
                        CONTAINING_COMPONENT_UUID,
                        super.COMPONENT_UUID,
                        ComponentMethods.COMPONENT_UUID,
                        INNER_COMPONENT_UUID)));

        assertEquals(inaugural.soliloquy.tools.valueobjects.Vertex.translate(contentLocs,
                EXPECTED_ORIGIN_ADJUST), output);
    }

    @Test
    public void testComponent_innerContentRenderingLocWithOverrideCalculationWithNoOriginOverride() {
        var contentLocs = randomVertex();
        //noinspection SuspiciousMethodCalls
        when(mockContentLocs.get(any())).thenReturn(contentLocs);
        setOf(
                pairOf(CONTENT_LOCS, mockContentLocs),
                pairOf(ORIGIN_OVERRIDE_ADJUST, null)
        ).forEach(p -> lenient().when(mockComponentData.get(p.FIRST)).thenReturn(p.SECOND));

        var output = componentMethods.Component_innerContentRenderingLocWithOverrideCalculation(
                providerInputs(TIMESTAMP, mapOf(
                        CONTAINING_COMPONENT_UUID,
                        super.COMPONENT_UUID,
                        ComponentMethods.COMPONENT_UUID,
                        INNER_COMPONENT_UUID)));

        assertEquals(contentLocs, output);
    }
}
