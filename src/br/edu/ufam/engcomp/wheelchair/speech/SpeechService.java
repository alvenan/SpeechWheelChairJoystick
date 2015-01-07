package br.edu.ufam.engcomp.wheelchair.speech;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class SpeechService extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... params) {
		int i = 0;
		Log.i("###", "Params = " + params[0]);
		while (!params[0].equals("stop")) {
			Log.i("###", "ENTROU! | " + i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i += 1;
		}
		Log.i("###", "PASSOU!");
		return null;
	}

}
