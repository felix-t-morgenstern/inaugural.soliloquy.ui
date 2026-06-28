package inaugural.soliloquy.ui.test.unit.components.contentcolumn;

import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinitionReader;
import inaugural.soliloquy.ui.components.contentcolumn.ContentColumnMethods;
import inaugural.soliloquy.ui.test.unit.components.ComponentDefinitionReaderTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnDefinition.column;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

@ExtendWith(MockitoExtension.class)
public class ContentColumnDefinitionReaderTests extends ComponentDefinitionReaderTest {
    private final float WIDTH = randomFloat();

    @Mock private AbstractProviderDefinition<Vertex> mockRenderingLocDef;
    @Mock private ProviderAtTime<Vertex> mockRenderingLoc;

    private ContentColumnDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new ContentColumnDefinitionReader(mockProviderDefReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ContentColumnDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockProviderDefReader.read(same(mockRenderingLocDef), anyLong()))
                .thenReturn(mockRenderingLoc);

        var content1 = rectangle(staticVal(randomFloatBox()), randomInt());
        var item1Spacing = randomFloat();
        var item1Alignment = HorizontalAlignment.fromValue(randomIntInRange(1, 3));
        var item1Indent = randomFloat();
        var item1 = itemOf(
                item1Indent,
                content1,
                item1Alignment,
                item1Spacing
        );
        var spacingAmt = randomFloat();
        var spacingItem = space(spacingAmt);
        var content2 = rectangle(staticVal(randomFloatBox()), randomInt());
        var item2Spacing = randomFloat();
        var item2Alignment = HorizontalAlignment.fromValue(randomIntInRange(1, 3));
        var item2Indent = randomFloat();
        var item2 = itemOf(
                item2Indent,
                content2,
                item2Alignment,
                item2Spacing
        );

        var definition = column(
                mockRenderingLocDef,
                WIDTH,
                Z
        )
                .withItems(item1, spacingItem, item2);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(Z, output.z);
        assertEquals(2, output.CONTENT.size());
        assertEquals(setOf(content1, content2), output.CONTENT);
        assertEquals(definition.UUID, output.UUID);
        assertIsFunctionalProviderWithData(
                output.dimensionsProviderDef,
                ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider,
                mapOf(
                        COMPONENT_UUID,
                        definition.UUID
                )
        );
        assertEquals(ContentColumn_add, output.addHookId);
        assertEquals(ContentColumn_setDimensForComponentAndContent, output.prerenderHookId);
        assertEquals(mapOf(
                Constants.COMPONENT_ORIGIN_PROVIDER,
                mockRenderingLoc,
                Constants.COMPONENT_WIDTH,
                WIDTH,
                CONTENTS,
                listOf(
                        new ContentColumnMethods.Content(
                                content1.UUID,
                                item1Indent,
                                item1Alignment,
                                item1Spacing
                        ),
                        new ContentColumnMethods.Content(
                                spacingItem.uuidForSpacingOnly(),
                                0f,
                                LEFT,
                                spacingAmt
                        ),
                        new ContentColumnMethods.Content(
                                content2.UUID,
                                item2Indent,
                                item2Alignment,
                                item2Spacing
                        )
                )
        ), output.data);

        verify(mockProviderDefReader, once()).read(mockRenderingLocDef, TIMESTAMP);
    }

    @Test
    public void testReadWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null, randomLong()));
    }
}
