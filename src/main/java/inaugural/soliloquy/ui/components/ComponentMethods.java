package inaugural.soliloquy.ui.components;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.RenderableWithMutableDimensions;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.encompassing;
import static inaugural.soliloquy.tools.valueobjects.FloatBox.translate;
import static inaugural.soliloquy.tools.valueobjects.Vertex.difference;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ComponentMethods {
    public final static String COMPONENT_UUID = "COMPONENT_UUID";
    public final static String CONTAINING_COMPONENT_UUID = "CONTAINING_COMPONENT_UUID";
    public final static String LAST_TIMESTAMP = "LAST_TIMESTAMP";
    public final static String COMPONENT_DIMENS = "COMPONENT_DIMENS";
    public final static String ORIGIN_OVERRIDE_PROVIDER = "ORIGIN_OVERRIDE_PROVIDER";
    public final static String ORIGIN_OVERRIDE_ADJUST = "ORIGIN_OVERRIDE_ADJUST";
    public final static String CONTENT_DIMENS = "CONTENT_DIMENS";
    public final static String CONTENT_LOCS = "CONTENT_LOCS";
    public final static String ORIG_CONTENT_DIMENS_PROVIDERS = "ORIG_CONTENT_DIMENS_PROVIDERS";
    public final static String ORIG_CONTENT_LOC_PROVIDERS = "ORIG_CONTENT_LOC_PROVIDERS";

    private final Function<UUID, Component> GET_COMPONENT;
    private final FunctionalProviderDefinitionReader FUNCTIONAL_PROVIDER_DEF_READER;

    public ComponentMethods(Function<UUID, Component> getComponent,
                            FunctionalProviderDefinitionReader functionalProviderDefReader) {
        GET_COMPONENT = Check.ifNull(getComponent, "getComponent");
        FUNCTIONAL_PROVIDER_DEF_READER =
                Check.ifNull(functionalProviderDefReader, "functionalProviderDefReader");
    }

    public final static String Component_setDimensForComponentAndContent =
            "Component_setDimensForComponentAndContent";

    public FloatBox Component_setDimensForComponentAndContent(Component component, long timestamp) {
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
        contentDimens.clear();
        contentLocs.clear();

        // 1. Get all _original_ content dimens providers; if not stored in data already, rip and
        // replace, while populating data with those original content dimens providers

        FloatBox componentNetDimens = null;

        for (var content : component.contentsRepresentation()) {
            if (content instanceof TextLineRenderable textLineRenderable) {
                var origContentLocProvider = origContentLocProviders.get(textLineRenderable.uuid());
                if (origContentLocProvider == null) {
                    origContentLocProvider =
                            Component_tearOutAndReplaceWithOriginOverrideForLoc(textLineRenderable);
                    origContentLocProviders.put(textLineRenderable.uuid(), origContentLocProvider);
                }

                var providedOrigContentLoc = origContentLocProvider.provide(timestamp);
                contentLocs.put(textLineRenderable.uuid(), providedOrigContentLoc);

                // (Text line length isn't being added into net dimens, since line length is
                // ideally dimensionless. Containing components should track text width instead,
                // e.g., Button, TextBlock)
            }

            else {
                var origContentDimensProvider = origContentDimensProviders.get(content.uuid());
                if (origContentDimensProvider == null) {
                    origContentDimensProvider =
                            Component_tearOutAndReplaceWithOriginOverrideForDimens(content);
                    origContentDimensProviders.put(content.uuid(), origContentDimensProvider);
                }

                // 2. Expand dimens to encompass contents

                var providedOrigContentDimens = origContentDimensProvider.provide(timestamp);
                // If these original content dimensions need translation, we will figure that out
                // later, after we've calculated the component net dimensions
                contentDimens.put(content.uuid(), providedOrigContentDimens);
                componentNetDimens = componentNetDimens == null ? providedOrigContentDimens :
                        encompassing(componentNetDimens, providedOrigContentDimens);
            }
        }

        // 3. Determine and set origin translation

        if (componentNetDimens == null) {
            return null;
        }

        ProviderAtTime<Vertex> originOverrideProvider =
                getFromData(component.data(), ORIGIN_OVERRIDE_PROVIDER);

        if (originOverrideProvider != null) {
            var originOverride = originOverrideProvider.provide(timestamp);

            var componentOrigin = componentNetDimens.topLeft();
            var originOverrideAdjust = difference(componentOrigin, originOverride);
            component.data().put(ORIGIN_OVERRIDE_ADJUST, originOverrideAdjust);
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
        UUID componentId = getFromData(inputs.data(), COMPONENT_UUID);
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
                            "contains " +
                            "null entry");
            default -> throw new IllegalArgumentException(
                    "ComponentMethods#Component_tearOutAndReplaceWithOriginOverrideForDimens: " +
                            "contains " +
                            "unsupported content type (" +
                            content.getClass().getCanonicalName() + ")");
        };

        var newContentDimensProvider = FUNCTIONAL_PROVIDER_DEF_READER.read(
                functionalProvider(Component_innerContentDimensWithOverrideCalculation,
                        FloatBox.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
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
                functionalProvider(Component_innerContentRenderingLocWithOverrideCalculation,
                        Vertex.class)
                        .withData(mapOf(
                                COMPONENT_UUID,
                                content.uuid(),
                                CONTAINING_COMPONENT_UUID,
                                content.containingComponent().uuid()
                        )));

        content.setRenderingLocationProvider(newContentLocProvider);

        return originalContentDimensProvider;
    }

    public final static String Component_innerContentDimensWithOverrideCalculation =
            "Component_innerContentDimensWithOverrideCalculation";

    public FloatBox Component_innerContentDimensWithOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs.data(), CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        // This will ensure that unadjusted contentDimens are up-to-date with the provided timestamp
        Component_setDimensForComponentAndContent(containingComponent, inputs.timestamp());

        Map<UUID, FloatBox> contentDimens = getFromData(containingComponent.data(), CONTENT_DIMENS);
        @SuppressWarnings("SuspiciousMethodCalls") var unadjustedDimens =
                contentDimens.get(getFromData(inputs.data(), COMPONENT_UUID));
        Vertex originOverrideAdjust =
                getFromData(containingComponent.data(), ORIGIN_OVERRIDE_ADJUST);
        if (originOverrideAdjust != null) {
            return translate(unadjustedDimens, originOverrideAdjust);
        }
        else {
            return unadjustedDimens;
        }
    }

    public final static String Component_innerContentRenderingLocWithOverrideCalculation =
            "Component_innerContentRenderingLocWithOverrideCalculation";

    public Vertex Component_innerContentRenderingLocWithOverrideCalculation(
            FunctionalProvider.Inputs inputs) {
        UUID containingComponentId = getFromData(inputs.data(), CONTAINING_COMPONENT_UUID);
        var containingComponent = GET_COMPONENT.apply(containingComponentId);

        // This will ensure that unadjusted contentLocs are up-to-date with the provided timestamp
        Component_setDimensForComponentAndContent(containingComponent, inputs.timestamp());

        Map<UUID, Vertex> contentLocs = getFromData(containingComponent.data(), CONTENT_LOCS);
        UUID componentId = getFromData(inputs.data(), COMPONENT_UUID);
        var unadjustedLoc = contentLocs.get(componentId);
        Vertex originOverrideAdjust =
                getFromData(containingComponent.data(), ORIGIN_OVERRIDE_ADJUST);
        if (originOverrideAdjust != null) {
            return inaugural.soliloquy.tools.valueobjects.Vertex.translate(unadjustedLoc,
                    originOverrideAdjust);
        }
        else {
            return unadjustedLoc;
        }
    }

    private static <T> T getFromComponentDataOrDefault(Component component, String key,
                                                       Supplier<T> getDefault) {
        T val = getFromData(component.data(), key);
        if (val == null) {
            val = getDefault.get();
            component.data().put(key, val);
        }
        return val;
    }
}
