package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.ComponentMethods;
import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ContentColumnMethods extends ComponentMethods {
    protected final String RENDERING_LOC = "RENDERING_LOC";
    protected final String WIDTH = "WIDTH";
    protected final String CONTENT_UUIDS_AND_SPACINGS = "CONTENT_UUIDS_AND_SPACINGS";

    public ContentColumnMethods(Function<UUID, Component> getComponent,
                                FunctionalProviderDefinitionReader functionalProviderDefReader) {
        super(getComponent, functionalProviderDefReader);
    }

    public final static String ContentColumn_setDimensForComponentAndContent =
            "ContentColumn_setDimensForComponentAndContent";

    public FloatBox ContentColumn_setDimensForComponentAndContent(
            Component component,
            long timestamp) {
        Long lastTimestamp = getFromData(component.data(), LAST_TIMESTAMP);
        // This makes calls to this method cheap, if dimens have already been calculated for the
        // provided timestamp
        if (lastTimestamp != null && timestamp == lastTimestamp) {
            return getFromData(component.data(), COMPONENT_DIMENS);
        }

        Map<UUID, ProviderAtTime<FloatBox>> origContentDimensProviders =
                getFromComponentDataOrDefault(component, ORIG_CONTENT_DIMENS_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, FloatBox> contentDimens =
                getFromComponentDataOrDefault(component, CONTENT_DIMENS, Collections::mapOf);
        Map<UUID, ProviderAtTime<Vertex>> origContentLocProviders =
                getFromComponentDataOrDefault(component, ORIG_CONTENT_LOC_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, Vertex> contentLocs =
                getFromComponentDataOrDefault(component, CONTENT_LOCS, Collections::mapOf);
        Map<UUID, List<ProviderAtTime<Vertex>>> origContentVerticesProviders =
                getFromComponentDataOrDefault(component, ORIG_CONTENT_VERTICES_PROVIDERS,
                        Collections::mapOf);
        Map<UUID, List<Vertex>> contentVertices =
                getFromComponentDataOrDefault(component, CONTENT_VERTICES, Collections::mapOf);
        contentDimens.clear();
        contentLocs.clear();
        contentVertices.clear();

        ProviderAtTime<Vertex> renderingLocProvider = getFromData(component.data(), RENDERING_LOC);
        var renderingLoc = renderingLocProvider.provide(timestamp);
        float width = getFromData(component.data(), WIDTH);
        var heightThusFar = 0f;

        var content = component.contentsRepresentation();
        List<Pair<UUID, Float>> contentUuidsAndSpacings =
                getFromData(component.data(), CONTENT_UUIDS_AND_SPACINGS);
        for (var contentUuidAndSpacing : contentUuidsAndSpacings) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            var contentFromUuid =
                    content.stream().filter(c -> c.uuid().equals(contentUuidAndSpacing.FIRST))
                            .findFirst().get();

            switch (contentFromUuid) {
                case Component c -> {

                }
                case null -> {}
                default -> throw new IllegalStateException("ContentColumnMethods#ContentColumn_setDimensForComponentAndContent: content has unsupported type (" + contentFromUuid.getClass().getCanonicalName() + ")");
            }

            heightThusFar += contentUuidAndSpacing.SECOND;
        }

        return floatBoxOf(
                renderingLoc.X,
                renderingLoc.Y,
                renderingLoc.X + width,
                renderingLoc.Y + heightThusFar
        );
    }
}
