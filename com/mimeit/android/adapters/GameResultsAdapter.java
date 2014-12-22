package com.mimeit.android.adapters;

import static com.mimeit.android.providers.base.BaseContract._DATE_CREATED;
import static com.mimeit.android.providers.gameresults.GameResultsContract.COLUMN_LEVEL;
import static com.mimeit.android.providers.gameresults.GameResultsContract.COLUMN_POINT;

import java.text.DateFormat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.mimeit.android.R;
import com.mimeit.android.utils.Fonts;

// results adapter
public class GameResultsAdapter extends ResourceCursorAdapter {

	// view holder pattern
	private class ViewHolder {

		public final TextView textInfo, textPoints, textDate;

		// new instance to the root view
		public ViewHolder(View rootView) {
			textDate = (TextView) rootView.findViewById(R.id.text_date);
			textInfo = (TextView) rootView.findViewById(R.id.text_info);
			textPoints = (TextView) rootView.findViewById(R.id.text_points);

			for (TextView tv : new TextView[] { textDate, textInfo, textPoints })
				tv.setTypeface(mFont);

			rootView.setTag(this);
		}

	}

	private final int mColorEven, mColorOdd;
	private final Typeface mFont;

	// new instance to context
	public GameResultsAdapter(Context context) {
		super(context, R.layout.list_item_game_result, null, 0);

		mFont = Fonts.Font.MAIN.getFont(context);

		mColorEven = context.getResources().getColor(
				R.color.list_item_background_even);
		mColorOdd = context.getResources().getColor(
				R.color.list_item_background_odd);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// create or obtain the holder
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null)
			holder = new ViewHolder(view);

		// UI
		if (cursor.getPosition() % 2 == 0)
			view.setBackgroundColor(mColorEven);
		else
			view.setBackgroundColor(mColorOdd);

		// Load data
		final long date = cursor.getLong(cursor.getColumnIndex(_DATE_CREATED));
		final int level = cursor.getInt(cursor.getColumnIndex(COLUMN_LEVEL));
		final int points = cursor.getInt(cursor.getColumnIndex(COLUMN_POINT));

		// Fill data to view
		holder.textDate
				.setText(DateUtils.formatSameDayTime(date,
						System.currentTimeMillis(), DateFormat.MEDIUM,
						DateFormat.SHORT));
		holder.textInfo.setText(context.getString(
				R.string.pattern_text_played_x, level + 1));
		holder.textPoints.setText(context.getString(
				R.string.pattern_text_points_archived, points));
	}

}
