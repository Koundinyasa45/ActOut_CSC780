package com.mimeit.android.providers.words;

import static android.provider.BaseColumns._ID;
import static com.mimeit.android.providers.base.BaseContract._DATE_CREATED;
import static com.mimeit.android.providers.base.BaseContract._DATE_MODIFIED;
import static com.mimeit.android.providers.words.SampleData.WORDS;
import static com.mimeit.android.providers.words.WordsContract.COLUMN_LEVEL;
import static com.mimeit.android.providers.words.WordsContract.COLUMN_WORDS;
import static com.mimeit.android.providers.words.WordsContract.TABLE_NAME;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite helper class for Words database.
 */
public class WordsHelper extends SQLiteOpenHelper {

	// db file name
	public static final String DB_FILENAME = "words.db";

	// version
	public static final int DB_VERSION = 1;

	// sql creator
	private static final String DB_CREATOR = "CREATE TABLE " + TABLE_NAME
			+ " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ _DATE_CREATED + " INTEGER NOT NULL DEFAULT 0, " + _DATE_MODIFIED
			+ " INTEGER NOT NULL DEFAULT 0, " + COLUMN_WORDS
			+ " TEXT NOT NULL, " + COLUMN_LEVEL + " INTEGER NOT NULL )";

	// creates new instance
	public WordsHelper(Context context) {
		super(context, DB_FILENAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATOR);

		// sample database
		for (int level = 0; level < WORDS.length; level++) {
			for (int i = 0; i < WORDS[level].length; i++) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_LEVEL, level);
				values.put(COLUMN_WORDS, WORDS[level][i]);

				db.insert(TABLE_NAME, null, values);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does nothing
	}

}
