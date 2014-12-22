package com.mimeit.android.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mimeit.android.R;
import com.mimeit.android.providers.words.SampleData;
import com.mimeit.android.utils.Fonts;

// adapter to display the levels to user 

public class GameLevelsAdapter extends BaseAdapter {

	// view holder pattern
	private class ViewHolder {

		public final TextView textLevel;

		// new instance
		public ViewHolder(View rootView) {
			textLevel = (TextView) rootView.findViewById(R.id.text_level);

			for (TextView tv : new TextView[] { textLevel })
				tv.setTypeface(mFont);

			rootView.setTag(this);
		}

	}

	private final Context mContext;
	private final Typeface mFont;

	// new instance
	public GameLevelsAdapter(Context context) {
		mContext = context;
		mFont = Fonts.Font.COMMAND_BUTTON.getFont(mContext);
	}

	@Override
	public int getCount() {
		// level count
		return SampleData.WORDS.length;
	}

	@Override
	public Integer getItem(int position) {
		// return the level
		return position;
	}

	@Override
	public long getItemId(int position) {
		// return position as ID
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(mContext, R.layout.list_item_game_level,
					null);

		// create or obtain view holder
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);

		// get level
		final int level = getItem(position);

		holder.textLevel.setText(mContext.getString(R.string.pattern_level_x,
				level + 1));

		return convertView;
	}

}
