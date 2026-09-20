package inaugural.soliloquy.ui.components.content.column;

import inaugural.soliloquy.ui.components.content.AbstractContentSpanDefinition;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.io.graphics.renderables.HorizontalAlignment;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.io.graphics.renderables.HorizontalAlignment.LEFT;

@SuppressWarnings("unused")
public class ContentColumnDefinition extends AbstractContentSpanDefinition {
    public final List<Item> ITEMS;

    private ContentColumnDefinition(ProviderAtTime<Vertex> renderingLocProvider,
                                    AbstractProviderDefinition<Vertex> renderingLocProviderDef,
                                    float width,
                                    int z,
                                    UUID uuid) {
        super(renderingLocProvider, renderingLocProviderDef, width, z, uuid);
        ITEMS = listOf();
    }

    public static ContentColumnDefinition column(
            ProviderAtTime<Vertex> renderingLocProvider,
            float width,
            int z,
            UUID uuid) {
        return new ContentColumnDefinition(renderingLocProvider, null, width, z, uuid);
    }

    public static ContentColumnDefinition column(
            ProviderAtTime<Vertex> renderingLocProvider,
            float width,
            int z) {
        return column(renderingLocProvider, width, z, randomUUID());
    }

    public static ContentColumnDefinition column(
            AbstractProviderDefinition<Vertex> renderingLocProviderDef,
            float width,
            int z,
            UUID uuid) {
        return new ContentColumnDefinition(null, renderingLocProviderDef, width, z, uuid);
    }

    public static ContentColumnDefinition column(
            AbstractProviderDefinition<Vertex> renderingLocProviderDef,
            float width,
            int z) {
        return column(renderingLocProviderDef, width, z, randomUUID());
    }

    public static ContentColumnDefinition column(float width) {
        return column((ProviderAtTime<Vertex>) null, width, 0, randomUUID());
    }

    public ContentColumnDefinition withItem(Item item) {
        ITEMS.add(item);

        return this;
    }

    public ContentColumnDefinition withItems(Item... items) {
        ITEMS.addAll(Arrays.asList(items));

        return this;
    }

    public record Item(UUID uuidForSpacingOnly,
                       float indent,
                       AbstractContentDefinition content,
                       HorizontalAlignment alignment,
                       float spacingAfter) {
        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  HorizontalAlignment alignment,
                                  float spacingAfter) {
            return new Item(null, indent, content, alignment, spacingAfter);
        }

        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  float spacingAfter) {
            return itemOf(indent, content, LEFT, spacingAfter);
        }

        public static Item itemOf(float indent,
                                  AbstractContentDefinition content,
                                  HorizontalAlignment alignment) {
            return itemOf(indent, content, alignment, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content) {
            return itemOf(0f, content, LEFT, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  HorizontalAlignment alignment) {
            return itemOf(0f, content, alignment, 0f);
        }

        public static Item itemOf(AbstractContentDefinition content,
                                  HorizontalAlignment alignment,
                                  float spacingAfter) {
            return itemOf(0f, content, alignment, spacingAfter);
        }

        public static Item itemOf(float indent, AbstractContentDefinition content) {
            return itemOf(indent, content, LEFT);
        }

        public static Item itemOf(AbstractContentDefinition content, float spacingAfter) {
            return itemOf(0f, content, LEFT, spacingAfter);
        }

        public static Item space(float spacing) {
            return new Item(randomUUID(), 0f, null, LEFT, spacing);
        }
    }
}
