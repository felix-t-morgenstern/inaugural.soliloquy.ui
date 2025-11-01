package inaugural.soliloquy.ui.readers.content.renderables;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.content.AbstractContentDefinitionReader;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.Component;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TextLineRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition;

import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static inaugural.soliloquy.tools.Tools.defaultIfNull;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class TextLineRenderableDefinitionReader extends AbstractContentDefinitionReader {
    private final TextLineRenderableFactory FACTORY;
    private final Function<String, Font> GET_FONT;

    public TextLineRenderableDefinitionReader(
            TextLineRenderableFactory factory,
            Function<String, Font> getFont,
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") ProviderAtTime nullProvider) {
        super(providerReader, nullProvider);
        FACTORY = Check.ifNull(factory, "factory");
        GET_FONT = Check.ifNull(getFont, "getFont");
    }

    public TextLineRenderable read(Component component,
                                   TextLineRenderableDefinition definition,
                                   long timestamp) {
        var font = GET_FONT.apply(definition.FONT_ID);

        var text = PROVIDER_READER.read(definition.TEXT_PROVIDER, timestamp);
        var location = definition.LOCATION_PROVIDER != null ? definition.LOCATION_PROVIDER :
                PROVIDER_READER.read(definition.LOCATION_PROVIDER_DEF, timestamp);
        var height = PROVIDER_READER.read(definition.HEIGHT_PROVIDER, timestamp);

        Map<Integer, ProviderAtTime<Color>> colors = defaultIfNull(
                definition.colorProviderIndices,
                definition.colorProviderIndicesDefs != null ?
                        definition.colorProviderIndicesDefs.entrySet().stream().collect(
                                Collectors.toMap(Map.Entry::getKey,
                                        e -> PROVIDER_READER.read(e.getValue(), timestamp))) :
                        mapOf()
        );
        var italics = defaultIfNull(definition.italicIndices, Collections.<Integer>listOf());
        var bolds = defaultIfNull(definition.boldIndices, Collections.<Integer>listOf());

        var borderThickness = provider(definition.borderThicknessProvider, timestamp);
        var borderColor = provider(definition.borderColorProvider, timestamp);

        var dropShadowSize = provider(definition.dropShadowSizeProvider, timestamp);
        var dropShadowOffset = provider(definition.dropShadowOffsetProvider, timestamp);
        var dropShadowColor = provider(definition.dropShadowColorProvider, timestamp);

        return FACTORY.make(
                font,
                text, location, height, definition.ALIGNMENT, definition.GLYPH_PADDING,
                colors, italics, bolds,
                borderThickness, borderColor,
                dropShadowSize, dropShadowOffset, dropShadowColor,
                definition.Z,
                UUID.randomUUID(),
                component);
    }
}
