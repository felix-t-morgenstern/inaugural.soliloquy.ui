package inaugural.soliloquy.ui.components.contentrow;

import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowDefinition.VerticalAlignment.TOP;
import static java.util.UUID.randomUUID;

@SuppressWarnings("unused")
public class ContentRowDefinition extends AbstractContentDefinition {
    public final AbstractProviderDefinition<Vertex> RENDERING_LOC_DEF;
    public final float HEIGHT;
    public final List<Item> ITEMS;

    private ContentRowDefinition(AbstractProviderDefinition<Vertex> renderingLocDef,
                                 float height,
                                 int z,
                                 UUID uuid) {
        super(z, uuid);
        RENDERING_LOC_DEF = renderingLocDef;
        HEIGHT = height;
        ITEMS = listOf();
    }

    public static ContentRowDefinition row(
            AbstractProviderDefinition<Vertex> renderingLocDef,
            float height,
            int z,
            UUID uuid) {
        return new ContentRowDefinition(renderingLocDef, height, z, uuid);
    }

    public static ContentRowDefinition row(
            AbstractProviderDefinition<Vertex> renderingLocDef,
            float height,
            int z) {
        return row(renderingLocDef, height, z, randomUUID());
    }

    public ContentRowDefinition withItem(Item item) {
        ITEMS.add(item);

        return this;
    }

    public ContentRowDefinition withItems(Item... items) {
        ITEMS.addAll(Arrays.asList(items));

        return this;
    }

    public record Item(UUID uuidForSpacingOnly,
                       float indent,
                       AbstractContentDefinition content,
                       VerticalAlignment alignment,
                       float spacingAfter) {
        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  VerticalAlignment alignment,
                                  float spacingAfter) {
            return new Item(null, indent, content, alignment, spacingAfter);
        }

        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  float spacingAfter) {
            return itemOf(indent, content, TOP, spacingAfter);
        }

        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  VerticalAlignment alignment) {
            return itemOf(indent, content, alignment, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content) {
            return itemOf(0f, content, TOP, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  VerticalAlignment alignment) {
            return itemOf(0f, content, alignment, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  VerticalAlignment alignment,
                                  float spacingAfter) {
            return itemOf(0f, content, alignment, spacingAfter);
        }

        public static Item itemOf(float indent, AbstractContentDefinition content) {
            return itemOf(indent, content, TOP);
        }

        public static Item itemOf(AbstractContentDefinition content, float spacingAfter) {
            return itemOf(0f, content, TOP, spacingAfter);
        }

        public static Item space(float spacing) {
            return new Item(randomUUID(), 0f, null, TOP, spacing);
        }
    }

    public enum VerticalAlignment {
        TOP(1),
        CENTER(2),
        BOTTOM(3);

        private final int VALUE;

        VerticalAlignment(int value) {
            VALUE = value;
        }

        public int getValue() {
            return VALUE;
        }

        public static VerticalAlignment fromValue(Integer value) {
            if (value == null) {
                return null;
            }
            return switch (value) {
                case 1 -> TOP;
                case 2 -> CENTER;
                case 3 -> BOTTOM;
                default -> throw new IllegalArgumentException("VerticalAlignment: value (" + value +
                        ") does not correspond to valid enum type");
            };
        }
    }
}
