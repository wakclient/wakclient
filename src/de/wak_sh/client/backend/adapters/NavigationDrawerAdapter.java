package de.wak_sh.client.backend.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {
	private final LayoutInflater mInflater;

	public NavigationDrawerAdapter(Context context,
			List<NavigationDrawerItem> items) {
		super(context, 0, items);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;

		if (convertView == null) {
			view = (TextView) mInflater.inflate(R.layout.drawer_list_item,
					parent, false);
		} else {
			view = (TextView) convertView;
		}

		int textId = getItem(position).getTitleId();
		int imageId = getItem(position).getIconId();

		view.setText(textId);
		view.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);

		return view;
	}

}
