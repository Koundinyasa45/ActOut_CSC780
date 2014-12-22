package com.mimeit.android.utils;

// Loaders utilities
public class Loaders {

	private Loaders() {
	}

	private static int mId = 0;

	// generates a global unique ID for session
	public static int newId() {
		return mId++;
	}

}
