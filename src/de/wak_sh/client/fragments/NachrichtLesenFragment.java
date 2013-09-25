package de.wak_sh.client.fragments;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.model.Message;
import de.wak_sh.client.backend.service.MessageService;

public class NachrichtLesenFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nachrichten_lesen,
				container, false);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		new MessageTask(activity).execute(getArguments().getInt("msgid"));
	}

	protected void setMessage(Message message) {
		Toast.makeText(getActivity(), message.getContent(), Toast.LENGTH_LONG)
				.show();
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
