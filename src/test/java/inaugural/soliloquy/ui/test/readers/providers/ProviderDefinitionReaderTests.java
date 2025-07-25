package inaugural.soliloquy.ui.test.readers.providers;

import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Map;
import java.util.function.Function;

import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.tools.testing.Mock.generateMockMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

@ExtendWith(MockitoExtension.class)
public class ProviderDefinitionReaderTests {
    @SuppressWarnings("rawtypes") @Mock private ProviderAtTime mockProvider;
    @SuppressWarnings("rawtypes") @Mock private Function<Object, ProviderAtTime> mockReader;
    @SuppressWarnings("rawtypes") @Mock private AbstractProviderDefinition mockDefinition;

    @SuppressWarnings("rawtypes") private Map<Class, Function<Object, ProviderAtTime>> readers;

    private ProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        readers = generateMockMap(pairOf(mockDefinition.getClass(), mockReader));

        reader = new ProviderDefinitionReader(readers);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockReader.apply(any())).thenReturn(mockProvider);

        @SuppressWarnings("unchecked") var provider = reader.read(mockDefinition);

        assertNotNull(provider);
        assertSame(mockProvider, provider);
        verify(readers, once()).get(eq(mockDefinition.getClass()));
        verify(mockReader, once()).apply(mockDefinition);
    }
}
