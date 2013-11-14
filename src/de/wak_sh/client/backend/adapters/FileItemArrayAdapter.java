package de.wak_sh.client.backend.adapters;

import java.io.IOException;
import java.util.List;

import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.model.FileItem;
import de.wak_sh.client.backend.service.FileService;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FileItemArrayAdapter extends ArrayAdapter<FileItem> implements
		OnClickListener {

	public FileItemArrayAdapter(Context context, List<FileItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.file_list_item, parent,
					false);
		}

		FileItem item = getItem(position);

		TextView text = (TextView) convertView.findViewById(R.id.file_name);
		TextView date = (TextView) convertView.findViewById(R.id.file_date);
		Button button = (Button) convertView.findViewById(R.id.file_actions);

		text.setSelected(true);
		text.setText(item.getName());
		date.setText(item.getDate().trim());

		if (item.isOwner()) {
			button.setTag(position);
			button.setBackgroundResource(android.R.drawable.ic_menu_more);
			button.setOnClickListener(this);
		} else {
			button.setBackgroundResource(0);
		}

		return convertView;
	}

	@Override
	public void onClick(View view) {
		int index = (Integer) view.getTag();
		String[] operations = { "Löschen", "Umbenennen" };

		final FileItem item = getItem(index);
		System.out.println(item.getPath());

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setItems(operations, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog.Builder builder = null;
				switch (which) {
				case 0:
					builder = new AlertDialog.Builder(getContext());
					builder.setTitle("Hinweis");
					builder.setMessage("Sind Sie sicher das Sie die folgende Datei löschen möchten?\n\n"
							+ item.getName());
					builder.setNegativeButton("Abbrechen",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.setPositiveButton("Ja",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									new DeleteTask(getContext(),
											"Datei wird gelöscht...")
											.execute(item);
								}
							});
					break;
				case 1:
					final EditText input = new EditText(getContext());

					builder = new AlertDialog.Builder(getContext());
					builder.setTitle("Umbenennen");
					builder.setMessage("Neuer Dateiname:");
					builder.setView(input);
					builder.setNegativeButton("Abbrechen",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String newName = input.getText().toString();
									if (newName.length() == 0) {
										Toast.makeText(getContext(),
												"Ungültiger Dateiname",
												Toast.LENGTH_SHORT).show();
									} else {
										new RenameTask(getContext(),
												"Datei wird umbenannt...",
												newName).execute(item);
									}
								}
							});
					break;
				}
				builder.create().show();
			}
		});
		builder.create().show();
	}

	private class RenameTask extends ProgressDialogTask<FileItem, Void> {
		private String newName;

		public RenameTask(Context context, String text, String newName) {
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

	private class DeleteTask extends ProgressDialogTask<FileItem, Void> {

		public DeleteTask(Context context, String text) {
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
}