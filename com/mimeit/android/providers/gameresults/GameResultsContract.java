package com.mimeit.android.providers.gameresults;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.mimeit.android.providers.base.BaseContract;

// game results contractor
public class GameResultsContract implements BaseContract {

	// singleton class
	private GameResultsContract() {
	}

	private static final String AUTHORITY = "game-results";

	// gets authority
	public static String getAuthority(Context context) {
		return context.getPackageName() + "." + AUTHORITY;
	}

	// gets content type
	public static String getContentType(Context context) {
		return "vnd.android.cursor.dir/vnd." + getAuthority(context);
	}

	// gets Content type
	public static String getContentItemType(Context context) {
		return "vnd.android.cursor.item/vnd." + getAuthority(context);
	}

	// gets content URI
	public static final Uri getContentUri(Context context) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
				.authority(getAuthority(context)).build();
	}

	// gets content item base URI
	public static final Uri getContentItemBaseUri(Context context) {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
				.authority(getAuthority(context)).build();
	}// getContentItemBaseUri()

	// gets content item URI
	public static final Uri getContentItemUri(Context context, long id) {
		return ContentUris.withAppendedId(getContentItemBaseUri(context), id);
	}

	// table name
	public static final String TABLE_NAME = "game_results";

	// the phrase content
	public static final String COLUMN_PHRASE_COUNT = "phrase_count";

	// level - integer
	public static final String COLUMN_LEVEL = "level";

	// points - integer
	public static final String COLUMN_POINT = "point";

}
