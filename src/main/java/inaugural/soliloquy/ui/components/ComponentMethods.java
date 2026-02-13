package inaugural.soliloquy.ui.components;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.reflection.Reflection;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.Constants.VERTICES_INDEX;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;

public class ComponentMethods {
    private final Function<UUID, Component> GET_COMPONENT;

    public ComponentMethods(Function<UUID, Component> getComponent) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
    }

    public final static String Component_innerContentDimensWithContentSpecificOverride =
            "Component_innerContentDimensWithContentSpecificOverride";

    public FloatBox Component_innerContentDimensWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs
    ) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, FloatBox> contentUnadjustedDimens = getFromData(containingComponent,
                CONTENT_UNADJUSTED_DIMENS);

        UUID contentUuid = getFromData(inputs, CONTENT_UUID);

        var unadjDimens = contentUnadjustedDimens.get(contentUuid);

        Map<UUID, Vertex> contentSpecificOrigins =
                getFromData(containingComponent, CONTENT_SPECIFIC_ORIGINS);
        if (contentSpecificOrigins != null && contentSpecificOrigins.containsKey(contentUuid)) {
            return floatBoxOf(
                    contentSpecificOrigins.get(contentUuid),
                    unadjDimens.width(),
                    unadjDimens.height()
            );
        }
        else {
            return unadjDimens;
        }
    }

    public final static String Component_innerContentSpecificRenderingLoc =
            "Component_innerContentSpecificRenderingLoc";

    public Vertex Component_innerContentSpecificRenderingLoc(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        //noinspection unchecked,SuspiciousMethodCalls
        return ((Map<UUID, Vertex>) getFromData(containingComponent, CONTENT_SPECIFIC_ORIGINS))
                .get(getFromData(inputs, CONTENT_UUID));
    }

    public final static String Component_innerContentPolygonVertexWithContentSpecificOverride =
            "Component_innerContentPolygonVertexWithContentSpecificOverride";

    public Vertex Component_innerContentPolygonVertexWithContentSpecificOverride(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs, CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        Map<UUID, List<Vertex>> contentUnadjustedVertices =
                getFromData(containingComponent, CONTENT_UNADJUSTED_VERTICES);
        UUID contentUuid = getFromData(inputs, CONTENT_UUID);
        var unadjustedVertices = contentUnadjustedVertices.get(contentUuid);
        int vertexIndex = getFromData(inputs, VERTICES_INDEX);
        var unadjVertex = unadjustedVertices.get(vertexIndex);

        Map<UUID, Vertex> contentPolygonOffsets =
                getFromData(containingComponent, CONTENT_POLYGON_OFFSETS);
        if (contentPolygonOffsets != null && contentPolygonOffsets.containsKey(contentUuid)) {
            return translateVertex(unadjVertex, contentPolygonOffsets.get(contentUuid));
        }
        else {
            return unadjVertex;
        }
    }

    @Reflection.DoNotReadMethod
    public static <T> T getFromComponentDataOrDefault(Component component,
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
