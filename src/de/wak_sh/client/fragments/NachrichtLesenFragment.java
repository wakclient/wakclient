package de.wak_sh.client.fragments;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.model.Message;
import de.wak_sh.client.backend.service.MessageService;

public class NachrichtLesenFragment extends SherlockFragment {
	private TextView date;
	private TextView from;
	private TextView subject;
	private TextView text;
	private TextView attachment;

	private OnClickListener onClickAttachment = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nachrichten_lesen,
				container, false);

		date = (TextView) rootView.findViewById(R.id.msg_date);
		from = (TextView) rootView.findViewById(R.id.msg_from);
		subject = (TextView) rootView.findViewById(R.id.msg_subject);
		text = (TextView) rootView.findViewById(R.id.msg_text);
		attachment = (TextView) rootView.findViewById(R.id.msg_attachment);

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		new MessageTask(activity).execute(getArguments().getInt("msgid"));
	}

	protected void setMessage(Message message) {
		date.setText(message.getDate());
		from.setText(message.getSender());
		subject.setText(message.getSubject());
		text.setText(message.getContent());

		if (message.getAttachmentId() > 0) {
			attachment.setOnClickListener(onClickAttachment);
			attachment.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.nachrichtenlesen_menu, menu);
	}

	private class MessageTask extends ProgressDialogTask<Integer, Message> {
		private Activity activity;

		public MessageTask(Activity activity) {
			super(activity, activity.getString(R.string.fetching_message));
			this.activity = activity;
		}

		@Override
		protected Message doInBackground(Integer... params) {
			MessageService service = MessageService.getInstance();
			int msgid = params[0];
			try {
				service.fetchMessagesContent(msgid);
				return service.getMessage(msgid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Message result) {
			super.onPostExecute(result);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setMessage(result);
				}
			});
		}
	}
}
