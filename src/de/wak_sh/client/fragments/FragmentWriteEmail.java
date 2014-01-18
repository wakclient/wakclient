package de.wak_sh.client.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.R;
import de.wak_sh.client.RecipientsActivity;
import de.wak_sh.client.model.Email;
import de.wak_sh.client.model.Recipient;
import de.wak_sh.client.service.JsoupEmailService;

public class FragmentWriteEmail extends WakFragment {

	private Email mEmail;
	private List<Recipient> mRecipients;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_write_email,
				container, false);

		EditText editRecipients = (EditText) rootView
				.findViewById(R.id.editText_recipients);
		EditText editSubject = (EditText) rootView
				.findViewById(R.id.editText_subject);
		EditText editMessage = (EditText) rootView
				.findViewById(R.id.editText_message);

		editRecipients.setOnTouchListener(touchListener);

		setHasOptionsMenu(true);

		return rootView;
	}

	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Intent intent = new Intent(getActivity(),
						RecipientsActivity.class);
				startActivityForResult(intent, RecipientsActivity.REQUEST_CODE);
				return true;
			}
			return false;
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RecipientsActivity.REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.email_write, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_send:
			new EmailTask().execute();
			break;
		case R.id.action_add_attachment:

			break;
		}
		return false;
	}

	private class EmailTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			JsoupEmailService.getInstance().sendEmail(mEmail, mRecipients);
			return null;
		}

	}

}
