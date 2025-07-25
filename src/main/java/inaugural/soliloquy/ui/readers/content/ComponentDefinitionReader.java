package inaugural.soliloquy.ui.readers.content;

import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

public class ComponentDefinitionReader {
    private final ContentDefinitionReader CONTENT_READER;

    public ComponentDefinitionReader(ContentDefinitionReader contentReader) {
        CONTENT_READER = contentReader;
    }

    public Component read(ComponentDefinition definition, Component component, long timestamp) {
        return null;
    }
}
