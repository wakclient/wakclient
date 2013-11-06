package de.wak_sh.client.backend.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.model.FileItem;

public class FileItemArrayAdapter extends ArrayAdapter<FileItem> {

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
			button.setBackgroundResource(android.R.drawable.ic_menu_more);
		} else {
			button.setBackgroundResource(0);
		}

		return convertView;
	}
}
