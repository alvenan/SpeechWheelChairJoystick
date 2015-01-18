package br.edu.ufam.engcomp.wheelchair.speech;

import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public class SpeechComponent {

	public static byte[] speechDirection(String direction) {

		byte[] byteCommand = { (byte) '-', (byte) ';', (byte) '-', (byte) ';',
				(byte) '-', (byte) ';', (byte) '-' };

		if (direction.equals("front") || direction.equals("truant")
				|| direction.equals("throat")||direction.equals("trunk")) {
			byteCommand[0] = Constants.COMMAND_UP;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE + (Constants.SEVENTY_PERCENT * 98 / 100));

		}
		if (direction.equals("back")) {
			byteCommand[0] = Constants.COMMAND_DOWN;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE - (Constants.SEVENTY_PERCENT * 9 / 10));

		}
		if (direction.equals("right")) {
			byteCommand[0] = Constants.COMMAND_RIGHT;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE - (Constants.SEVENTY_PERCENT * 9 / 10));

		}
		if (direction.equals("left")) {
			byteCommand[0] = Constants.COMMAND_LEFT;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE + (Constants.SEVENTY_PERCENT * 98 / 100));

		}
		if (direction.equals("stop") || direction.equals("stopped")) {
			byteCommand[0] = Constants.COMMAND_CENTER;
			byteCommand[2] = (byte) (Constants.PATTERN_DAC_VALUE);

		}

		return byteCommand;
	}
}
