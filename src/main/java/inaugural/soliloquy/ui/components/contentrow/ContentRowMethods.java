package inaugural.soliloquy.ui.components.contentrow;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import soliloquy.specs.common.shared.HasUuid;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.rendering.renderers.TextLineRenderer;
import soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.Tools.defaultIfNullElseTransform;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.valueobjects.Vertex.polygonEncompassingDimens;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment;
import static inaugural.soliloquy.ui.components.ComponentMethods.*;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentRowMethods {
    private final Function<UUID, Component> GET_COMPONENT;
    @SuppressWarnings("rawtypes")
    private final Function<FunctionalProviderDefinition, ProviderAtTime>
            FUNCTIONAL_PROVIDER_DEF_READER;
    private final TextLineRenderer TEXT_LINE_RENDERER;

    public ContentRowMethods(
            Function<UUID, Component> getComponent,
            @SuppressWarnings("rawtypes")
            Function<FunctionalProviderDefinition, ProviderAtTime> functionalProviderDefReader,
            TextLineRenderer textLineRenderer
    ) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        FUNCTIONAL_PROVIDER_DEF_READER =
                Check.ifNull(functionalProviderDefReader, "functionalProviderDefReader");
        TEXT_LINE_RENDERER = Check.ifNull(textLineRenderer, "textLineRenderer");
    }

    public final static String ContentRow_setDimensForComponentAndContent =
            "ContentRow_setDimensForComponentAndContent";

    public FloatBox ContentRow_setDimensForComponentAndContent(
            Component row,
            long timestamp
    ) {
        Long lastTimestamp = getFromData(row, LAST_TIMESTAMP);

        if (lastTimestamp != null && timestamp == lastTimestamp) {
            return getFromData(row, COMPONENT_DIMENS);
        }

        Map<UUID, ProviderAtTime<FloatBox>> unadjContentDimensProviders =
                getFromComponentDataOrDefault(row, CONTENT_UNADJUSTED_DIMENS_PROVIDERS,
                        Collections::mapOf);

        Map<UUID, FloatBox> contentUnadjustedDimens =
                getFromComponentDataOrDefault(row, CONTENT_UNADJUSTED_DIMENS,
                        Collections::mapOf);
        contentUnadjustedDimens.clear();

        Map<UUID, List<ProviderAtTime<Vertex>>> contentUnadjVerticesProviders =
                getFromComponentDataOrDefault(row, CONTENT_UNADJUSTED_VERTICES_PROVIDERS,
                        Collections::mapOf);

        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromComponentDataOrDefault(row, CONTENT_UNADJUSTED_VERTICES,
                        Collections::mapOf);
        contentUnadjustedVertices.clear();

        Map<UUID, Vertex> contentPolygonOffsets =
                getFromComponentDataOrDefault(row, CONTENT_POLYGON_OFFSETS,
                        Collections::mapOf);
        contentPolygonOffsets.clear();

        ProviderAtTime<Vertex> renderingLocProvider = getFromData(row, COMPONENT_RENDERING_LOC);
        var renderingLoc = renderingLocProvider.provide(timestamp);
        float rowHeight = getFromData(row, COMPONENT_HEIGHT);
        var widthThusFar = 0f;

        var contentsFromComponent = row.contentsRepresentation();

        List<Content> contentsFromData = getFromData(row, CONTENTS);

        Set<UUID> registeredContentsInData =
                getFromComponentDataOrDefault(row, REGISTERED_CONTENTS, Collections::setOf);

        Map<UUID, Vertex> newContentSpecificOrigins = mapOf();

        for (var content : contentsFromData) {
            var contentFromUuid =
                    contentsFromComponent.stream().filter(c -> c.uuid().equals(content.uuid))
                            .findFirst().orElse(null);

            switch (contentFromUuid) {
                case Component c -> {
                    var origDimens = c.getDimensionsProvider().provide(timestamp);

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
                                                row.uuid()
                                        ))
                        ));
                        registeredContentsInData.add(c.uuid());
                    }

                    newContentSpecificOrigins.put(
                            c.uuid(),
                            vertexOf(
                                    renderingLoc.X + widthThusFar,
                                    midpoint(
                                            renderingLoc.Y,
                                            rowHeight,
                                            origDimens.height(),
                                            content.indent,
                                            content.alignment
                                    )
                            )
                    );

                    widthThusFar += origDimens.width();
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
                                                row.uuid()
                                        ))
                        ));
                        registeredContentsInData.add(t.uuid());

                        // (Rendering loc is overridden by the functional provider who assumes a
                        // left-aligned text line
                        t.setAlignment(LEFT);
                    }

                    var lineWidth = TEXT_LINE_RENDERER.textLineLength(t, timestamp);
                    var lineHeight = t.lineHeightProvider().provide(timestamp);

                    newContentSpecificOrigins.put(
                            t.uuid(),
                            vertexOf(
                                    renderingLoc.X + widthThusFar,
                                    midpoint(
                                            renderingLoc.Y,
                                            rowHeight,
                                            lineHeight,
                                            content.indent,
                                            content.alignment
                                    )
                            )
                    );

                    widthThusFar += lineWidth;
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
                                                row.uuid()
                                        ))
                        ));
                    }

                    var origDimens = unadjDimensProvider.provide(timestamp);
                    contentUnadjustedDimens.put(r.uuid(), origDimens);

                    newContentSpecificOrigins.put(
                            r.uuid(),
                            vertexOf(
                                    renderingLoc.X + widthThusFar,
                                    midpoint(
                                            renderingLoc.Y,
                                            rowHeight,
                                            origDimens.height(),
                                            content.indent,
                                            content.alignment
                                    )
                            )
                    );

                    widthThusFar += origDimens.width();
                }
                case TriangleRenderable t -> {
                    var unadjContentVerticesProvidersForRenderable =
                            contentUnadjVerticesProviders.get(t.uuid());
                    if (unadjContentVerticesProvidersForRenderable == null) {
                        unadjContentVerticesProvidersForRenderable =
                                ContentRow_tearOutAndReplaceWithOriginOverrideForTriangle(t);
                        contentUnadjVerticesProviders.put(t.uuid(),
                                unadjContentVerticesProvidersForRenderable);
                    }

                    var providedUnadjContentVertices =
                            unadjContentVerticesProvidersForRenderable.stream()
                                    .map(p -> p.provide(timestamp)).toList();
                    contentUnadjustedVertices.put(t.uuid(), providedUnadjContentVertices);
                    var origEncompassingDimens =
                            polygonEncompassingDimens(providedUnadjContentVertices);
                    contentPolygonOffsets.put(
                            t.uuid(),
                            vertexOf(renderingLoc.X + widthThusFar,
                                    midpoint(
                                            renderingLoc.Y,
                                            rowHeight,
                                            origEncompassingDimens.height(),
                                            content.indent,
                                            content.alignment
                                    )
                            )
                    );

                    widthThusFar += origEncompassingDimens.width();
                }
                case null -> {
                    // null is expected for spacing, c.f. ContentColumnDefinition.Item::space
                }
                default -> throw new IllegalStateException(
                        "ContentColumnMethods#ContentRow_setDimensForComponentAndContent: " +
                                "contentsFromComponent has unsupported type (" +
                                contentFromUuid.getClass().getCanonicalName() + ")");
            }

            widthThusFar += content.spacingAfter();
        }

        var componentDimens = floatBoxOf(
                renderingLoc,
                widthThusFar,
                rowHeight
        );
        row.data().put(COMPONENT_DIMENS, componentDimens);
        row.data().put(CONTENT_SPECIFIC_ORIGINS, newContentSpecificOrigins);

        row.data().put(LAST_TIMESTAMP, timestamp);

        return componentDimens;
    }

    private List<ProviderAtTime<Vertex>> ContentRow_tearOutAndReplaceWithOriginOverrideForTriangle(
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

    private float midpoint(float startY, float rowHeight, float contentHeight, float indent,
                           VerticalAlignment alignment) {
        return switch (alignment) {
            case TOP -> startY + indent;
            case CENTER -> startY + ((rowHeight - contentHeight) / 2f);
            case BOTTOM -> startY + rowHeight - contentHeight - indent;
        };
    }

    public final static String ContentRow_setAndRetrieveDimensForComponentAndContentForProvider =
            "ContentRow_setAndRetrieveDimensForComponentAndContentForProvider";

    public FloatBox ContentRow_setAndRetrieveDimensForComponentAndContentForProvider(
            FunctionalProvider.Inputs inputs) {
        UUID componentId = getFromData(inputs, COMPONENT_UUID);
        var component = GET_COMPONENT.apply(componentId);
        return ContentRow_setDimensForComponentAndContent(component, inputs.timestamp());
    }

    public final static String ContentRow_add = "ContentRow_add";

    public void ContentRow_add(Component component, Component.Addend addend) {
        var contentToAddToDataUuid = defaultIfNullElseTransform(
                addend.content(),
                HasUuid::uuid,
                defaultIfNullElseTransform(
                        addend.data(),
                        d -> defaultIfNull(getFromData(d, SPACING_UUID), randomUUID()),
                        randomUUID()
                )
        );
        List<Content> contentsFromData = getFromData(component, CONTENTS);
        if (contentsFromData.stream().noneMatch(c -> c.uuid() == contentToAddToDataUuid)) {
            contentsFromData.add(new Content(
                    contentToAddToDataUuid,
                    defaultIfNull(getFromData(addend.data(), INDENT), 0f),
                    getFromData(addend.data(), ALIGNMENT),
                    getFromData(addend.data(), SPACING_AFTER)
            ));
        }
    }

    public record Content(UUID uuid,
                          float indent,
                          VerticalAlignment alignment,
                          float spacingAfter) {
    }
}
