package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.ui.readers.content.renderables.GlobalLoopingAnimationRenderableDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.assets.GlobalLoopingAnimation;
import soliloquy.specs.io.graphics.renderables.GlobalLoopingAnimationRenderable;
import soliloquy.specs.io.graphics.renderables.factories.GlobalLoopingAnimationRenderableFactory;

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
import static org.mockito.Mockito.*;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;
import static soliloquy.specs.ui.definitions.content.GlobalLoopingAnimationRenderableDefinition.globalLooping;

@ExtendWith(MockitoExtension.class)
public class GlobalLoopingAnimationRenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final long TIMESTAMP = randomLong();
    private final String GLOBAL_LOOPING_ANIMATION_ID = randomString();
    private final LookupAndEntitiesWithId<GlobalLoopingAnimation> MOCK_GLOBAL_LOOPING_ANIMATION_AND_LOOKUP = generateMockLookupFunctionWithId(GlobalLoopingAnimation.class, GLOBAL_LOOPING_ANIMATION_ID);
    private final GlobalLoopingAnimation MOCK_GLOBAL_LOOPING_ANIMATION = MOCK_GLOBAL_LOOPING_ANIMATION_AND_LOOKUP.entities.getFirst();
    private final Function<String, GlobalLoopingAnimation> MOCK_GET_GLOBAL_LOOPING_ANIMATION = MOCK_GLOBAL_LOOPING_ANIMATION_AND_LOOKUP.lookup;

    @Mock private GlobalLoopingAnimationRenderable mockRenderable;
    @Mock private GlobalLoopingAnimationRenderableFactory mockFactory;

    private GlobalLoopingAnimationRenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockFactory.make(
                any(),
                any(), any(),
                any(), any(), any(), any(), any(),
                any(),
                any(),
                anyInt(),
                any(),
                any())).thenReturn(mockRenderable);

        reader = new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory,
                MOCK_GET_GLOBAL_LOOPING_ANIMATION, MOCK_GET_CONSUMER,
                mockProviderDefinitionReader, mockColorShiftDefinitionReader, mockNullProvider);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(null, MOCK_GET_GLOBAL_LOOPING_ANIMATION, MOCK_GET_CONSUMER,
                        mockProviderDefinitionReader, mockColorShiftDefinitionReader,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory, null, MOCK_GET_CONSUMER,
                        mockProviderDefinitionReader, mockColorShiftDefinitionReader,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory,
                        MOCK_GET_GLOBAL_LOOPING_ANIMATION, null,
                        mockProviderDefinitionReader, mockColorShiftDefinitionReader,
                        mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory,
                        MOCK_GET_GLOBAL_LOOPING_ANIMATION,
                        MOCK_GET_CONSUMER, null, mockColorShiftDefinitionReader, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory,
                        MOCK_GET_GLOBAL_LOOPING_ANIMATION,
                        MOCK_GET_CONSUMER, mockProviderDefinitionReader, null, mockNullProvider));
        assertThrows(IllegalArgumentException.class,
                () -> new GlobalLoopingAnimationRenderableDefinitionReader(mockFactory,
                        MOCK_GET_GLOBAL_LOOPING_ANIMATION,
                        MOCK_GET_CONSUMER, mockProviderDefinitionReader,
                        mockColorShiftDefinitionReader, null));
    }

    @Test
    public void testReadWithMaximalArgsFromDefs() {
        var definition = globalLooping(GLOBAL_LOOPING_ANIMATION_ID, mockAreaProviderDefinition, Z)
                .withBorder(mockBorderThicknessDefinition, mockBorderColorDefinition)
                .withColorShifts(mockShiftDefinition)
                .onPress(mapOf(pairOf(ON_PRESS_BUTTON, ON_PRESS_ID)))
                .onRelease(mapOf(pairOf(ON_RELEASE_BUTTON, ON_RELEASE_ID)))
                .onWheelScroll(ON_WHEEL_SCROLL_ID)
                .onMouseOver(ON_MOUSE_OVER_ID)
                .onMouseLeave(ON_MOUSE_LEAVE_ID);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(mockRenderable, once()).setCapturesMouseButtonEvents(true);
        verify(mockRenderable, once()).setCapturesMouseWheelEvents(true);
        verify(MOCK_GET_GLOBAL_LOOPING_ANIMATION, once()).apply(GLOBAL_LOOPING_ANIMATION_ID);
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockBorderThicknessDefinition),
                eq(TIMESTAMP));
        verify(mockProviderDefinitionReader, once()).read(same(mockBorderColorDefinition),
                eq(TIMESTAMP));
        verify(mockColorShiftDefinitionReader, once()).read(same(mockShiftDefinition),
                eq(TIMESTAMP));
        verify(MOCK_GET_CONSUMER, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_RELEASE_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_MOUSE_OVER_ID);
        verify(MOCK_GET_CONSUMER, once()).apply(ON_MOUSE_LEAVE_ID);
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_GLOBAL_LOOPING_ANIMATION),
                same(mockBorderThickness), same(mockBorderColor),
                eq(mapOf(pairOf(ON_PRESS_BUTTON, MOCK_ON_PRESS))),
                eq(mapOf(pairOf(ON_RELEASE_BUTTON, MOCK_ON_RELEASE))),
                same(MOCK_ON_WHEEL_SCROLL),
                same(MOCK_ON_MOUSE_OVER),
                same(MOCK_ON_MOUSE_LEAVE),
                eq(listOf(mockShift)),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
        verify(mockRenderable, once()).setCapturesMouseButtonEvents(true);
    }

    @Test
    public void testReadShiftsFromProviders() {
        var definition = globalLooping(GLOBAL_LOOPING_ANIMATION_ID, mockAreaProvider, Z)
                .withColorShifts(mockShift);

        reader.read(mockComponent, definition, TIMESTAMP);

        verify(mockProviderDefinitionReader, never()).read(any(), anyLong());
        verify(mockColorShiftDefinitionReader, never()).read(any(), anyLong());
        verify(mockFactory, once()).make(
                any(),
                any(), any(),
                anyMap(),
                anyMap(),
                any(),
                any(),
                any(),
                eq(listOf(mockShift)),
                any(),
                anyInt(),
                any(),
                any()
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = globalLooping(GLOBAL_LOOPING_ANIMATION_ID, mockAreaProviderDefinition, Z);

        var renderable = reader.read(mockComponent, definition, TIMESTAMP);

        assertNotNull(renderable);
        assertSame(mockRenderable, renderable);
        verify(MOCK_GET_GLOBAL_LOOPING_ANIMATION, once()).apply(GLOBAL_LOOPING_ANIMATION_ID);
        verify(mockProviderDefinitionReader, once()).read(same(mockAreaProviderDefinition),
                eq(TIMESTAMP));
        //noinspection unchecked
        verify(mockFactory, once()).make(
                same(MOCK_GLOBAL_LOOPING_ANIMATION),
                same(mockNullProvider), same(mockNullProvider),
                eq(mapOf()),
                eq(mapOf()),
                isNull(),
                isNull(),
                isNull(),
                eq(listOf()),
                same(mockAreaProvider),
                eq(Z),
                isNotNull(),
                same(mockComponent)
        );
        verify(mockRenderable, never()).setCapturesMouseButtonEvents(anyBoolean());
    }
}
