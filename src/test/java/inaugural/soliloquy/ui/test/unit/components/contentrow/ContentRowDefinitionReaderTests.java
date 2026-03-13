package inaugural.soliloquy.ui.test.unit.components.contentrow;

import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.components.contentrow.ContentRowDefinitionReader;
import inaugural.soliloquy.ui.test.unit.components.ComponentDefinitionReaderTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.tools.random.Random.*;
import static inaugural.soliloquy.tools.testing.Assertions.once;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.itemOf;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.Item.space;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment.TOP;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.row;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowMethods.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static soliloquy.specs.ui.definitions.content.RectangleRenderableDefinition.rectangle;
import static soliloquy.specs.ui.definitions.providers.StaticProviderDefinition.staticVal;

@ExtendWith(MockitoExtension.class)
public class ContentRowDefinitionReaderTests extends ComponentDefinitionReaderTest {
    private final float HEIGHT = randomFloat();

    @Mock private AbstractProviderDefinition<Vertex> mockRenderingLocDef;
    @Mock private ProviderAtTime<Vertex> mockRenderingLoc;

    private ContentRowDefinitionReader reader;

    @BeforeEach
    public void setUp() {
        reader = new ContentRowDefinitionReader(mockProviderDefReader);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new ContentRowDefinitionReader(null));
    }

    @Test
    public void testRead() {
        when(mockProviderDefReader.read(same(mockRenderingLocDef), anyLong()))
                .thenReturn(mockRenderingLoc);

        var content1 = rectangle(staticVal(randomFloatBox()), randomInt());
        var item1Spacing = randomFloat();
        var item1Alignment = VerticalAlignment.fromValue(randomIntInRange(1, 3));
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
        var item2Alignment = VerticalAlignment.fromValue(randomIntInRange(1, 3));
        var item2Indent = randomFloat();
        var item2 = itemOf(
                item2Indent,
                content2,
                item2Alignment,
                item2Spacing
        );

        var definition = row(
                mockRenderingLocDef,
                HEIGHT,
                Z
        )
                .withItems(item1, spacingItem, item2);

        var output = reader.read(definition, TIMESTAMP);

        assertNotNull(output);
        assertEquals(Z, output.Z);
        assertEquals(2, output.CONTENT.size());
        assertEquals(setOf(content1, content2), output.CONTENT);
        assertEquals(definition.UUID, output.UUID);
        assertIsFunctionalProviderWithData(
                output.dimensionsProviderDef,
                ContentRow_setAndRetrieveDimensForComponentAndContentForProvider,
                mapOf(
                        COMPONENT_UUID,
                        definition.UUID
                )
        );
        assertEquals(ContentRow_add, output.addHookId);
        assertEquals(ContentRow_setDimensForComponentAndContent, output.prerenderHookId);
        assertEquals(mapOf(
                COMPONENT_RENDERING_LOC,
                mockRenderingLoc,
                Constants.COMPONENT_HEIGHT,
                HEIGHT,
                CONTENTS,
                listOf(
                        new Content(
                                content1.UUID,
                                item1Indent,
                                item1Alignment,
                                item1Spacing
                        ),
                        new Content(
                                spacingItem.uuidForSpacingOnly(),
                                0f,
                                TOP,
                                spacingAmt
                        ),
                        new Content(
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
