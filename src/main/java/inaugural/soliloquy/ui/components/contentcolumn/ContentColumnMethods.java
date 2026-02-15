package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import soliloquy.specs.common.shared.HasUuid;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.valueobjects.Vertex.*;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static java.util.UUID.randomUUID;
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

        Map<UUID, ProviderAtTime<FloatBox>> unadjContentDimensProviders =
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

        Map<UUID, Vertex> contentPolygonOffsets =
                getFromComponentDataOrDefault(column, CONTENT_POLYGON_OFFSETS,
                        Collections::mapOf);
        contentPolygonOffsets.clear();

        ProviderAtTime<Vertex> renderingLocProvider = getFromData(column, COMPONENT_RENDERING_LOC);
        var renderingLoc = renderingLocProvider.provide(timestamp);
        float colWidth = getFromData(column, COMPONENT_WIDTH);
        var heightThusFar = 0f;

        var contentsFromComponent = column.contentsRepresentation();

        List<Content> contents = getFromData(column, CONTENTS);

        Set<UUID> registeredContentsInData =
                getFromComponentDataOrDefault(column, REGISTERED_CONTENTS, Collections::setOf);

        Map<UUID, Vertex> newContentSpecificOrigins = mapOf();

        for (var content : contents) {
            var contentFromUuid =
                    contentsFromComponent.stream().filter(c -> c.uuid().equals(content.uuid))
                            .findFirst().orElse(null);

            // NOT handling alignment properly yet
            // NOT handling alignment properly yet
            // NOT handling alignment properly yet

            switch (contentFromUuid) {
                case Component c -> {
                    var dimens = c.getDimensionsProvider().provide(timestamp);

                    if (!registeredContentsInData.contains(c.uuid())) {
                        c.data().put(ORIGIN_OVERRIDE_PROVIDER, FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentSpecificRenderingLoc,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                c.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                column.uuid()
                                        ))
                        ));
                        registeredContentsInData.add(c.uuid());
                    }

                    newContentSpecificOrigins.put(
                            c.uuid(),
                            vertexOf(midpoint(
                                    renderingLoc.X,
                                    colWidth,
                                    dimens.width(),
                                    content.indent,
                                    content.alignment
                            ), renderingLoc.Y + heightThusFar)
                    );

                    heightThusFar += dimens.height();
                }
                case TextLineRenderable t -> {
                    if (!registeredContentsInData.contains(t.uuid())) {
                        //noinspection unchecked
                        t.setRenderingLocationProvider(FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentSpecificRenderingLoc,
                                        Vertex.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                t.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                column.uuid()
                                        ))
                        ));
                        registeredContentsInData.add(t.uuid());
                    }

                    t.setAlignment(content.alignment);

                    var xLoc = switch (content.alignment) {
                        case LEFT -> renderingLoc.X + content.indent;
                        case CENTER -> renderingLoc.X + (colWidth / 2f);
                        case RIGHT -> renderingLoc.X + colWidth - content.indent;
                    };

                    newContentSpecificOrigins.put(
                            t.uuid(),
                            vertexOf(xLoc, renderingLoc.Y + heightThusFar)
                    );

                    heightThusFar += t.lineHeightProvider().provide(timestamp);
                }
                case RenderableWithMutableDimensions r -> {
                    var unadjDimensProvider = unadjContentDimensProviders.get(r.uuid());
                    if (unadjDimensProvider == null) {
                        unadjContentDimensProviders.put(r.uuid(),
                                unadjDimensProvider = r.getRenderingDimensionsProvider());
                        //noinspection unchecked
                        r.setRenderingDimensionsProvider(FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentDimensWithContentSpecificOverride,
                                        FloatBox.class
                                )
                                        .withData(mapOf(
                                                CONTENT_UUID,
                                                r.uuid(),
                                                CONTAINING_COMPONENT_UUID,
                                                column.uuid()
                                        ))
                        ));
                    }

                    var origDimens = unadjDimensProvider.provide(timestamp);
                    contentUnadjustedDimens.put(r.uuid(), origDimens);

                    newContentSpecificOrigins.put(
                            r.uuid(),
                            vertexOf(midpoint(
                                    renderingLoc.X,
                                    colWidth,
                                    origDimens.width(),
                                    content.indent,
                                    content.alignment
                            ), renderingLoc.Y + heightThusFar)
                    );

                    heightThusFar += origDimens.height();
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

                    var providedUnadjContentVertices =
                            unadjContentVerticesProvidersForRenderable.stream()
                                    .map(p -> p.provide(timestamp)).toList();
                    contentUnadjustedVertices.put(t.uuid(), providedUnadjContentVertices);
                    var unadjPolygonDimens = polygonEncompassingDimens(providedUnadjContentVertices);
                    contentPolygonOffsets.put(
                            t.uuid(),
                            vertexOf(midpoint(
                                    renderingLoc.X,
                                    colWidth,
                                    unadjPolygonDimens.width(),
                                    content.indent,
                                    content.alignment
                            ), renderingLoc.Y + heightThusFar)
                    );

                    heightThusFar += unadjPolygonDimens.height();
                }
                case null -> {
                    // null is expected for spacing, c.f. ContentColumnDefinition.Item::space
                }
                default -> throw new IllegalStateException(
                        "ContentColumnMethods#ContentColumn_setDimensForComponentAndContent: " +
                                "contentsFromComponent has unsupported type (" +
                                contentFromUuid.getClass().getCanonicalName() + ")");
            }

            heightThusFar += content.spacingAfter();
        }

        var componentDimens = floatBoxOf(
                renderingLoc,
                colWidth,
                heightThusFar
        );
        column.data().put(COMPONENT_DIMENS, componentDimens);
        column.data().put(CONTENT_SPECIFIC_ORIGINS, newContentSpecificOrigins);

        column.data().put(LAST_TIMESTAMP, timestamp);

        return componentDimens;
    }

    private List<ProviderAtTime<Vertex>> ContentColumn_tearOutAndReplaceWithOriginOverrideForTriangle(
            TriangleRenderable triangleRenderable) {
        var originalContentVerticesProviders = listOf(
                triangleRenderable.getVertex1Provider(),
                triangleRenderable.getVertex2Provider(),
                triangleRenderable.getVertex3Provider()
        );

        var newContentVerticesProviders =
                IntStream.range(0, 3).mapToObj(i -> FUNCTIONAL_PROVIDER_DEF_READER.apply(
                                functionalProvider(
                                        Component_innerContentPolygonVertexWithContentSpecificOverride,
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

    private float midpoint(float start, float colWidth, float contentWidth, float indent,
                           HorizontalAlignment alignment) {
        return switch(alignment) {
            case LEFT -> start + indent;
            case CENTER -> start + ((colWidth - contentWidth) / 2f);
            case RIGHT -> start + colWidth - contentWidth - indent;
        };
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
        var contentToAddToDataUuid = defaultIfNull(
                addend.content(),
                HasUuid::uuid,
                defaultIfNull(
                        addend.data(),
                        d -> defaultIfNull(getFromData(d, SPACING_UUID), randomUUID()),
                        randomUUID()
                )
        );
        List<Content> contentsFromData = getFromData(component, CONTENTS);
        if (contentsFromData.stream().noneMatch(c -> c.uuid() == contentToAddToDataUuid)) {
            contentsFromData.add(new Content(
                    contentToAddToDataUuid,
                    getFromData(addend.data(), SPACING_AFTER),
                    getFromData(addend.data(), ALIGNMENT),
                    defaultIfNull(getFromData(addend.data(), INDENT), 0f)
            ));
        }
    }

    public record Content(UUID uuid,
                          float spacingAfter,
                          HorizontalAlignment alignment,
                          float indent) {
    }
}
