package de.wak_sh.client.backend;

import java.io.File;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import de.wak_sh.client.R;
import de.wak_sh.client.model.FileLink;

public class FileUploader {
	private Context mContext;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mManager;

	private int id;

	public FileUploader(Context context) {
		this.mContext = context;
	}

	public void upload(final FileLink fileLink, final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(path);
				buildNotification(file.getName());

				// TODO: Implement

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
		mBuilder.setContentText("Upload...");
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

			mBuilder.setContentText("Upload fertiggestellt");
			mBuilder.setSmallIcon(R.drawable.ic_menu_goto);
			mBuilder.setContentIntent(pendingIntent);
		} else {
			mBuilder.setContentText("Upload fehlerhaft");
			mBuilder.setSmallIcon(R.drawable.ic_menu_close_clear_cancel);
		}

		mManager.notify(id, mBuilder.build());
	}

}
