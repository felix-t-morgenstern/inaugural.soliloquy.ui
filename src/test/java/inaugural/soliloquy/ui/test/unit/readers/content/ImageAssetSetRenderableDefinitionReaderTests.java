package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.ImageAssetSetRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.ImageAssetSet;
import soliloquy.specs.io.graphics.renderables.ImageAssetSetRenderable;
import soliloquy.specs.io.graphics.renderables.factories.ImageAssetSetRenderableFactory;

import java.util.Map;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.ImageAssetSetRenderableDefinition.imageAssetSet;

@ExtendWith(MockitoExtension.class)
public class ImageAssetSetRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final String IMAGE_ASSET_SET_ID = randomString();
    private final String DATA_KEY = randomString();
    private final String DATA_VAL = randomString();
    private final Map<String, String> DATA = mapOf(pairOf(DATA_KEY, DATA_VAL));
    private final LookupAndEntitiesWithId<ImageAssetSet> MOCK_IMAGE_ASSET_SET_AND_LOOKUP = generateMockLookupFunctionWithId(ImageAssetSet.class, IMAGE_ASSET_SET_ID);
    private final ImageAssetSet MOCK_IMAGE_ASSET_SET = MOCK_IMAGE_ASSET_SET_AND_LOOKUP.entities.getFirst();
    private final Function<String, ImageAssetSet> MOCK_GET_IMAGE_ASSET_SET = MOCK_IMAGE_ASSET_SET_AND_LOOKUP.lookup;

    @Mock private ImageAssetSetRenderable mockRenderable;
    @Mock private ImageAssetSetRenderableFactory mockFactory;

    private ImageAssetSetRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockFactory.make(
                any(),
                any(),
                any(), any(),
                any(), any(), any(), any(),
                any(),
                any(),
                anyInt(),
                any(),
                any())).thenReturn(mockRenderable);

        reader = new ImageAssetSetRenderableDefinitionReader(mockFactory, MOCK_GET_IMAGE_ASSET_SET, MOCK_GET_ACTION, mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(null, MOCK_GET_IMAGE_ASSET_SET, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(mockFactory, null, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(mockFactory, MOCK_GET_IMAGE_ASSET_SET, null,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(mockFactory, MOCK_GET_IMAGE_ASSET_SET,
                        MOCK_GET_ACTION, null, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(mockFactory, MOCK_GET_IMAGE_ASSET_SET,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new ImageAssetSetRenderableDefinitionReader(mockFactory, MOCK_GET_IMAGE_ASSET_SET,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, mockShiftDefinitionReader,
                        null));
    }

    @Test
    public void testRead() {
        var definition = imageAssetSet(IMAGE_ASSET_SET_ID, DATA, mockAreaProviderDefinition, Z)
                .withBorder(mockBorderThicknessDefinition, mockBorderColorDefinition)
                .withColorShifts(mockShiftDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID);

        var renderable = reader.read(mockComponent, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_IMAGE_ASSET_SET, once()).apply(IMAGE_ASSET_SET_ID);
        verify(mockProviderDefinitionReader, once()).read(mockAreaProviderDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockBorderThicknessDefinition);
        verify(mockProviderDefinitionReader, once()).read(mockBorderColorDefinition);
        verify(mockShiftDefinitionReader, once()).read(mockShiftDefinition);
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_IMAGE_ASSET_SET),
                eq(DATA),
                same(mockBorderThickness), same(mockBorderColor),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                eq(listOf(mockShift)),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = imageAssetSet(IMAGE_ASSET_SET_ID, DATA, mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockComponent, definition);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_IMAGE_ASSET_SET, once()).apply(IMAGE_ASSET_SET_ID);
        verify(mockProviderDefinitionReader, once()).read(mockAreaProviderDefinition);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_IMAGE_ASSET_SET),
                eq(DATA),
                same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                eq(listOf()),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
    }
}
