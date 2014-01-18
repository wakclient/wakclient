package de.wak_sh.client.fragments.backend;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.Email;

public class AdapterEmails extends ArrayAdapter<Email> {

	private static class ViewHolder {
		public ImageView imageAttachment;
		public TextView textFrom;
		public TextView textDate;
		public TextView textSubject;
	}

	public AdapterEmails(Context context, List<Email> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_email, parent,
					false);

			ViewHolder holder = new ViewHolder();
			holder.imageAttachment = (ImageView) convertView
					.findViewById(R.id.imageView_attachment);
			holder.textFrom = (TextView) convertView
					.findViewById(R.id.textView_from);
			holder.textDate = (TextView) convertView
					.findViewById(R.id.textView_date);
			holder.textSubject = (TextView) convertView
					.findViewById(R.id.textView_subject);

			convertView.setTag(holder);
		}

		Email email = getItem(position);

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.textFrom.setText(Html.fromHtml(email.getFrom()));
		holder.textDate.setText(email.getDate());
		holder.textSubject.setText(Html.fromHtml(email.getSubject()));

		if (email.hasAttachment()) {
			holder.imageAttachment.setVisibility(View.VISIBLE);
		} else {
			holder.imageAttachment.setVisibility(View.GONE);
		}

		return convertView;
	}

}
