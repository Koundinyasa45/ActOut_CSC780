package com.mimeit.android.providers.gameresults;

import static com.mimeit.android.providers.gameresults.GameResultsContract.TABLE_NAME;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.mimeit.android.providers.base.BaseProvider;

// content provider for game results db
public class GameResultsProvider extends BaseProvider {

	@Override
	public boolean onCreate() {
		mUriMatcher.addURI(GameResultsContract.getAuthority(getContext()),
				null, URI_CONTENT);
		mUriMatcher.addURI(GameResultsContract.getAuthority(getContext()), "#",
				URI_CONTENT_ITEM);

		return super.onCreate();
	}

	// db helper
	@Override
	protected SQLiteOpenHelper newDbHelper() {
		return new GameResultsHelper(getContext());
	}

	// table name
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Uri getContentItemBaseUri() {
		return GameResultsContract.getContentItemBaseUri(getContext());
	}

	// gets default sor order
	@Override
	protected String getDefaultSortOrder() {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case URI_CONTENT:
			return GameResultsContract.getContentType(getContext());
		case URI_CONTENT_ITEM:
			return GameResultsContract.getContentItemType(getContext());
		}

		return null;
	} // gets type

}
