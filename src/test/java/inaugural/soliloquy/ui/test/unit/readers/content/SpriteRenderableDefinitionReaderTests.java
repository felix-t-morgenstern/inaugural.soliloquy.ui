package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.SpriteRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.Sprite;
import soliloquy.specs.io.graphics.renderables.SpriteRenderable;
import soliloquy.specs.io.graphics.renderables.factories.SpriteRenderableFactory;

import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.SpriteRenderableDefinition.sprite;

@ExtendWith(MockitoExtension.class)
public class SpriteRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final long TIMESTAMP = randomLong();
    private final String SPRITE_ID = randomString();
    private final LookupAndEntitiesWithId<Sprite> MOCK_SPRITE_AND_LOOKUP =
            generateMockLookupFunctionWithId(Sprite.class, SPRITE_ID);
    private final Sprite MOCK_SPRITE = MOCK_SPRITE_AND_LOOKUP.entities.getFirst();
    private final Function<String, Sprite> MOCK_GET_SPRITE = MOCK_SPRITE_AND_LOOKUP.lookup;

    @Mock private SpriteRenderable mockRenderable;
    @Mock private SpriteRenderableFactory mockFactory;

    private SpriteRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockFactory.make(
                any(),
                any(), any(),
                any(), any(), any(), any(),
                any(),
                any(),
                anyInt(),
                any(),
                any())).thenReturn(mockRenderable);

        reader = new SpriteRenderableDefinitionReader(mockFactory, MOCK_GET_SPRITE, MOCK_GET_ACTION,
                mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(null, MOCK_GET_SPRITE, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(mockFactory, null, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(mockFactory, MOCK_GET_SPRITE, null,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(mockFactory, MOCK_GET_SPRITE,
                        MOCK_GET_ACTION, null, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(mockFactory, MOCK_GET_SPRITE,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new SpriteRenderableDefinitionReader(mockFactory, MOCK_GET_SPRITE,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, mockShiftDefinitionReader,
                        null));
    }

    @Test
    public void testRead() {
        var definition = sprite(SPRITE_ID, mockAreaProviderDefinition, Z)
                .withBorder(mockBorderThicknessDefinition, mockBorderColorDefinition)
                .withColorShifts(mockShiftDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID);
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockBorderThicknessDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockBorderColorDefinition),
                eq(TIMESTAMP));
        verify(mockShiftDefinitionReader, once()).read(same(mockShiftDefinition), eq(TIMESTAMP));
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_SPRITE),
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
        var definition = sprite(SPRITE_ID, mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_SPRITE, once()).apply(SPRITE_ID);
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_SPRITE),
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
