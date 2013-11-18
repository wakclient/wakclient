package de.wak_sh.client.backend.adapters;

import java.util.List;

import de.wak_sh.client.R;
import de.wak_sh.client.backend.model.FileItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FileItemArrayAdapter extends ArrayAdapter<FileItem> implements
		OnClickListener {

	private FragmentInterface fragmentInterface;

	public interface FragmentInterface {
		public void doRename(FileItem item);

		public void doDelete(FileItem item);
	}

	public FileItemArrayAdapter(Context context, List<FileItem> objects,
			FragmentInterface fragmentInterface) {
		super(context, 0, objects);
		this.fragmentInterface = fragmentInterface;
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

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setItems(operations, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					fragmentInterface.doDelete(item);
					break;
				case 1:
					fragmentInterface.doRename(item);
					break;
				}
			}
		});
		builder.create().show();
	}

}