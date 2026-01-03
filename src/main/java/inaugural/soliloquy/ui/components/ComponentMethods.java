package inaugural.soliloquy.ui.components;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.translate;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static inaugural.soliloquy.tools.valueobjects.Vertex.polygonDimens;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ComponentMethods {
    public final static String COMPONENT_UUID = "COMPONENT_UUID";
    public final static String CONTENT_UUID = "CONTENT_UUID";
    public final static String CONTAINING_COMPONENT_UUID = "CONTAINING_COMPONENT_UUID";
    public final static String LAST_TIMESTAMP = "LAST_TIMESTAMP";
    public final static String COMPONENT_DIMENS = "COMPONENT_DIMENS";
    public final static String ORIGIN_OVERRIDE_PROVIDER = "ORIGIN_OVERRIDE_PROVIDER";
    public final static String ORIGIN_OVERRIDE = "ORIGIN_OVERRIDE";
    public final static String ORIGINAL_ORIGIN = "ORIGINAL_ORIGIN";
    public final static String CONTENT_UNADJUSTED_DIMENS = "CONTENT_UNADJUSTED_DIMENS";
    public final static String CONTENT_UNADJUSTED_LOCS = "CONTENT_UNADJUSTED_LOCS";
    public final static String CONTENT_UNADJUSTED_VERTICES = "CONTENT_UNADJUSTED_VERTICES";
    public final static String UNADJUSTED_CONTENT_DIMENS_PROVIDERS =
            "UNADJUSTED_CONTENT_DIMENS_PROVIDERS";
    public final static String UNADJUSTED_CONTENT_LOC_PROVIDERS =
            "UNADJUSTED_CONTENT_LOC_PROVIDERS";
    public final static String UNADJUSTED_CONTENT_VERTICES_PROVIDERS =
            "UNADJUSTED_CONTENT_VERTICES_PROVIDERS";
    public final static String VERTICES_INDEX = "VERTICES_INDEX";
    public final static String CONTENTS_TOP_LEFT_LOCS = "CONTENTS_TOP_LEFT_LOCS";

    protected final Function<UUID, Component> GET_COMPONENT;
    protected final FunctionalProviderDefinitionReader FUNCTIONAL_PROVIDER_DEF_READER;

    public ComponentMethods(Function<UUID, Component> getComponent,
                            FunctionalProviderDefinitionReader functionalProviderDefReader) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        FUNCTIONAL_PROVIDER_DEF_READER =
                Check.ifNull(functionalProviderDefReader, "functionalProviderDefReader");
    }

    public final static String Component_setDimensForComponentAndContent =
            "Component_setDimensForComponentAndContent";

    public FloatBox Component_setDimensForComponentAndContent(Component component,
                                                              long timestamp) {
        Long lastTimestamp = getFromData(component, LAST_TIMESTAMP);
        // This makes calls to this method cheap, if dimens have already been calculated for the
        // provided timestamp
        if (lastTimestamp != null && timestamp == lastTimestamp) {
            return getFromData(component, COMPONENT_DIMENS);
        }

        Map<UUID, ProviderAtTime<FloatBox>> unadjustedContentDimensProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_DIMENS_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, FloatBox> contentUnadjustedDimens =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_DIMENS,
                        Collections::mapOf);
        contentUnadjustedDimens.clear();

        Map<UUID, ProviderAtTime<Vertex>> unadjustedContentLocProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_LOC_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, Vertex> contentUnadjustedLocs =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_LOCS,
                        Collections::mapOf);
        contentUnadjustedLocs.clear();

        Map<UUID, List<ProviderAtTime<Vertex>>> unadjustedContentVerticesProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_VERTICES_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_VERTICES,
                        Collections::mapOf);
        contentUnadjustedVertices.clear();

        // 1. Get all _original_ content dimens providers; if not stored in data already, rip and
        // replace, while populating data with those original content dimens providers

        FloatBox componentNetDimens = null;

        for (var content : component.contentsRepresentation()) {
            // LOTS of duplicate code here
            switch (content) {
                case TextLineRenderable textLineRenderable -> {
                    var origContentLocProvider =
                            unadjustedContentLocProviders.get(textLineRenderable.uuid());
                    if (origContentLocProvider == null) {
                        origContentLocProvider =
                                Component_tearOutAndReplaceWithOriginOverrideForLoc(
                                        textLineRenderable);
                        unadjustedContentLocProviders.put(textLineRenderable.uuid(),
                                origContentLocProvider);
                    }

                    var providedOrigContentLoc = origContentLocProvider.provide(timestamp);
                    contentUnadjustedLocs.put(textLineRenderable.uuid(), providedOrigContentLoc);

                    // (Text line length isn't being added into net dimens, since line length is
                    // ideally dimensionless. Containing components should track text width instead,
                    // e.g., Button, TextBlock)
                }

                case TriangleRenderable triangleRenderable -> {
                    var origContentVerticesProvidersForRenderable =
                            unadjustedContentVerticesProviders.get(triangleRenderable.uuid());
                    if (origContentVerticesProvidersForRenderable == null) {
                        origContentVerticesProvidersForRenderable =
                                Component_tearOutAndReplaceWithOriginOverrideForTriangle(
                                        triangleRenderable);
                        unadjustedContentVerticesProviders.put(triangleRenderable.uuid(),
                                origContentVerticesProvidersForRenderable);
                    }

                    var providedOrigContentVertices =
                            origContentVerticesProvidersForRenderable.stream()
                                    .map(p -> p.provide(timestamp)).toList();
                    contentUnadjustedVertices.put(triangleRenderable.uuid(),
                            providedOrigContentVertices);
                    var triangleEncompassingDimens =
                            polygonDimens(providedOrigContentVertices.toArray(Vertex[]::new));
                    componentNetDimens = componentNetDimens == null ? triangleEncompassingDimens :
                            encompassing(componentNetDimens, triangleEncompassingDimens);
                }

                case RenderableWithMutableDimensions _ -> {
                    var origContentDimensProvider =
                            unadjustedContentDimensProviders.get(content.uuid());
                    if (origContentDimensProvider == null) {
                        origContentDimensProvider =
                                Component_tearOutAndReplaceWithOriginOverrideForDimens(content);
                        unadjustedContentDimensProviders.put(content.uuid(),
                                origContentDimensProvider);
                    }

                    // 2. Expand dimens to encompass contents

                    var providedOrigContentDimens = origContentDimensProvider.provide(timestamp);
                    // If these original content dimensions need translation, we will figure that
                    // out later, after we've calculated the component net dimensions
                    contentUnadjustedDimens.put(content.uuid(), providedOrigContentDimens);
                    componentNetDimens = componentNetDimens == null ? providedOrigContentDimens :
                            encompassing(componentNetDimens, providedOrigContentDimens);
                }
                case Component c -> {
                    var origContentDimensProvider =
                            unadjustedContentDimensProviders.get(content.uuid());
                    if (origContentDimensProvider == null) {
                        origContentDimensProvider =
                                Component_tearOutAndReplaceWithOriginOverrideForDimens(content);
                        unadjustedContentDimensProviders.put(content.uuid(),
                                origContentDimensProvider);
                    }

                    // 2. Expand dimens to encompass contents

                    var providedOrigContentDimens = origContentDimensProvider.provide(timestamp);
                    // If these original content dimensions need translation, we will figure that
                    // out later, after we've calculated the component net dimensions
                    contentUnadjustedDimens.put(content.uuid(), providedOrigContentDimens);
                    componentNetDimens = componentNetDimens == null ? providedOrigContentDimens :
                            encompassing(componentNetDimens, providedOrigContentDimens);
                }
                default -> throw new IllegalStateException("Unexpected content type: " + content);
            }
        }

        // 3. Determine and set origin translation

        ProviderAtTime<Vertex> originOverrideProvider =
                getFromData(component, ORIGIN_OVERRIDE_PROVIDER);

        if (originOverrideProvider != null) {
            var originOverride = originOverrideProvider.provide(timestamp);

            //noinspection DataFlowIssue
            component.data().put(ORIGINAL_ORIGIN, componentNetDimens.topLeft());
            component.data().put(ORIGIN_OVERRIDE, originOverride);
            component.data().put(COMPONENT_DIMENS, componentNetDimens = floatBoxOf(
                    originOverride.X,
                    originOverride.Y,
                    originOverride.X + componentNetDimens.width(),
                    originOverride.Y + componentNetDimens.height()
            ));
        }
        else {
            component.data().put(COMPONENT_DIMENS, componentNetDimens);
        }

        component.data().put(LAST_TIMESTAMP, timestamp);

        return componentNetDimens;
    }

    public final static String Component_setAndRetrieveDimensForComponentAndContentForProvider =
            "Component_setAndRetrieveDimensForComponentAndContentForProvider";

    public FloatBox Component_setAndRetrieveDimensForComponentAndContentForProvider(
            FunctionalProvider.Inputs inputs) {
        System.out.println("in Component_setAndRetrieveDimensForComponentAndContentForProvider...");
        UUID componentId = getFromData(inputs, COMPONENT_UUID);
        System.out.println("componentId = " + componentId);
        var component = GET_COMPONENT.apply(componentId);
        return Component_setDimensForComponentAndContent(component, inputs.timestamp());
    }

    private ProviderAtTime<FloatBox> Component_tearOutAndReplaceWithOriginOverrideForDimens(
            Renderable content) {
        ProviderAtTime<FloatBox> originalContentDimensProvider = switch (content) {
            case RenderableWithMutableDimensions r -> r.getRenderingDimensionsProvider();
            case Component c -> c.getDimensionsProvider();
            case null -> throw new IllegalArgumentException(
                    "ComponentMethods#Component_tearOutAndReplaceWithOriginOverrideForDimens: " +
                            "contains null entry");
            default -> throw new IllegalArgumentException(
                    "ComponentMethods#Component_tearOutAndReplaceWithOriginOverrideForDimens: " +
                            "contains unsupported content type (" +
                            content.getClass().getCanonicalName() + ")");
        };

        var newContentDimensProvider = FUNCTIONAL_PROVIDER_DEF_READER.read(
                functionalProvider(
                        Component_innerContentDimensWithWholeComponentOverrideCalculation,
                        FloatBox.class)
                        .withData(mapOf(
                                CONTENT_UUID,
                                content.uuid(),
                                CONTAINING_COMPONENT_UUID,
                                content.containingComponent().uuid()
                        )));

        switch (content) {
            case RenderableWithMutableDimensions r ->
                    r.setRenderingDimensionsProvider(newContentDimensProvider);
            case Component c -> c.setDimensionsProvider(newContentDimensProvider);
            // Exceptional cases already handled previously
            default -> {
            }
        }

        return originalContentDimensProvider;
    }

    private ProviderAtTime<Vertex> Component_tearOutAndReplaceWithOriginOverrideForLoc(
            TextLineRenderable content) {
        var originalContentDimensProvider = content.getRenderingLocationProvider();

        var newContentLocProvider = FUNCTIONAL_PROVIDER_DEF_READER.read(
                functionalProvider(
                        Component_innerContentRenderingLocWithWholeComponentOverrideCalculation,
                        Vertex.class)
                        .withData(mapOf(
                                CONTENT_UUID,
                                content.uuid(),
                                CONTAINING_COMPONENT_UUID,
                                content.containingComponent().uuid()
                        )));

        content.setRenderingLocationProvider(newContentLocProvider);

        return originalContentDimensProvider;
    }

    protected List<ProviderAtTime<Vertex>> Component_tearOutAndReplaceWithOriginOverrideForTriangle(
            TriangleRenderable content) {
        var originalContentVerticesProviders = listOf(
                content.getVertex1Provider(),
                content.getVertex2Provider(),
                content.getVertex3Provider()
        );

        var newContentVerticesProviders =
                IntStream.range(0, 3).mapToObj(i -> FUNCTIONAL_PROVIDER_DEF_READER.read(
                                functionalProvider(
                                        Component_innerContentVertexWithWholeComponentOverrideCalculation,
                                        Vertex.class)
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                content.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                content.containingComponent().uuid(),
                                                VERTICES_INDEX,
                                                i
                                        ))))
                        .toList();

        content.setVertex1Provider(newContentVerticesProviders.get(0));
        content.setVertex2Provider(newContentVerticesProviders.get(1));
        content.setVertex3Provider(newContentVerticesProviders.get(2));

        return originalContentVerticesProviders;
    }

    public final static String Component_innerContentDimensWithWholeComponentOverrideCalculation =
            "Component_innerContentDimensWithWholeComponentOverrideCalculation";

    public FloatBox Component_innerContentDimensWithWholeComponentOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentDimensWithOverrideCalculation(inputs,
                (c, _) -> getWholeComponentOverride(c));
    }

    public final static String Component_innerContentDimensWithContentSpecificOverride =
            "Component_innerContentDimensWithContentSpecificOverride";

    public FloatBox Component_innerContentDimensWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentDimensWithOverrideCalculation(inputs,
                ComponentMethods::getComponentSpecificOverride);
    }

    protected FloatBox Component_innerContentDimensWithOverrideCalculation(
            FunctionalProvider.Inputs inputs,
            BiFunction<Component, FunctionalProvider.Inputs, Vertex> getContentOriginOverride) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        // This will ensure that unadjusted contentUnadjustedDimens are up-to-date with the
        // provided timestamp
        //Component_setDimensForComponentAndContent(containingComponent, inputs.timestamp());

        Map<UUID, FloatBox> contentUnadjustedDimens = getFromData(containingComponent,
                CONTENT_UNADJUSTED_DIMENS);
        @SuppressWarnings("SuspiciousMethodCalls") var unadjustedDimens =
                contentUnadjustedDimens.get(getFromData(inputs, CONTENT_UUID));
        Vertex contentOrigin = getContentOriginOverride.apply(containingComponent, inputs);
        if (contentOrigin != null) {
            Vertex originalOrigin = getFromData(containingComponent, ORIGINAL_ORIGIN);
            var contentOriginAdjust = difference(originalOrigin, contentOrigin);
            return translate(unadjustedDimens, contentOriginAdjust);
        }
        else {
            return unadjustedDimens;
        }
    }

    public final static String
            Component_innerContentRenderingLocWithWholeComponentOverrideCalculation =
            "Component_innerContentRenderingLocWithWholeComponentOverrideCalculation";

    public Vertex Component_innerContentRenderingLocWithWholeComponentOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentRenderingLocWithOverrideCalculation(inputs,
                (c, _) -> getWholeComponentOverride(c));
    }

    public final static String Component_innerContentRenderingLocWithContentSpecificOverride =
            "Component_innerContentRenderingLocWithContentSpecificOverride";

    public Vertex Component_innerContentRenderingLocWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentRenderingLocWithOverrideCalculation(inputs,
                ComponentMethods::getComponentSpecificOverride);
    }

    protected Vertex Component_innerContentRenderingLocWithOverrideCalculation(
            FunctionalProvider.Inputs inputs,
            BiFunction<Component, FunctionalProvider.Inputs, Vertex> getContentOriginOverride) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, Vertex> contentUnadjustedLocs =
                getFromData(containingComponent, CONTENT_UNADJUSTED_LOCS);
        UUID contentUuid = getFromData(inputs, CONTENT_UUID);
        var unadjustedLoc = contentUnadjustedLocs.get(contentUuid);
        Vertex contentOrigin = getContentOriginOverride.apply(containingComponent, inputs);
        if (contentOrigin != null) {
            Vertex originalOrigin = getFromData(containingComponent, ORIGINAL_ORIGIN);
            if (originalOrigin != null) {
                var contentOriginAdjust = difference(originalOrigin, contentOrigin);
                return inaugural.soliloquy.tools.valueobjects.Vertex.translate(unadjustedLoc,
                        contentOriginAdjust);
            }
            else {
                return contentOrigin;
            }
        }
        else {
            return unadjustedLoc;
        }
    }

    public final static String Component_innerContentVertexWithWholeComponentOverrideCalculation =
            "Component_innerContentVertexWithWholeComponentOverrideCalculation";

    public Vertex Component_innerContentVertexWithWholeComponentOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentVertexWithOverrideCalculation(inputs,
                (c, _) -> getWholeComponentOverride(c));
    }

    public final static String Component_innerContentVertexWithSpecificContentOverride =
            "Component_innerContentVertexWithSpecificContentOverride";

    public Vertex Component_innerContentVertexWithSpecificContentOverride(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentVertexWithOverrideCalculation(inputs,
                ComponentMethods::getComponentSpecificOverride);
    }

    protected Vertex Component_innerContentVertexWithOverrideCalculation(
            FunctionalProvider.Inputs inputs,
            BiFunction<Component, FunctionalProvider.Inputs, Vertex> getContentOriginOverride) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromData(containingComponent, CONTENT_UNADJUSTED_VERTICES);
        UUID contentUuid = getFromData(inputs, CONTENT_UUID);
        var unadjustedVertices = contentUnadjustedVertices.get(contentUuid);
        int vertexIndex = getFromData(inputs, VERTICES_INDEX);
        var unadjustedVertex = unadjustedVertices.get(vertexIndex);
        Vertex contentOrigin = getContentOriginOverride.apply(containingComponent, inputs);
        if (contentOrigin != null) {
            Vertex originalOrigin = getFromData(containingComponent, ORIGINAL_ORIGIN);
            var contentOriginAdjust = difference(originalOrigin, contentOrigin);
            return inaugural.soliloquy.tools.valueobjects.Vertex.translate(unadjustedVertex,
                    contentOriginAdjust);
        }
        else {
            return unadjustedVertex;
        }
    }

    private static Vertex getWholeComponentOverride(Component component) {
        return getFromData(component, ORIGIN_OVERRIDE);
    }

    private static Vertex getComponentSpecificOverride(Component component,
                                                       FunctionalProvider.Inputs inputs) {
        //noinspection unchecked,SuspiciousMethodCalls
        return ((Map<UUID, Vertex>) getFromData(component, CONTENTS_TOP_LEFT_LOCS))
                .get(getFromData(inputs, CONTENT_UUID));
    }

    protected static <T> T getFromComponentDataOrDefault(Component component,
                                                         String key,
                                                         Supplier<T> getDefault) {
        T val = getFromData(component, key);
        if (val == null) {
            val = getDefault.get();
            component.data().put(key, val);
        }
        return val;
    }
}
