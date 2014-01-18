package de.wak_sh.client.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.DrawerItem;

public class AdapterDrawerItems extends ArrayAdapter<DrawerItem> {

	public AdapterDrawerItems(Context context, List<DrawerItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_drawer, parent,
					false);
		}

		ImageView imageDrawer = (ImageView) convertView
				.findViewById(R.id.image_drawer);
		TextView textDrawer = (TextView) convertView
				.findViewById(R.id.text_drawer);

		DrawerItem item = getItem(position);

		imageDrawer.setImageResource(item.getImageRessource());
		textDrawer.setText(item.getName());

		return convertView;
	}

}
