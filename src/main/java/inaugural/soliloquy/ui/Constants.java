package inaugural.soliloquy.ui;

import java.awt.*;
import java.util.Map;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;

public class Constants {
    private final static String BLACK = "black";
    private final static String DARKGREY = "darkgrey";
    private final static String GREY = "grey";
    private final static String LIGHTGREY = "lightgrey";
    private final static String WHITE = "white";
    private final static String RED = "red";
    private final static String ORANGE = "orange";
    private final static String YELLOW = "yellow";
    private final static String YELLOWGREEN = "yellowgreen";
    private final static String GREEN = "green";
    private final static String SPRINGGREEN = "springgreen";
    private final static String CYAN = "cyan";
    private final static String INDIGO = "indigo";
    private final static String BLUE = "blue";
    private final static String PURPLE = "purple";
    private final static String MAGENTA = "magenta";
    private final static String HOTPINK = "hotpink";

    public static final Map<String, Color> COLOR_PRESETS = mapOf(
            BLACK,
            new Color(0, 0, 0),
            DARKGREY,
            new Color(63, 63, 63),
            GREY,
            new Color(127, 127, 127),
            LIGHTGREY,
            new Color(191, 191, 191),
            WHITE,
            new Color(255, 255, 255),
            RED,
            new Color(255, 0, 0),
            ORANGE,
            new Color(255, 127, 0),
            YELLOW,
            new Color(255, 255, 0),
            YELLOWGREEN,
            new Color(127, 255, 0),
            GREEN,
            new Color(0, 255, 0),
            SPRINGGREEN,
            new Color(0, 255, 127),
            CYAN,
            new Color(0, 255, 255),
            INDIGO,
            new Color(0, 127, 255),
            BLUE,
            new Color(0, 0, 255),
            PURPLE,
            new Color(127, 0, 255),
            MAGENTA,
            new Color(255, 0, 255),
            HOTPINK,
            new Color(255, 0, 127)
    );
}
