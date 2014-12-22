package com.mimeit.android.providers.words;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.mimeit.android.providers.base.BaseContract;

// Contract for Words provider 
public class WordsContract implements BaseContract {

	// words contract
	private WordsContract() {
	}

	private static final String AUTHORITY = "words";

	public static String getAuthority(Context context) {
		return context.getPackageName() + "." + AUTHORITY;
	}

	public static String getContentType(Context context) {
		return "vnd.android.cursor.dir/vnd." + getAuthority(context);
	}

	public static String getContentItemType(Context context) {
		return "vnd.android.cursor.item/vnd." + getAuthority(context);
	}

	public static final Uri getContentUri(Context context) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
				.authority(getAuthority(context)).build();
	}

	public static final Uri getContentItemBaseUri(Context context) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
				.authority(getAuthority(context)).build();
	}

	public static final Uri getContentItemUri(Context context, long id) {
		return ContentUris.withAppendedId(getContentItemBaseUri(context), id);
	}

	// Gets maximum points for given level.

	public static int getMaxPoints(int level) {
		// max 60 sec to describe the phrase
		return (level + 1) * 60;
	}

	// table name
	public static final String TABLE_NAME = "words";

	// words - string
	public static final String COLUMN_WORDS = "words";

	// level name - string
	public static final String COLUMN_LEVEL = "level";

}
