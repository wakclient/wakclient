package de.wak_sh.client.backend;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import de.wak_sh.client.R;
import de.wak_sh.client.SettingsActivity;
import de.wak_sh.client.backend.service.FileService;

public class FileDownloadTask extends AsyncTask<String, Void, Void> {

	private Context context;
	private String filename;
	private File file;

	private int id;

	private NotificationCompat.Builder builder;
	private NotificationManager manager;

	private File dir;

	public FileDownloadTask(Context context, String filename) {
		this.context = context;
		this.filename = filename;
		id = (int) System.currentTimeMillis();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String dirPath = prefs.getString(
				SettingsActivity.PREF_STORAGE_LOCATION, "/");

		dir = new File(dirPath);
	}

	@Override
	protected void onPreExecute() {
		builder = new NotificationCompat.Builder(context);
		builder.setContentTitle(context.getString(R.string.download));
		builder.setContentText(filename);
		builder.setSmallIcon(R.drawable.download_animation);
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		file = new File(dir, filename);

		if (!dir.exists()) {
			boolean create = dir.mkdirs();
			if (!create) {
				Toast.makeText(context, "Ordner konnte nicht erstellt werden!",
						Toast.LENGTH_SHORT).show();
			}
		}

		builder.setProgress(0, 0, true);
		manager.notify(id, builder.build());
	}

	@Override
	protected Void doInBackground(String... params) {
		String url = params[0];

		if (!isCancelled()) {
			FileService service = FileService.getInstance();

			try {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, R.string.start_download,
								Toast.LENGTH_SHORT).show();
					}
				});

				service.downloadFile(url, filename, context);
			} catch (final IOException e) {
				manager.cancel(id);
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("IOException");
						builder.setMessage(e.getMessage());
						builder.setNeutralButton(android.R.string.cancel,
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.create().show();
					}
				});
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(file
				.getAbsolutePath());
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				extension);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), mime);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		builder.setAutoCancel(true);
		builder.setContentTitle(context.getString(R.string.download_complete));
		builder.setProgress(0, 0, false);
		builder.setSmallIcon(R.drawable.menuitem_checkbox_on);
		builder.setContentIntent(pendingIntent);
		manager.notify(id, builder.build());
	}
}
