package de.wak_sh.client.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.Recipient;

public class AdapterRecipients extends ArrayAdapter<Recipient> {

	private static class ViewHolder {
		public TextView textName;
		public TextView textLocation;
		public ImageView image;
	}

	public AdapterRecipients(Context context, List<Recipient> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_recipient,
					parent, false);

			ViewHolder holder = new ViewHolder();
			holder.textName = (TextView) convertView
					.findViewById(R.id.textView_name);
			holder.textLocation = (TextView) convertView
					.findViewById(R.id.textView_location);
			holder.image = (ImageView) convertView.findViewById(R.id.image);

			convertView.setTag(holder);
		}

		final Recipient recipient = getItem(position);
		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.textName.setText(recipient.getName());
		if (recipient.getLocation() != null) {
			holder.textLocation.setText(recipient.getLocation());
			holder.textLocation.setVisibility(View.VISIBLE);
		} else {
			holder.textLocation.setVisibility(View.GONE);
		}

		holder.image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				remove(recipient);
			}
		});

		return convertView;
	}
}
