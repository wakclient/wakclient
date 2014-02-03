package de.wak_sh.client.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.model.Email;
import de.wak_sh.client.model.Recipient;

public class FragmentWriteEmail extends WakFragment {

	private static final int REQUEST_CODE_RECIPIENTS = 1;

	private Email mEmail;
	private List<Recipient> mRecipients = new ArrayList<Recipient>();

	private EditText mEditRecipients;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_write_email,
				container, false);

		mEditRecipients = (EditText) rootView
				.findViewById(R.id.editText_recipients);
		EditText editSubject = (EditText) rootView
				.findViewById(R.id.editText_subject);
		EditText editMessage = (EditText) rootView
				.findViewById(R.id.editText_message);

		mEditRecipients.setOnTouchListener(touchListener);

		setHasOptionsMenu(true);

		return rootView;
	}

	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Intent intent = new Intent(getActivity(),
						RecipientsActivity.class);
				intent.putExtra("list", (Serializable) mRecipients);
				startActivityForResult(intent, REQUEST_CODE_RECIPIENTS);
				return true;
			}
			return false;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_RECIPIENTS
				&& resultCode == Activity.RESULT_OK) {
			mRecipients = (List<Recipient>) data.getSerializableExtra("list");

			StringBuilder builder = new StringBuilder();
			for (Recipient recipient : mRecipients) {
				builder.append(recipient.getName()).append("; ");
			}

			builder.replace(builder.length() - 2, builder.length(), "");

			mEditRecipients.setText(builder.toString());
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
			new EmailTask(getActivity(), null,
					getString(R.string.email_send_process)).execute();
			break;
		case R.id.action_add_attachment:

			break;
		}
		return false;
	}

	private class EmailTask extends ProgressTask<Void, Void, Void> {

		public EmailTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO: Implement
			return null;
		}

	}

}
