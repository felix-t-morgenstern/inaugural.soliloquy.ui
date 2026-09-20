package inaugural.soliloquy.ui.components.dialogbox;

import inaugural.soliloquy.ui.components.Orientation;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.components.scrollablecontent.ScrollableContentDefinition;
import inaugural.soliloquy.ui.components.scrollbar.ScrollbarDefinition;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.List;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

public class DialogBoxDefinition extends AbstractContentDefinition {
    public final List<Pair<ButtonDefinition, Pair<String, String>>> BUTTON_DEFS_AND_HOOK_IDS;

    public RectangleRenderableDefinition boxDef;
    public ProviderAtTime<Vertex> boxOriginProvider;
    public AbstractProviderDefinition<Vertex> boxOriginProviderDef;
    public ScrollableContentDefinition contentDef;
    public ProviderAtTime<Vertex> contentRelativeOriginProvider;
    public AbstractProviderDefinition<Vertex> contentRelativeOriginProviderDef;
    public ProviderAtTime<Vertex> buttonsSpanRelOriginProvider;
    public AbstractProviderDefinition<Vertex> buttonsSpanRelOriginProviderDef;
    public FloatBox buttonsSpanUnadjDimens;
    public Orientation buttonsSpanOrientation;
    public ScrollbarDefinition buttonsSpanScrollbarDef;
    public float buttonPadding;

    private DialogBoxDefinition(int z) {
        super(z, randomUUID());
        BUTTON_DEFS_AND_HOOK_IDS = listOf();
    }

    public static DialogBoxDefinition dialogBox(int z) {
        return new DialogBoxDefinition(z);
    }

    public DialogBoxDefinition withBox(RectangleRenderableDefinition boxDef,
                                       ProviderAtTime<Vertex> boxOriginProvider) {
        this.boxDef = boxDef;
        this.boxOriginProvider = boxOriginProvider;

        return this;
    }

    public DialogBoxDefinition withBox(RectangleRenderableDefinition boxDef,
                                       AbstractProviderDefinition<Vertex> boxOriginProviderDef) {
        this.boxDef = boxDef;
        this.boxOriginProviderDef = boxOriginProviderDef;

        return this;
    }

    public DialogBoxDefinition withBox(RectangleRenderableDefinition boxDef, Vertex boxOrigin) {
        this.boxDef = boxDef;
        this.boxOriginProviderDef = staticVal(boxOrigin);

        return this;
    }

    public DialogBoxDefinition withContent(ScrollableContentDefinition contentDef,
                                           ProviderAtTime<Vertex> contentRelativeOriginProvider) {
        this.contentDef = contentDef;
        this.contentRelativeOriginProvider = contentRelativeOriginProvider;

        return this;
    }

    public DialogBoxDefinition withContent(
            ScrollableContentDefinition contentDef,
            AbstractProviderDefinition<Vertex> contentRelativeOriginProviderDef
    ) {
        this.contentDef = contentDef;
        this.contentRelativeOriginProviderDef = contentRelativeOriginProviderDef;

        return this;
    }

    public DialogBoxDefinition withContent(ScrollableContentDefinition contentDef,
                                           Vertex contentRelativeOrigin) {
        this.contentDef = contentDef;
        this.contentRelativeOriginProviderDef = staticVal(contentRelativeOrigin);

        return this;
    }

    public DialogBoxDefinition withButtonsSpanRelOriginProvider(
            ProviderAtTime<Vertex> buttonsSpanRelOriginProvider
    ) {
        this.buttonsSpanRelOriginProvider = buttonsSpanRelOriginProvider;

        return this;
    }

    public DialogBoxDefinition withButtonsSpanRelOriginProviderDef(
            AbstractProviderDefinition<Vertex> buttonsSpanRelOriginProviderDef
    ) {
        this.buttonsSpanRelOriginProviderDef = buttonsSpanRelOriginProviderDef;

        return this;
    }

    public DialogBoxDefinition withButtonsSpanRelOrigin(Vertex buttonsSpanRelOrigin) {
        this.buttonsSpanRelOriginProviderDef = staticVal(buttonsSpanRelOrigin);

        return this;
    }

    public DialogBoxDefinition withButtonsSpanUnadjDimens(FloatBox buttonsSpanUnadjDimens) {
        this.buttonsSpanUnadjDimens = buttonsSpanUnadjDimens;

        return this;
    }

    /**
     * @param orientation VERTICAL if the span will be a ContentColumn; else, HORIZONTAL for a
     *                    ContentRow
     */
    public DialogBoxDefinition withButtonsSpanOrientation(Orientation orientation) {
        buttonsSpanOrientation = orientation;

        return this;
    }

    public DialogBoxDefinition withButtonsPadding(float buttonPadding) {
        this.buttonPadding = buttonPadding;

        return this;
    }

    public DialogBoxDefinition withButtonsSpanScrollbar(ScrollbarDefinition buttonSpanScrollbarDef) {
        this.buttonsSpanScrollbarDef = buttonSpanScrollbarDef;

        return this;
    }

    /**
     * This method allows Button actions to be wrapped by hooks, which can insert UI-specific
     * behavior before or after the Button action executes
     */
    public DialogBoxDefinition withButton(ButtonDefinition buttonDef,
                                          String preActionHookId,
                                          String postActionHookId) {
        BUTTON_DEFS_AND_HOOK_IDS.add(pairOf(buttonDef, pairOf(preActionHookId, postActionHookId)));

        return this;
    }

    public DialogBoxDefinition withButton(ButtonDefinition buttonDef) {
        BUTTON_DEFS_AND_HOOK_IDS.add(pairOf(buttonDef, null));

        return this;
    }
}
