package com.mimeit.android.providers.base;

import android.provider.BaseColumns;

// base contract - can be used by most db
public interface BaseContract extends BaseColumns {

	// date creation
	public static final String _DATE_CREATED = "date_created";

	// date modification
	public static final String _DATE_MODIFIED = "date_modified";

}
