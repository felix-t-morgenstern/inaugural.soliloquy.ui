package inaugural.soliloquy.ui.components.scrollablecontent;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.AbstractCustomComponentDefinitionReader;
import inaugural.soliloquy.ui.components.Orientation;
import inaugural.soliloquy.ui.components.content.column.ContentColumnDefinition;
import inaugural.soliloquy.ui.components.content.column.ContentColumnDefinitionReader;
import inaugural.soliloquy.ui.components.content.row.ContentRowDefinition;
import inaugural.soliloquy.ui.components.content.row.ContentRowDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import static inaugural.soliloquy.tools.Tools.supplyIfNull;
import static inaugural.soliloquy.tools.collections.Collections.getFromData;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.Orientation.HORIZONTAL;
import static inaugural.soliloquy.ui.components.Orientation.VERTICAL;
import static inaugural.soliloquy.ui.components.scrollablecontent.ScrollableContentMethods.*;
import static org.lwjgl.glfw.GLFW.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ScrollableContentDefinitionReader extends
        AbstractCustomComponentDefinitionReader<ScrollableContentDefinition> {
    private static final int SPAN_Z = 0;
    private static final int SCROLLBAR_Z = 1;
    private static final int SCROLL_WHEEL_CAPTURE_Z = 2;

    private final ContentColumnDefinitionReader COL_DEF_READER;
    private final ContentRowDefinitionReader ROW_DEF_READER;

    public ScrollableContentDefinitionReader(ProviderDefinitionReader providerDefReader,
                                             ContentColumnDefinitionReader colDefReader,
                                             ContentRowDefinitionReader rowDefReader) {
        super(providerDefReader);
        COL_DEF_READER = Check.ifNull(colDefReader, "colDefReader");
        ROW_DEF_READER = Check.ifNull(rowDefReader, "rowDefReader");
    }

    public ComponentDefinition read(ScrollableContentDefinition def, long timestamp) {
        var scrollableContentOriginProvider = supplyIfNull(def.ORIGIN_PROVIDER,
                () -> PROVIDER_DEF_READER.read(def.ORIGIN_PROVIDER_DEF, timestamp));

        def.SPAN.z = SPAN_Z;
        ComponentDefinition spanComponentDef;
        Orientation spanOrientation;

        var innerProviderData = Collections.<String, Object>mapOf(COMPONENT_UUID, def.UUID);

        def.SPAN.renderingLocDef = functionalProvider(ScrollableContent_spanOrigin, Vertex.class)
                .withData(innerProviderData);
        if (def.SPAN instanceof ContentColumnDefinition colDef) {
            spanComponentDef = COL_DEF_READER.read(colDef, timestamp);
            spanOrientation = VERTICAL;
        }
        else {
            spanComponentDef = ROW_DEF_READER.read((ContentRowDefinition) def.SPAN, timestamp);
            spanOrientation = HORIZONTAL;
        }
        spanComponentDef.renderingBoundariesProvider = PROVIDER_DEF_READER.read(
                functionalProvider(ScrollableContent_spanRenderingDimens, FloatBox.class)
                        .withData(innerProviderData),
                timestamp
        );

        def.SCROLLBAR.z = SCROLLBAR_Z;
        def.SCROLLBAR.originProvider = PROVIDER_DEF_READER.read(
                functionalProvider(ScrollableContent_scrollbarOrigin, Vertex.class)
                        .withData(innerProviderData),
                timestamp
        );

        // TODO: Leave this be until scroll wheel capture has been implemented in IO
//        var scrollWheelCapture = rectangle(
//                functionalProvider(ScrollableContent_scrollWheelCaptureDimens, FloatBox.class)
//                        .withData(mapOf(
//                                SCROLLBAR_UUID,
//                                def.SCROLLBAR.UUID,
//                                SPAN_UUID,
//                                spanComponentDef.UUID
//                        )),
//                SCROLL_WHEEL_CAPTURE_Z
//        );

        var scrollableWindowLengthProvider = supplyIfNull(def.SCROLLABLE_WINDOW_LENGTH_PROVIDER,
                () -> PROVIDER_DEF_READER.read(def.SCROLLABLE_WINDOW_LENGTH_PROVIDER_DEF,
                        timestamp));

        var scrollableContentComponentDef = component(def.z, def.UUID)
                .withDimensions(
                        functionalProvider(ScrollableContent_encompassingDimens, FloatBox.class)
                                .withData(mapOf(
                                        SPAN_UUID,
                                        def.SPAN.UUID,
                                        SCROLLBAR_UUID,
                                        def.SCROLLBAR.UUID
                                ))
                )
                .withContent(
                        spanComponentDef,
                        def.SCROLLBAR
                )
                .withData(mapOf(
                        COMPONENT_ORIGIN_PROVIDER,
                        scrollableContentOriginProvider,
                        SPAN_UUID,
                        def.SPAN.UUID,
                        SCROLLBAR_UUID,
                        def.SCROLLBAR.UUID,
                        SPAN_LENGTH,
                        def.SPAN.LENGTH,
                        SCROLLABLE_WINDOW_LENGTH_PROVIDER,
                        scrollableWindowLengthProvider,
                        ORIENTATION,
                        spanOrientation,
                        SCROLLBAR_PADDING,
                        def.scrollbarPadding
                ))
                .withKeyBindings(
                        false,
                        0,
                        binding(GLFW_KEY_UP, GLFW_KEY_DOWN, GLFW_KEY_LEFT, GLFW_KEY_RIGHT)
                                .onRelease(ScrollableContent_pressArrowKey)
                );

        return scrollableContentComponentDef;
    }
}
