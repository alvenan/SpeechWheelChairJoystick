package br.edu.ufam.engcomp.wheelchair.speech;

import java.util.ArrayList;

import android.graphics.Color;
import android.widget.TextView;
import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public class SpeechComponent {

	public static byte[] doSpeech(String direction, ArrayList<TextView> tvList,
			int power) {
		changeTextColor(direction, tvList);
		return speechDirection(direction, power);

	}

	public static byte[] speechDirection(String direction, int power) {

		byte[] byteCommand = { (byte) '-', (byte) ';', (byte) '-', (byte) ';',
				(byte) '-', (byte) ';', (byte) '-' };

		if (direction.equals("front") || direction.equals("truant")
				|| direction.equals("throat") || direction.equals("trunk")) {
			byteCommand[0] = Constants.COMMAND_UP;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE + (power * 98 / 100));

		}
		if (direction.equals("back") || direction.equals("rec")
				|| direction.equals("bec")) {
			byteCommand[0] = Constants.COMMAND_DOWN;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE - (power * 9 / 10));

		}
		if (direction.equals("right")) {
			byteCommand[0] = Constants.COMMAND_RIGHT;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE - (power * 9 / 10));

		}
		if (direction.equals("left")) {
			byteCommand[0] = Constants.COMMAND_LEFT;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE + (power * 98 / 100));

		}
		if (direction.equals("stop") || direction.equals("stopped")) {
			byteCommand[0] = Constants.COMMAND_CENTER;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE);

		}

		return byteCommand;
	}

	public static void changeTextColor(String direction,
			ArrayList<TextView> tvList) {
		if (direction.equals("front") || direction.equals("truant")
				|| direction.equals("throat") || direction.equals("trunk")) {
			tvList.get(0).setTextColor(Color.RED);
			tvList.get(1).setTextColor(Color.BLACK);
			tvList.get(2).setTextColor(Color.BLACK);
			tvList.get(3).setTextColor(Color.BLACK);
			tvList.get(4).setTextColor(Color.BLACK);
		}
		if (direction.equals("back") || direction.equals("rec")
				|| direction.equals("bec")) {

			tvList.get(0).setTextColor(Color.BLACK);
			tvList.get(1).setTextColor(Color.RED);
			tvList.get(2).setTextColor(Color.BLACK);
			tvList.get(3).setTextColor(Color.BLACK);
			tvList.get(4).setTextColor(Color.BLACK);

		}
		if (direction.equals("right")) {
			tvList.get(0).setTextColor(Color.BLACK);
			tvList.get(1).setTextColor(Color.BLACK);
			tvList.get(2).setTextColor(Color.RED);
			tvList.get(3).setTextColor(Color.BLACK);
			tvList.get(4).setTextColor(Color.BLACK);

		}
		if (direction.equals("left")) {
			tvList.get(0).setTextColor(Color.BLACK);
			tvList.get(1).setTextColor(Color.BLACK);
			tvList.get(2).setTextColor(Color.BLACK);
			tvList.get(3).setTextColor(Color.RED);
			tvList.get(4).setTextColor(Color.BLACK);

		}
		if (direction.equals("stop") || direction.equals("stopped")) {
			tvList.get(0).setTextColor(Color.BLACK);
			tvList.get(1).setTextColor(Color.BLACK);
			tvList.get(2).setTextColor(Color.BLACK);
			tvList.get(3).setTextColor(Color.BLACK);
			tvList.get(4).setTextColor(Color.RED);

		}

	}

	public static void setAllTextColorBlack(ArrayList<TextView> tvList) {

		tvList.get(0).setTextColor(Color.BLACK);
		tvList.get(1).setTextColor(Color.BLACK);
		tvList.get(2).setTextColor(Color.BLACK);
		tvList.get(3).setTextColor(Color.BLACK);
		tvList.get(4).setTextColor(Color.BLACK);
	}
}
