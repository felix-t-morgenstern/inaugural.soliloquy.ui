package inaugural.soliloquy.ui.test.readers.content;

import inaugural.soliloquy.ui.readers.content.FiniteAnimationRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.Animation;
import soliloquy.specs.io.graphics.renderables.FiniteAnimationRenderable;
import soliloquy.specs.io.graphics.renderables.factories.FiniteAnimationRenderableFactory;

import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static inaugural.soliloquy.tools.testing.Mock.LookupAndEntitiesWithId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.FiniteAnimationRenderableDefinition.finiteAnimation;

@ExtendWith(MockitoExtension.class)
public class FiniteAnimationRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final String ANIMATION_ID = randomString();
    private final LookupAndEntitiesWithId<Animation> MOCK_ANIMATION_AND_LOOKUP =
            generateMockLookupFunctionWithId(Animation.class, ANIMATION_ID);
    private final Animation MOCK_ANIMATION = MOCK_ANIMATION_AND_LOOKUP.entities.getFirst();
    private final Function<String, Animation> MOCK_GET_ANIMATION = MOCK_ANIMATION_AND_LOOKUP.lookup;

    @Mock private FiniteAnimationRenderable mockRenderable;
    @Mock private FiniteAnimationRenderableFactory mockFactory;

    private FiniteAnimationRenderableDefinitionReader reader;

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
                any(),
                anyLong(), any(), any())).thenReturn(mockRenderable);

        reader = new FiniteAnimationRenderableDefinitionReader(mockFactory, MOCK_GET_ANIMATION, MOCK_GET_ACTION,
                mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(null, MOCK_GET_ANIMATION, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(mockFactory, null, MOCK_GET_ACTION,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(mockFactory, MOCK_GET_ANIMATION, null,
                        mockProviderDefinitionReader, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(mockFactory, MOCK_GET_ANIMATION,
                        MOCK_GET_ACTION, null, mockShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(mockFactory, MOCK_GET_ANIMATION,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new FiniteAnimationRenderableDefinitionReader(mockFactory, MOCK_GET_ANIMATION,
                        MOCK_GET_ACTION, mockProviderDefinitionReader, mockShiftDefinitionReader,
                        null));
    }

    @Test
    public void testRead() {
        var startTimestampOffset = randomInt();

        var definition = finiteAnimation(ANIMATION_ID, mockAreaProviderDefinition, Z)
                .withBorder(mockBorderThicknessDefinition, mockBorderColorDefinition)
                .withColorShifts(mockShiftDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID)
                .withStartTimestampOffset(startTimestampOffset);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        var startTimestamp = TIMESTAMP + startTimestampOffset;
        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_ANIMATION, once()).apply(ANIMATION_ID);
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
                same(MOCK_ANIMATION),
                same(mockBorderThickness), same(mockBorderColor),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                eq(listOf(mockShift)),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent),
                eq(startTimestamp), any(), any()
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = finiteAnimation(ANIMATION_ID, mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_ANIMATION, once()).apply(ANIMATION_ID);
        verify(mockProviderDefinitionReader, once()).read(mockAreaProviderDefinition);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_ANIMATION),
                same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                eq(listOf()),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent),
                eq(TIMESTAMP), any(), any()
        );
    }
}
