package br.edu.ufam.engcomp.wheelchair;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.ufam.engcomp.wheelchair.adk.AbsAdkActivity;
import br.edu.ufam.engcomp.wheelchair.joystick.JoystickComponent;
import br.edu.ufam.engcomp.wheelchair.speech.SpeechService;
import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public class MainActivity extends AbsAdkActivity {

	private RelativeLayout joystickLayout;
	private JoystickComponent joystick;

	SpeechService service;

	private ImageButton voiceCommandButton;
	private TextView text;

	private boolean enableLogcatDebug = false;

	private boolean isRunning = false;

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
		text = (TextView) findViewById(R.id.voice_text);
		joystick = new JoystickComponent(getApplicationContext(),
				joystickLayout, R.drawable.joystick_button);
	}

	public OnTouchListener onTouchJoystickListener() {
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				joystick.drawStick(event);
				text.setText(Arrays.toString(joystick
						.getJoystickPositionInByte(event, enableLogcatDebug)));
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
					text.setText("");
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
		if (isRunning) {
			onCancelService();
		}
		isRunning = true;

		switch (requestCode) {
		case Constants.RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> textDirection = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				text.setText(textDirection.get(0));
				service = new SpeechService();
				service.execute(textDirection.get(0));
			}
			break;
		}

		}
	}

	public void onCancelService() {
		if (isRunning) {
			isRunning = false;
			service.cancel(true);

		}
	}

	@Override
	protected void doAdkRead(String stringIn) {
	}
}
