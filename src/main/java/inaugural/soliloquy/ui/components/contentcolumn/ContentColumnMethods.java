package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.valueobjects.Vertex.*;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentColumnMethods {
    private final Function<UUID, Component> GET_COMPONENT;
    @SuppressWarnings("rawtypes")
    private final Function<FunctionalProviderDefinition, ProviderAtTime>
            FUNCTIONAL_PROVIDER_DEF_READER;

    public ContentColumnMethods(
            Function<UUID, Component> getComponent,
            @SuppressWarnings("rawtypes")
            Function<FunctionalProviderDefinition, ProviderAtTime> functionalProviderDefReader
    ) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        FUNCTIONAL_PROVIDER_DEF_READER =
                Check.ifNull(functionalProviderDefReader, "functionalProviderDefReader");
    }

    public final static String ContentColumn_setDimensForComponentAndContent =
            "ContentColumn_setDimensForComponentAndContent";

    public FloatBox ContentColumn_setDimensForComponentAndContent(
            Component column,
            long timestamp
    ) {
        Long lastTimestamp = getFromData(column, LAST_TIMESTAMP);

        if (lastTimestamp != null && timestamp == lastTimestamp) {
            return getFromData(column, COMPONENT_DIMENS);
        }

        Map<UUID, ProviderAtTime<FloatBox>> origContentDimensProviders =
                getFromComponentDataOrDefault(column, CONTENT_UNADJUSTED_DIMENS_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, FloatBox> contentUnadjustedDimens =
                getFromComponentDataOrDefault(column, CONTENT_UNADJUSTED_DIMENS,
                        Collections::mapOf);
        contentUnadjustedDimens.clear();

        Map<UUID, List<ProviderAtTime<Vertex>>> contentUnadjVerticesProviders =
                getFromComponentDataOrDefault(column, CONTENT_UNADJUSTED_VERTICES_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromComponentDataOrDefault(column, CONTENT_UNADJUSTED_VERTICES,
                        Collections::mapOf);
        contentUnadjustedVertices.clear();

        ProviderAtTime<Vertex> renderingLocProvider = getFromData(column, RENDERING_LOC);
        var renderingLoc = renderingLocProvider.provide(timestamp);
        float colWidth = getFromData(column, WIDTH);
        var heightThusFar = 0f;

        var contentsFromComponent = column.contentsRepresentation();
        List<Content> contents = getFromData(column, CONTENTS);
        Map<UUID, Vertex> contentTopLeftLocs = getFromData(column, CONTENTS_TOP_LEFT_LOCS);
        // (setOf is since Map#values is backed by the map)
        var prevContent = setOf(contentTopLeftLocs.values());
        contentTopLeftLocs.clear();

        for (var item : contents) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var contentFromUuid =
                    contentsFromComponent.stream().filter(c -> c.uuid().equals(item.uuid))
                            .findFirst().get();

            // NOT handling alignment properly yet
            // NOT handling alignment properly yet
            // NOT handling alignment properly yet
            contentTopLeftLocs.put(item.uuid,
                    vertexOf(renderingLoc.X, renderingLoc.Y + heightThusFar));

            switch (contentFromUuid) {
                case Component c -> {
                    var dimens = c.getDimensionsProvider().provide(timestamp);
                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(c.uuid())) {
                        c.data().put(ORIGIN_OVERRIDE_PROVIDER, FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentRenderingLocWithContentSpecificOverride,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                c.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                column.uuid()
                                        ))
                        ));
                    }
                    heightThusFar += dimens.height();
                }
                case TextLineRenderable t -> {
                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(t.uuid())) {
                        //noinspection unchecked
                        t.setRenderingLocationProvider(FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentRenderingLocWithContentSpecificOverride,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                t.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                column.uuid()
                                        ))
                        ));
                    }
                    heightThusFar += t.lineHeightProvider().provide(timestamp);
                }
                case TriangleRenderable t -> {
                    var unadjContentVerticesProvidersForRenderable =
                            contentUnadjVerticesProviders.get(t.uuid());
                    if (unadjContentVerticesProvidersForRenderable == null) {
                        unadjContentVerticesProvidersForRenderable =
                                ContentColumn_tearOutAndReplaceWithOriginOverrideForTriangle(t);
                        contentUnadjVerticesProviders.put(t.uuid(),
                                unadjContentVerticesProvidersForRenderable);
                    }

                    var providedOrigContentVertices =
                            unadjContentVerticesProvidersForRenderable.stream()
                                    .map(p -> p.provide(timestamp)).toList();
                    contentUnadjustedVertices.put(t.uuid(), providedOrigContentVertices);

                    var triangleEncompassingDimens =
                            polygonDimens(providedOrigContentVertices.toArray(Vertex[]::new));

                    heightThusFar += triangleEncompassingDimens.height();
                }
                case RenderableWithMutableDimensions r -> {
                    var origDimensProvider = origContentDimensProviders.get(r.uuid());
                    var origDimens = origDimensProvider.provide(timestamp);
                    contentUnadjustedDimens.put(r.uuid(), origDimens);

                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(r.uuid())) {
                        //noinspection unchecked
                        r.setRenderingDimensionsProvider(FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentDimensWithContentSpecificOverride,
                                        FloatBox.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                r.uuid()
                                        ))
                        ));
                    }

                    heightThusFar += origDimens.height();
                }
                default -> throw new IllegalStateException(
                        "ContentColumnMethods#ContentColumn_setDimensForComponentAndContent: " +
                                "contentsFromComponent has unsupported type (" +
                                contentFromUuid.getClass().getCanonicalName() + ")");
            }

            heightThusFar += item.spacingAfter();
        }

        var componentDimens = floatBoxOf(
                renderingLoc.X,
                renderingLoc.Y,
                renderingLoc.X + colWidth,
                renderingLoc.Y + heightThusFar
        );
        column.data().put(COMPONENT_DIMENS, componentDimens);

        column.data().put(LAST_TIMESTAMP, timestamp);

        return componentDimens;
    }

    public List<ProviderAtTime<Vertex>> ContentColumn_tearOutAndReplaceWithOriginOverrideForTriangle(
            TriangleRenderable triangleRenderable) {
        var originalContentVerticesProviders = listOf(
                triangleRenderable.getVertex1Provider(),
                triangleRenderable.getVertex2Provider(),
                triangleRenderable.getVertex3Provider()
        );

        var newContentVerticesProviders =
                IntStream.range(0, 3).mapToObj(i -> FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentPolygonVertexWithWholeComponentOverrideCalculation,
                                        Vertex.class)
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                triangleRenderable.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                triangleRenderable.containingComponent().uuid(),
                                                VERTICES_INDEX,
                                                i
                                        ))))
                        .toList();

        //noinspection unchecked
        triangleRenderable.setVertex1Provider(newContentVerticesProviders.get(0));
        //noinspection unchecked
        triangleRenderable.setVertex2Provider(newContentVerticesProviders.get(1));
        //noinspection unchecked
        triangleRenderable.setVertex3Provider(newContentVerticesProviders.get(2));

        return originalContentVerticesProviders;
    }

    public final static String Component_innerContentDimensWithContentSpecificOverride =
            "Component_innerContentDimensWithContentSpecificOverride";

    public FloatBox Component_innerContentDimensWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, FloatBox> contentUnadjustedDimens = getFromData(containingComponent,
                CONTENT_UNADJUSTED_DIMENS);
        @SuppressWarnings("SuspiciousMethodCalls") var unadjustedDimens =
                contentUnadjustedDimens.get(getFromData(inputs, CONTENT_UUID));
        return unadjustedDimens;
    }

    public final static String Component_innerContentRenderingLocWithContentSpecificOverride =
            "Component_innerContentRenderingLocWithContentSpecificOverride";

    public Vertex Component_innerContentRenderingLocWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs) {
        return Component_innerContentRenderingLocWithOverrideCalculation(inputs,
                ContentColumnMethods::getComponentSpecificOverride);
    }

    protected Vertex Component_innerContentRenderingLocWithOverrideCalculation(
            FunctionalProvider.Inputs inputs,
            BiFunction<Component, FunctionalProvider.Inputs, Vertex> getContentOriginOverride) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        return getContentOriginOverride.apply(containingComponent, inputs);
    }

    private static Vertex getComponentSpecificOverride(Component component,
                                                       FunctionalProvider.Inputs inputs) {
        //noinspection unchecked,SuspiciousMethodCalls
        return ((Map<UUID, Vertex>) getFromData(component, CONTENTS_TOP_LEFT_LOCS))
                .get(getFromData(inputs, CONTENT_UUID));
    }

    public final static String
            Component_innerContentPolygonVertexWithWholeComponentOverrideCalculation =
            "Component_innerContentPolygonVertexWithWholeComponentOverrideCalculation";

    public Vertex Component_innerContentPolygonVertexWithWholeComponentOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromData(containingComponent, CONTENT_UNADJUSTED_VERTICES);
        UUID contentUuid = getFromData(inputs, CONTENT_UUID);
        var unadjustedVertices = contentUnadjustedVertices.get(contentUuid);
        int vertexIndex = getFromData(inputs, VERTICES_INDEX);
        return unadjustedVertices.get(vertexIndex);
    }

    public final static String ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider =
            "ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider";

    public FloatBox ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider(
            FunctionalProvider.Inputs inputs) {
        UUID componentId = getFromData(inputs, COMPONENT_UUID);
        var component = GET_COMPONENT.apply(componentId);
        return ContentColumn_setDimensForComponentAndContent(component, inputs.timestamp());
    }

    public final static String ContentColumn_add = "ContentColumn_add";

    public void ContentColumn_add(Component component, Component.Addend addend) {
        if (addend.data() != null) {
            //noinspection unchecked
            ((List<Content>) component.data().get(CONTENTS)).add(new Content(
                    addend.content().uuid(),
                    getFromData(addend.data(), SPACING_AFTER),
                    getFromData(addend.data(), ALIGNMENT),
                    getFromData(addend.data(), INDENT)
            ));
        }
    }

    public record Content(UUID uuid,
                          float spacingAfter,
                          HorizontalAlignment alignment,
                          float indent) {
    }
}
