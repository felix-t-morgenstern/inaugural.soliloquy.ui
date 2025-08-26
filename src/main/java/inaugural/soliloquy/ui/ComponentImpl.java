package inaugural.soliloquy.ui;

import inaugural.soliloquy.tools.Check;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.Renderable;
import soliloquy.specs.io.graphics.renderables.RenderableWithMouseEvents;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.ui.Component;
import soliloquy.specs.ui.keyboard.KeyBindingContext;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static inaugural.soliloquy.tools.collections.Collections.setOf;

public class ComponentImpl implements Component {
    private final UUID UUID;
    private final Component CONTAINING_COMPONENT;
    private final KeyBindingContext BINDING_CONTEXT;
    private final Set<Renderable> RENDERABLES;
    private final Consumer<RenderableWithMouseEvents> ADD_TO_CAPTURING;
    private final Consumer<RenderableWithMouseEvents> REMOVE_FROM_CAPTURING;
    private final int TIER;

    private int z;
    private boolean isDeleted;
    private ProviderAtTime<FloatBox> renderingBoundariesProvider;

    @SuppressWarnings("ConstantConditions")
    public ComponentImpl(UUID uuid,
                         int z,
                         KeyBindingContext bindingContext,
                         Component containingComponent,
                         ProviderAtTime<FloatBox> renderingBoundariesProvider,
                         Consumer<RenderableWithMouseEvents> addToCapturing,
                         Consumer<RenderableWithMouseEvents> removeFromCapturing) {
        UUID = Check.ifNull(uuid, "uuid");
        this.z = z;
        TIER = containingComponent == null ? 0 : (containingComponent.tier() + 1);
        this.renderingBoundariesProvider =
                Check.ifNull(renderingBoundariesProvider, "renderingBoundariesProvider");
        CONTAINING_COMPONENT = containingComponent;
        if (CONTAINING_COMPONENT != null) {
            CONTAINING_COMPONENT.add(this);
        }
        BINDING_CONTEXT = Check.ifNull(bindingContext, "bindingContext");
        RENDERABLES = setOf();
        ADD_TO_CAPTURING = Check.ifNull(addToCapturing, "addToCapturing");
        REMOVE_FROM_CAPTURING = Check.ifNull(removeFromCapturing, "removeFromCapturing");
    }

    @Override
    public KeyBindingContext keyBindingContext() {
        return BINDING_CONTEXT;
    }

    @Override
    public void add(Renderable renderable) throws IllegalArgumentException {
        Check.ifNull(renderable, "renderable");
        if (renderable.component() != null && renderable.component() != this) {
            throw new IllegalArgumentException(
                    "ComponentImpl.add: renderable already in another Component");
        }
        if (renderable instanceof Component) {
            var newComponentTier = ((Component) renderable).tier();
            if (newComponentTier != TIER + 1) {
                throw new IllegalArgumentException(
                        "ComponentImpl.add: renderable is Component whose tier (" +
                                newComponentTier +
                                ") is not one greater than this Component's tier (" + TIER + ")");
            }
        }
        RENDERABLES.add(renderable);
        if (renderable instanceof RenderableWithMouseEvents) {
            ADD_TO_CAPTURING.accept((RenderableWithMouseEvents) renderable);
        }
    }

    @Override
    public void remove(Renderable renderable) throws IllegalArgumentException {
        Check.ifNull(renderable, "renderable");
        if (renderable.component() != this) {
            throw new IllegalArgumentException(
                    "ComponentImpl.remove: renderable not in this Component");
        }
        RENDERABLES.remove(renderable);
        if (renderable instanceof RenderableWithMouseEvents) {
            REMOVE_FROM_CAPTURING.accept((RenderableWithMouseEvents) renderable);
        }
    }

    @Override
    public Set<Renderable> contents() {
        return setOf(RENDERABLES);
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
    public int tier() {
        return TIER;
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
        RENDERABLES.forEach(Renderable::delete);
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
