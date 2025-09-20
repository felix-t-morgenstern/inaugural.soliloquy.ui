package inaugural.soliloquy.ui.test.unit.readers.providers;

import inaugural.soliloquy.ui.readers.providers.FunctionalProviderDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider;
import soliloquy.specs.io.graphics.renderables.providers.factories.FunctionalProviderFactory;

import java.util.Map;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.random.Random.randomLong;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

@ExtendWith(MockitoExtension.class)
public class FunctionalProviderDefinitionReaderTests {
    private final UUID UUID = randomUUID();
    private final String PROVIDE_ID = randomString();
    private final String PAUSE_ID = randomString();
    private final String UNPAUSE_ID = randomString();
    private final Long PAUSE_TIME = randomLong();

    @Mock private Map<String, Object> mockData;
    @Mock private FunctionalProviderFactory mockFactory;
    @SuppressWarnings("rawtypes") @Mock private FunctionalProvider mockProvider;

    private FunctionalProviderDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        //noinspection unchecked
        lenient().when(mockFactory.make(any(), anyString(), any(), any(), any(), any()))
                .thenReturn(mockProvider);

        reader = new FunctionalProviderDefinitionReader(mockFactory);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class,
                () -> new FunctionalProviderDefinitionReader(null));
    }

    @Test
    public void testRead() {
        var definition = functionalProvider(PROVIDE_ID)
                .withUuid(UUID)
                .withPauseActions(PAUSE_ID, UNPAUSE_ID)
                .withPauseTimestamp(PAUSE_TIME)
                .withData(mockData);

        var output = reader.read(definition);

        assertNotNull(output);
        assertSame(mockProvider, output);
        verify(mockFactory, once()).make(
                eq(UUID),
                eq(PROVIDE_ID),
                eq(PAUSE_ID),
                eq(UNPAUSE_ID),
                eq(PAUSE_TIME),
                same(mockData)
        );
    }

    @Test
    public void testReadWithMinimalArgs() {
        var definition = functionalProvider(PROVIDE_ID);

        var output = reader.read(definition);

        assertNotNull(output);
        assertSame(mockProvider, output);
        verify(mockFactory, once()).make(
                isNotNull(),
                eq(PROVIDE_ID),
                isNull(),
                isNull(),
                isNull(),
                eq(mapOf())
        );
    }
}
