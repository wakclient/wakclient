package de.wak_sh.client;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import de.wak_sh.client.backend.AdapterRecipients;
import de.wak_sh.client.model.Recipient;

public class RecipientsActivity extends SherlockActivity {

	public static final int REQUEST_CODE = 1;

	private List<Recipient> mRecipients = new ArrayList<Recipient>();
	private AdapterRecipients mAdapter;

	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipients);

		ListView listView = (ListView) findViewById(android.R.id.list);
		mAdapter = new AdapterRecipients(this, mRecipients);
		listView.setAdapter(mAdapter);
	}

	@Override
	public void onBackPressed() {

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.recipients, menu);
		MenuItem item = menu.findItem(R.id.action_search);
		searchView = (SearchView) item.getActionView();
		return true;
	}

	private class SearchTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}

	}

}
