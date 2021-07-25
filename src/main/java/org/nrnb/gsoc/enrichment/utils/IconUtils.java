package org.nrnb.gsoc.enrichment.utils;

import java.awt.*;
import java.io.IOException;

/**
 * @author ighosh98
 * @description  Icons designs
 */
public abstract class IconUtils {

    // gProfiler Icon
    public static final String PROFILER_ICON = "\uE903";
    // gProfiler Icon layers
    public static final String PROFILER_ICON_LAYER_1 = "\uE904";
    public static final String PROFILER_ICON_LAYER_2 = "\uE905";
    public static final String PROFILER_ICON_LAYER_3 = "\uE906";
    public static final String PROFILER_ICON_LAYER_4 = "\uE907";
    // EnrichmentMap Icon
    public static final String EM_ICON_LAYER_1 = "\uE900";
    public static final String EM_ICON_LAYER_2 = "\uE901";
    public static final String EM_ICON_LAYER_3 = "\uE901";
    public static final String PROFILER_LAYER = "\uE90C";

    public static final String[] LAYERED_PROFILER_ICON = new String[] { PROFILER_ICON_LAYER_1, PROFILER_ICON_LAYER_2, PROFILER_ICON_LAYER_3 };
    public static final Color[] PROFILER_COLORS = new Color[] { new Color(163, 172, 216), Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK };

    public static final String[] ENRICH_LAYERS = new String[] { PROFILER_ICON_LAYER_1, PROFILER_ICON_LAYER_2, PROFILER_ICON_LAYER_3, PROFILER_ICON_LAYER_4 };
    public static final String[] PROFILER_LAYERS = new String[] { PROFILER_ICON_LAYER_1, PROFILER_ICON_LAYER_2, PROFILER_ICON_LAYER_3, PROFILER_ICON_LAYER_4, PROFILER_LAYER };

    public static final String[] LAYERED_EM_ICON = new String[] { EM_ICON_LAYER_1, EM_ICON_LAYER_2, EM_ICON_LAYER_3 };
    public static final Color[] EM_COLORS = new Color[] { Color.WHITE, new Color(31, 120, 180), new Color(52, 160, 44) };

    private static Font iconFont;

    static {
        try {
            iconFont = Font.createFont(Font.TRUETYPE_FONT, IconUtils.class.getResourceAsStream("/fonts/string.ttf"));
        } catch (FontFormatException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static Font getIconFont(float size) {
        return iconFont.deriveFont(size);
    }

    private IconUtils() {
        // ...
    }
}
