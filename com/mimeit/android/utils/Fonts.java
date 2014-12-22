package com.mimeit.android.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

// Font utilities
public class Fonts {

	public Fonts() {
	}

	// Fonts
	public static enum Font {
		// main font
		MAIN("fonts/Ubuntu-Light.ttf.zip"),
		// command button font
		COMMAND_BUTTON("fonts/MedulaOne-Regular.ttf.zip"),
		// card font
		CARD("fonts/Lobster-Regular.ttf.zip"),
		// actionbar title font
		ACTION_BAR_TITLE("fonts/GreatVibes-Regular.ttf.zip");

		// Asset path to font
		private final String assetPathname;

		// font
		private Typeface typeface;

		// new instance
		private Font(String assetPathname) {
			this.assetPathname = assetPathname;
		}

		// get font
		public Typeface getFont(Context context) {
			if (typeface == null) {
				AssetManager assetManager = context.getAssets();
				typeface = Typeface
						.createFromAsset(assetManager, assetPathname);
			}

			return typeface;
		}

	}

}
