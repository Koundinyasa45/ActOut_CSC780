package com.mimeit.android;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;

import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.mimeit.android.providers.gameresults.GameResultsContract;
import com.mimeit.android.providers.words.WordsContract;
import com.mimeit.android.utils.Fonts;

//Play activity
public class PlayActivity extends BaseActivity implements SensorEventListener {

	private static final String CLASSNAME = PlayActivity.class.getName();

	// to set the level - interger
	public static final String EXTRA_LEVEL = CLASSNAME + ".level";

	private static final int ARRAY_CACHE_SIZE = 10;

	private static final long VIBRATION_DURATION = 200;

	// Minimum proximity sensor distance measured in centimeters, which is considered a game pause
	private static final float PROXIMITY_MIN = 1.5f;

	// Device orientation 
	private static enum DeviceOrientation {
		STAND_UP, TILTED_LEFT, TILTED_RIGHT, TILTED_UPWARDS, TILTED_DOWNWARDS, UNKNOWN;

		// get orientation
		public static DeviceOrientation getOrientation(float pitch, float roll) {
			if (Math.abs(pitch) <= 20) {
				if (roll >= 45)
					return TILTED_RIGHT;
				if (roll <= -45)
					return TILTED_LEFT;
				return STAND_UP;
			}

			if (pitch >= 75)
				return TILTED_UPWARDS;
			if (pitch <= -75)
				return TILTED_DOWNWARDS;

			return UNKNOWN;
		}

	}

	// float array to store float values and get the average
	private static class FloatArray {

		private final float[] data;
		private int count = 0;

		// new instance
		public FloatArray(int size) {
			data = new float[size];
		}

		// adds new value to array. if its full - moves left. just like a queue
		public float add(float v) {
			if (count < data.length)
				count++;
			else
				System.arraycopy(data, 1, data, 0, data.length - 1);

			data[count - 1] = v;

			return average();
		}

		// calculates average value of the array
		public float average() {
			float sum = 0;
			for (int i = 0; i < count; i++)
				sum += data[i];

			return sum / count;
		}

	}

	private View mContentView, mViewGroupProgressBar;
	private TextView mTextPoints, mTextWords, mTextTimer, mTextPaused,
			mTextDescription;

	private WordsLoader mWordsLoader;
	private GamePlay mGamePlay;

	private SensorManager mSensorManager;
	private Sensor mSensorAccelerometer, mSensorManeticField, mSensorProximity;

	private final float[] mVectorGravity = new float[3];
	private final float[] mVectorGeomagnetic = new float[3];
	private final float[] mRotationMatrix_R = new float[9];
	private final float[] mRotationMatrix_NewR = new float[9];
	private final float[] mRotationMatrix_I = new float[9];
	private final float[] mOrientation = new float[3];

	private FloatArray mPitches = new FloatArray(ARRAY_CACHE_SIZE),
			mRolls = new FloatArray(ARRAY_CACHE_SIZE);

	private boolean mWaitingForStandingUp = true;

	private DeviceOrientation mDeviceOrientation = DeviceOrientation.UNKNOWN;

	private Vibrator mVibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		// setup services

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManeticField = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorProximity = mSensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		mSensorManager.registerListener(this, mSensorAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mSensorManeticField,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mSensorProximity,
				SensorManager.SENSOR_DELAY_GAME);

		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// map controls

		mContentView = findViewById(R.id.content);
		mViewGroupProgressBar = findViewById(R.id.view_group_progress_bar);

		mTextDescription = (TextView) findViewById(R.id.text_description);
		mTextPaused = (TextView) findViewById(R.id.text_paused);
		mTextPoints = (TextView) findViewById(R.id.text_points);
		mTextTimer = (TextView) findViewById(R.id.text_timer);
		mTextWords = (TextView) findViewById(R.id.text_words);

		// set fonts
		for (TextView tv : new TextView[] { mTextDescription, mTextPaused })
			tv.setTypeface(Fonts.Font.MAIN.getFont(this));
		for (TextView tv : new TextView[] { mTextPoints, mTextTimer })
			tv.setTypeface(Fonts.Font.COMMAND_BUTTON.getFont(this));
		mTextWords.setTypeface(Fonts.Font.CARD.getFont(this));

