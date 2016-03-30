package com.kai7framework.network;

import java.util.HashMap;

import android.R;
import android.app.Notification;

import com.kai7framework.Main;

public class ApkDownloadService extends FileDownloadService {
	protected Class<?> getIntentForLatestInfo() {
		return Main.class;
	}

	protected int getNotificationFlag() {
		return Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
	}

	protected HashMap<String, String> getTargetFiles() {
		HashMap<String, String> files = new HashMap<String, String>();
		files.put("http://www.ohbaba.com/files/Ohbaba-Release-20111223-A.apk", android.os.Environment.getExternalStorageDirectory()+"/Ohbaba-Release-20111223-A.apk");
		return files;
	}

	protected void onFinishDownload(int successCount, HashMap<String, String> failedFiles) {
		
	}

	protected int getNotificationIcon() {
		return R.drawable.arrow_down_float;
	}
}
