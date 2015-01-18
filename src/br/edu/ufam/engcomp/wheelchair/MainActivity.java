package br.edu.ufam.engcomp.wheelchair;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.edu.ufam.engcomp.wheelchair.adk.AbsAdkActivity;
import br.edu.ufam.engcomp.wheelchair.joystick.JoystickComponent;
import br.edu.ufam.engcomp.wheelchair.speech.SpeechComponent;

@SuppressLint("NewApi")
public class MainActivity extends AbsAdkActivity implements
		android.view.View.OnClickListener {

	private RelativeLayout joystickLayout;
	private JoystickComponent joystick;

	private String direction = "stop";

	private ImageButton voiceCommandButton;

	private boolean enableLogcatDebug = false;

	private SpeechRecognizer sr;

	private ArrayList<TextView> tvDirections;

	// private boolean isRunning = false;

	// private Handler handler;

	// private final Runnable writer = new Runnable() {
	//
	// @Override
	// public void run() {
	// if (!direction.equals("stop")) {
	// WriteAdk(SpeechComponent.speechDirection(direction));
	// long now = SystemClock.uptimeMillis();
	// long next = now + (100 - now % 1000);
	// Log.i("###","ENVIANDO");
	// isRunning=true;
	//
	// handler.postAtTime(writer, next);
	// } else {
	// if (isRunning) {
	// handler.removeCallbacks(writer);
	// isRunning = false;
	// }
	// }
	//
	// }
	// };

	@Override
	protected void doOnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		FullScreencall();
		initilize();
		tvDirections.get(4).setTextColor(Color.RED);
		joystickLayout.setOnTouchListener(onTouchJoystickListener());
		voiceCommandButton.setOnClickListener(this);
		sr = SpeechRecognizer.createSpeechRecognizer(this);
		sr.setRecognitionListener(new VoiceRecognition());
		;
	}

	public void initilize() {

		joystickLayout = (RelativeLayout) findViewById(R.id.joystick_layout);
		voiceCommandButton = (ImageButton) findViewById(R.id.voice_button);
		joystick = new JoystickComponent(getApplicationContext(),
				joystickLayout, R.drawable.joystick_button);

		tvDirections = new ArrayList<TextView>();
		tvDirections.add((TextView) findViewById(R.id.tv_front));
		tvDirections.add((TextView) findViewById(R.id.tv_back));
		tvDirections.add((TextView) findViewById(R.id.tv_right));
		tvDirections.add((TextView) findViewById(R.id.tv_left));
		tvDirections.add((TextView) findViewById(R.id.tv_stop));
		// handler = new Handler();
		// writer.run();
	}

	public OnTouchListener onTouchJoystickListener() {
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// if (isRunning) {
				// direction = "stop";
				// }
				SpeechComponent.setAllTextColorBlack(tvDirections);
				joystick.drawStick(event);
				WriteAdk(joystick.getJoystickPositionInByte(event,
						enableLogcatDebug));
				return true;
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		joystick.drawStick();
	}

	@Override
	protected void doAdkRead(String stringIn) {
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.voice_button) {
			view.setBackground(getResources().getDrawable(
					R.drawable.voice_button_pressed));
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
					"voice.recognition.test");

			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
			sr.startListening(intent);
		}

	}

	public void FullScreencall() {
		if (Build.VERSION.SDK_INT < 19) { // 19 or above api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else {
			// for lower api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	public class VoiceRecognition implements RecognitionListener {
		@Override
		public void onReadyForSpeech(Bundle params) {
		}

		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			voiceCommandButton.setBackground(getResources().getDrawable(
					R.drawable.voice_button));
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(int error) {
			Log.i("@@@", "error " + error);
			voiceCommandButton.setBackground(getResources().getDrawable(
					R.drawable.voice_button));
		}

		@Override
		public void onResults(Bundle results) {
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			direction = data.get(0);
			WriteAdk(SpeechComponent.doSpeech(direction, tvDirections));
			Log.i("@@@", direction);
			voiceCommandButton.setBackground(getResources().getDrawable(
					R.drawable.voice_button));
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

	}

}
