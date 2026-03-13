package inaugural.soliloquy.ui.components.contentrow;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.List;
import java.util.Set;

import static inaugural.soliloquy.tools.Tools.defaultIfNullElseTransform;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowMethods.*;
import static inaugural.soliloquy.ui.components.contentrow.ContentRowMethods.Content;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentRowDefinitionReader {
    private final ProviderDefinitionReader PROVIDER_DEF_READER;

    public ContentRowDefinitionReader(ProviderDefinitionReader providerDefReader) {
        PROVIDER_DEF_READER = Check.ifNull(providerDefReader, "providerDefReader");
    }

    public ComponentDefinition read(ContentRowDefinition definition, long timestamp) {
        Check.ifNull(definition, "definition");

        Set<AbstractContentDefinition> componentContents = setOf();
        List<Content> contentsForData = listOf();

        definition.ITEMS.forEach(item -> {
            if (item.content() != null) {
                componentContents.add(item.content());
            }
            contentsForData.add(new Content(
                    defaultIfNullElseTransform(item.content(), c -> c.UUID,
                            item.uuidForSpacingOnly()),
                    item.indent(),
                    item.alignment(),
                    item.spacingAfter()
            ));
        });

        return component(
                definition.Z,
                componentContents,
                definition.UUID
        )
                .withDimensions(functionalProvider(
                                ContentRow_setAndRetrieveDimensForComponentAndContentForProvider,
                                FloatBox.class
                        )
                                .withData(mapOf(
                                        COMPONENT_UUID,
                                        definition.UUID
                                ))
                )
                .withAddHook(ContentRow_add)
                .withPrerenderHook(ContentRow_setDimensForComponentAndContent)
                .withData(mapOf(
                        COMPONENT_RENDERING_LOC,
                        PROVIDER_DEF_READER.read(definition.RENDERING_LOC_DEF, timestamp),
                        COMPONENT_HEIGHT,
                        definition.HEIGHT,
                        CONTENTS,
                        contentsForData
                ));
    }
}
