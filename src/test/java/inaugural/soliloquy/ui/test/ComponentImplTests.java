package inaugural.soliloquy.ui.test;

import inaugural.soliloquy.ui.ComponentImpl;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.input.keyboard.entities.KeyBindingContext;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.UUID;
import java.util.function.Consumer;

import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentImplTests {
    private final UUID UUID = java.util.UUID.randomUUID();
    private final int Z = randomInt();

    @Mock private AbstractProviderDefinition<FloatBox> mockRenderingBoundariesDefinition;
    @Mock private ProviderDefinitionReader mockProviderReader;
    @Mock private ProviderAtTime<FloatBox> mockRenderingBoundaries;
    @Mock private Consumer<RenderableWithMouseEvents> mockAddToCapturing;
    @Mock private Consumer<RenderableWithMouseEvents> mockRemoveFromCapturing;

    @Mock private KeyBindingContext mockBindingContext;
    @Mock private Renderable mockRenderable;
    @Mock private RenderableWithMouseEvents mockRenderableWithMouseEvents;
    @Mock private Component mockComponent;

    private Component component;

    @BeforeEach
    public void setUp() {
        lenient().when(mockProviderReader.read(mockRenderingBoundariesDefinition))
                .thenReturn(mockRenderingBoundaries);

        component = new ComponentImpl(UUID, Z, mockBindingContext, null, mockRenderingBoundaries,
                mockAddToCapturing, mockRemoveFromCapturing);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentImpl(null, Z, mockBindingContext, null, mockRenderingBoundaries,
                        mockAddToCapturing, mockRemoveFromCapturing));
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentImpl(UUID, Z, null, null, mockRenderingBoundaries,
                        mockAddToCapturing, mockRemoveFromCapturing));
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentImpl(UUID, Z, mockBindingContext, null, null, mockAddToCapturing,
                        mockRemoveFromCapturing));
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentImpl(UUID, Z, mockBindingContext, null, mockRenderingBoundaries,
                        null, mockRemoveFromCapturing));
        assertThrows(IllegalArgumentException.class,
                () -> new ComponentImpl(UUID, Z, mockBindingContext, null, mockRenderingBoundaries,
                        mockAddToCapturing, null));
    }

    @Test
    public void testConstructorAddsSelfToContainingComponent() {
        var mockComponent = mock(Component.class);

        component = new ComponentImpl(UUID, Z, mockBindingContext, mockComponent,
                mockRenderingBoundaries, mockAddToCapturing, mockRemoveFromCapturing);

        verify(mockComponent, once()).add(component);
    }

    @Test
    public void testKeyBindingContext() {
        assertSame(mockBindingContext, component.keyBindingContext());
    }

    @Test
    public void testAddAndContents() {
        component.add(mockRenderable);
        var content = component.contents();

        assertNotNull(content);
        assertEquals(setOf(mockRenderable), content);
    }

    @Test
    public void testAddToSameComponent() {
        when(mockRenderable.component()).thenReturn(component);

        component.add(mockRenderable);
        var content = component.contents();

        assertNotNull(content);
        assertEquals(setOf(mockRenderable), content);
    }

    @Test
    public void testAddNull() {
        var mockComponent = mock(Component.class);

        assertThrows(IllegalArgumentException.class, () -> component.add(null));
    }

    @Test
    public void testAddRenderableInDifferentComponent() {
        when(mockRenderable.component()).thenReturn(mock(Component.class));
        assertThrows(IllegalArgumentException.class, () -> component.add(mockRenderable));
    }

    @Test
    public void testAddComponentOfInvalidTier() {
        when(mockComponent.tier()).thenReturn(component.tier());
        assertThrows(IllegalArgumentException.class, () -> component.add(mockComponent));
        when(mockComponent.tier()).thenReturn(component.tier() + 2);
        assertThrows(IllegalArgumentException.class, () -> component.add(mockComponent));
    }

    @Test
    public void testAddPlacesInMouseCapturing() {
        component.add(mockRenderable);
        component.add(mockRenderableWithMouseEvents);

        verify(mockAddToCapturing, once()).accept(any());
        verify(mockAddToCapturing, once()).accept(mockRenderableWithMouseEvents);
    }

    @Test
    public void testRemove() {
        when(mockRenderable.component()).thenReturn(component);
        component.add(mockRenderable);

        ((ComponentImpl) component).remove(mockRenderable);

        assertTrue(component.contents().isEmpty());
    }

    @Test
    public void testRemoveRemovesFromMouseCapturing() {
        when(mockRenderable.component()).thenReturn(component);
        when(mockRenderableWithMouseEvents.component()).thenReturn(component);
        component.add(mockRenderable);
        component.add(mockRenderableWithMouseEvents);

        ((ComponentImpl) component).remove(mockRenderable);
        ((ComponentImpl) component).remove(mockRenderableWithMouseEvents);

        verify(mockRemoveFromCapturing, once()).accept(any());
        verify(mockRemoveFromCapturing, once()).accept(mockRenderableWithMouseEvents);
    }

    @Test
    public void testRemoveWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> ((ComponentImpl) component).remove(null));
        when(mockRenderable.component()).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> ((ComponentImpl) component).remove(mockRenderable));
        when(mockRenderable.component()).thenReturn(mock(Component.class));
        assertThrows(IllegalArgumentException.class,
                () -> ((ComponentImpl) component).remove(mockRenderable));
    }

    @Test
    public void testTierDefaultValue() {
        assertEquals(0, component.tier());
    }

    @Test
    public void testTierIncrementing() {
        var firstChild = new ComponentImpl(UUID, randomInt(), mockBindingContext, component,
                mockRenderingBoundaries, mockAddToCapturing, mockRemoveFromCapturing);
        var secondChild = new ComponentImpl(UUID, randomInt(), mockBindingContext, firstChild,
                mockRenderingBoundaries, mockAddToCapturing, mockRemoveFromCapturing);

        assertEquals(1, firstChild.tier());
        assertEquals(2, secondChild.tier());
    }
}
