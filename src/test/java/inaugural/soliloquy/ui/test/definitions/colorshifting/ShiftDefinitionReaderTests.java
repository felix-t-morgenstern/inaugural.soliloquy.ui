package inaugural.soliloquy.ui.test.definitions.colorshifting;

import inaugural.soliloquy.ui.definitions.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.colorshifting.BrightnessShift;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorComponentIntensityShift;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorRotationShift;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.random.Random.randomBoolean;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.io.graphics.renderables.colorshifting.ColorComponent.RED;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.*;

@ExtendWith(MockitoExtension.class)
public class ShiftDefinitionReaderTests {
    private final boolean OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE = randomBoolean();

    @Mock private AbstractProviderDefinition<Float> mockProviderDefinition;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @Mock private ProviderAtTime<Float> mockProvider;

    private ShiftDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockProviderDefinitionReader.read(mockProviderDefinition))
                .thenReturn(mockProvider);

        reader = new ShiftDefinitionReader(mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ShiftDefinitionReader(null));
    }

    @Test
    public void testReadBrightnessShift() {
        var definition = brightness(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);

        var shift = reader.read(definition);

        assertNotNull(shift);
        assertInstanceOf(BrightnessShift.class, shift);
        assertSame(mockProvider, shift.shiftAmountProvider());
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE, shift.overridesPriorShiftsOfSameType());
        verify(mockProviderDefinitionReader, once()).read(mockProviderDefinition);
    }

    @Test
    public void testReadComponentIntensityShift() {
        var definition = componentIntensity(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE, RED);

        var shift = reader.read(definition);

        assertNotNull(shift);
        assertInstanceOf(ColorComponentIntensityShift.class, shift);
        assertSame(mockProvider, shift.shiftAmountProvider());
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE, shift.overridesPriorShiftsOfSameType());
        assertSame(RED, ((ColorComponentIntensityShift)shift).colorComponent());
        verify(mockProviderDefinitionReader, once()).read(mockProviderDefinition);
    }

    @Test
    public void testReadRotationShift() {
        var definition = rotation(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);

        var shift = reader.read(definition);

        assertNotNull(shift);
        assertInstanceOf(ColorRotationShift.class, shift);
        assertSame(mockProvider, shift.shiftAmountProvider());
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE, shift.overridesPriorShiftsOfSameType());
        verify(mockProviderDefinitionReader, once()).read(mockProviderDefinition);
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
        assertThrows(IllegalArgumentException.class, () -> reader.read(brightness(null, randomBoolean())));
        assertThrows(IllegalArgumentException.class, () -> reader.read(componentIntensity(null, randomBoolean(), RED)));
        assertThrows(IllegalArgumentException.class, () -> reader.read(componentIntensity(mockProviderDefinition, randomBoolean(), null)));
        assertThrows(IllegalArgumentException.class, () -> reader.read(rotation(null, randomBoolean())));
    }
}
