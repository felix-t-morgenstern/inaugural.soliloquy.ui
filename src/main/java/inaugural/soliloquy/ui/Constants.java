package inaugural.soliloquy.ui;

import soliloquy.specs.common.valueobjects.Vertex;

import java.awt.*;
import java.util.Map;
import java.util.Set;

import static inaugural.soliloquy.tools.collections.Collections.mapOf;
import static inaugural.soliloquy.tools.collections.Collections.setOf;
import static soliloquy.specs.common.valueobjects.Vertex.vertexOf;

public class Constants {
    public final static String COMPONENT_UUID = "COMPONENT_UUID";

    public final static String COMPONENT_DIMENS = "COMPONENT_DIMENS";
    public final static String COMPONENT_WIDTH = "COMPONENT_WIDTH";
    public final static String COMPONENT_HEIGHT = "COMPONENT_HEIGHT";

    public final static String CONTENTS = "CONTENTS";

    public final static String CONTENT_UUID = "CONTENT_UUID";
    public final static String CONTAINING_COMPONENT_UUID = "CONTAINING_COMPONENT_UUID";
    public final static String LAST_TIMESTAMP = "LAST_TIMESTAMP";
    public final static String COMPONENT_ORIGIN = "COMPONENT_ORIGIN";
    public final static String COMPONENT_ORIGIN_PROVIDER = "COMPONENT_ORIGIN_PROVIDER";
    public final static String COMPONENT_ORIGIN_ADJUST = "COMPONENT_ORIGIN_ADJUST";

    public final static String VERTICES_INDEX = "VERTICES_INDEX";
    public final static String REGISTERED_CONTENTS = "REGISTERED_CONTENTS";
    public final static String CONTENT_SPECIFIC_ORIGINS = "CONTENT_SPECIFIC_ORIGINS";

    public final static String CONTENT_UNADJUSTED_DIMENS_PROVIDERS =
            "CONTENT_UNADJUSTED_DIMENS_PROVIDERS";
    public final static String CONTENT_UNADJUSTED_DIMENS = "CONTENT_UNADJUSTED_DIMENS";
    public final static String CONTENT_UNADJUSTED_VERTICES_PROVIDERS =
            "CONTENT_UNADJUSTED_VERTICES_PROVIDERS";
    public final static String CONTENT_UNADJUSTED_VERTICES = "CONTENT_UNADJUSTED_VERTICES";
    public final static String CONTENT_POLYGON_OFFSETS = "CONTENT_POLYGON_OFFSETS";

    public final static String INDENT = "INDENT";
    public final static String ALIGNMENT = "ALIGNMENT";
    public final static String SPACING_AFTER = "SPACING_AFTER";
    public final static String SPACING_UUID = "SPACING_UUID";

    public final static float NO_MAX_LINE_LENGTH = 0f;

    public final static Vertex WINDOW_ORIGIN = vertexOf(0f, 0f);
    public final static Vertex WINDOW_CENTER = vertexOf(0.5f, 0.5f);

    public final static String BLACK = "black";
    public final static String DARK_GREY = "darkgrey";
    public final static String GREY = "grey";
    public final static String LIGHT_GREY = "lightgrey";
    public final static String WHITE = "white";
    public final static String DARK_RED = "darkred";
    public final static String DARK_ORANGE = "darkorange";
    public final static String DARK_YELLOW = "darkyellow";
    public final static String DARK_YELLOWGREEN = "darkyellowgreen";
    public final static String DARK_GREEN = "darkgreen";
    public final static String DARK_SPRINGGREEN = "darkspringgreen";
    public final static String DARK_CYAN = "darkcyan";
    public final static String DARK_INDIGO = "darkindigo";
    public final static String DARK_BLUE = "darkblue";
    public final static String DARK_PURPLE = "darkpurple";
    public final static String DARK_MAGENTA = "darkmagenta";
    public final static String DARK_HOTPINK = "darkhotpink";
    public final static String RED = "red";
    public final static String ORANGE = "orange";
    public final static String YELLOW = "yellow";
    public final static String YELLOWGREEN = "yellowgreen";
    public final static String GREEN = "green";
    public final static String SPRINGGREEN = "springgreen";
    public final static String CYAN = "cyan";
    public final static String INDIGO = "indigo";
    public final static String BLUE = "blue";
    public final static String PURPLE = "purple";
    public final static String MAGENTA = "magenta";
    public final static String HOTPINK = "hotpink";
    public final static String LIGHT_RED = "lightred";
    public final static String LIGHT_ORANGE = "lightorange";
    public final static String LIGHT_YELLOW = "lightyellow";
    public final static String LIGHT_YELLOWGREEN = "lightyellowgreen";
    public final static String LIGHT_GREEN = "lightgreen";
    public final static String LIGHT_SPRINGGREEN = "lightspringgreen";
    public final static String LIGHT_CYAN = "lightcyan";
    public final static String LIGHT_INDIGO = "lightindigo";
    public final static String LIGHT_BLUE = "lightblue";
    public final static String LIGHT_PURPLE = "lightpurple";
    public final static String LIGHT_MAGENTA = "lightmagenta";
    public final static String LIGHT_HOTPINK = "lighthotpink";
    public final static String SILVER = "silver";
    public final static String MAROON = "maroon";
    public final static String BROWN = "brown";
    public final static String OLIVE = "olive";
    public final static String FERN = "fern";
    public final static String FOREST_GREEN = "forestgreen";
    public final static String TEAL = "teal";
    public final static String WINE = "wine";
    public final static String LIME = "lime";
    public final static String ROSE = "rose";
    public final static String SALMON = "salmon";
    public final static String TAN = "tan";

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
