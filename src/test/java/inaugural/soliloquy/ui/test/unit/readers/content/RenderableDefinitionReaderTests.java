package inaugural.soliloquy.ui.test.unit.readers.content;

import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.renderables.*;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.*;
import soliloquy.specs.io.graphics.renderables.factories.ComponentFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.keyboard.KeyBinding;
import soliloquy.specs.ui.definitions.content.*;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Set;
import java.util.function.Function;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.keyboard.KeyBindingDefinition.binding;

@ExtendWith(MockitoExtension.class)
public class RenderableDefinitionReaderTests extends AbstractContentDefinitionTests {
    private final int DEFAULT_KEY_BINDING_PRIORITY = randomInt();
    private final int Z = randomInt();
    private final long TIMESTAMP = randomLong();

    @Mock private RasterizedLineSegmentRenderableDefinition mockRasterizedLineDefinition;
    @Mock private AntialiasedLineSegmentRenderableDefinition mockAntialiasedLineDefinition;
    @Mock private RectangleRenderableDefinition mockRectangleDefinition;
    @Mock private TriangleRenderableDefinition mockTriangleDefinition;
    @Mock private SpriteRenderableDefinition mockSpriteDefinition;
    @Mock private ImageAssetSetRenderableDefinition mockImageAssetSetDefinition;
    @Mock private FiniteAnimationRenderableDefinition mockFiniteAnimationDefinition;
    @Mock private TextLineRenderableDefinition mockTextLineDefinition;

    @Mock private RasterizedLineSegmentRenderableDefinitionReader mockRasterizedLineReader;
    @Mock private AntialiasedLineSegmentRenderableDefinitionReader mockAntialiasedLineReader;
    @Mock private RectangleRenderableDefinitionReader mockRectangleReader;
    @Mock private TriangleRenderableDefinitionReader mockTriangleReader;
    @Mock private SpriteRenderableDefinitionReader mockSpriteReader;
    @Mock private ImageAssetSetRenderableDefinitionReader mockImageAssetSetReader;
    @Mock private FiniteAnimationRenderableDefinitionReader mockFiniteAnimationReader;
    @Mock private TextLineRenderableDefinitionReader mockTextLineReader;
    @Mock private ComponentFactory mockComponentFactory;

    @Mock private RasterizedLineSegmentRenderable mockRasterizedLineRenderable;
    @Mock private AntialiasedLineSegmentRenderable mockAntialiasedLineRenderable;
    @Mock private RectangleRenderable mockRectangleRenderable;
    @Mock private TriangleRenderable mockTriangleRenderable;
    @Mock private SpriteRenderable mockSpriteRenderable;
    @Mock private ImageAssetSetRenderable mockImageAssetSetRenderable;
    @Mock private FiniteAnimationRenderable mockFiniteAnimationRenderable;
    @Mock private TextLineRenderable mockTextLineRenderable;
    @Mock private Component mockComponent;
    @Mock private AbstractContentDefinition mockCustomDef;
    @Mock private Function<AbstractContentDefinition, ComponentDefinition> mockCustomReader;
    @SuppressWarnings("rawtypes") @Mock
    private Function<Class, Function<AbstractContentDefinition, ComponentDefinition>>
            mockComponentReaders;

    @Mock private ProviderAtTime<FloatBox> mockWholeScreenProvider;

    @Mock private AbstractProviderDefinition<FloatBox> mockComponentDimensDef;
    @Mock private ProviderDefinitionReader mockProviderReader;
    @Mock private ProviderAtTime<FloatBox> mockComponentDimens;

    @Mock private Component mockContainingComponent;

