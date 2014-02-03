package de.wak_sh.client.backend;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import de.wak_sh.client.R;
import de.wak_sh.client.SettingsActivity;
import de.wak_sh.client.model.FileLink;
import de.wak_sh.client.service.JsoupFileService;

public class FileDownloader {

	private Context mContext;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mManager;

	private int id;

	public FileDownloader(Context context) {
		this.mContext = context;
	}

	public void download(final FileLink fileLink) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(mContext);

				String path = prefs.getString(
						SettingsActivity.PREF_STORAGE_LOCATION,
						Environment.getExternalStorageDirectory()
								+ File.separator + "Download")
						+ File.separator + fileLink.getName();
				buildNotification(fileLink.getName());

				try {
					File file = new File(path.replaceAll("\n", ""));
					file.createNewFile();

					FileOutputStream fos = new FileOutputStream(file);

					InputStream is = JsoupFileService.getInstance()
							.getFileStream(fileLink);
					BufferedInputStream bis = new BufferedInputStream(is);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					byte[] buffer = new byte[1024];

					while (bis.read(buffer) != -1) {
						bos.write(buffer);
					}

					bis.close();
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(mContext,
							"Möglicherweise ist der Downloadpfad ungültig",
							Toast.LENGTH_LONG).show();
					;
					killNotification(null, false);
					return;
				}

				killNotification(path, true);
			}
		}).start();
	}

	public void download(final String url, final String filename) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(mContext);

				String path = prefs.getString(
						SettingsActivity.PREF_STORAGE_LOCATION,
						Environment.getExternalStorageDirectory()
								+ File.separator + "Download")
						+ File.separator + filename;
				buildNotification(filename);

				try {
					File file = new File(path.replaceAll("\n", ""));
					file.createNewFile();

					FileOutputStream fos = new FileOutputStream(file);

					InputStream is = JsoupFileService.getInstance()
							.getFileStream(url);
					BufferedInputStream bis = new BufferedInputStream(is);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					byte[] buffer = new byte[1024];

					while (bis.read(buffer) != -1) {
						bos.write(buffer);
					}

					bis.close();
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
					killNotification(null, false);
					return;
				}

				killNotification(path, true);
			}
		}).start();
	}

	private void buildNotification(String filename) {
		id = (int) System.currentTimeMillis();

		mManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);

		mBuilder.setContentTitle(filename);
		mBuilder.setContentText(mContext.getString(R.string.download));
		mBuilder.setSmallIcon(R.drawable.download_animation);
		mBuilder.setProgress(0, 0, true);

		mManager.notify(id, mBuilder.build());
	}

	private void killNotification(String path, boolean result) {
		mBuilder.setAutoCancel(true);
		mBuilder.setProgress(0, 0, false);

		if (result) {
			String extension = MimeTypeMap.getFileExtensionFromUrl(path);
			String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
					extension);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(path)), mime);
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
					0, intent, 0);

			mBuilder.setContentText(mContext
					.getString(R.string.download_complete));
			mBuilder.setSmallIcon(R.drawable.ic_menu_goto);
			mBuilder.setContentIntent(pendingIntent);
		} else {
			mBuilder.setContentText(mContext
					.getString(R.string.download_incomplete));
			mBuilder.setSmallIcon(R.drawable.ic_menu_close_clear_cancel);
		}

		mManager.notify(id, mBuilder.build());
	}
}
