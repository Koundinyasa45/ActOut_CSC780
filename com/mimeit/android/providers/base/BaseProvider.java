package com.mimeit.android.providers.base;

import static android.provider.BaseColumns._ID;
import static com.mimeit.android.BuildConfig.DEBUG;
import static com.mimeit.android.providers.base.BaseContract._DATE_CREATED;
import static com.mimeit.android.providers.base.BaseContract._DATE_MODIFIED;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

// base dprovider - uses base contactor and implements abstract classes

public abstract class BaseProvider extends ContentProvider {

	private static final String CLASSNAME = BaseProvider.class.getName();

	// type of URI of content type
	protected static final int URI_CONTENT = 1;

	// type of URI of content item
	protected static final int URI_CONTENT_ITEM = 2;

	// URI matcher
	protected final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	// DB helper
	private SQLiteOpenHelper mDbHelper;

	// new instance for DB helper
	protected abstract SQLiteOpenHelper newDbHelper();

	// new instance for DB helper
	protected SQLiteOpenHelper getDbHelper() {
		return mDbHelper;
	} // getDbHelper()

	// gets table name
	protected abstract String getTableName();

	// gets content base URI
	protected abstract Uri getContentItemBaseUri();

	// gets default sort order
	protected abstract String getDefaultSortOrder();

	@Override
	public boolean onCreate() {
		mDbHelper = newDbHelper();

		return true;
	} // onCreate()

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (DEBUG)
			Log.d(CLASSNAME, "delete() >> " + uri + " -- MATCHER="
					+ mUriMatcher.match(uri));

		// gets writable db
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		String finalWhere;
		int count = 0;

		// checks the type of incoming URI
		switch (mUriMatcher.match(uri)) {
		case URI_CONTENT: {
			count = db.delete(getTableName(), selection, selectionArgs);
			break;
		} // URI_CONTENT
		case URI_CONTENT_ITEM: {
			finalWhere = _ID + "=" + uri.getLastPathSegment();
			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;
			count = db.delete(getTableName(), finalWhere, selectionArgs);
			break;
		} // URI_CONTENT_ITEM
		}

		// method to notify user to update, if some rows are deleted
		if (count > 0 || selection == null)
			getContext().getContentResolver().notifyChange(uri, null);

		return count;
	} // delete()

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (DEBUG)
			Log.d(CLASSNAME, "insert() >> " + uri);

		if (mUriMatcher.match(uri) != URI_CONTENT)
			return null;

		// gets time in milliseconds
		final long now = System.currentTimeMillis();
		// sets the value to current time, if the values does not match
		for (String col : new String[] { _DATE_CREATED, _DATE_MODIFIED })
			if (!values.containsKey(col))
				values.put(col, now);

		// opens db in write mode
		SQLiteDatabase db = getDbHelper().getWritableDatabase();

		// Does insert and returns the ID of new note
		long rowId = db.insert(getTableName(), null, values);

		// if insert passes,the row ID exists
		if (rowId >= 0) {
			// creates a URI and a new row id pattern
			Uri noteUri = ContentUris.withAppendedId(getContentItemBaseUri(),
					rowId);

			// notifies that the data is changed
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		return null;
	}                   // insert()

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (DEBUG)
			Log.d(CLASSNAME, "query() >> " + uri);

		// creates a new query builder
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(getTableName());

		SQLiteDatabase db = null;
		Cursor cursor = null;

		// Choose the projection and adjust the "where" clause based on URI pattern-matching
		switch (mUriMatcher.match(uri)) {
		case URI_CONTENT: {
			break;
		}           // URI_CONTENT

		case URI_CONTENT_ITEM: {
			qb.appendWhere(_ID + "=" + uri.getLastPathSegment());
			break;
		}
		}

		if (TextUtils.isEmpty(sortOrder))
			sortOrder = getDefaultSortOrder();

		if (db == null) {
			db = getDbHelper().getReadableDatabase();
			cursor = qb.query(db, projection, selection, selectionArgs, null,
					null, sortOrder);
		}

		/*
		 * If we queried some rows, set a notification URI on this cursor to let
		 * clients know where to listen for new changes to update.
		 */
		if (cursor != null)
			cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}// query()

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (DEBUG)
			Log.d(CLASSNAME, "update() >> " + uri);

		// get writable db
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		int count = 0;
		String finalWhere;

		// set modification time if not set
		if (!values.containsKey(_DATE_MODIFIED))
			values.put(_DATE_MODIFIED, System.currentTimeMillis());

		// check the type of incoming URI
		switch (mUriMatcher.match(uri)) {
		case URI_CONTENT: {
			count = db.update(getTableName(), values, selection, selectionArgs);
			break;
		}

		case URI_CONTENT_ITEM: {
			finalWhere = _ID + "=" + uri.getLastPathSegment();

			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;

			count = db
					.update(getTableName(), values, finalWhere, selectionArgs);
			break;
		}
		}

		// notifies that its time to update
		if (count > 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}                    

}
