package inaugural.soliloquy.ui.components.dialogbox;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.AbstractCustomComponentDefinitionReader;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.components.content.column.ContentColumnDefinition;
import inaugural.soliloquy.ui.components.content.row.ContentRowDefinition;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.Map;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.Constants.COMPONENT_ORIGIN_PROVIDER;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.Orientation.VERTICAL;
import static inaugural.soliloquy.ui.components.content.column.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.content.row.ContentRowDefinition.row;
import static inaugural.soliloquy.ui.components.dialogbox.DialogBoxMethods.*;
import static inaugural.soliloquy.ui.components.scrollablecontent.ScrollableContentDefinition.scrollableContent;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class DialogBoxDefinitionReader extends
        AbstractCustomComponentDefinitionReader<DialogBoxDefinition> {
    private final static int BOX_Z = 0;
    private final static int CONTENT_Z = 1;
    private final static int BUTTONS_Z = 2;

    public DialogBoxDefinitionReader(ProviderDefinitionReader providerDefReader) {
        super(providerDefReader);
    }

    @Override
    public ComponentDefinition read(DialogBoxDefinition def, long timestamp) {
        var componentOriginProvider = providerOrReadDef(
                def.boxOriginProvider,
                def.boxOriginProviderDef,
                timestamp
        );

        var functionalProviderData = Collections.<String, Object>mapOf(COMPONENT_UUID, def.UUID);

        var boxUnadjDimens = providerOrReadDef(
                def.boxDef.dimensProvider,
                def.boxDef.dimensProviderDef,
                timestamp
        );
        def.boxDef.dimensProvider = PROVIDER_DEF_READER.read(
                functionalProvider(DialogBox_boxDimens, FloatBox.class)
                        .withData(functionalProviderData),
                timestamp
        );
        def.boxDef.z = BOX_Z;

        var contentRelOriginProvider = providerOrReadDef(
                def.contentRelativeOriginProvider,
                def.contentRelativeOriginProviderDef,
                timestamp
        );

        def.contentDef.SPAN.renderingLocProvider = PROVIDER_DEF_READER.read(
                functionalProvider(DialogBox_contentOrigin, Vertex.class)
                        .withData(functionalProviderData),
                timestamp
        );
        def.contentDef.z = CONTENT_Z;

        var buttonsSpanRelOriginProvider = providerOrReadDef(
                def.buttonsSpanRelOriginProvider,
                def.buttonsSpanRelOriginProviderDef,
                timestamp
        );

        var buttonHooksAndActions = Collections.<UUID, String[]>mapOf();
        AbstractContentDefinition buttonsSpanDef;
        if (def.buttonsSpanOrientation == VERTICAL) {
            var columnItems = Collections.<ContentColumnDefinition.Item>listOf();
            for (var i = 0; i < def.BUTTON_DEFS_AND_HOOK_IDS.size(); i++) {
                var buttonDefAndHookIds = def.BUTTON_DEFS_AND_HOOK_IDS.get(i);
                registerButtonAndHooks(buttonDefAndHookIds, buttonHooksAndActions);
                columnItems.add(ContentColumnDefinition.Item.itemOf(buttonDefAndHookIds.FIRST));
                if (i < def.BUTTON_DEFS_AND_HOOK_IDS.size() - 1) {
                    columnItems.add(ContentColumnDefinition.Item.space(def.buttonPadding));
                }
            }
            buttonsSpanDef = scrollableContent(
                    column(
                            functionalProvider(DialogBox_buttonsSpanOrigin, Vertex.class)
                                    .withData(functionalProviderData),
                            def.buttonsSpanUnadjDimens.width(),
                            0
                    )
                            .withItems(columnItems.toArray(ContentColumnDefinition.Item[]::new)),
                    def.buttonsSpanUnadjDimens.height(),
                    def.buttonsSpanScrollbarDef,
                    BUTTONS_Z
            )
                    .hidesScrollbarWhenContentFits();
        }
        else {
            var rowItems = Collections.<ContentRowDefinition.Item>listOf();
            for (var i = 0; i < def.BUTTON_DEFS_AND_HOOK_IDS.size(); i++) {
                var buttonDefAndHookIds = def.BUTTON_DEFS_AND_HOOK_IDS.get(i);
                registerButtonAndHooks(buttonDefAndHookIds, buttonHooksAndActions);
                rowItems.add(ContentRowDefinition.Item.itemOf(buttonDefAndHookIds.FIRST));
                if (i < def.BUTTON_DEFS_AND_HOOK_IDS.size() - 1) {
                    rowItems.add(ContentRowDefinition.Item.space(def.buttonPadding));
                }
            }
            buttonsSpanDef = scrollableContent(
                    row(
                            functionalProvider(DialogBox_buttonsSpanOrigin, Vertex.class)
                                    .withData(functionalProviderData),
                            def.buttonsSpanUnadjDimens.height(),
                            0
                    )
                            .withItems(rowItems.toArray(ContentRowDefinition.Item[]::new)),
                    def.buttonsSpanUnadjDimens.width(),
                    def.buttonsSpanScrollbarDef,
                    BUTTONS_Z
            );
        }

        return component(def.z, def.UUID)
                .withContent(
                        def.boxDef,
                        def.contentDef,
                        buttonsSpanDef
                )
                .withData(mapOf(
                        COMPONENT_ORIGIN_PROVIDER,
                        componentOriginProvider,
                        BOX_UNADJ_DIMENS_PROVIDER,
                        boxUnadjDimens,
                        CONTENT_REL_ORIGIN_PROVIDER,
                        contentRelOriginProvider,
                        BUTTONS_SPAN_REL_ORIGIN_PROVIDER,
                        buttonsSpanRelOriginProvider,
                        BUTTON_HOOKS_AND_ACTIONS,
                        buttonHooksAndActions
                ))
                .withDimensions(functionalProvider(DialogBox_boxDimens, FloatBox.class)
                        .withData(functionalProviderData))
                .withUnadjDimensions(functionalProvider(DialogBox_boxUnadjDimens, FloatBox.class)
                        .withData(functionalProviderData));
    }

    private void registerButtonAndHooks(
            Pair<ButtonDefinition, Pair<String, String>> buttonDefAndHookIds,
            Map<UUID, String[]> buttonHooksAndActions
    ) {
        if (buttonDefAndHookIds.SECOND != null) {
            if (buttonDefAndHookIds.SECOND.FIRST != null ||
                    buttonDefAndHookIds.SECOND.SECOND != null) {
                buttonHooksAndActions.put(
                        buttonDefAndHookIds.FIRST.UUID,
                        arrayOf(
                                buttonDefAndHookIds.SECOND.FIRST,
                                buttonDefAndHookIds.FIRST.onReleaseAfterPressId,
                                buttonDefAndHookIds.SECOND.SECOND
                        )
                );
                buttonDefAndHookIds.FIRST.onReleaseAfterPressId = DialogBox_callHookForButtonAction;
            }
        }
    }
}
