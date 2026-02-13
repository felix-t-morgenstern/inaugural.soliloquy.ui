package inaugural.soliloquy.ui.components.beveledbutton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.Component;

import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockMap;
import static inaugural.soliloquy.tools.valueobjects.Vertex.translateVertex;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods.*;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.BUTTON_RECT_DIMENS;
import static inaugural.soliloquy.ui.components.button.ButtonMethods.IS_PRESSED;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.FloatBox.floatBoxOf;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class BeveledButtonMethodsTests {
    private final UUID BUTTON_UUID = randomUUID();
    private final FloatBox RECT_DIMENS = randomFloatBox();
    private final float BEVEL_PERCENT = randomFloat();
    private final Vertex INNER_TRANSFORMS_TOP_LEFT = vertexOf(
            RECT_DIMENS.width() * BEVEL_PERCENT,
            RECT_DIMENS.height() * BEVEL_PERCENT
    );
    private final Color BEVEL_COLOR_LIT = randomColor();
    private final Color BEVEL_COLOR_UNLIT = randomColor();
    private final float BEVEL_INTENSITY = randomFloatInRange(0f, 1f);
    private final Color BEVEL_LIT_FROM_INTENSITY = new Color(1f, 1f, 1f, BEVEL_INTENSITY);
    private final Color BEVEL_UNLIT_FROM_INTENSITY = new Color(0f, 0f, 0f, BEVEL_INTENSITY);

    private final long TIMESTAMP = randomLong();

    @Mock private Component mockButton;

    @Mock private Function<UUID, Component> mockGetComponent;

    private Map<String, Object> mockButtonData;

    private BeveledButtonMethods methods;

    @BeforeEach
    public void setUp() {
        lenient().when(mockGetComponent.apply(any())).thenReturn(mockButton);

        mockButtonData = generateMockMap(
                pairOf(BUTTON_RECT_DIMENS, RECT_DIMENS),
                pairOf(BEVELED_BUTTON_LAST_TIMESTAMP, TIMESTAMP - 1),
                pairOf(BeveledButtonMethods.BEVEL_PERCENT, BEVEL_PERCENT)
        );

        lenient().when(mockButton.data()).thenReturn(mockButtonData);

        methods = new BeveledButtonMethods(mockGetComponent);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new BeveledButtonMethods(null));
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp_Slot0() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                0,
                BeveledButton_ySlot,
                0
        ));

        var output = methods.BeveledButton_provideVertex(inputs);

        assertEquals(
                vertexOf(
                        RECT_DIMENS.LEFT_X,
                        RECT_DIMENS.TOP_Y
                ),
                output
        );

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(BUTTON_RECT_DIMENS);
        verify(mockButtonData, once()).get(BEVELED_BUTTON_LAST_TIMESTAMP);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_PERCENT);
        verify(mockButtonData, once()).put(BEVELED_BUTTON_LAST_TIMESTAMP, TIMESTAMP);
        verify(mockButtonData, once()).put(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT,
                INNER_TRANSFORMS_TOP_LEFT);
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp_Slot1() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                1,
                BeveledButton_ySlot,
                1
        ));

        var output = methods.BeveledButton_provideVertex(inputs);

        assertEquals(
                vertexOf(
                        RECT_DIMENS.LEFT_X + INNER_TRANSFORMS_TOP_LEFT.X,
                        RECT_DIMENS.TOP_Y + INNER_TRANSFORMS_TOP_LEFT.Y
                ),
                output
        );
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp_Slot2() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                2,
                BeveledButton_ySlot,
                2
        ));

        var output = methods.BeveledButton_provideVertex(inputs);

        assertEquals(
                vertexOf(
                        RECT_DIMENS.RIGHT_X - INNER_TRANSFORMS_TOP_LEFT.X,
                        RECT_DIMENS.BOTTOM_Y - INNER_TRANSFORMS_TOP_LEFT.Y
                ),
                output
        );
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp_Slot3() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                3,
                BeveledButton_ySlot,
                3
        ));

        var output = methods.BeveledButton_provideVertex(inputs);

        assertEquals(
                vertexOf(
                        RECT_DIMENS.RIGHT_X,
                        RECT_DIMENS.BOTTOM_Y
                ),
                output
        );
    }

    @Test
    public void testProvideVertex_BeveledButtonWithSameTimestampAsPrev() {
        var inputs = providerInputs(TIMESTAMP - 1, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                0,
                BeveledButton_ySlot,
                0
        ));

        var output = methods.BeveledButton_provideVertex(inputs);

        assertEquals(
                vertexOf(
                        RECT_DIMENS.LEFT_X,
                        RECT_DIMENS.TOP_Y
                ),
                output
        );

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(BUTTON_RECT_DIMENS);
        verify(mockButtonData, once()).get(BEVELED_BUTTON_LAST_TIMESTAMP);
        verify(mockButtonData, never()).get(BeveledButtonMethods.BEVEL_PERCENT);
        verify(mockButtonData, never()).put(BEVELED_BUTTON_LAST_TIMESTAMP, TIMESTAMP);
        verify(mockButtonData, never()).put(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT,
                INNER_TRANSFORMS_TOP_LEFT);
    }

    @Test
    public void testBeveledButton_provideBoxWithNewTimestamp_Slots0And1() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                0,
                BeveledButton_provideBox_xSlotRight,
                1,
                BeveledButton_ySlot,
                0
        ));

        var output = methods.BeveledButton_provideBox(inputs);

        assertEquals(
                floatBoxOf(
                        RECT_DIMENS.topLeft(),
                        translateVertex(RECT_DIMENS.topLeft(), INNER_TRANSFORMS_TOP_LEFT)
                ),
                output
        );

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(BUTTON_RECT_DIMENS);
        verify(mockButtonData, once()).get(BEVELED_BUTTON_LAST_TIMESTAMP);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_PERCENT);
        verify(mockButtonData, once()).put(BEVELED_BUTTON_LAST_TIMESTAMP, TIMESTAMP);
        verify(mockButtonData, once()).put(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT,
                INNER_TRANSFORMS_TOP_LEFT);
    }

    @Test
    public void testBeveledButton_provideBoxWithNewTimestamp_Slots2and3() {
        var inputs = providerInputs(TIMESTAMP, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                2,
                BeveledButton_provideBox_xSlotRight,
                3,
                BeveledButton_ySlot,
                2
        ));

        var output = methods.BeveledButton_provideBox(inputs);

        assertEquals(
                floatBoxOf(
                        vertexOf(
                                RECT_DIMENS.RIGHT_X - INNER_TRANSFORMS_TOP_LEFT.X,
                                RECT_DIMENS.BOTTOM_Y - INNER_TRANSFORMS_TOP_LEFT.Y
                        ),
                        RECT_DIMENS.bottomRight()
                ),
                output
        );
    }

    @Test
    public void testBeveledButton_provideBoxWithSameTimestampAsPrev() {
        when(mockButtonData.get(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT))
                .thenReturn(INNER_TRANSFORMS_TOP_LEFT);
        var inputs = providerInputs(TIMESTAMP - 1, null, mapOf(
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_xSlot,
                0,
                BeveledButton_provideBox_xSlotRight,
                1,
                BeveledButton_ySlot,
                0
        ));

        var output = methods.BeveledButton_provideBox(inputs);

        assertEquals(
                floatBoxOf(
                        RECT_DIMENS.topLeft(),
                        translateVertex(RECT_DIMENS.topLeft(), INNER_TRANSFORMS_TOP_LEFT)
                ),
                output
        );

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButton, atLeastOnce()).data();
        verify(mockButtonData, once()).get(BUTTON_RECT_DIMENS);
        verify(mockButtonData, once()).get(BEVELED_BUTTON_LAST_TIMESTAMP);
        verify(mockButtonData, never()).get(BeveledButtonMethods.BEVEL_PERCENT);
        verify(mockButtonData, never()).put(BEVELED_BUTTON_LAST_TIMESTAMP, TIMESTAMP);
        verify(mockButtonData, never()).put(BEVEL_LAST_INNER_TRANSFORMS_TOP_LEFT,
                INNER_TRANSFORMS_TOP_LEFT);
    }

    // (I'm not testing for invalid args here, because if any developer is calling these methods
    // directly, they're already behaving in unsupported ways)

    @Test
    public void testBeveledButton_provideColorFromPresetLitByDefaultAndUnpressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(false);
        when(mockButtonData.get(BeveledButtonMethods.BEVEL_COLOR_LIT)).thenReturn(BEVEL_COLOR_LIT);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                true,
                COMPONENT_UUID,
                BUTTON_UUID
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_COLOR_LIT, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_LIT);
    }

    @Test
    public void testBeveledButton_provideColorFromPresetLitByDefaultAndPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);
        when(mockButtonData.get(BeveledButtonMethods.BEVEL_COLOR_UNLIT)).thenReturn(BEVEL_COLOR_UNLIT);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                true,
                COMPONENT_UUID,
                BUTTON_UUID
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_COLOR_UNLIT, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_UNLIT);
    }

    @Test
    public void testBeveledButton_provideColorFromPresetUnlitByDefaultAndUnpressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(false);
        when(mockButtonData.get(BeveledButtonMethods.BEVEL_COLOR_UNLIT)).thenReturn(BEVEL_COLOR_UNLIT);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                false,
                COMPONENT_UUID,
                BUTTON_UUID
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_COLOR_UNLIT, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_UNLIT);
    }

    @Test
    public void testBeveledButton_provideColorFromPresetUnlitByDefaultAndPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);
        when(mockButtonData.get(BeveledButtonMethods.BEVEL_COLOR_LIT)).thenReturn(BEVEL_COLOR_LIT);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                false,
                COMPONENT_UUID,
                BUTTON_UUID
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_COLOR_LIT, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_LIT);
    }

    @Test
    public void testBeveledButton_provideColorFromIntensityLitByDefaultAndUnpressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(false);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                true,
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_provideColor_bevelIntensity,
                BEVEL_INTENSITY
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_LIT_FROM_INTENSITY, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_LIT);
        verify(mockButtonData, once())
                .put(BeveledButtonMethods.BEVEL_COLOR_LIT, BEVEL_LIT_FROM_INTENSITY);
    }

    @Test
    public void testBeveledButton_provideColorFromIntensityLitByDefaultAndPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                true,
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_provideColor_bevelIntensity,
                BEVEL_INTENSITY
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_UNLIT_FROM_INTENSITY, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_UNLIT);
        verify(mockButtonData, once())
                .put(BeveledButtonMethods.BEVEL_COLOR_UNLIT, BEVEL_UNLIT_FROM_INTENSITY);
    }

    @Test
    public void testBeveledButton_provideColorFromIntensityUnlitByDefaultAndUnpressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(false);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                false,
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_provideColor_bevelIntensity,
                BEVEL_INTENSITY
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_UNLIT_FROM_INTENSITY, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_UNLIT);
        verify(mockButtonData, once())
                .put(BeveledButtonMethods.BEVEL_COLOR_UNLIT, BEVEL_UNLIT_FROM_INTENSITY);
    }

    @Test
    public void testBeveledButton_provideColorFromIntensityUnlitByDefaultAndPressed() {
        when(mockButtonData.get(IS_PRESSED)).thenReturn(true);

        var inputs = providerInputs(TIMESTAMP, mapOf(
                BeveledButton_provideColor_isLitByDefault,
                false,
                COMPONENT_UUID,
                BUTTON_UUID,
                BeveledButton_provideColor_bevelIntensity,
                BEVEL_INTENSITY
        ));

        var output = methods.BeveledButton_provideColor(inputs);

        assertEquals(BEVEL_LIT_FROM_INTENSITY, output);

        verify(mockGetComponent, once()).apply(BUTTON_UUID);
        verify(mockButtonData, once()).get(IS_PRESSED);
        verify(mockButtonData, once()).get(BeveledButtonMethods.BEVEL_COLOR_LIT);
        verify(mockButtonData, once())
                .put(BeveledButtonMethods.BEVEL_COLOR_LIT, BEVEL_LIT_FROM_INTENSITY);
    }
}
