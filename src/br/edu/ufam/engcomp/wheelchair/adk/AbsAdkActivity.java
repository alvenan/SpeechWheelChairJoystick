/*
 * abstract class for Activities have to read ADK
 * for android:minSdkVersion="12"
 * 
 */

package br.edu.ufam.engcomp.wheelchair.adk;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import br.edu.ufam.engcomp.wheelchair.utils.Constants;

public abstract class AbsAdkActivity extends Activity {

	private PendingIntent PendingIntent_UsbPermission;

	private UsbManager myUsbManager;
	private UsbAccessory myUsbAccessory;
	private ParcelFileDescriptor myAdkParcelFileDescriptor;
	private FileInputStream myAdkInputStream;
	private FileOutputStream myAdkOutputStream;
	boolean firstRqsPermission;
	
	// do something in onCreate()
	protected abstract void doOnCreate(Bundle savedInstanceState);

	// do something after adk read
	protected abstract void doAdkRead(String stringIn);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		
		myUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(myUsbReceiver, intentFilter);

		// Ask USB Permission from user
		Intent intent_UsbPermission = new Intent(
				Constants.ACTION_USB_PERMISSION);
		PendingIntent_UsbPermission = PendingIntent.getBroadcast(this, // context
				Constants.RQS_USB_PERMISSION, // request code
				intent_UsbPermission, // intent
				0); // flags
		IntentFilter intentFilter_UsbPermission = new IntentFilter(
				Constants.ACTION_USB_PERMISSION);
		registerReceiver(myUsbPermissionReceiver, intentFilter_UsbPermission);

		firstRqsPermission = true;
		doOnCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();


		if (myAdkInputStream == null || myAdkOutputStream == null) {

			// myAdkInputStream = null;
			// myAdkOutputStream = null;

			UsbAccessory[] usbAccessoryList = myUsbManager.getAccessoryList();
			UsbAccessory usbAccessory = null;
			if (usbAccessoryList != null) {
				usbAccessory = usbAccessoryList[0];

				if (usbAccessory != null) {
					if (myUsbManager.hasPermission(usbAccessory)) {
						// already have permission
						myUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
						OpenUsbAccessory(usbAccessory);
					} else {

						if (firstRqsPermission) {

							firstRqsPermission = false;

							synchronized (myUsbReceiver) {
								myUsbManager.requestPermission(usbAccessory,
										PendingIntent_UsbPermission);
							}
						}

					}
				}
			}
		}

	}

	// Write String to Adk
	void WriteAdk(String text) {

		byte[] buffer = text.getBytes();

		if (myAdkOutputStream != null) {

			try {
				myAdkOutputStream.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Write an Array of Bytes to Adk
	public void WriteAdk(byte[] buffer) {

		if (myAdkOutputStream != null) {
			try {
				myAdkOutputStream.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Write a single byte to Adk
	void WriteByteAdk(byte oneByte) {
		if (myAdkOutputStream != null) {
			try {
				myAdkOutputStream.write(oneByte);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeUsbAccessory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myUsbReceiver);
		unregisterReceiver(myUsbPermissionReceiver);
	}

	Runnable runnableReadAdk = new Runnable() {

		@Override
		public void run() {
			int numberOfByteRead = 0;
			byte[] buffer = new byte[255];

			while (numberOfByteRead >= 0) {

				try {
					numberOfByteRead = myAdkInputStream.read(buffer, 0,
							buffer.length);
					final StringBuilder stringBuilder = new StringBuilder();
					for (int i = 0; i < numberOfByteRead; i++) {
						stringBuilder.append((char) buffer[i]);
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							doAdkRead(stringBuilder.toString());
						}
					});

				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

	};

	private BroadcastReceiver myUsbReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)) {

				UsbAccessory usbAccessory = (UsbAccessory) intent
						.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

				if (usbAccessory != null && usbAccessory.equals(myUsbAccessory)) {
					closeUsbAccessory();
				}
			}
		}
	};

	private BroadcastReceiver myUsbPermissionReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.ACTION_USB_PERMISSION)) {

				synchronized (this) {

					UsbAccessory usbAccessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						OpenUsbAccessory(usbAccessory);
					} else {
						finish();
					}
				}
			}
		}

	};

	private void OpenUsbAccessory(UsbAccessory acc) {
		myAdkParcelFileDescriptor = myUsbManager.openAccessory(acc);

		if (myAdkParcelFileDescriptor != null) {
			myUsbAccessory = acc;
			FileDescriptor fileDescriptor = myAdkParcelFileDescriptor
					.getFileDescriptor();
			myAdkInputStream = new FileInputStream(fileDescriptor);
			myAdkOutputStream = new FileOutputStream(fileDescriptor);

			Thread thread = new Thread(runnableReadAdk);
			thread.start();
		}
	}

	private void closeUsbAccessory() {

		if (myAdkParcelFileDescriptor != null) {
			try {
				myAdkParcelFileDescriptor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		myAdkParcelFileDescriptor = null;
		myUsbAccessory = null;
	}
}
