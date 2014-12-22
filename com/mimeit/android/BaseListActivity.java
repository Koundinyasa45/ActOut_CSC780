package com.mimeit.android;

import android.app.ListActivity;
import android.os.Bundle;

// Base list activity 

public abstract class BaseListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.base_list_activity);

		BaseActivity.setupActionBarTitleFont(this);
	}

}
