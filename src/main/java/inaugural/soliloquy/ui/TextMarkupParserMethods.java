package inaugural.soliloquy.ui;

import java.awt.*;

public class TextMarkupParserMethods {
    public static String coloredText(String colorPreset, String text) {
        return String.format("[color=%s]%s", colorPreset, text);
    }

    public static String coloredText(Color color, String text) {
        return String.format("[color=%s,%s,%s,%s]%s",
                color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), text);
    }
}
