package de.wak_sh.client.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.wak_sh.client.R;
import de.wak_sh.client.SettingsActivity;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.adapters.FileItemArrayAdapter;
import de.wak_sh.client.backend.adapters.FileItemArrayAdapter.FragmentInterface;
import de.wak_sh.client.backend.model.FileItem;
import de.wak_sh.client.backend.service.FileService;

public class DateiablageFragment extends Fragment implements FragmentInterface {

	private FileItemArrayAdapter adapter;
	private List<FileItem> items = new ArrayList<FileItem>();
	private String path;
	private File dir;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		adapter = new FileItemArrayAdapter(getActivity(), items, this);
		ListView listView = (ListView) rootView;
		listView.setOnItemClickListener(clickListener);
		listView.setAdapter(adapter);

		if (getArguments() != null) {
			path = getArguments().getString("path");
		}

		if (items.isEmpty()) {
			new FileResolveTask(getActivity()).execute();
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(rootView.getContext());
		String dirPath = prefs.getString(
				SettingsActivity.PREF_STORAGE_LOCATION, "/");

		dir = new File(dirPath);

		return rootView;
	}

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final FileItem item = items.get(position);

			if (item.isFile()) {
				final File file = new File(dir, item.getName());

				if (file.exists()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Hinweis");
					builder.setMessage(String
							.format("Die Datei '%s' ist bereits vorhanden. Soll sie ersetzt werden?",
									item.getName()));
					builder.setPositiveButton(android.R.string.yes,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (!file.delete()) {
										Toast.makeText(
												getActivity(),
												"Datei konnte nicht gel�scht werden!",
												Toast.LENGTH_SHORT).show();
									}
									dialog.dismiss();
									new FileDownloadTask(getActivity(), item)
											.execute();
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					new FileDownloadTask(getActivity(), item).execute();
				}
			} else {
				Bundle bundle = new Bundle();
				bundle.putString("path", items.get(position).getPath());

				DateiablageFragment fragment = new DateiablageFragment();
				fragment.setArguments(bundle);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment)
						.addToBackStack(null).commit();
			}
		}
	};

	private class FileResolveTask extends ProgressDialogTask<Void, Void> {

		private Activity activity;

		public FileResolveTask(Activity activity) {
			super(activity, activity.getString(R.string.fetching_filesystem));
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			FileService service = FileService.getInstance();
			List<FileItem> list = null;

			try {
				if (path == null) {
					list = service.getMountpoints();
				} else {
					list = service.getFileItems(path);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			items.clear();
			items.addAll(list);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});

			return null;
		}
	}

	private class FileDownloadTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private FileItem item;
		private File file;

		private int id;

		private NotificationCompat.Builder builder;
		private NotificationManager manager;

		public FileDownloadTask(Context context, FileItem item) {
			this.context = context;
			this.item = item;
			id = (int) System.currentTimeMillis();
		}

		@Override
		protected void onPreExecute() {
			builder = new NotificationCompat.Builder(context);
			builder.setContentTitle(context.getString(R.string.download));
			builder.setContentText(item.getName());
			builder.setSmallIcon(R.drawable.download_animation);
			manager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			file = new File(dir, item.getName());

			if (!dir.exists()) {
				boolean create = dir.mkdirs();
				if (!create) {
					Toast.makeText(context,
							"Ordner konnte nicht erstellt werden!",
							Toast.LENGTH_SHORT).show();
				}
			}

			builder.setProgress(0, 0, true);
			manager.notify(id, builder.build());
		}

		@Override
		protected Void doInBackground(Void... params) {
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

					service.downloadFile(item.getPath(), item.getName(),
							context);
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
										public void onClick(
												DialogInterface dialog,
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
			builder.setContentTitle(context
					.getString(R.string.download_complete));
			builder.setProgress(0, 0, false);
			builder.setSmallIcon(R.drawable.menuitem_checkbox_on);
			builder.setContentIntent(pendingIntent);
			manager.notify(id, builder.build());
		}
	}

	private class FileRenameTask extends ProgressDialogTask<FileItem, Void> {
		private String newName;

		public FileRenameTask(Context context, String text, String newName) {
			super(context, text);
			this.newName = newName;
		}

		@Override
		protected Void doInBackground(FileItem... params) {
			FileItem item = params[0];
			try {
				FileService.getInstance().renameFile(item, newName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private class FileDeleteTask extends ProgressDialogTask<FileItem, Void> {

		public FileDeleteTask(Context context, String text) {
			super(context, text);
		}

		@Override
		protected Void doInBackground(FileItem... params) {
			FileItem item = params[0];
			try {
				FileService.getInstance().deleteFile(item);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public void doRename(final FileItem item) {
		final EditText input = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Umbennen");
		builder.setMessage("Neuer Dateiname");
		builder.setView(input);
		builder.setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newName = input.getText().toString();
				if (newName.length() == 0) {
					Toast.makeText(getActivity(), "Ung�ltiger Dateiname",
							Toast.LENGTH_SHORT).show();
				} else {
					new FileRenameTask(getActivity(),
							"Datei wird umbenannt...", newName).execute(item);
					getActivity().onBackPressed();
				}
			}
		});
		builder.create().show();
	}

	@Override
	public void doDelete(final FileItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Hinweis");
		builder.setMessage("Sind Sie sicher das Sie die folgende Datei l�schen m�chten?\n\n"
				+ item.getName());
		builder.setNegativeButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new FileDeleteTask(getActivity(), "Datei wird gel�scht...")
						.execute(item);
				getActivity().onBackPressed();
			}
		});
		builder.create().show();
	}
}
