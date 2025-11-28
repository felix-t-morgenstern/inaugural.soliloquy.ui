package inaugural.soliloquy.ui;

import java.awt.*;
import java.util.Map;
import java.util.Set;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;

public class Constants {
    private final static String BLACK = "black";
    private final static String DARK_GREY = "darkgrey";
    private final static String GREY = "grey";
    private final static String LIGHT_GREY = "lightgrey";
    private final static String WHITE = "white";
    private final static String DARK_RED = "darkred";
    private final static String DARK_ORANGE = "darkorange";
    private final static String DARK_YELLOW = "darkyellow";
    private final static String DARK_YELLOWGREEN = "darkyellowgreen";
    private final static String DARK_GREEN = "darkgreen";
    private final static String DARK_SPRINGGREEN = "darkspringgreen";
    private final static String DARK_CYAN = "darkcyan";
    private final static String DARK_INDIGO = "darkindigo";
    private final static String DARK_BLUE = "darkblue";
    private final static String DARK_PURPLE = "darkpurple";
    private final static String DARK_MAGENTA = "darkmagenta";
    private final static String DARK_HOTPINK = "darkhotpink";
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
    private final static String LIGHT_RED = "lightred";
    private final static String LIGHT_ORANGE = "lightorange";
    private final static String LIGHT_YELLOW = "lightyellow";
    private final static String LIGHT_YELLOWGREEN = "lightyellowgreen";
    private final static String LIGHT_GREEN = "lightgreen";
    private final static String LIGHT_SPRINGGREEN = "lightspringgreen";
    private final static String LIGHT_CYAN = "lightcyan";
    private final static String LIGHT_INDIGO = "lightindigo";
    private final static String LIGHT_BLUE = "lightblue";
    private final static String LIGHT_PURPLE = "lightpurple";
    private final static String LIGHT_MAGENTA = "lightmagenta";
    private final static String LIGHT_HOTPINK = "lighthotpink";
    private final static String SILVER = "silver";
    private final static String MAROON = "maroon";
    private final static String BROWN = "brown";
    private final static String OLIVE = "olive";
    private final static String FERN = "fern";
    private final static String FOREST_GREEN = "forestgreen";
    private final static String TEAL = "teal";
    private final static String WINE = "wine";
    private final static String LIME = "lime";
    private final static String ROSE = "rose";
    private final static String SALMON = "salmon";
    private final static String TAN = "tan";

    public static final Map<Set<String>, Color> COLOR_PRESETS = mapOf(
            setOf(BLACK),
            new Color(0, 0, 0),
            setOf(DARK_GREY),
            new Color(63, 63, 63),
            setOf(GREY),
            new Color(127, 127, 127),
            setOf(LIGHT_GREY, SILVER),
            new Color(191, 191, 191),
            setOf(WHITE),
            new Color(255, 255, 255),
            setOf(DARK_RED, MAROON),
            new Color(127, 0, 0),
            setOf(DARK_ORANGE, BROWN),
            new Color(127, 63, 0),
            setOf(DARK_YELLOW, OLIVE),
            new Color(127, 127, 0),
            setOf(DARK_YELLOWGREEN, FERN),
            new Color(63, 127, 0),
            setOf(DARK_GREEN, FOREST_GREEN),
            new Color(0, 127, 0),
            setOf(DARK_SPRINGGREEN),
            new Color(0, 127, 63),
            setOf(DARK_CYAN, TEAL),
            new Color(0, 127, 127),
            setOf(DARK_INDIGO),
            new Color(0, 63, 127),
            setOf(DARK_BLUE),
            new Color(0, 0, 127),
            setOf(DARK_PURPLE),
            new Color(63, 0, 127),
            setOf(DARK_MAGENTA),
            new Color(127, 0, 127),
            setOf(DARK_HOTPINK, WINE),
            new Color(127, 0, 63),
            setOf(RED),
            new Color(255, 0, 0),
            setOf(ORANGE),
            new Color(255, 127, 0),
            setOf(YELLOW),
            new Color(255, 255, 0),
            setOf(YELLOWGREEN, LIME),
            new Color(127, 255, 0),
            setOf(GREEN),
            new Color(0, 255, 0),
            setOf(SPRINGGREEN),
            new Color(0, 255, 127),
            setOf(CYAN),
            new Color(0, 255, 255),
            setOf(INDIGO),
            new Color(0, 127, 255),
            setOf(BLUE),
            new Color(0, 0, 255),
            setOf(PURPLE),
            new Color(127, 0, 255),
            setOf(MAGENTA),
            new Color(255, 0, 255),
            setOf(HOTPINK, ROSE),
            new Color(255, 0, 127),
            setOf(LIGHT_RED, SALMON),
            new Color(255, 127, 127),
            setOf(LIGHT_ORANGE, TAN),
            new Color(255, 191, 127),
            setOf(LIGHT_YELLOW),
            new Color(255, 255, 127),
            setOf(LIGHT_YELLOWGREEN),
            new Color(191, 255, 127),
            setOf(LIGHT_GREEN),
            new Color(127, 255, 127),
            setOf(LIGHT_SPRINGGREEN),
            new Color(127, 255, 191),
            setOf(LIGHT_CYAN),
            new Color(127, 255, 255),
            setOf(LIGHT_INDIGO),
            new Color(127, 191, 255),
            setOf(LIGHT_BLUE),
            new Color(127, 127, 255),
            setOf(LIGHT_PURPLE),
            new Color(191, 127, 255),
            setOf(LIGHT_MAGENTA),
            new Color(255, 127, 255),
            setOf(LIGHT_HOTPINK),
            new Color(255, 127, 191)
    );
}
