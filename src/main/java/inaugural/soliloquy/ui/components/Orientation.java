package inaugural.soliloquy.ui.components;

public enum Orientation {
    HORIZONTAL(1),
    VERTICAL(2);

    private final int VALUE;

    Orientation(int value) {
        VALUE = value;
    }

    public int getValue() {
        return VALUE;
    }

    public static Orientation fromValue(Integer value) {
        return switch (value) {
            case 1 -> HORIZONTAL;
            case 2 -> VERTICAL;
            case null -> null;
            default -> throw new IllegalArgumentException(
                    "inaugural.soliloquy.ui.components.Orientation: value (" + value +
                            ") does not correspond to valid enum type");
        };
    }
}
