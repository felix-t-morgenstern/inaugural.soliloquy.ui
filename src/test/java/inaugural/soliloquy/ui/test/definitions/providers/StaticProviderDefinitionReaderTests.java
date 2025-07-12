package inaugural.soliloquy.ui.test.definitions.providers;

import definitions.providers.StaticProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.StaticProviderFactory;

import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

@ExtendWith(MockitoExtension.class)
public class StaticProviderDefinitionReaderTests {
    @Mock private StaticProviderFactory mockFactory;
    @SuppressWarnings("rawtypes")
    @Mock private StaticProvider mockStaticProvider;

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
        //noinspection unchecked
        when(mockFactory.make(any(), any(), any())).thenReturn(mockStaticProvider);
        var val = randomInt();
        var definition = staticVal(val);

        var output = reader.read(definition);

        assertSame(mockStaticProvider, output);
        verify(mockFactory, once()).make(any(), eq(val), isNull());
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
    }
}
