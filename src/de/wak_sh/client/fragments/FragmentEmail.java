package de.wak_sh.client.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.FileDownloader;
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.model.Attachment;
import de.wak_sh.client.model.Email;
import de.wak_sh.client.service.JsoupDataService;
import de.wak_sh.client.service.JsoupEmailService;

public class FragmentEmail extends WakFragment {

	private TextView mTextFrom;
	private TextView mTextDate;
	private TextView mTextSubject;
	private TextView mTextMessage;
	private TextView mTextAttachment;
	private LinearLayout mLayoutAttachmentList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_email, container,
				false);

		Email email = (Email) getArguments().getSerializable("email");

		mTextFrom = (TextView) rootView.findViewById(R.id.textView_from);
		mTextDate = (TextView) rootView.findViewById(R.id.textView_date);
		mTextSubject = (TextView) rootView.findViewById(R.id.textView_subject);
		mTextMessage = (TextView) rootView.findViewById(R.id.textView_message);
		mTextAttachment = (TextView) rootView
				.findViewById(R.id.textView_attachment);
		mLayoutAttachmentList = (LinearLayout) rootView
				.findViewById(R.id.layout_attachment_list);

		new EmailTask(getActivity(), null, getString(R.string.fetching_message))
				.execute(email);

		setHasOptionsMenu(true);

		return rootView;
	}

	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// inflater.inflate(R.menu.emails, menu);
	// }

	private class EmailTask extends ProgressTask<Email, Void, Email> {

		public EmailTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected Email doInBackground(Email... params) {
			try {
				return JsoupEmailService.getInstance().getEmailContent(
						params[0]);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Email result) {
			super.onPostExecute(result);

			if (result != null) {
				mTextFrom.setText(Html.fromHtml(result.getFrom()));
				mTextDate.setText(result.getDate());
				mTextMessage.setText(Html.fromHtml(result.getMessage()));
				mTextSubject.setText(Html.fromHtml(result.getSubject()));

				if (!result.getAttachments().isEmpty()) {
					mTextAttachment.setVisibility(View.VISIBLE);
					String[] attachmentNames = new String[result
							.getAttachments().size()];
					for (int i = 0; i < attachmentNames.length; i++) {
						attachmentNames[i] = result.getAttachments().get(i)
								.getName();
					}

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity(), R.layout.list_item_attachment,
							R.id.textView_attachment, attachmentNames);

					for (int i = 0; i < result.getAttachments().size(); i++) {
						View item = adapter.getView(i, null, null);
						mLayoutAttachmentList.addView(item);

						final Attachment attachment = result.getAttachments()
								.get(i);
						final String url = JsoupDataService.BASE_URL
								+ "/index.php?eID=tx_cwtcommunity_pi1_download&m="
								+ result.getId() + "&a=" + attachment.getId();

						item.setFocusable(true);
						item.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								new FileDownloader(getActivity()).download(url,
										attachment.getName());
							}
						});
					}
				}
			}
		}

	}

}
