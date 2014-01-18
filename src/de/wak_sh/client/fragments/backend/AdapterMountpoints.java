package de.wak_sh.client.fragments.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.Mountpoint;

public class AdapterMountpoints extends ArrayAdapter<Mountpoint> {

	private static class ViewHolder {
		public TextView textName;
	}

	public AdapterMountpoints(Context context, List<Mountpoint> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_mountpoint,
					parent, false);

			ViewHolder holder = new ViewHolder();
			holder.textName = (TextView) convertView
					.findViewById(R.id.textView_name);

			convertView.setTag(holder);
		}

		Mountpoint mountpoint = getItem(position);

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.textName.setText(mountpoint.getName());

		return convertView;
	}

}
