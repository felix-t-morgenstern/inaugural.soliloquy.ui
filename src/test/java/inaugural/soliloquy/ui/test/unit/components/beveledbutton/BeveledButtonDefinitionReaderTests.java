package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.components.button.ButtonDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import inaugural.soliloquy.ui.test.unit.components.FunctionalProviderDefMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Coordinate2d;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;
import soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition;
import soliloquy.specs.ui.definitions.content.TriangleRenderableDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.List;
import java.util.Map;

import static inaugural.soliloquy.tools.collections.Collections.listInts;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.ui.components.ComponentMethods.COMPONENT_UUID;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonDefinition.beveledButton;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Coordinate2d.coordinate2dOf;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;

@ExtendWith(MockitoExtension.class)
public class BeveledButtonDefinitionReaderTests {
    private final float BEVEL_DIMENS_PERCENT = randomFloat();
    private final float BEVEL_INTENSITY = randomFloat();
    private final int Z = randomInt();
    private final long TIMESTAMP = randomLong();

    private static final int RECT_Z = 0;
    private static final int BEVEL_Z = 3;

    private final static String BeveledButton_rectDimensProvider =
            "BeveledButton_rectDimensProvider";
    private final static String BeveledButton_bevelPercent = "BeveledButton_bevelPercent";
    private final static String BeveledButton_xSlot = "BeveledButton_xSlot";
    private final static String BeveledButton_ySlot = "BeveledButton_ySlot";
    private final static String BeveledButton_provideBox_xSlotRight =
            "BeveledButton_provideBox_xSlotRight";
    private final static String BeveledButton_provideColor_bevelIntensity =
            "BeveledButton_provideColor_bevelIntensity";
    private final static String BeveledButton_provideColor_isLitByDefault =
            "BeveledButton_provideColor_isLitByDefault";

    @Mock private ButtonDefinitionReader mockButtonDefinitionReader;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @Mock private Map<String, Object> mockComponentDefinitionData;
    @Mock private ProviderAtTime<FloatBox> mockRectDimens;

    private ComponentDefinition componentDefinition;

    private BeveledButtonDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        var rectDefinition = rectangle(mockRectDimens, RECT_Z);

        componentDefinition = component(Z)
                .withData(mockComponentDefinitionData)
                .withContent(rectDefinition);

        lenient().when(mockButtonDefinitionReader.read(any(), anyLong()))
                .thenReturn(componentDefinition);

