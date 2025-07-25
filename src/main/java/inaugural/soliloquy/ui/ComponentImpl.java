package inaugural.soliloquy.ui;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.keyboard.KeyBindingContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static inaugural.soliloquy.io.api.Constants.WHOLE_SCREEN_PROVIDER;
import static inaugural.soliloquy.tools.collections.Collections.listOf;
import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class ComponentImpl implements Component {
    private final UUID UUID;
    private final Component CONTAINING_COMPONENT;
    private final Map<Integer, List<Renderable>> RENDERABLES;
    private final Map<Renderable, Integer> Z_INDICES_OF_INTEGERS;

    private int z;
    private boolean isDeleted;
    private ProviderAtTime<FloatBox> renderingBoundariesProvider;

    public ComponentImpl() {
        UUID = null;
        z = 0;
        renderingBoundariesProvider = WHOLE_SCREEN_PROVIDER;
        CONTAINING_COMPONENT = null;
        RENDERABLES = mapOf();
        Z_INDICES_OF_INTEGERS = mapOf();
    }

    @SuppressWarnings("ConstantConditions")
    public ComponentImpl(UUID uuid, int z,
                         ProviderAtTime<FloatBox> renderingBoundariesProvider,
                         Component containingStack) {
        UUID = Check.ifNull(uuid, "uuid");
        this.z = z;
        this.renderingBoundariesProvider =
                Check.ifNull(renderingBoundariesProvider, "renderingBoundariesProvider");
        CONTAINING_COMPONENT = Check.ifNull(containingStack, "containingStack");
        CONTAINING_COMPONENT.add(this);
        RENDERABLES = mapOf();
        Z_INDICES_OF_INTEGERS = mapOf();
    }

    @Override
    public KeyBindingContext keyBindingContext() {
        return null;
    }

    @Override
    public void add(Renderable renderable) throws IllegalArgumentException {
        if (renderable.component() != null && renderable.component() != this) {
            throw new IllegalArgumentException(
                    "ComponentImpl.add: renderable already in another Component");
        }
        if (Z_INDICES_OF_INTEGERS.containsKey(renderable)) {
            var previousZIndex = Z_INDICES_OF_INTEGERS.get(renderable);
            if (previousZIndex == renderable.getZ()) {
                return;
            }
            RENDERABLES.get(previousZIndex).remove(renderable);
            if (RENDERABLES.get(previousZIndex).isEmpty()) {
                RENDERABLES.remove(previousZIndex);
            }
        }
        Z_INDICES_OF_INTEGERS.put(renderable, renderable.getZ());
        if (!RENDERABLES.containsKey(renderable.getZ())) {
            RENDERABLES.put(renderable.getZ(), listOf(renderable));
        }
        else {
            RENDERABLES.get(renderable.getZ()).add(renderable);
        }
    }

    @Override
    public Set<Renderable> content() {
        return Set.of();
    }

    public void remove(Renderable renderable) throws IllegalArgumentException {
        var z = renderable.getZ();
        RENDERABLES.get(z).remove(renderable);
        if (RENDERABLES.get(z).isEmpty()) {
            RENDERABLES.remove(z);
        }
    }

    @Override
    public ProviderAtTime<FloatBox> getRenderingBoundariesProvider() {
        return renderingBoundariesProvider;
    }

    @Override
    public void setRenderingBoundariesProvider(ProviderAtTime<FloatBox> providerAtTime)
            throws IllegalArgumentException, UnsupportedOperationException {
        if (CONTAINING_COMPONENT == null) {
            throw new UnsupportedOperationException(
                    "RenderableStackImpl.setRenderingBoundariesProvider: cannot assign new " +
                            "rendering boundaries for top-level Component");
        }
        renderingBoundariesProvider = Check.ifNull(providerAtTime, "providerAtTime");
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setZ(int z) {
        if (CONTAINING_COMPONENT == null) {
            throw new UnsupportedOperationException(
                    "RenderableStackImpl.setZ: cannot set z value on top-level Component");
        }
        this.z = z;
        CONTAINING_COMPONENT.add(this);
    }

    @Override
    public Component component() {
        return CONTAINING_COMPONENT;
    }

    @Override
    public void delete() {
        RENDERABLES.values().forEach(renderables -> renderables.forEach(Renderable::delete));
        isDeleted = true;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public UUID uuid() {
        return UUID;
    }
}
