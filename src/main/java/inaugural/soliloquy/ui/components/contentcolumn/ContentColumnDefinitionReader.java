package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.common.valueobjects.Vertex;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.COMPONENT_UUID;
import static inaugural.soliloquy.ui.Constants.CONTENTS_TOP_LEFT_LOCS;
import static inaugural.soliloquy.ui.components.contentcolumn.ContentColumnMethods.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentColumnDefinitionReader {
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public ContentColumnDefinitionReader(ProviderDefinitionReader providerDefReader) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ComponentDefinition read(ContentColumnDefinition definition, long timestamp) {
        Check.ifNull(definition, "definition");

        Set<AbstractContentDefinition> content = setOf();
        List<ContentColumnMethods.Content> contents = listOf();

        definition.ITEMS.forEach(item -> {
            if (item.content() != null) {
                content.add(item.content());
            }
            contents.add(new ContentColumnMethods.Content(
                    defaultIfNull(item.content(), item.itemUuid(), c -> c.UUID),
                    item.spacingAfter(),
                    item.alignment(),
                    item.indent()
            ));
        });

        return component(
                definition.Z,
                content,
                definition.UUID
        )
                .withDimensions(functionalProvider(
                                ContentColumn_setAndRetrieveDimensForComponentAndContentForProvider,
                                FloatBox.class
                        )
                                .withData(mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))
                )
                .withAddHook(ContentColumn_add)
                .withPrerenderHook(ContentColumn_setDimensForComponentAndContent)
                .withData(mapOf(
                        RENDERING_LOC,
                        PROVIDER_DEF_READER.read(definition.RENDERING_LOC_DEF, timestamp),
                        WIDTH,
                        definition.WIDTH,
                        CONTENTS,
                        contents,
                        CONTENTS_TOP_LEFT_LOCS,
                        Collections.<UUID, Vertex>mapOf()
                ));
    }
}
