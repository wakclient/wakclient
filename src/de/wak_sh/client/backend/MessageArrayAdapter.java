package de.wak_sh.client.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.model.Message;

public class MessageArrayAdapter extends ArrayAdapter<Message> {
	private LayoutInflater mInflater;

	public MessageArrayAdapter(Context context, List<Message> objects) {
		super(context, R.layout.messages_list_item, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = mInflater
					.inflate(R.layout.messages_list_item, parent, false);
		} else {
			view = convertView;
		}

		Message message = getItem(position);
		((TextView) view.findViewById(R.id.msg_date))
				.setText(message.getDate());
		((TextView) view.findViewById(R.id.msg_from)).setText(message
				.getSender());
		((TextView) view.findViewById(R.id.msg_subject)).setText(message
				.getSubject());

		return view;
	}
}
