package com.kai7framework.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * service class for downloading multiple files from web <br>
 * <br>
 * - to start: startService(new Intent(context, SomeOverriddenClass.class)); <br>
 * - to stop: stopService(new Intent(context, SomeOverriddenClass.class)); <br>
 * 
 * @author meinside@gmail.com
 * @since 10.11.05.
 *        last update 11.03.13.
 * 
 */
public abstract class FileDownloadService extends Service {
	public static final int SERVICE_ID = 0x101104;
	public static final int BYTES_BUFFER_SIZE = 32 * 1024;

	private NotificationManager notificationManager;
	private final IBinder binder = new FileDownloadBinder();
	private AsyncDownloadTask task = null;

	protected static boolean isRunning = false;

	public class FileDownloadBinder extends Binder {
		FileDownloadService getService() {
			return FileDownloadService.this;
		}
	}

	/**
	 * 
	 * @return if service is running or not
	 */
	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public void onCreate() {
		if (isRunning)
			return;
		else
			isRunning = true;

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// start downloading immediately
		task = new AsyncDownloadTask();
		task.execute();

		// Logger.v("service created");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (task != null) {
			if (!task.isCancelled())
				task.cancel(true);
		}

		isRunning = false;

		// Logger.v("service destroyed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * implement this function to decide what intent to be called when user
	 * clicks download notification <br>
	 * <br>
	 * ex) <br>
	 * 
	 * <pre>
	 * protected Class&lt;?&gt; getIntentForLatestInfo() {
	 * 	return SomeActivity.class;
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	abstract protected Class<?> getIntentForLatestInfo();

	/**
	 * implement this function to customize notification flag <br>
	 * <br>
	 * ex) <br>
	 * 
	 * <pre>
	 * protected int getNotificationFlag() {
	 * 	return Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
	 * }
	 * </pre>
	 * 
	 * @return
	 */
	abstract protected int getNotificationFlag();

	/**
	 * implement this function to provide target files <br>
	 * <br>
	 * (HashMap's key = remote file path, value = local file path)
	 * 
	 * @return
	 */
	abstract protected HashMap<String, String> getTargetFiles();

	/**
	 * called when all downloads are finished
	 * 
	 * @param successCount
	 * @param failedFiles
	 */
	abstract protected void onFinishDownload(int successCount, HashMap<String, String> failedFiles);

	/**
	 * 
	 * @return
	 */
	abstract protected int getNotificationIcon();

	/**
	 * override this function to customize progress view on notification <br>
	 * <br>
	 * ex) <br>
	 * 
	 * <pre>
	 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
	 * &lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	 *   android:orientation="horizontal" android:layout_width="fill_parent"
	 *   android:layout_height="fill_parent" android:padding="3dp"&gt;
	 *   &lt;ImageView android:id="@+id/image" android:layout_width="30dip"
	 *     android:layout_height="30dip" android:layout_marginRight="10dp" /&gt;
	 *   &lt;LinearLayout android:orientation="vertical"
	 *     android:layout_width="fill_parent" android:layout_height="fill_parent"
	 *     android:padding="3dp"&gt;
	 *     &lt;ProgressBar android:id="@+id/progress"
	 *       android:layout_width="200dip" android:layout_height="20dip"
	 *       style="?android:attr/progressBarStyleHorizontal" android:max="100"
	 *       android:progress="0" /&gt;
	 *     &lt;TextView android:id="@+id/text" android:layout_width="wrap_content"
	 *       android:layout_height="20dip" android:textColor="#000" /&gt;
	 *   &lt;/LinearLayout&gt;
	 * &lt;/LinearLayout&gt;
	 * </pre>
	 * 
	 * and <br>
	 * <br>
	 * 
	 * <pre>
	 * protected RemoteViews getProgressView(int currentNumFile, int totalNumFiles,
	 * 		int currentReceivedBytes, int totalNumBytes) {
	 * 	RemoteViews contentView = new RemoteViews(getPackageName(),
	 * 			R.layout.progress);
	 * 	contentView.setImageViewResource(R.id.image, R.drawable.icon);
	 * 	contentView.setTextViewText(R.id.text,
	 * 			String.format(&quot;Progress (%d / %d)&quot;, currentNumFile, totalNumFiles));
	 * 	contentView.setProgressBar(R.id.progress, 100, 100 * currentReceivedBytes
	 * 			/ totalNumBytes, false);
	 * 	return contentView;
	 * }
	 * </pre>
	 * 
	 * @param currentNumFile
	 * @param totalNumFiles
	 * @param currentReceivedBytes
	 * @param totalNumBytes
	 * @return
	 */
	protected RemoteViews getProgressView(int currentNumFile,
			int totalNumFiles, int currentReceivedBytes, int totalNumBytes) {
		return null;
	}

	/**
	 * 
	 * @param title
	 * @param content
	 */
	protected void showNotification(String ticker, String title, String content) {
		Notification notification = new Notification(getNotificationIcon(),
				ticker, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getIntentForLatestInfo()),
				Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notification.setLatestEventInfo(getApplicationContext(), title,
				content, contentIntent);
		notification.flags = getNotificationFlag();

		notificationManager.notify(SERVICE_ID, notification);
	}

	/**
	 * 
	 * @param remoteView
	 * @param ticker
	 */
	protected void showNotification(RemoteViews remoteView, String ticker) {
		Notification notification = new Notification(getNotificationIcon(),
				ticker, System.currentTimeMillis());
		notification.contentView = remoteView;
		notification.contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getIntentForLatestInfo()),
				Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notification.flags = getNotificationFlag();

		notificationManager.notify(SERVICE_ID, notification);
	}

	/**
	 * override this function to alter socket connect timeout value
	 * 
	 * @return
	 */
	protected int getConnectTimeout() {
		return 2000;
	}

	/**
	 * override this function to alter socket read timeout value
	 * 
	 * @return
	 */
	protected int getReadTimeout() {
		return 15000;
	}

	/**
	 * 
	 * AsyncTask for downloading multiple files
	 * 
	 * @author meinside@gmail.com
	 * @since 10.11.05.
	 * 
	 *        last update 11.03.13.
	 * 
	 */
	private class AsyncDownloadTask extends AsyncTask<Void, Void, Void> {
		private int successCount;
		private int numTotalFiles;
		private HashMap<String, String> targetFiles = null;
		private HashMap<String, String> failedFiles = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			successCount = 0;

			targetFiles = getTargetFiles();
			numTotalFiles = targetFiles.size();
			failedFiles = new HashMap<String, String>();
		}

		/**
		 * get file's size at given url (using http header)
		 * 
		 * @param url
		 * @return -1 when failed
		 */
		public int getFileSizeAtURL(URL url) {
			int filesize = -1;
			try {
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();
				filesize = http.getContentLength();
				http.disconnect();
			} catch (Exception e) {
				// Logger.e(e.toString());
			}
			return filesize;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			String remoteFilepath, localFilepath;
			for (Entry<String, String> entry : targetFiles.entrySet()) {
				remoteFilepath = entry.getKey();
				localFilepath = entry.getValue();

				// Logger.v("downloading: '" + remoteFilepath + "' => '" +
				// localFilepath + "'");

				try {
					if (isCancelled())
						return null;

					URL url = new URL(remoteFilepath);
					int filesize = getFileSizeAtURL(url);

					int loopCount = 0;
					if (filesize > 0) {
						URLConnection connection = url.openConnection();
						connection.setConnectTimeout(getConnectTimeout());
						connection.setReadTimeout(getReadTimeout());

						BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
						FileOutputStream fos = new FileOutputStream(new File(localFilepath));
						int bytesRead, totalBytesRead = 0;
						byte[] bytes = new byte[BYTES_BUFFER_SIZE];
						String progress, kbytes;
						while (!isCancelled() && (bytesRead = bis.read(bytes)) != -1) {
							totalBytesRead += bytesRead;
							fos.write(bytes, 0, bytesRead);

							// don't show notification too often
							if (!isCancelled() && loopCount++ % 20 == 0) {
								RemoteViews progressView = getProgressView(
										successCount + 1, numTotalFiles,
										totalBytesRead, filesize);
								if (progressView == null) {
									progress = String.format(
											"Download Progress (%d / %d)",
											successCount + 1, numTotalFiles);
									kbytes = String.format("%s / %s",
											getStringByteSize(totalBytesRead),
											getStringByteSize(filesize));

									if (!isCancelled())
										showNotification("Downloading File(s)",
												progress, kbytes);
								} else {
									if (!isCancelled())
										showNotification(progressView, "Downloading File(s)");
								}
							}
						}
						fos.close();
						bis.close();

						if (isCancelled())
							return null;

						successCount++;
					} else {
						// Logger.i("file size unknown for remote file: " +
						// remoteFilepath);

						failedFiles.put(remoteFilepath, localFilepath);
					}
				} catch (Exception e) {
					Log.e("FileDownloadService", e.toString());

					showNotification("Download Failed", "Download Progress", "Failed: " + (new File(remoteFilepath)).getName());

					failedFiles.put(remoteFilepath, localFilepath);
				}
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			// Logger.v("download task cancelled");

			showNotification("Download Cancelled", "Download Progress", "Cancelled");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			onFinishDownload(successCount, failedFiles);

			String finished;
			if (successCount != numTotalFiles)
				finished = String.format("Finished (%d download(s) failed)", numTotalFiles - successCount);
			else
				finished = "Finished";
			showNotification("Download Finished", "Download Progress", finished);

			// Logger.v("download task finished");
		}
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	protected String getStringByteSize(int size) {
		if (size > 1024 * 1024) // mega
		{
			return String.format("%.1f MB", size / (float) (1024 * 1024));
		} else if (size > 1024) // kilo
		{
			return String.format("%.1f KB", size / 1024.0f);
		} else {
			return String.format("%d B");
		}
	}
}