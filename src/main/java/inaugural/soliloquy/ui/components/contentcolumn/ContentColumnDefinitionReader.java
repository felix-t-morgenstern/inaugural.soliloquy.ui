package inaugural.soliloquy.ui.components.contentcolumn;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.List;
import java.util.Set;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.*;
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

        Set<AbstractContentDefinition> componentContents = setOf();
        List<ContentColumnMethods.Content> contentsForData = listOf();

        definition.ITEMS.forEach(item -> {
            if (item.content() != null) {
                componentContents.add(item.content());
            }
            contentsForData.add(new ContentColumnMethods.Content(
                    defaultIfNull(item.content(), c -> c.UUID, item.uuidForSpacingOnly()),
                    item.spacingAfter(),
                    item.alignment(),
                    item.indent()
            ));
        });

        return component(
                definition.Z,
                componentContents,
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
                        COMPONENT_RENDERING_LOC,
                        PROVIDER_DEF_READER.read(definition.RENDERING_LOC_DEF, timestamp),
                        COMPONENT_WIDTH,
                        definition.WIDTH,
                        CONTENTS,
                        contentsForData
                ));
    }
}