		// start game
		startGame();
	}

	@Override
	protected void onDestroy() {
		mSensorManager.unregisterListener(this);

		if (mWordsLoader != null) {
			mWordsLoader.cancel(false);
			mWordsLoader = null;
		}

		if (mGamePlay != null) {
			mGamePlay.cancel();
			mGamePlay = null;
		}

		super.onDestroy();
	}

	/*********************
	 SensorEventListener
	 *********************/

	@Override
	public void onSensorChanged(SensorEvent event) {
		final GamePlay gamePlay = mGamePlay;

		if (gamePlay != null && gamePlay.isStopped()) {
			mTextWords.setRotation(0);
			return;
		}

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER: {
			System.arraycopy(event.values, 0, mVectorGravity, 0,
					mVectorGravity.length);

			break;
		} // Accelerator
		case Sensor.TYPE_MAGNETIC_FIELD: {
			System.arraycopy(event.values, 0, mVectorGeomagnetic, 0,
					mVectorGeomagnetic.length);

			break;
		} // magnetic field

		case Sensor.TYPE_PROXIMITY: {
			if (gamePlay == null)
				return;

			final float distance = event.values[0];
			if (distance <= Math.min(PROXIMITY_MIN,
					mSensorProximity.getMaximumRange()) - 0.5) {
				if (!gamePlay.isPaused())
					gamePlay.pause();
			} else {
				if (gamePlay.isPaused())
					gamePlay.resume();
			}

			return;
		}  // Proximity
		}

		if (SensorManager.getRotationMatrix(mRotationMatrix_R,
				mRotationMatrix_I, mVectorGravity, mVectorGeomagnetic)) {
			// Using the camera (Y axis along the camera's axis) where the rotation angles are needed
			
			SensorManager.remapCoordinateSystem(mRotationMatrix_R,
					SensorManager.AXIS_X, SensorManager.AXIS_Z,
					mRotationMatrix_NewR);
			SensorManager.getOrientation(mRotationMatrix_NewR, mOrientation);

			float pitch = mPitches.add((float) Math.toDegrees(mOrientation[1]));
			float roll = mRolls.add((float) Math.toDegrees(mOrientation[2]));

			mDeviceOrientation = DeviceOrientation.getOrientation(pitch, roll);

			switch (mDeviceOrientation) {
			case TILTED_LEFT:
			case TILTED_RIGHT: {
				if (mGamePlay != null && !mGamePlay.isPaused())
					mTextWords.setRotation(-roll);
				else
					mTextWords.setRotation(0);

				handleTilt(mDeviceOrientation);

				break;
			}

			case TILTED_UPWARDS: {
				mTextWords.setRotation(0);
				handleTilt(mDeviceOrientation);

				break;
			}

			case TILTED_DOWNWARDS: {
				mTextWords.setRotation(0);

				if (mWaitingForStandingUp) {
					// wait...
				} else {
					mWaitingForStandingUp = true;

					if (mGamePlay != null && !mGamePlay.isPaused())
						if (!mGamePlay.next())
							mGamePlay.stop();
				}

				break;
			}

			case STAND_UP: {
				mTextWords.setRotation(0);
				mWaitingForStandingUp = false;

				break;
			}

			default: {
				mTextWords.setRotation(0);
				break;
			}
			}
		}// if
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}

	// starts game
	private void startGame() {
		if (mWordsLoader != null)
			mWordsLoader.cancel(true);

		mTextPoints.setText(Integer.toString(0));
		mTextWords.setText(null);
		mTextTimer.setText(null);

		mTextPaused.setVisibility(View.GONE);
		mTextDescription.setText(R.string.msg_hold_phone_up_to_start_game);

		mWordsLoader = new WordsLoader();
		mWordsLoader.execute(getIntent().getIntExtra(EXTRA_LEVEL, 0));
	}

	// Handles a tilt and takes appropriate actions
	private void handleTilt(DeviceOrientation orientation) {
		final GamePlay gamePlay = mGamePlay;
		if (gamePlay == null)
			return;

		switch (orientation) {
		case TILTED_LEFT: {
			if (!gamePlay.isPaused() && !mWaitingForStandingUp) {
				mWaitingForStandingUp = true;
				gamePlay.score();
			}

			break;
		}

		case TILTED_RIGHT: {
			if (!gamePlay.isPaused() && !mWaitingForStandingUp) {
				mWaitingForStandingUp = true;
				gamePlay.score();
			}

			break;
		}

		case TILTED_UPWARDS: {
			if (!mWaitingForStandingUp) {
				mTextWords.setRotation(0);
				mWaitingForStandingUp = true;
			}

			break;
		}

		default: {
			// do nothing 
			break;
		}
		}
	}

	// class used to load words from db
	private class WordsLoader extends
			AsyncTask<Integer, Void, LinkedList<String>> {

		@Override
		protected void onPreExecute() {
			mContentView.setVisibility(View.GONE);
			mViewGroupProgressBar.setVisibility(View.VISIBLE);

			mGamePlay = null;
		}

		@Override
		protected LinkedList<String> doInBackground(Integer... params) {
			LinkedList<String> result = new LinkedList<String>();
			
			Cursor query = getContentResolver().query(
					WordsContract.getContentUri(PlayActivity.this), null,
					WordsContract.COLUMN_LEVEL + "=" + params[0], null,
					"RANDOM() LIMIT 1000");
			if (query == null)
				return result;

			try {
				if (query.moveToFirst()) {
					do {
						if (isCancelled())
							return null;

						result.add(query.getString(query
								.getColumnIndex(WordsContract.COLUMN_WORDS)));
					} while (query.moveToNext());
				}
			} finally {
				query.close();
			}

			// wait to hold the phone in proper direction
			while (mWaitingForStandingUp && !isCancelled()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					return null;
				}
			}

			return result;
		}

		@Override
		protected void onPostExecute(LinkedList<String> result) {
			if (isCancelled() || result == null || result.isEmpty()) {
				finish();
				return;
			}

			mViewGroupProgressBar.setVisibility(View.GONE);
			mContentView.setVisibility(View.VISIBLE);

			if (mGamePlay != null)
				mGamePlay.cancel();

			mGamePlay = new GamePlay(result, getIntent().getIntExtra(
					EXTRA_LEVEL, 0));
			mGamePlay.start();
		}

	}

	// game play
	private class GamePlay extends Handler {

		private static final long TIME_TO_GUESS = MINUTE_IN_MILLIS;
		private static final long DELAY = 200;
		private static final int MSG_TICK = 0;

		private final LinkedList<String> words;
		private final int level;

		private long startTime = 0;
		private int points = 0, phraseCount = 0;
		private boolean started = false, stopped = false;
		private long pausedTime = 0;

		// creates a new instance
		public GamePlay(LinkedList<String> words, int level) {
			this.words = words;
			this.level = level;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TICK: {
				final long remainingTime = TIME_TO_GUESS
						- (System.currentTimeMillis() - startTime);
				if (remainingTime > 0) {
					mTextTimer
							.setText(DateUtils.formatElapsedTime(remainingTime
									/ SECOND_IN_MILLIS));
					sendEmptyMessageDelayed(MSG_TICK, DELAY);
				} else {
					if (next())
						sendEmptyMessageDelayed(MSG_TICK, DELAY);
					else
						stop();
				}

				break;
			} // tick message
			}
		}

		// starts game play
		public void start() {
			if (started)
				return;

			started = true;
			mVibrator.vibrate(VIBRATION_DURATION);

			next();
			sendEmptyMessage(MSG_TICK);
		}

		// scores and goes to next word
		public void score() {
			mVibrator.vibrate(VIBRATION_DURATION);

			int maxPoints = WordsContract.getMaxPoints(level);
			int elapsedTimeSeconds = (int) ((System.currentTimeMillis() - startTime) / SECOND_IN_MILLIS);
			points += maxPoints / Math.max(1, elapsedTimeSeconds);

			mTextPoints.setText(Integer.toString(points));
			if (!next())
				stop();
		}

		// skips current word and goes to next word
		public boolean next() {
			if (words.isEmpty())
				return false;

			startTime = System.currentTimeMillis();
			mTextWords.setText(words.pop());
			phraseCount++;

			return true;
		}

		// pause game
		public void pause() {
			if (isPaused() || !started)
				return;

			removeCallbacksAndMessages(null);
			mVibrator.vibrate(VIBRATION_DURATION);

			pausedTime = System.currentTimeMillis();

			mTextDescription.setText(R.string.msg_some_actions_to_resume_games);
			mTextPaused.setVisibility(View.VISIBLE);
			mViewGroupProgressBar.setVisibility(View.VISIBLE);
		}

		// Resumes game
		public void resume() {
			if (!isPaused())
				return;

			mVibrator.vibrate(VIBRATION_DURATION);

			mViewGroupProgressBar.setVisibility(View.GONE);
			startTime = System.currentTimeMillis() - (pausedTime - startTime);
			pausedTime = 0;

			sendEmptyMessage(MSG_TICK);
		}

		// check to see if the game is paused
		public boolean isPaused() {
			return pausedTime > 0;
		}

		// check to see if the game is stopped
		public boolean isStopped() {
			return stopped;
		}

		// stops game and shows a dialogue to to view results or exit or playagain
		public void stop() {
			stopped = true;
			removeCallbacksAndMessages(null);

			ContentValues values = new ContentValues();
			values.put(GameResultsContract.COLUMN_LEVEL, level);
			values.put(GameResultsContract.COLUMN_PHRASE_COUNT, phraseCount);
			values.put(GameResultsContract.COLUMN_POINT, points);

			getContentResolver().insert(
					GameResultsContract.getContentUri(PlayActivity.this),
					values);

			// build the dialogue
			new AlertDialog.Builder(PlayActivity.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage(
							getString(R.string.pattern_text_your_points, points))
					.setNegativeButton(R.string.exit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNeutralButton(R.string.text_view_results,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											PlayActivity.this,
											GameResultsActivity.class);
									startActivity(intent);

									finish();
								}
							})
					.setPositiveButton(R.string.text_play_again,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startGame();
								}
							}).show();
		} // cancels game
		public void cancel() {
			removeCallbacksAndMessages(null);
		}

	}

}
