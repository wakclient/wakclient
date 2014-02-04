package de.wak_sh.client;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import de.wak_sh.client.backend.AdapterRecipients;
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.backend.SelectionDialogRecipient;
import de.wak_sh.client.backend.SelectionDialogRecipient.OnRecipientSelectedListener;
import de.wak_sh.client.model.Recipient;
import de.wak_sh.client.service.JsoupMessageService;

public class RecipientsActivity extends SherlockFragmentActivity implements
		OnQueryTextListener, OnRecipientSelectedListener {

	private ListView listView;
	private List<Recipient> mRecipients = new ArrayList<Recipient>();

	private SearchView searchView;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipients);

		mRecipients = (List<Recipient>) getIntent().getExtras()
				.getSerializable("list");

		listView = (ListView) findViewById(android.R.id.list);
		updateList();
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("list", (Serializable) mRecipients);
		setResult(RESULT_OK, data);
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.recipients, menu);
		MenuItem item = menu.findItem(R.id.action_search);
		searchView = (SearchView) item.getActionView();
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		new SearchTask(this, null, getString(R.string.search_recipients))
				.execute(query);
		searchView.clearFocus();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public void onRecipientSelected(DialogFragment dialog, Recipient recipient) {
		if (!mRecipients.contains(recipient)) {
			mRecipients.add(recipient);
			updateList();
		}

		searchView.setQuery("", false);
		searchView.requestFocus();
		dialog.dismiss();
	}

	private void updateList() {
		listView.setAdapter(new AdapterRecipients(this, mRecipients));
	}

	private class SearchTask extends
			ProgressTask<String, Void, List<Recipient>> {

		public SearchTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected List<Recipient> doInBackground(String... params) {
			try {
				return JsoupMessageService.getInstance().getRecipients(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Recipient> result) {
			super.onPostExecute(result);
			if (result != null) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("list", (Serializable) result);

				SelectionDialogRecipient dialog = new SelectionDialogRecipient();
				dialog.setArguments(bundle);
				dialog.show(getSupportFragmentManager(), null);
			}
		}
	}
}
