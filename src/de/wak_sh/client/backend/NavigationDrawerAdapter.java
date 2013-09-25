package de.wak_sh.client.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;

public class NavigationDrawerAdapter extends ArrayAdapter<String> {
	private final List<NavigationDrawerItem> mItems;
	private final LayoutInflater mInflater;

	public NavigationDrawerAdapter(Context context,
			List<NavigationDrawerItem> items) {
		super(context, 0);
		this.mItems = items;
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

		int textId = mItems.get(position).getTitleId();
		int imageId = mItems.get(position).getIconId();

		view.setText(textId);
		view.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);

		return view;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}
}
