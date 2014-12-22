package com.mimeit.android;

import static com.mimeit.android.providers.base.BaseContract._DATE_CREATED;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.mimeit.android.adapters.GameResultsAdapter;
import com.mimeit.android.providers.gameresults.GameResultsContract;
import com.mimeit.android.utils.Loaders;

// Game results activity 

public class GameResultsActivity extends BaseListActivity implements
		LoaderCallbacks<Cursor> {

	private final int mIdLoaderData = Loaders.newId();

	private GameResultsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// New adapter assigning to listview 

		mAdapter = new GameResultsAdapter(this);
		getListView().setAdapter(mAdapter);

		// init loader 
		getLoaderManager().initLoader(mIdLoaderData, null, this);
	}

	@Override
	protected void onDestroy() {
		// call this to close the existing cursor (if any) 
		mAdapter.changeCursor(null);

		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == mIdLoaderData) {
			// creates a new loader //
			return new CursorLoader(this,
					GameResultsContract.getContentUri(this), null, null, null,
					_DATE_CREATED + " DESC");
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == mIdLoaderData) {
			// updates the adapter to automatically close cursor (if any)
			mAdapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == mIdLoaderData) {
			// updates the adapter to automatically close cursor (if any)
			mAdapter.changeCursor(null);
		}
	}

}
