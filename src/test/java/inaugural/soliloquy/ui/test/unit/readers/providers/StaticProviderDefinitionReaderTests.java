package inaugural.soliloquy.ui.test.unit.readers.providers;

import inaugural.soliloquy.ui.readers.providers.StaticProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;

import java.util.UUID;
import java.util.function.BiFunction;

import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

@ExtendWith(MockitoExtension.class)
public class StaticProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private BiFunction<UUID, Object, ProviderAtTime> mockFactory;
    @SuppressWarnings("rawtypes")
    @Mock private ProviderAtTime mockStaticProvider;

    private StaticProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new StaticProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new StaticProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockFactory.apply(any(), any())).thenReturn(mockStaticProvider);
        var val = randomInt();
        var definition = staticVal(val);

        var output = reader.read(definition);

        assertSame(mockStaticProvider, output);
        verify(mockFactory, once()).apply(any(), eq(val));
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
    }
}
