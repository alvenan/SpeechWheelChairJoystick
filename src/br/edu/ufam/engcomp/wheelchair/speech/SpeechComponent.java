package br.edu.ufam.engcomp.wheelchair.speech;

import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public class SpeechComponent {

	public static byte[] speechDirection(String direction) {

		byte[] byteCommand = { (byte) '-', (byte) ';',
				(byte) Constants.PATTERN_DAC_VALUE, (byte) ';', (byte) '-',
				(byte) ';', (byte) '-' };
		if (direction.equals("front")) {
			byteCommand[0] = Constants.COMMAND_UP;

		}
		if (direction.equals("back")) {
			byteCommand[0] = Constants.COMMAND_DOWN;

		}
		if (direction.equals("right")) {
			byteCommand[0] = Constants.COMMAND_RIGHT;

		}
		if (direction.equals("left")) {
			byteCommand[0] = Constants.COMMAND_LEFT;

		}

		return byteCommand;
	}
}
