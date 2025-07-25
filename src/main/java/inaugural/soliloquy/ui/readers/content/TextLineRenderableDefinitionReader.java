package inaugural.soliloquy.ui.readers.content;

import inaugural.soliloquy.tools.Check;
import inaugural.soliloquy.tools.collections.Collections;
import inaugural.soliloquy.ui.readers.providers.ProviderDefinitionReader;
import soliloquy.specs.io.graphics.assets.Font;
import soliloquy.specs.io.graphics.renderables.TextLineRenderable;
import soliloquy.specs.io.graphics.renderables.factories.TextLineRenderableFactory;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.definitions.content.TextLineRenderableDefinition;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextLineRenderableDefinitionReader extends AbstractContentDefinitionReader {
    private final TextLineRenderableFactory FACTORY;
    private final Function<String, Font> GET_FONT;

    public TextLineRenderableDefinitionReader(
            TextLineRenderableFactory factory,
            Function<String, Font> getFont,
            ProviderDefinitionReader providerReader,
            @SuppressWarnings("rawtypes") StaticProvider nullProvider) {
        super(providerReader, nullProvider);
        FACTORY = Check.ifNull(factory, "factory");
        GET_FONT = Check.ifNull(getFont, "getFont");
    }

    public TextLineRenderable read(Component component, TextLineRenderableDefinition definition) {
        var font = GET_FONT.apply(definition.FONT_ID);

        var text = PROVIDER_READER.read(definition.TEXT_PROVIDER);
        var location = PROVIDER_READER.read(definition.LOCATION_PROVIDER);
        var height = PROVIDER_READER.read(definition.HEIGHT_PROVIDER);

        var colors = Collections.<Integer, ProviderAtTime<Color>>mapOf();
        if (definition.colorProviderIndices != null) {
            Arrays.stream(definition.colorProviderIndices)
                    .forEach(c -> colors.put(c.FIRST, PROVIDER_READER.read(c.SECOND)));
        }
        var italics = definition.italicIndices == null ? Collections.<Integer>listOf() :
                Arrays.stream(definition.italicIndices).boxed().collect(Collectors.toList());
        var bolds = definition.boldIndices == null ? Collections.<Integer>listOf() :
                Arrays.stream(definition.boldIndices).boxed().collect(Collectors.toList());

        var borderThickness = provider(definition.borderThicknessProvider);
        var borderColor = provider(definition.borderColorProvider);

        var dropShadowSize = provider(definition.dropShadowSizeProvider);
        var dropShadowOffset = provider(definition.dropShadowOffsetProvider);
        var dropShadowColor = provider(definition.dropShadowColorProvider);

        return FACTORY.make(
                font,
                text, location, height, definition.JUSTIFICATION, definition.GLYPH_PADDING,
                colors, italics, bolds,
                borderThickness, borderColor,
                dropShadowSize, dropShadowOffset, dropShadowColor,
                definition.Z,
                UUID.randomUUID(),
                component);
    }
}
