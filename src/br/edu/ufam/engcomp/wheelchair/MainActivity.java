package br.edu.ufam.engcomp.wheelchair;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
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

public class MainActivity extends AbsAdkActivity implements
		android.view.View.OnClickListener {

	private RelativeLayout joystickLayout;
	private JoystickComponent joystick;

	private String direction = "stop";

	private ImageButton voiceCommandButton;

	private boolean enableLogcatDebug = false;

	private SpeechRecognizer sr;

	private TextView mText;

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

		initilize();

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
		mText = (TextView) findViewById(R.id.textView1);
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
				joystick.drawStick(event);
				WriteAdk(joystick.getJoystickPositionInByte(event,
						enableLogcatDebug));
				return true;
			}
		};
	}

	public View.OnClickListener onClickToSpeakListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				// try {
				// startActivityForResult(intent, Constants.RESULT_SPEECH);
				// } catch (ActivityNotFoundException a) {
				// Toast t = Toast.makeText(getApplicationContext(),
				// "Ops! Your device doesn't support Speech to Text",
				// Toast.LENGTH_SHORT);
				// t.show();
				// }

			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		joystick.drawStick();
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// switch (requestCode) {
	// case Constants.RESULT_SPEECH: {
	// if (resultCode == RESULT_OK && null != data) {
	//
	// ArrayList<String> textDirection = data
	// .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	// direction = textDirection.get(0);
	// // if (!isRunning) {
	// // long now = SystemClock.uptimeMillis();
	// // long next = now + (100 - now % 1000);
	// //
	// // handler.postAtTime(writer, next);
	// // isRunning = true;
	// // }
	// }
	// break;
	// }
	//
	// }
	// }

	@Override
	protected void doAdkRead(String stringIn) {
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.voice_button) {
			view.setBackground(getResources().getDrawable(R.drawable.voice_button_pressed));
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
					"voice.recognition.test");

			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
			sr.startListening(intent);
		}

	}

	public class VoiceRecognition implements RecognitionListener {
		boolean takeTheMessage = false;

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
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(int error) {
			Log.i("@@@", "error " + error);
		}

		@Override
		public void onResults(Bundle results) {
			ArrayList<String> data = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			direction = data.get(0);
			WriteAdk(SpeechComponent.speechDirection(direction));
			takeTheMessage = true;
			Log.i("@@@", direction);
			voiceCommandButton.setBackground(getResources().getDrawable(R.drawable.voice_button));
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

	}
}
