package br.edu.ufam.engcomp.wheelchair;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.ufam.engcomp.wheelchair.adk.AbsAdkActivity;
import br.edu.ufam.engcomp.wheelchair.joystick.JoystickComponent;
import br.edu.ufam.engcomp.wheelchair.speech.SpeechComponent;
import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public class MainActivity extends AbsAdkActivity {

	private RelativeLayout joystickLayout;
	private JoystickComponent joystick;

	private String direction = "stop";

	private ImageButton voiceCommandButton;

	private boolean enableLogcatDebug = false;

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
		voiceCommandButton.setOnClickListener(onClickToSpeakListener());
	}

	public void initilize() {
		joystickLayout = (RelativeLayout) findViewById(R.id.joystick_layout);
		voiceCommandButton = (ImageButton) findViewById(R.id.voice_button);
		joystick = new JoystickComponent(getApplicationContext(),
				joystickLayout, R.drawable.joystick_button);
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

				try {
					startActivityForResult(intent, Constants.RESULT_SPEECH);
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}

			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		joystick.drawStick();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> textDirection = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				direction = textDirection.get(0);
				create = false;
				// if (!isRunning) {
				// long now = SystemClock.uptimeMillis();
				// long next = now + (100 - now % 1000);
				//
				// handler.postAtTime(writer, next);
				// isRunning = true;
				// }
			}
			break;
		}

		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		WriteAdk(SpeechComponent.speechDirection(direction));
	}

	@Override
	protected void doAdkRead(String stringIn) {
	}
}
