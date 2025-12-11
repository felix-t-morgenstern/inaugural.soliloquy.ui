package inaugural.soliloquy.ui.components.contentcolumn;

import soliloquy.specs.common.valueobjects.Pair;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.util.List;
import java.util.UUID;

import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static java.util.UUID.randomUUID;
import static soliloquy.specs.common.valueobjects.Pair.pairOf;

public class ContentColumnDefinition extends AbstractContentDefinition {
    public final AbstractProviderDefinition<Vertex> RENDERING_LOC_DEF;
    public final float WIDTH;
    public final List<Pair<AbstractContentDefinition, Float>> CONTENT_AND_SPACINGS;

    private ContentColumnDefinition(AbstractProviderDefinition<Vertex> renderingLocDef,
                                    float width,
                                    int z,
                                    UUID uuid) {
        super(z, uuid);
        RENDERING_LOC_DEF = renderingLocDef;
        WIDTH = width;
        CONTENT_AND_SPACINGS = listOf();
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

    public ContentColumnDefinition withContent(AbstractContentDefinition content, float spacingAfter) {
        CONTENT_AND_SPACINGS.add(pairOf(content, spacingAfter));

        return this;
    }

    public ContentColumnDefinition withContent(
            Pair<AbstractContentDefinition,Float>... contentAndSpacingAfter) {
        for(var c : contentAndSpacingAfter) {
            CONTENT_AND_SPACINGS.add(pairOf(c.FIRST, c.SECOND));
        }

        return this;
    }
}
