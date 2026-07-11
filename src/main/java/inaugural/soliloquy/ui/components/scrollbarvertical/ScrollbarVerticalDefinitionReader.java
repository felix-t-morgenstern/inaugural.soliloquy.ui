package inaugural.soliloquy.ui.components.scrollbarvertical;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.AbstractCustomComponentDefinitionReader;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.components.button.ButtonDefinitionReader;
import inaugural.soliloquy.ui.readers.content.renderables.RectangleRenderableDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.UUID;

import static inaugural.soliloquy.io.api.Constants.LEFT_MOUSE_BUTTON;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.scrollbarvertical.ScrollbarVerticalMethods.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ScrollbarVerticalDefinitionReader extends
        AbstractCustomComponentDefinitionReader<ScrollbarVerticalDefinition> {
    private final ButtonDefinitionReader BUTTON_DEF_READER;
    private final RectangleRenderableDefinitionReader RECT_DEF_READER;

    public ScrollbarVerticalDefinitionReader(ButtonDefinitionReader buttonDefReader,
                                             RectangleRenderableDefinitionReader rectDefReader,
                                             ProviderDefinitionReader providerDefReader) {
        super(providerDefReader);
        BUTTON_DEF_READER = Check.ifNull(buttonDefReader, "buttonDefReader");
        RECT_DEF_READER = Check.ifNull(rectDefReader, "rectDefReader");
    }

    @Override
    public ComponentDefinition read(ScrollbarVerticalDefinition def, long timestamp) {
        var scrollbarData = Collections.<String, Object>mapOf();

        def.TRACK_DEF.onPress(mapOf(LEFT_MOUSE_BUTTON, ScrollbarVertical_trackClick));
        var track = RECT_DEF_READER.read(null, def.TRACK_DEF, timestamp);
        var unadjTrackDimens = track.getRenderingDimensionsProvider();
        track.setRenderingDimensionsProvider(PROVIDER_DEF_READER.read(
                functionalProvider(
                        ScrollbarVertical_provideAdjTrackDimens,
                        FloatBox.class
                )
                        .withData(mapOf(COMPONENT_UUID, def.UUID)),
                timestamp
        ));
        scrollbarData.put(TRACK_UNADJ_DIMENS_PROVIDER, unadjTrackDimens);

        var thumb = BUTTON_DEF_READER.read(def.THUMB_DEF, timestamp);

        scrollbarData.put(
                COMPONENT_ORIGIN_PROVIDER,
                providerOrReadDef(def.ORIGIN_PROVIDER, def.ORIGIN_PROVIDER_DEF, timestamp)
        );

        var scrollbarComponentDef = component(def.z, def.UUID)
                .withBindings(
                        false,
                        0,
                        binding()
                )
                .withData(scrollbarData)
                .withPrerenderHook(ScrollbarVertical_setDimens)
                .withContent(thumb)
                .withPrereadContent(track);

        if (def.topArrowDef != null) {
            scrollbarComponentDef.withContent(
                    prepareButton(def.UUID, def.topArrowDef, timestamp),
                    prepareButton(def.UUID, def.bottomArrowDef, timestamp)
            );
        }

        return scrollbarComponentDef;
    }

    private ComponentDefinition prepareButton(UUID scrollbarUuid,
                                              ButtonDefinition buttonDef,
                                              long timestamp) {
        return BUTTON_DEF_READER.read(
                buttonDef
                        .onReleaseAfterPress(ScrollbarVertical_topArrowClick)
                        .withData(mapOf(
                                COMPONENT_ORIGIN_PROVIDER,
                                PROVIDER_DEF_READER.read(
                                        functionalProvider(
                                                ScrollbarVertical_provideAdjTopArrowOrigin,
                                                Vertex.class
                                        )
                                                .withData(mapOf(COMPONENT_UUID, scrollbarUuid)),
                                        timestamp
                                )
                        )),
                timestamp
        );
    }
}
