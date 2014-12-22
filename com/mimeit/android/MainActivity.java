package com.mimeit.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.mimeit.android.adapters.GameLevelsAdapter;
import com.mimeit.android.providers.words.SampleData;
import com.mimeit.android.utils.Fonts;

// main activiity
public class MainActivity extends BaseActivity {

	private static final String URI_HOW_TO_PLAY = "file:///android_res/raw/how_to_play.html";
	private static final String URI_ABOUT = "file:///android_res/raw/about.html";

	private WebView mWebView;
	private TextView mTextAppName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* MAP CONTROLS */

		mWebView = (WebView) findViewById(R.id.web);
		mTextAppName = (TextView) findViewById(R.id.text_app_name);

		// control setup

		mTextAppName.setText(Html
				.fromHtml(getString(R.string.html_app_name_home_screen)));
		mTextAppName.setTypeface(Fonts.Font.ACTION_BAR_TITLE.getFont(this));

		// click listener for buttons
		for (int id : new int[] { R.id.button_about, R.id.button_how_to_play,
				R.id.button_start }) {
			Button button = (Button) findViewById(id);
			button.setTypeface(Fonts.Font.COMMAND_BUTTON.getFont(this));
			button.setOnClickListener(mButtonsOnClickListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflating menu from xml resourses
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_view_high_scores: {
			// start game results activity
			Intent intent = new Intent(this, GameResultsActivity.class);
			startActivity(intent);

			break;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	// hides text app view  and show the webview
	
	private void hideTextAppNameAndShowWebView() {
		mTextAppName.setVisibility(View.GONE);
		mWebView.setVisibility(View.VISIBLE);
	}

	// click listener for all common buttons
	
	private final View.OnClickListener mButtonsOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_about: {
				mWebView.loadUrl(URI_ABOUT);
				hideTextAppNameAndShowWebView();

				break;
			} // "About" button

			case R.id.button_how_to_play: {
				mWebView.loadUrl(URI_HOW_TO_PLAY);
				hideTextAppNameAndShowWebView();

				break;
			}// "How to Play" button

			case R.id.button_start: {
				// makes level names array
				CharSequence[] levelNames = new CharSequence[SampleData.WORDS.length];
				for (int level = 0; level < levelNames.length; level++)
					levelNames[level] = getString(R.string.pattern_level_x,
							level + 1);

				// dialogues of levels to show the user

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle(R.string.text_select_level);
				builder.setAdapter(new GameLevelsAdapter(MainActivity.this),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								// Start Play activity

								Intent intent = new Intent(MainActivity.this,
										PlayActivity.class);
								intent.putExtra(PlayActivity.EXTRA_LEVEL, which);
								startActivity(intent);
							}// onClick()

						});

				builder.show();

				break;
			}// "Start" button
			}
		}

	};

}
