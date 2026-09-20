package inaugural.soliloquy.ui.test.integration.display.components.dialogbox;

import inaugural.soliloquy.io.api.dto.AssetDefinitionsDTO;
import inaugural.soliloquy.io.api.dto.ImageDefinitionDTO;
import inaugural.soliloquy.ui.UIModule;
import inaugural.soliloquy.ui.components.Orientation;
import inaugural.soliloquy.ui.components.button.ButtonDefinition;
import inaugural.soliloquy.ui.readers.content.renderables.RenderableDefinitionReader;
import inaugural.soliloquy.ui.test.integration.display.DisplayTest;
import soliloquy.specs.io.graphics.renderables.Component;

import static inaugural.soliloquy.tools.Tools.centeringOrigin;
import static inaugural.soliloquy.tools.collections.Collections.arrayOf;
import static inaugural.soliloquy.tools.random.Random.randomHighSaturationColor;
import static inaugural.soliloquy.ui.Denormalization.denormHeight;
import static inaugural.soliloquy.ui.Denormalization.denormWidth;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinition.beveledButton;
import static inaugural.soliloquy.ui.components.content.column.ContentColumnDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.content.column.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.dialogbox.DialogBoxDefinition.dialogBox;
import static inaugural.soliloquy.ui.components.dialogbox.DialogBoxMethods.DialogBox_close;
import static inaugural.soliloquy.ui.components.scrollablecontent.ScrollableContentDefinition.scrollableContent;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.NO_MAX_LINE_LENGTH;
import static inaugural.soliloquy.ui.components.textblock.TextBlockDefinition.textBlock;
import static inaugural.soliloquy.ui.test.integration.display.components.scrollbar.ScrollbarVerticalWithArrowButtonsDisplayTest.makeVerticalScrollbarDef;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

public class DialogBoxSimpleTest extends DisplayTest {
    public static void main(String[] args) {
        new DisplayTest().runTest(
                "Dialog box display test",
                new AssetDefinitionsDTO(
                        arrayOf(
                                new ImageDefinitionDTO(BACKGROUND_TEXTURE_RELATIVE_LOCATION, false)
                        ),
                        arrayOf(
                                MERRIWEATHER_DEFINITION_DTO
                        ),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        arrayOf()
                ),
                () -> DisplayTest.runThenClose("Dialog box", 16000),
                DialogBoxSimpleTest::populateTopLevelComponent
        );
    }

    protected static void populateTopLevelComponent(UIModule uiModule,
                                                    Component topLevelComponent) {
        var boxDenormWidth = 0.4f;
        var boxHeight = 0.5f;
        var boxDimens = denormWidth(floatBoxOf(boxDenormWidth, boxHeight));
        var xPadding = 0.025f;
        var yPadding = 0.025f;
        var contentWidth = boxDimens.width() - (xPadding * 2);
        var contentHeight = 0.25f;
        var scrollbarButtonHeight = 0.02f;
        var buttonsSpanHeight = boxHeight - yPadding - contentHeight - yPadding - yPadding;
        var buttonPadding = 0.025f;
        var buttonHeight = (buttonsSpanHeight - buttonPadding) / 2f;
        var scrollbarWidth = 0.0125f;
        var bevelIntensity = 0.5f;
        var bevelPercent = 0.1f;

        var dialogBoxDef = dialogBox(0)
                .withBox(
                        rectangle(boxDimens)
                                .withColor(randomHighSaturationColor(0.5f)),
                        centeringOrigin(boxDimens)
                )
                .withContent(
                        scrollableContent(
                                column(contentWidth)
                                        .withItem(itemOf(textBlock(MERRIWEATHER_ID, 0.025f,
                                                contentWidth,
                                                """
                                                This is the text of the dialog box. It is part of a text block, which may overflow if the text is too long.
                                                
                                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."""
                                        ))),
                                contentHeight,
                                makeVerticalScrollbarDef(
                                        contentHeight - (2f * scrollbarButtonHeight),
                                        scrollbarButtonHeight,
                                        scrollbarWidth
                                )
                        )
                                .hidesScrollbarWhenContentFits(),
                        vertexOf(xPadding, yPadding)
                )
                .withButtonsSpanOrientation(Orientation.VERTICAL)
                .withButtonsSpanRelOrigin(vertexOf(xPadding, yPadding + contentHeight + yPadding))
                .withButtonsSpanUnadjDimens(floatBoxOf(contentWidth, buttonsSpanHeight))
                .withButtonsPadding(buttonPadding)
                .withButtonsSpanScrollbar(
                        makeVerticalScrollbarDef(buttonHeight, scrollbarWidth, scrollbarWidth))
                .withButton(
                        makeDialogBoxButton(
                                bevelPercent,
                                bevelIntensity,
                                buttonHeight,
                                "Option1"
                        )
                )
                .withButton(
                        makeDialogBoxButton(
                                bevelPercent,
                                bevelIntensity,
                                buttonHeight,
                                "Close"
                        ),
                        null,
                        DialogBox_close
                );

        var reader = uiModule.provide(RenderableDefinitionReader.class);

        reader.read(topLevelComponent, dialogBoxDef, timestamp(uiModule));
    }


    private static ButtonDefinition makeDialogBoxButton(float bevelPercent,
                                                        float bevelIntensity,
                                                        float buttonHeight,
                                                        String text) {
        return beveledButton(
                rectangle()
                        .withColor(randomHighSaturationColor())
                        .withTexture(BACKGROUND_TEXTURE_RELATIVE_LOCATION),
                bevelPercent,
                bevelIntensity
        )
                .withTextBlockDef(textBlock(
                        MERRIWEATHER_ID,
                        buttonHeight * 0.5f,
                        NO_MAX_LINE_LENGTH,
                        text
                ))
                .textBlockDefinesRectDimens()
                .withTextBlockPadding(0.025f);
    }
}
