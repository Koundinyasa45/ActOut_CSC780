package com.mimeit.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import com.mimeit.android.utils.Fonts;
import com.mimeit.android.utils.TypefaceSpan;

/* Base activity. */
public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupActionBarTitleFont(this);
	}

	// custom font for action title bar 

	public static void setupActionBarTitleFont(Activity activity) {

		CharSequence title = activity.getTitle();
		if (!TextUtils.isEmpty(title)) {
			SpannableString newTitle = new SpannableString(title);
			newTitle.setSpan(new TypefaceSpan(activity,
					Fonts.Font.ACTION_BAR_TITLE), 0, title.length(),
					Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			activity.setTitle(newTitle);
		}
	}

}
