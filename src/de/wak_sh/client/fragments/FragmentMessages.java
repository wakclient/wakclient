package de.wak_sh.client.fragments;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.R;
import de.wak_sh.client.backend.DataStorage;
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.fragments.backend.AdapterMessages;
import de.wak_sh.client.model.Message;
import de.wak_sh.client.service.JsoupMessageService;

public class FragmentMessages extends WakFragment implements
		OnItemClickListener {

	private DataStorage mStorage = DataStorage.getInstance();

	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_messages, container,
				false);

		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);

		if (mStorage.getMessages().isEmpty()) {
			new MessagesTask(getActivity(), null,
					getString(R.string.fetching_messages)).execute();
		} else {
			updateViews();
		}

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.messages, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (item.getItemId() == R.id.action_write) {
		// Fragment fragment = new FragmentWriteMessage();
		//
		// Bundle bundle = new Bundle();
		// bundle.putString("title", getString(R.string.message_write));
		// bundle.putInt("iconRes", R.drawable.ic_launcher);
		//
		// fragment.setArguments(bundle);
		//
		// FragmentManager manager = getFragmentManager();
		//
		// FragmentTransaction transaction = manager.beginTransaction();
		// transaction.addToBackStack(null);
		// transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
		// transaction.commit();
		//
		// getSherlockActivity().getSupportActionBar().setTitle(
		// getString(R.string.message_write));
		// getSherlockActivity().getSupportActionBar().setIcon(
		// R.drawable.ic_launcher);
		//
		// return true;
		// } else
		if (item.getItemId() == R.id.action_refresh) {
			new MessagesTask(getActivity(), null,
					getString(R.string.fetching_messages)).execute();
			return true;
		}
		return false;
	}

	private void updateViews() {
		mListView.setAdapter(new AdapterMessages(getActivity(), mStorage
				.getMessages()));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Message message = mStorage.getMessages().get(position);

		Fragment fragment = new FragmentMessage();

		Bundle bundle = new Bundle();
		bundle.putString("title", getString(R.string.message));
		bundle.putInt("iconRes", R.drawable.ic_launcher);
		bundle.putSerializable("message", message);

		fragment.setArguments(bundle);

		FragmentManager manager = getFragmentManager();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.addToBackStack(null);
		transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
		transaction.commit();
	}

	private class MessagesTask extends ProgressTask<Void, Void, List<Message>> {

		public MessagesTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected List<Message> doInBackground(Void... params) {
			try {
				return JsoupMessageService.getInstance().getMessages();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Message> result) {
			super.onPostExecute(result);

			if (result != null) {
				mStorage.setMessages(result);
				updateViews();
			}
		}
	}

}
