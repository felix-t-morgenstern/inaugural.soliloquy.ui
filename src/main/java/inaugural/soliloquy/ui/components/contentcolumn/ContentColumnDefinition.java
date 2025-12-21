package inaugural.soliloquy.ui.components.contentcolumn;

import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static java.util.UUID.randomUUID;

public class ContentColumnDefinition extends AbstractContentDefinition {
    public final AbstractProviderDefinition<Vertex> RENDERING_LOC_DEF;
    public final float WIDTH;
    public final List<Item> ITEMS;

    private ContentColumnDefinition(AbstractProviderDefinition<Vertex> renderingLocDef,
                                    float width,
                                    int z,
                                    UUID uuid) {
        super(z, uuid);
        RENDERING_LOC_DEF = renderingLocDef;
        WIDTH = width;
        ITEMS = listOf();
    }

    public static ContentColumnDefinition column(
            AbstractProviderDefinition<Vertex> renderingLocDef,
            float width,
            int z,
            UUID uuid) {
        return new ContentColumnDefinition(renderingLocDef, width, z, uuid);
    }

    public static ContentColumnDefinition column(
            AbstractProviderDefinition<Vertex> renderingLocDef,
            float width,
            int z) {
        return column(renderingLocDef, width, z, randomUUID());
    }

    public ContentColumnDefinition withItem(Item item) {
        ITEMS.add(item);

        return this;
    }

    public ContentColumnDefinition withItems(Item... items) {
        ITEMS.addAll(Arrays.asList(items));

        return this;
    }

    public record Item(AbstractContentDefinition content,
                       UUID itemUuid,
                       float spacingAfter,
                       HorizontalAlignment alignment,
                       float indent) {
        public static Item itemOf(AbstractContentDefinition content,
                                  float spacingAfter,
                                  HorizontalAlignment alignment,
                                  float indent) {
            return new Item(content, null, spacingAfter, alignment, indent);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  float spacingAfter,
                                  float indent) {
            return itemOf(content, spacingAfter, HorizontalAlignment.LEFT, indent);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  HorizontalAlignment alignment,
                                  float indent) {
            return itemOf(content, 0f, alignment, indent);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  HorizontalAlignment alignment) {
            return itemOf(content, 0f, alignment, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  float spacingAfter) {
            return itemOf(content, spacingAfter, HorizontalAlignment.LEFT, 0f);
        }

        public static Item space(float spacing) {
            return new Item(null, randomUUID(), spacing, null, 0f);
        }
    }
}
