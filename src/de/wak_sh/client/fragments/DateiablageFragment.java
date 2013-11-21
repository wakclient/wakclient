package de.wak_sh.client.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.R;
import de.wak_sh.client.SettingsActivity;
import de.wak_sh.client.backend.FileDownloadTask;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.adapters.FileItemArrayAdapter;
import de.wak_sh.client.backend.adapters.FileItemArrayAdapter.FragmentInterface;
import de.wak_sh.client.backend.model.FileItem;
import de.wak_sh.client.backend.service.FileService;

public class DateiablageFragment extends SherlockFragment implements
		FragmentInterface {

	private FileItemArrayAdapter adapter;
	private List<FileItem> items = new ArrayList<FileItem>();
	private String path;
	private static String fileToUploadPath;
	private MenuItem menuItemUploadFile;

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
			if (getArguments().containsKey("path")) {
				path = getArguments().getString("path");
			}
			if (getArguments().containsKey("fileToUploadPath")) {
				fileToUploadPath = getArguments().getString("fileToUploadPath");
			}
		}

		if (items.isEmpty()) {
			new FileResolveTask(getActivity()).execute();
		}

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_dateiablage, menu);
		menuItemUploadFile = menu.getItem(0);
		menuItemUploadFile.setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuitem_upload_file) {
			if (menuItemUploadFile.isVisible()) {
				new FileUploadPermissionChecker(getSherlockActivity(),
						getString(R.string.check_permission)).execute();
			}
			return true;
		}
		return false;
	}

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final FileItem item = items.get(position);

			if (item.isFile()) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				String dirPath = prefs.getString(
						SettingsActivity.PREF_STORAGE_LOCATION, "/");

				final File file = new File(dirPath, item.getName());

				if (file.exists()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle(R.string.hint);
					builder.setMessage(String.format(
							getString(R.string.file_exists), item.getName()));
					builder.setPositiveButton(android.R.string.yes,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (!file.delete()) {
										Toast.makeText(getActivity(),
												R.string.can_not_delete,
												Toast.LENGTH_SHORT).show();
									}
									dialog.dismiss();
									doDownload(item);
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
					doDownload(item);
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
					getSherlockActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (fileToUploadPath != null) {
								menuItemUploadFile.setVisible(true);
							} else {
								menuItemUploadFile.setVisible(false);
							}
						}
					});

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

	private class FileUploadTask extends ProgressDialogTask<Void, Void> {

		public FileUploadTask(Context context, String text) {
			super(context, text);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				FileService.getInstance().uploadFile("/" + path,
						new File(fileToUploadPath));
				fileToUploadPath = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			getSherlockActivity().finish();
		}
	}

	private class FileUploadPermissionChecker extends
			ProgressDialogTask<Void, Boolean> {

		public FileUploadPermissionChecker(Context context, String text) {
			super(context, text);
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			try {
				return FileService.getInstance().canUploadFile("/" + path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				new FileUploadTask(getSherlockActivity(),
						getString(R.string.upload_process)).execute();
			} else {
				getSherlockActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getSherlockActivity());
						builder.setTitle(R.string.error);
						builder.setMessage(R.string.no_rights);
						builder.setNeutralButton(android.R.string.ok,
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

	}

	@Override
	public void doRename(final FileItem item) {
		final EditText input = new EditText(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.rename);
		builder.setMessage(R.string.new_filename);
		builder.setView(input);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = input.getText().toString();
						if (newName.length() == 0) {
							Toast.makeText(getActivity(),
									R.string.invalid_filename,
									Toast.LENGTH_SHORT).show();
						} else {
							new FileRenameTask(getActivity(),
									getString(R.string.rename_process), newName)
									.execute(item);
							getActivity().onBackPressed();
						}
					}
				});
		builder.create().show();
	}

	@Override
	public void doDelete(final FileItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.hint);
		builder.setMessage(String.format(
				getString(R.string.delete_confirmation), item.getName()));
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new FileDeleteTask(getActivity(),
								getString(R.string.delete_process))
								.execute(item);
						getActivity().onBackPressed();
					}
				});
		builder.create().show();
	}

	private void doDownload(FileItem item) {
		new FileDownloadTask(getActivity(), item.getName()).execute(item
				.getPath());
	}

}
