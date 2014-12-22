package com.mimeit.android.providers.gameresults;

import static android.provider.BaseColumns._ID;
import static com.mimeit.android.providers.base.BaseContract._DATE_CREATED;
import static com.mimeit.android.providers.base.BaseContract._DATE_MODIFIED;
import static com.mimeit.android.providers.gameresults.GameResultsContract.COLUMN_LEVEL;
import static com.mimeit.android.providers.gameresults.GameResultsContract.COLUMN_PHRASE_COUNT;
import static com.mimeit.android.providers.gameresults.GameResultsContract.COLUMN_POINT;
import static com.mimeit.android.providers.gameresults.GameResultsContract.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQL helper class for gameresults db
public class GameResultsHelper extends SQLiteOpenHelper {

	// db file name
	public static final String DB_FILENAME = "game-results.db";

	// db version
	public static final int DB_VERSION = 1;

	// sql creator
	private static final String DB_CREATOR = "CREATE TABLE " + TABLE_NAME
			+ " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ _DATE_CREATED + " INTEGER NOT NULL DEFAULT 0, " + _DATE_MODIFIED
			+ " INTEGER NOT NULL DEFAULT 0, " + COLUMN_LEVEL
			+ " INTEGER NOT NULL, " + COLUMN_PHRASE_COUNT
			+ " INTEGER NOT NULL, " + COLUMN_POINT + " INTEGER NOT NULL )";

	// new instance
	public GameResultsHelper(Context context) {
		super(context, DB_FILENAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATOR);
	} // onCreate()

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// does nothing
	} // on update

}
