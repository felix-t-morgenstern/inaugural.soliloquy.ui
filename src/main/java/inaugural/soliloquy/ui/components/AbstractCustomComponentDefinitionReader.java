package inaugural.soliloquy.ui.components;

import soliloquy.specs.ui.definitions.content.AbstractContentDefinition;
import soliloquy.specs.ui.definitions.content.ComponentDefinition;

public abstract class AbstractCustomComponentDefinitionReader<TDef extends AbstractContentDefinition> {
    protected abstract ComponentDefinition read(TDef def, long timestamp);
}
