package inaugural.soliloquy.ui.components;

import soliloquy.specs.io.graphics.renderables.Component;

import java.util.function.Supplier;

import static inaugural.soliloquy.tools.collections.Collections.getFromData;

public class ComponentMethods {
    public static <T> T getFromComponentDataOrDefault(Component component,
                                                      String key,
                                                      Supplier<T> getDefault) {
        T val = getFromData(component, key);
        if (val == null) {
            val = getDefault.get();
            component.data().put(key, val);
        }
        return val;
    }
}
