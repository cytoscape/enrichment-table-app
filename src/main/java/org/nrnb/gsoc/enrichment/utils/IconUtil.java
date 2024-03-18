package org.nrnb.gsoc.enrichment.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public abstract class IconUtil {
	
	public static final String APP_LOGO_MONO = "a";
	public static final String APP_LOGO_LAYER_1 = "b";
	public static final String APP_LOGO_LAYER_2 = "c";
	public static final String APP_LOGO_LAYER_3 = "d";
	
	public static final String[] APP_ICON_LAYERS = new String[] {
			APP_LOGO_LAYER_1,
			APP_LOGO_LAYER_2,
			APP_LOGO_LAYER_3
	};
	public static final Color[] APP_ICON_COLORS = new Color[] {
			new Color(5, 62, 96),
			Color.WHITE,
			new Color(56, 120, 158)
	};
	
	private static Font iconFont;

	static {
		try {
			iconFont = Font.createFont(Font.TRUETYPE_FONT, IconUtil.class.getResourceAsStream("/fonts/enrichmenttable.ttf"));
		} catch (FontFormatException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public static Font getIconFont(float size) {
		return iconFont.deriveFont(size);
	}

	private IconUtil() {
		// ...
	}
}
