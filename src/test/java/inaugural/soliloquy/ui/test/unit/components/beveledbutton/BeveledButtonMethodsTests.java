package inaugural.soliloquy.ui.components.beveledbutton;

import inaugural.soliloquy.ui.test.unit.components.ComponentMethodsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.ui.components.beveledbutton.BeveledButtonMethods.BEVEL_LAST_TIMESTAMP;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static soliloquy.specs.io.graphics.renderables.providers.FunctionalProvider.Inputs.providerInputs;

@ExtendWith(MockitoExtension.class)
public class BeveledButtonMethodsTests extends ComponentMethodsTest {
    private BeveledButtonMethods methods;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lenient().when(mockComponentData.get(BEVEL_LAST_TIMESTAMP)).thenReturn(TIMESTAMP);

        methods = new BeveledButtonMethods(MOCK_GET_COMPONENT);
    }

    @Test
    public void testConstructorWithInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new BeveledButtonMethods(null));
    }

    @Test
    public void testProvideVertex_BeveledButtonWithNewTimestamp() {
        var inputs = providerInputs(TIMESTAMP + 1, null, mapOf(

        ));
    }
}
