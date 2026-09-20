package inaugural.soliloquy.ui.components.content.row;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.Constants;
import inaugural.soliloquy.ui.components.AbstractCustomComponentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

import java.util.List;
import java.util.Set;

import static inaugural.soliloquy.tools.Tools.defaultIfNullElseTransform;
import static inaugural.soliloquy.tools.collections.Collections.*;
import static inaugural.soliloquy.ui.Constants.*;
import static inaugural.soliloquy.ui.components.content.row.ContentRowMethods.*;
import static soliloquy.specs.ui.definitions.content.ComponentDefinition.component;
import static soliloquy.specs.ui.definitions.providers.FunctionalProviderDefinition.functionalProvider;

public class ContentRowDefinitionReader
        extends AbstractCustomComponentDefinitionReader<ContentRowDefinition> {
    public ContentRowDefinitionReader(ProviderDefinitionReader providerDefReader) {
        super(providerDefReader);
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

        var renderingLoc = providerOrReadDef(
                definition.renderingLocProvider,
                definition.renderingLocProviderDef,
                timestamp
        );

        var innerData = Collections.<String, Object>mapOf(COMPONENT_UUID, definition.UUID);

        return component(
                definition.z,
                componentContents,
                definition.UUID
        )
                .withDimensions(
                        functionalProvider(
                                ContentRow_setAndRetrieveDimensForComponentAndContentForProvider,
                                FloatBox.class
                        ).withData(innerData)
                )
                .withUnadjDimensions(
                        functionalProvider(ContentRow_provideUnadjDimens, FloatBox.class)
                                .withData(innerData)
                )
                .withAddHook(ContentRow_add)
                .withPrerenderHook(ContentRow_setDimensForComponentAndContent)
                .withData(mapOf(
                        Constants.COMPONENT_ORIGIN_PROVIDER,
                        renderingLoc,
                        COMPONENT_HEIGHT,
                        definition.LENGTH,
                        CONTENTS,
                        contentsForData
                ));
    }
}
