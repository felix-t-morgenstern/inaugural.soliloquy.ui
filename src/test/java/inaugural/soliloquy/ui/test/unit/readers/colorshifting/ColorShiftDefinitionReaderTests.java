package inaugural.soliloquy.ui.test.unit.readers.colorshifting;

import inaugural.soliloquy.ui.readers.colorshifting.ColorShiftDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
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
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static soliloquy.specs.io.graphics.renderables.colorshifting.ColorComponent.RED;
import static soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition.*;

@ExtendWith(MockitoExtension.class)
public class ColorShiftDefinitionReaderTests {
    private final long TIMESTAMP = randomLong();
    private final boolean OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE = randomBoolean();

    @Mock private AbstractProviderDefinition<Float> mockProviderDefinition;
    @Mock private ProviderDefinitionReader mockProviderDefinitionReader;
    @Mock private ProviderAtTime<Float> mockProvider;

    private ColorShiftDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        lenient().when(mockProviderDefinitionReader.read(same(mockProviderDefinition), anyLong()))
                .thenReturn(mockProvider);

        reader = new ColorShiftDefinitionReader(mockProviderDefinitionReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ColorShiftDefinitionReader(null));
    }

    @Test
    public void testReadBrightnessShift() {
        var definition = brightness(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);

        var shift = reader.read(definition, TIMESTAMP);

        assertNotNull(shift);
        assertInstanceOf(BrightnessShift.class, shift);
        assertSame(mockProvider, shift.AMOUNT_PROVIDER);
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE,
                shift.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);
        verify(mockProviderDefinitionReader, once())
                .read(same(mockProviderDefinition), eq(TIMESTAMP));
    }

    @Test
    public void testReadComponentIntensityShift() {
        var definition =
                componentIntensity(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE,
                        RED);

        var shift = reader.read(definition, TIMESTAMP);

        assertNotNull(shift);
        assertInstanceOf(ColorComponentIntensityShift.class, shift);
        assertSame(mockProvider, shift.AMOUNT_PROVIDER);
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE,
                shift.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);
        assertSame(RED, ((ColorComponentIntensityShift) shift).COLOR_COMPONENT);
        verify(mockProviderDefinitionReader, once())
                .read(same(mockProviderDefinition), eq(TIMESTAMP));
    }

    @Test
    public void testReadRotationShift() {
        var definition = rotation(mockProviderDefinition, OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);

        var shift = reader.read(definition, TIMESTAMP);

        assertNotNull(shift);
        assertInstanceOf(ColorRotationShift.class, shift);
        assertSame(mockProvider, shift.AMOUNT_PROVIDER);
        assertEquals(OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE,
                shift.OVERRIDES_PRIOR_SHIFTS_OF_SAME_TYPE);
        verify(mockProviderDefinitionReader, once())
                .read(same(mockProviderDefinition), eq(TIMESTAMP));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(brightness(null, randomBoolean()), TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(componentIntensity(null, randomBoolean(), RED), TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(componentIntensity(mockProviderDefinition, randomBoolean(), null),
                        TIMESTAMP));
        assertThrows(IllegalArgumentException.class,
                () -> reader.read(rotation(null, randomBoolean()), TIMESTAMP));
    }
}