    private RenderableDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockRasterizedLineReader.read(any(), any(), anyLong()))
                .thenReturn(mockRasterizedLineRenderable);
        lenient().when(mockAntialiasedLineReader.read(any(), any(), anyLong()))
                .thenReturn(mockAntialiasedLineRenderable);
        lenient().when(mockRectangleReader.read(any(), any(), anyLong()))
                .thenReturn(mockRectangleRenderable);
        lenient().when(mockTriangleReader.read(any(), any(), anyLong()))
                .thenReturn(mockTriangleRenderable);
        lenient().when(mockSpriteReader.read(any(), any(), anyLong()))
                .thenReturn(mockSpriteRenderable);
        lenient().when(mockImageAssetSetReader.read(any(), any(), anyLong()))
                .thenReturn(mockImageAssetSetRenderable);
        lenient().when(mockFiniteAnimationReader.read(any(), any(), anyLong()))
                .thenReturn(mockFiniteAnimationRenderable);
        lenient().when(mockTextLineReader.read(any(), any(), anyLong()))
                .thenReturn(mockTextLineRenderable);

        lenient().when(mockComponentFactory.make(any(), anyInt(), any(), anyBoolean(), anyInt(), any(), any(), any())).thenReturn(mockComponent);

        lenient().when(mockProviderReader.read(same(mockComponentDimensDef), anyLong()))
                .thenReturn(mockComponentDimens);

        reader = new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        );
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                null,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                null,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                null,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                null,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                null,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                null,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                null,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                null,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                null,
                mockProviderReader,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                null,
                MOCK_GET_ACTION,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                null,
                mockWholeScreenProvider,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
        assertThrows(IllegalArgumentException.class, () -> new RenderableDefinitionReader(
                mockRasterizedLineReader,
                mockAntialiasedLineReader,
                mockRectangleReader,
                mockTriangleReader,
                mockSpriteReader,
                mockImageAssetSetReader,
                mockFiniteAnimationReader,
                mockTextLineReader,
                mockComponentFactory,
                mockProviderReader,
                MOCK_GET_ACTION,
                null,
                DEFAULT_KEY_BINDING_PRIORITY
        ));
    }

    @Test
    public void testReadRasterizedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockRasterizedLineDefinition, TIMESTAMP);

        assertSame(mockRasterizedLineRenderable, output);
        verify(mockRasterizedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockRasterizedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadAntialiasedLineDefinition() {
        var output = reader.read(mockContainingComponent, mockAntialiasedLineDefinition, TIMESTAMP);

        assertSame(mockAntialiasedLineRenderable, output);
        verify(mockAntialiasedLineReader, once()).read(
                same(mockContainingComponent),
                same(mockAntialiasedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadRectangleDefinition() {
        var output = reader.read(mockContainingComponent, mockRectangleDefinition, TIMESTAMP);

        assertSame(mockRectangleRenderable, output);
        verify(mockRectangleReader, once()).read(
                same(mockContainingComponent),
                same(mockRectangleDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadTriangleDefinition() {
        var output = reader.read(mockContainingComponent, mockTriangleDefinition, TIMESTAMP);

        assertSame(mockTriangleRenderable, output);
        verify(mockTriangleReader, once()).read(
                same(mockContainingComponent),
                same(mockTriangleDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadSpriteDefinition() {
        var output = reader.read(mockContainingComponent, mockSpriteDefinition, TIMESTAMP);

        assertSame(mockSpriteRenderable, output);
        verify(mockSpriteReader, once()).read(
                same(mockContainingComponent),
                same(mockSpriteDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadImageAssetSetDefinition() {
        var output = reader.read(mockContainingComponent, mockImageAssetSetDefinition, TIMESTAMP);

        assertSame(mockImageAssetSetRenderable, output);
        verify(mockImageAssetSetReader, once()).read(
                same(mockContainingComponent),
                same(mockImageAssetSetDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadFiniteAnimationDefinition() {
        var output = reader.read(mockContainingComponent, mockFiniteAnimationDefinition, TIMESTAMP);

        assertSame(mockFiniteAnimationRenderable, output);
        verify(mockFiniteAnimationReader, once()).read(
                same(mockContainingComponent),
                same(mockFiniteAnimationDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadTextLineDefinition() {
        var output = reader.read(mockContainingComponent, mockTextLineDefinition, TIMESTAMP);

        assertSame(mockTextLineRenderable, output);
        verify(mockTextLineReader, once()).read(
                same(mockContainingComponent),
                same(mockTextLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadComponentDefinitionWithFullArgsAndDimensProviderDef() {
        var key = randomInt();
        var overrides = randomBoolean();
        var priority = randomInt();
        var dataKey = randomString();
        var dataVal = randomInt();
        var data = Collections.<String, Object>mapOf(dataKey, dataVal);
        var definition = component(Z, mockComponentDimensDef)
                .withContent(mockRasterizedLineDefinition)
                .withBindings(
                        overrides,
                        priority,
                        binding(
                                ON_PRESS_ID,
                                ON_RELEASE_ID,
                                key
                        )
                )
                .withData(data);

        var output = reader.read(mockContainingComponent, definition, TIMESTAMP);

        assertSame(mockComponent, output);
        var bindingsCaptor = ArgumentCaptor.forClass(Set.class);
        //noinspection unchecked
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                eq(Z),
                bindingsCaptor.capture(),
                eq(overrides),
                eq(priority),
                same(mockComponentDimens),
                same(mockContainingComponent),
                eq(data)
        );
        @SuppressWarnings("unchecked") var bindings = (Set<KeyBinding>) bindingsCaptor.getValue();
        assertEquals(1, bindings.size());
        @SuppressWarnings("OptionalGetWithoutIsPresent") var binding =
                bindings.stream().findFirst().get();
        assertArrayEquals(arrayInts(key), binding.BOUND_CODEPOINTS);
        assertSame(MOCK_ON_PRESS, binding.ON_PRESS);
        assertSame(MOCK_ON_RELEASE, binding.ON_RELEASE);
        verify(MOCK_GET_ACTION, times(2)).apply(anyString());
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);

        verify(mockRasterizedLineReader, once()).read(
                same((Component) output),
                same(mockRasterizedLineDefinition),
                eq(TIMESTAMP)
        );
    }

    @Test
    public void testReadComponentDefinitionWithMinimalArgs() {
        var definition = component(Z, mockComponentDimensDef);

        var output = reader.read(mockContainingComponent, definition, TIMESTAMP);

        assertSame(mockComponent, output);
        var bindingsCaptor = ArgumentCaptor.forClass(Set.class);
        //noinspection unchecked
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                eq(Z),
                bindingsCaptor.capture(),
                eq(false),
                eq(DEFAULT_KEY_BINDING_PRIORITY),
                same(mockComponentDimens),
                same(mockContainingComponent),
                eq(mapOf())
        );
        @SuppressWarnings("unchecked") var bindings = (Set<KeyBinding>) bindingsCaptor.getValue();
        assertTrue(bindings.isEmpty());
        verify(MOCK_GET_ACTION, never()).apply(anyString());
    }

    @Test
    public void testReadComponentDefinitionWithDimensProvider() {
        var componentDefinition = component(Z, mockComponentDimens);

        var output = reader.read(mockContainingComponent, componentDefinition, TIMESTAMP);

        assertSame(mockComponent, output);
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                eq(Z),
                any(),
                eq(false),
                eq(DEFAULT_KEY_BINDING_PRIORITY),
                same(mockComponentDimens),
                same(mockContainingComponent),
                eq(mapOf())
        );
    }

    @Test
    public void testReadComponentDefinitionWithNoDimens() {
        var definition = component(Z);

        var output = reader.read(mockContainingComponent, definition, TIMESTAMP);

        assertSame(mockComponent, output);
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                anyInt(),
                any(),
                anyBoolean(),
                anyInt(),
                same(mockWholeScreenProvider),
                any(),
                anyMap()
        );
    }

    @Test
    public void testRead() {
        var key = randomInt();
        var overrides = randomBoolean();
        var priority = randomInt();
        var dataKey = randomString();
        var dataVal = randomInt();
        var data = Collections.<String, Object>mapOf(dataKey, dataVal);
        var componentDef = component(Z, mockComponentDimensDef)
                .withContent(mockRasterizedLineDefinition)
                .withBindings(
                        overrides,
                        priority,
                        binding(
                                ON_PRESS_ID,
                                ON_RELEASE_ID,
                                key
                        )
                )
                .withData(data);
        when(mockCustomReader.apply(any())).thenReturn(componentDef);

        reader.addCustomComponentReader(mockCustomDef.getClass(), mockCustomReader);
        var output = reader.read(mockContainingComponent, mockCustomDef, TIMESTAMP);

        assertSame(mockComponent, output);
        var bindingsCaptor = ArgumentCaptor.forClass(Set.class);
        //noinspection unchecked
        verify(mockComponentFactory, once()).make(
                isNotNull(),
                eq(Z),
                bindingsCaptor.capture(),
                eq(overrides),
                eq(priority),
                same(mockComponentDimens),
                same(mockContainingComponent),
                eq(data)
        );
        @SuppressWarnings("unchecked") var bindings = (Set<KeyBinding>) bindingsCaptor.getValue();
        assertEquals(1, bindings.size());
        @SuppressWarnings("OptionalGetWithoutIsPresent") var binding =
                bindings.stream().findFirst().get();
        assertArrayEquals(arrayInts(key), binding.BOUND_CODEPOINTS);
        assertSame(MOCK_ON_PRESS, binding.ON_PRESS);
        assertSame(MOCK_ON_RELEASE, binding.ON_RELEASE);
        verify(MOCK_GET_ACTION, times(2)).apply(anyString());
        verify(MOCK_GET_ACTION, once()).apply(ON_PRESS_ID);
        verify(MOCK_GET_ACTION, once()).apply(ON_RELEASE_ID);

        verify(mockRasterizedLineReader, once()).read(
                same((Component) output),
                same(mockRasterizedLineDefinition),
                eq(TIMESTAMP)
        );

        verify(mockCustomReader, once()).apply(mockCustomDef);
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(null, mock(ComponentDefinition.class), TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockContainingComponent, null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(mockContainingComponent, mock(AbstractContentDefinition.class),
                        TIMESTAMP));
    }

    @Test
    public void testAddCustomComponentReaderWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> reader.addCustomComponentReader(null, _ -> null));
        assertThrows(IllegalArgumentException.class,
                () -> reader.addCustomComponentReader(AbstractContentDefinition.class, null));
    }
}
