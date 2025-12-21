package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.valueobjects.Vertex.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentColumnMethods extends ComponentMethods {
    public final static String RENDERING_LOC = "RENDERING_LOC";
    public final static String WIDTH = "WIDTH";
    public final static String CONTENTS = "CONTENTS";

    public final static String INDENT = "INDENT";
    public final static String ALIGNMENT = "ALIGNMENT";
    public final static String SPACING_AFTER = "SPACING_AFTER";

    public ContentColumnMethods(Function<UUID, Component> getComponent,
                                FunctionalProviderDefinitionReader functionalProviderDefReader) {
        super(getComponent, functionalProviderDefReader);
    }

    public final static String ContentColumn_setDimensForComponentAndContent =
            "ContentColumn_setDimensForComponentAndContent";

    public FloatBox ContentColumn_setDimensForComponentAndContent(
            Component component,
            long timestamp) {
        System.out.println("in ContentColumn_setDimensForComponentAndContent...");
        System.out.println("column uuid = " + component.uuid());
        Long lastTimestamp = getFromData(component, LAST_TIMESTAMP);
        // This makes calls to this method cheap, if dimens have already been calculated for the
        // provided timestamp
        if (lastTimestamp != null && timestamp == lastTimestamp) {
            return getFromData(component, COMPONENT_DIMENS);
        }

        System.out.println("doing the thing...");
        Map<UUID, ProviderAtTime<FloatBox>> origContentDimensProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_DIMENS_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, FloatBox> contentUnadjustedDimens =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_DIMENS,
                        Collections::mapOf);
        contentUnadjustedDimens.clear();

        Map<UUID, ProviderAtTime<Vertex>> origContentLocProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_LOC_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, Vertex> contentUnadjustedLocs =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_LOCS,
                        Collections::mapOf);
        contentUnadjustedLocs.clear();

        Map<UUID, List<ProviderAtTime<Vertex>>> origContentVerticesProviders =
                getFromComponentDataOrDefault(component, UNADJUSTED_CONTENT_VERTICES_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromComponentDataOrDefault(component, CONTENT_UNADJUSTED_VERTICES,
                        Collections::mapOf);
        contentUnadjustedVertices.clear();

        ProviderAtTime<Vertex> renderingLocProvider = getFromData(component, RENDERING_LOC);
        var renderingLoc = renderingLocProvider.provide(timestamp);
        float colWidth = getFromData(component, WIDTH);
        var heightThusFar = 0f;

        var contentsFromComponent = component.contentsRepresentation();
        System.out.println("contentsFromComponent.size = " + contentsFromComponent.size());
        List<Content> contents = getFromData(component, CONTENTS);
        Map<UUID, Vertex> contentTopLeftLocs = getFromData(component, CONTENTS_TOP_LEFT_LOCS);
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
                    System.out.println("it's a component, getting dimens...");
                    System.out.println("(component uuid = " + c.uuid() + ")");
                    var dimens = c.getDimensionsProvider().provide(timestamp);
                    System.out.println("dimens = " + dimens);
                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(c.uuid())) {
                        System.out.println(">>> was not previously present! inserting origin override");
                        c.data().put(ORIGIN_OVERRIDE_PROVIDER, FUNCTIONAL_PROVIDER_DEF_READER.read(
                                functionalProvider(
                                        Component_innerContentRenderingLocWithContentSpecificOverride,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                c.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                component.uuid()
                                        ))
                        ));
                    }
                    heightThusFar += dimens.height();
                }
                case TextLineRenderable t -> {
                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(t.uuid())) {
                        t.setRenderingLocationProvider(FUNCTIONAL_PROVIDER_DEF_READER.read(
                                functionalProvider(
                                        Component_innerContentRenderingLocWithContentSpecificOverride,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                t.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                component.uuid()
                                        ))
                        ));
                    }
                    heightThusFar += t.lineHeightProvider().provide(timestamp);
                }
                case TriangleRenderable t -> {
                    var origContentVerticesProvidersForRenderable =
                            origContentVerticesProviders.get(t.uuid());
                    if (origContentVerticesProvidersForRenderable == null) {
                        origContentVerticesProvidersForRenderable =
                                Component_tearOutAndReplaceWithOriginOverrideForTriangle(t);
                        origContentVerticesProviders.put(t.uuid(),
                                origContentVerticesProvidersForRenderable);
                    }

                    var providedOrigContentVertices =
                            origContentVerticesProvidersForRenderable.stream()
                                    .map(p -> p.provide(timestamp)).toList();
                    contentUnadjustedVertices.put(t.uuid(), providedOrigContentVertices);

                    var triangleEncompassingDimens =
                            polygonDimens(providedOrigContentVertices.toArray(Vertex[]::new));

                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(t.uuid())) {
                        var newProviders = IntStream.range(0, 3).mapToObj(i ->
                                FUNCTIONAL_PROVIDER_DEF_READER.read(
                                        functionalProvider(
                                                Component_innerContentVertexWithSpecificContentOverride,
                                                Vertex.class
                                        )
                                                .withData(mapOf(
                                                        CONTENT_UUID,
                                                        t.uuid(),
                                                        CONTAINING_COMPONENT_UUID,
                                                        component.uuid(),
                                                        VERTICES_INDEX,
                                                        i
                                                ))
                                )).toList();

                        t.setVertex1Provider(newProviders.get(0));
                        t.setVertex1Provider(newProviders.get(1));
                        t.setVertex1Provider(newProviders.get(2));
                    }

                    heightThusFar += triangleEncompassingDimens.height();
                }
                case RenderableWithMutableDimensions r -> {
                    var origDimensProvider = origContentDimensProviders.get(r.uuid());
                    var origDimens = origDimensProvider.provide(timestamp);
                    contentUnadjustedDimens.put(r.uuid(), origDimens);

                    //noinspection SuspiciousMethodCalls
                    if (!prevContent.contains(r.uuid())) {
                        r.setRenderingDimensionsProvider(FUNCTIONAL_PROVIDER_DEF_READER.read(
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
            System.out.println("heightThusFar = " + heightThusFar);
        }

        return floatBoxOf(
                renderingLoc.X,
                renderingLoc.Y,
                renderingLoc.X + colWidth,
                renderingLoc.Y + heightThusFar
        );
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
