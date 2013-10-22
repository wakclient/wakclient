package de.wak_sh.client.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.adapters.MessageArrayAdapter;
import de.wak_sh.client.backend.model.Message;
import de.wak_sh.client.backend.service.MessageService;

public class NachrichtenFragment extends SherlockFragment {
	protected List<Message> messages = new ArrayList<Message>();
	protected ListView listView;

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Bundle bundle = new Bundle();
			bundle.putInt("msgid", (int) id);

			NachrichtLesenFragment fragment = new NachrichtLesenFragment();
			fragment.setArguments(bundle);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();
		}
	};
	private MessageArrayAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		listView = (ListView) rootView;
		listView.setOnItemClickListener(clickListener);
		adapter = new MessageArrayAdapter(getActivity(), messages);
		listView.setAdapter(adapter);
		setHasOptionsMenu(true);
		return rootView;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (messages.isEmpty()) {
			new MessageTask(activity).execute();
		}
	}

	private class MessageTask extends ProgressDialogTask<Void, Void> {
		private Activity activity;

		public MessageTask(Activity activity) {
			super(activity, activity.getString(R.string.fetching_messages));
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			MessageService service = MessageService.getInstance();
			try {
				service.fetchMessages();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			messages.clear();
			messages.addAll(service.getMessages());
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});

			return null;

		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.nachrichten_menu, menu);

	}
}
