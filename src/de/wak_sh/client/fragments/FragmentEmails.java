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
import de.wak_sh.client.fragments.backend.AdapterEmails;
import de.wak_sh.client.model.Email;
import de.wak_sh.client.service.JsoupEmailService;

public class FragmentEmails extends WakFragment implements OnItemClickListener {

	private DataStorage mStorage = DataStorage.getInstance();

	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_emails, container,
				false);

		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);

		if (mStorage.getEmails().isEmpty()) {
			new EmailTask(getActivity(), null,
					getString(R.string.fetching_messages)).execute();
		} else {
			updateViews();
		}

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.emails, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_write) {
			Fragment fragment = new FragmentWriteEmail();

			Bundle bundle = new Bundle();
			bundle.putString("title", getString(R.string.email_write));
			bundle.putInt("iconRes", R.drawable.ic_launcher);

			fragment.setArguments(bundle);

			FragmentManager manager = getFragmentManager();

			FragmentTransaction transaction = manager.beginTransaction();
			transaction.addToBackStack(null);
			transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
			transaction.commit();

			getSherlockActivity().getSupportActionBar().setTitle(
					getString(R.string.email_write));
			getSherlockActivity().getSupportActionBar().setIcon(
					R.drawable.ic_launcher);

			return true;
		} else if (item.getItemId() == R.id.action_refresh) {
			new EmailTask(getActivity(), null,
					getString(R.string.fetching_messages)).execute();
			return true;
		}
		return false;
	}

	private void updateViews() {
		mListView.setAdapter(new AdapterEmails(getActivity(), mStorage
				.getEmails()));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Email email = mStorage.getEmails().get(position);

		Fragment fragment = new FragmentEmail();

		Bundle bundle = new Bundle();
		bundle.putString("title", getString(R.string.email));
		bundle.putInt("iconRes", R.drawable.ic_launcher);
		bundle.putSerializable("email", email);

		fragment.setArguments(bundle);

		FragmentManager manager = getFragmentManager();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.addToBackStack(null);
		transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
		transaction.commit();
	}

	private class EmailTask extends ProgressTask<Void, Void, List<Email>> {

		public EmailTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected List<Email> doInBackground(Void... params) {
			try {
				return JsoupEmailService.getInstance().getEmails();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Email> result) {
			super.onPostExecute(result);

			if (result != null) {
				mStorage.setEmails(result);
				updateViews();
			}
		}
	}

}
