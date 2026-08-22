package inaugural.soliloquy.ui.components.scrollbar.vertical;

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
import static inaugural.soliloquy.ui.components.scrollbar.vertical.ScrollbarVerticalMethods.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ScrollbarVerticalDefinitionReader extends
        AbstractCustomComponentDefinitionReader<ScrollbarVerticalDefinition> {
    private final static int TRACK_AND_BUTTON_Z = 0;
    private final static int THUMB_Z = 1;

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

        def.TRACK_DEF.z = TRACK_AND_BUTTON_Z;
        def.TRACK_DEF.onPress(mapOf(LEFT_MOUSE_BUTTON, ScrollbarVertical_trackPress));
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

        var thumbOriginProvider = PROVIDER_DEF_READER.read(
                functionalProvider(
                        ScrollbarVertical_thumbOrigin,
                        Vertex.class
                )
                        .withData(mapOf(
                                COMPONENT_UUID,
                                def.UUID
                        )),
                timestamp
        );
        def.THUMB_DEF.z = THUMB_Z;
        def.THUMB_DEF
                .onPress(ScrollbarVertical_thumbPress)
                .withData(mapOf(
                        COMPONENT_ORIGIN_PROVIDER,
                        thumbOriginProvider
                ));
        var thumb = BUTTON_DEF_READER.read(def.THUMB_DEF, timestamp);

        scrollbarData.putAll(mapOf(
                COMPONENT_ORIGIN_PROVIDER,
                providerOrReadDef(def.ORIGIN_PROVIDER, def.ORIGIN_PROVIDER_DEF, timestamp),
                THUMB_UUID,
                def.THUMB_DEF.UUID,
                THUMB_IS_PRESSED,
                false
        ));

        var scrollbarComponentDef = component(def.z, def.UUID)
                .withBindings(
                        false,
                        0,
                        binding()
                )
                .withData(scrollbarData)
                .withPrerenderHook(ScrollbarVertical_getDimens)
                .withContent(thumb)
                .withPrereadContent(track);

        if (def.topArrowDef != null) {
            var thumbMoveAmountProvider =
                    PROVIDER_DEF_READER.read(def.THUMB_MOVE_AMOUNT_PROVIDER_DEF, timestamp);
            scrollbarComponentDef.withContent(
                    prepareArrowButton(
                            def.UUID,
                            def.topArrowDef,
                            ScrollbarVertical_topArrowPress,
                            ScrollbarVertical_topArrowReleaseAfterPress,
                            ScrollbarVertical_provideAdjTopArrowOrigin,
                            timestamp
                    ),
                    prepareArrowButton(
                            def.UUID,
                            def.bottomArrowDef,
                            ScrollbarVertical_bottomArrowPress,
                            ScrollbarVertical_bottomArrowReleaseAfterPress,
                            ScrollbarVertical_provideAdjBottomArrowOrigin,
                            timestamp
                    )
            );
            scrollbarComponentDef.withData(mapOf(
                    THUMB_MOVE_AMOUNT_PROVIDER,
                    thumbMoveAmountProvider,
                    THUMB_INCREMENT_MOVE_DUR,
                    def.THUMB_INCREMENT_MOVE_DUR,
                    ARROW_HOLD_START_THRESHOLD,
                    def.arrowHoldStartThreshold,
                    MIN_TIME_BETWEEN_THUMB_MOVEMENTS_WHILE_HELD,
                    def.minDurBetweenMovesWhileArrowHeld,
                    ARROW_HELD_REPEATED_TIME_EXPONENT,
                    def.arrowHeldRepeatedTimeExponent,
                    ARROW_HELD_REPEATED_TIME_EXPONENT_FACTOR,
                    def.arrowHeldRepeatedTimeExponentFactor,
                    TOP_ARROW_UUID,
                    def.topArrowDef.UUID,
                    BOTTOM_ARROW_UUID,
                    def.bottomArrowDef.UUID
            ));
        }

        return scrollbarComponentDef;
    }

    private ComponentDefinition prepareArrowButton(UUID scrollbarUuid,
                                                   ButtonDefinition buttonDef,
                                                   String pressFunctionId,
                                                   String releaseAfterPressFunctionId,
                                                   String originFunctionId,
                                                   long timestamp) {
        buttonDef.z = TRACK_AND_BUTTON_Z;
        return BUTTON_DEF_READER.read(
                buttonDef
                        .onPress(pressFunctionId)
                        .onReleaseAfterPress(releaseAfterPressFunctionId)
                        .withData(mapOf(
                                COMPONENT_ORIGIN_PROVIDER,
                                PROVIDER_DEF_READER.read(
                                        functionalProvider(originFunctionId, Vertex.class)
                                                .withData(mapOf(COMPONENT_UUID, scrollbarUuid)),
                                        timestamp
                                )
                        )),
                timestamp
        );
    }
}