        reader = new BeveledButtonDefinitionReader(mockButtonDefinitionReader,
                mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new BeveledButtonDefinitionReader(null, mockProviderDefinitionReader));
        assertThrows(IllegalArgumentException.class,
                () -> new BeveledButtonDefinitionReader(mockButtonDefinitionReader, null));
    }

    @Test
    public void testRead() {
        var bevelLastTimestampDataKey = "BEVEL_LAST_TIMESTAMP";
        @SuppressWarnings("rawtypes") var providersRead = Collections.<ProviderAtTime>listOf();
        when(mockProviderDefinitionReader.read(any(), anyLong())).thenAnswer(_ -> {
            var provider = mock(ProviderAtTime.class);
            providersRead.add(provider);
            return provider;
        });
        var slots = listInts(0, 1, 2, 3);
        @SuppressWarnings("unchecked") BeveledButtonDefinition definition = beveledButton(
                mock(AbstractProviderDefinition.class),
                BEVEL_DIMENS_PERCENT,
                BEVEL_INTENSITY,
                Z
        );

        var output = reader.read(definition, TIMESTAMP);

        assertSame(componentDefinition, output);

        assertEquals(9, output.CONTENT.size());
        assertEquals(22, providersRead.size());

        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> triangleMatch(c, providersRead, coordinate2dOf(0, 0),
                        coordinate2dOf(1, 0), coordinate2dOf(1, 1), 20)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> rectangleMatch(c, providersRead, 17, 20)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> rectangleMatch(c, providersRead, 18, 20)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> triangleMatch(c, providersRead, coordinate2dOf(2, 2),
                        coordinate2dOf(3, 2), coordinate2dOf(3, 3), 20)));

        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> triangleMatch(c, providersRead, coordinate2dOf(2, 2),
                        coordinate2dOf(2, 3), coordinate2dOf(3, 3), 21)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> rectangleMatch(c, providersRead, 19, 21)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> rectangleMatch(c, providersRead, 16, 21)));
        assertTrue(output.CONTENT.stream()
                .anyMatch(c -> triangleMatch(c, providersRead, coordinate2dOf(0, 0),
                        coordinate2dOf(0, 1), coordinate2dOf(1, 1), 21)));

        verify(mockButtonDefinitionReader, once()).read(definition, TIMESTAMP);
        verify(mockComponentDefinitionData, once()).put(bevelLastTimestampDataKey, TIMESTAMP - 1);

        var inOrder = inOrder(mockProviderDefinitionReader);
        slots.forEach(xSlot -> {
            slots.forEach(ySlot -> {
                //noinspection unchecked,rawtypes
                inOrder.verify(mockProviderDefinitionReader, once()).read(
                        argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                                BeveledButton_provideVertex,
                                mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID,
                                        BeveledButton_rectDimensProvider,
                                        mockRectDimens,
                                        BeveledButton_xSlot,
                                        xSlot,
                                        BeveledButton_ySlot,
                                        ySlot,
                                        BeveledButton_bevelPercent,
                                        definition.BEVEL_DIMENS_PERCENT
                                ))), eq(TIMESTAMP));
            });
        });
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideBox,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                mockRectDimens,
                                BeveledButton_xSlot,
                                0,
                                BeveledButton_provideBox_xSlotRight,
                                1,
                                BeveledButton_ySlot,
                                1,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        ))), eq(TIMESTAMP));
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideBox,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                mockRectDimens,
                                BeveledButton_xSlot,
                                1,
                                BeveledButton_provideBox_xSlotRight,
                                3,
                                BeveledButton_ySlot,
                                0,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        ))), eq(TIMESTAMP));
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideBox,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                mockRectDimens,
                                BeveledButton_xSlot,
                                2,
                                BeveledButton_provideBox_xSlotRight,
                                3,
                                BeveledButton_ySlot,
                                1,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        ))), eq(TIMESTAMP));
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideBox,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_rectDimensProvider,
                                mockRectDimens,
                                BeveledButton_xSlot,
                                0,
                                BeveledButton_provideBox_xSlotRight,
                                2,
                                BeveledButton_ySlot,
                                2,
                                BeveledButton_bevelPercent,
                                definition.BEVEL_DIMENS_PERCENT
                        ))), eq(TIMESTAMP));
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideColor,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_provideColor_isLitByDefault,
                                true,
                                BeveledButton_provideColor_bevelIntensity,
                                BEVEL_INTENSITY
                        ))), eq(TIMESTAMP));
        //noinspection unchecked,rawtypes
        inOrder.verify(mockProviderDefinitionReader, once()).read(
                argThat(new FunctionalProviderDefMatcher<AbstractProviderDefinition>(
                        BeveledButton_provideColor,
                        mapOf(
                                COMPONENT_UUID,
                                definition.UUID,
                                BeveledButton_provideColor_isLitByDefault,
                                false,
                                BeveledButton_provideColor_bevelIntensity,
                                BEVEL_INTENSITY
                        ))), eq(TIMESTAMP));
    }

    private boolean triangleMatch(AbstractContentDefinition content,
                                  @SuppressWarnings("rawtypes")
                                  List<ProviderAtTime> providersGenerated,
                                  Coordinate2d vertex1,
                                  Coordinate2d vertex2,
                                  Coordinate2d vertex3,
                                  int colorProviderIndex) {
        if (!(content instanceof TriangleRenderableDefinition t)) {
            return false;
        }
        return t.Z == BEVEL_Z &&
                t.VERTEX_1_PROVIDER == providersGenerated.get(getIndexFromCoord(vertex1)) &&
                t.VERTEX_2_PROVIDER == providersGenerated.get(getIndexFromCoord(vertex2)) &&
                t.VERTEX_3_PROVIDER == providersGenerated.get(getIndexFromCoord(vertex3)) &&
                t.vertex1ColorProvider == providersGenerated.get(colorProviderIndex) &&
                t.vertex2ColorProvider == providersGenerated.get(colorProviderIndex) &&
                t.vertex3ColorProvider == providersGenerated.get(colorProviderIndex);
    }

    private int getIndexFromCoord(Coordinate2d coord) {
        return (coord.X * 4) + coord.Y;
    }

    private boolean rectangleMatch(AbstractContentDefinition content,
                                   @SuppressWarnings("rawtypes")
                                   List<ProviderAtTime> providersGenerated,
                                   int rectDimensProviderIndex,
                                   int colorProviderIndex) {
        if (!(content instanceof RectangleRenderableDefinition r)) {
            return false;
        }
        return r.Z == BEVEL_Z &&
                r.DIMENS_PROVIDER == providersGenerated.get(rectDimensProviderIndex) &&
                r.topLeftColorProvider == providersGenerated.get(colorProviderIndex) &&
                r.topRightColorProvider == providersGenerated.get(colorProviderIndex) &&
                r.bottomLeftColorProvider == providersGenerated.get(colorProviderIndex) &&
                r.bottomRightColorProvider == providersGenerated.get(colorProviderIndex);
    }
}
