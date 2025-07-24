package inaugural.soliloquy.ui.test.definitions.content;

import inaugural.soliloquy.tools.testing.Mock;
import inaugural.soliloquy.ui.definitions.colorshifting.ShiftDefinitionReader;
import inaugural.soliloquy.ui.definitions.providers.ProviderDefinitionReader;
import soliloquy.specs.common.entities.Action;
import soliloquy.specs.common.valueobjects.FloatBox;
import soliloquy.specs.io.graphics.renderables.colorshifting.ColorShift;
import soliloquy.specs.io.graphics.renderables.providers.ProviderAtTime;
import soliloquy.specs.io.graphics.renderables.providers.StaticProvider;
import soliloquy.specs.io.graphics.rendering.RenderableStack;
import soliloquy.specs.ui.definitions.colorshifting.ShiftDefinition;
import soliloquy.specs.ui.definitions.providers.AbstractProviderDefinition;

import java.awt.*;
import java.util.function.Function;

import static inaugural.soliloquy.tools.random.Random.randomInt;
import static inaugural.soliloquy.tools.random.Random.randomString;
import static inaugural.soliloquy.tools.testing.Mock.generateMockLookupFunctionWithId;
import static org.mockito.Mockito.*;

public abstract class AbstractContentDefinitionTests {
    protected final String ON_PRESS_ID = randomString();
    protected final String ON_RELEASE_ID = randomString();
    protected final String ON_MOUSE_OVER_ID = randomString();
    protected final String ON_MOUSE_LEAVE_ID = randomString();
    @SuppressWarnings("rawtypes") private final Mock.LookupAndEntitiesWithId<Action>
            MOCK_ACTIONS_AND_LOOKUP =
            generateMockLookupFunctionWithId(Action.class, ON_PRESS_ID, ON_RELEASE_ID,
                    ON_MOUSE_OVER_ID, ON_MOUSE_LEAVE_ID);
    @SuppressWarnings("rawtypes") protected final Action MOCK_ON_PRESS =
            MOCK_ACTIONS_AND_LOOKUP.entities.getFirst();
    @SuppressWarnings("rawtypes") protected final Action MOCK_ON_RELEASE =
            MOCK_ACTIONS_AND_LOOKUP.entities.get(1);
    @SuppressWarnings("rawtypes") protected final Action MOCK_ON_MOUSE_OVER =
            MOCK_ACTIONS_AND_LOOKUP.entities.get(2);
    @SuppressWarnings("rawtypes") protected final Action MOCK_ON_MOUSE_LEAVE =
            MOCK_ACTIONS_AND_LOOKUP.entities.get(3);
    @SuppressWarnings("rawtypes") protected final Function<String, Action> MOCK_GET_ACTION =
            MOCK_ACTIONS_AND_LOOKUP.lookup;
    protected final int ON_PRESS_BUTTON = randomInt();
    protected final int ON_RELEASE_BUTTON = randomInt();

    protected final int Z = randomInt();

    @org.mockito.Mock protected ProviderDefinitionReader mockProviderDefinitionReader;
    @org.mockito.Mock protected ShiftDefinitionReader mockShiftDefinitionReader;
    @org.mockito.Mock protected RenderableStack mockStack;
    @SuppressWarnings("rawtypes") @org.mockito.Mock protected StaticProvider mockNullProvider;

    @org.mockito.Mock protected ShiftDefinition mockShiftDefinition;
    @org.mockito.Mock protected ColorShift mockShift;

    @org.mockito.Mock protected AbstractProviderDefinition<FloatBox> mockAreaProviderDefinition;
    @org.mockito.Mock protected ProviderAtTime<FloatBox> mockAreaProvider;

    @org.mockito.Mock protected AbstractProviderDefinition<Float> mockBorderThicknessDefinition;
    @org.mockito.Mock protected AbstractProviderDefinition<Color> mockBorderColorDefinition;
    @org.mockito.Mock protected ProviderAtTime<Float> mockBorderThickness;
    @org.mockito.Mock protected ProviderAtTime<Color> mockBorderColor;

    @org.mockito.Mock protected AbstractProviderDefinition<Integer> mockTextureIdProviderDefinition;
    @org.mockito.Mock protected AbstractProviderDefinition<Float>
            mockTextureWidthProviderDefinition;
    @org.mockito.Mock protected AbstractProviderDefinition<Float>
            mockTextureHeightProviderDefinition;
    @org.mockito.Mock protected ProviderAtTime<Integer> mockTextureIdProvider;
    @org.mockito.Mock protected ProviderAtTime<Float> mockTextureWidthProvider;
    @org.mockito.Mock protected ProviderAtTime<Float> mockTextureHeightProvider;

    protected void setUp() {
        lenient().when(mockShiftDefinitionReader.read(any())).thenReturn(mockShift);

        lenient().when(mockProviderDefinitionReader.read(mockAreaProviderDefinition)).thenReturn(
                mockAreaProvider);

        lenient().when(mockProviderDefinitionReader.read(mockTextureIdProviderDefinition))
                .thenReturn(mockTextureIdProvider);
        lenient().when(mockProviderDefinitionReader.read(mockTextureWidthProviderDefinition))
                .thenReturn(mockTextureWidthProvider);
        lenient().when(mockProviderDefinitionReader.read(mockTextureHeightProviderDefinition))
                .thenReturn(mockTextureHeightProvider);

        lenient().when(mockProviderDefinitionReader.read(mockBorderThicknessDefinition)).thenReturn(
                mockBorderThickness);
        lenient().when(mockProviderDefinitionReader.read(mockBorderColorDefinition)).thenReturn(
                mockBorderColor);
    }
}
