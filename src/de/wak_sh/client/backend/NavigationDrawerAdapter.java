package de.wak_sh.client.backend;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;

public class NavigationDrawerAdapter extends ArrayAdapter<String> {
	private SparseIntArray mTitles;
	private LayoutInflater mInflater;

	public NavigationDrawerAdapter(Context context, SparseIntArray titles) {
		super(context, 0);
		this.mTitles = titles;
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

		int textId = mTitles.keyAt(position);
		int imageId = mTitles.get(textId);

		view.setText(textId);
		view.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);

		return view;
	}

	@Override
	public int getCount() {
		return mTitles.size();
	}
}
