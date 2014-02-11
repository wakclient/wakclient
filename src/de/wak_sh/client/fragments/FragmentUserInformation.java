package de.wak_sh.client.fragments;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.DataStorage;
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.model.UserInformation;
import de.wak_sh.client.service.JsoupUserInformationService;

public class FragmentUserInformation extends WakFragment {

	private DataStorage mStorage = DataStorage.getInstance();

	private TextView mTextUsername;
	private TextView mTextStudentNumber;
	private TextView mTextStudentGroup;
	private TextView mTextStudy;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_information,
				container, false);

		mTextUsername = (TextView) rootView
				.findViewById(R.id.textView_username);
		mTextStudentNumber = (TextView) rootView
				.findViewById(R.id.textView_student_number);
		mTextStudentGroup = (TextView) rootView
				.findViewById(R.id.textView_student_group);
		mTextStudy = (TextView) rootView.findViewById(R.id.textView_study);

		mTextStudentNumber.setOnClickListener(onClickStudentNumber);

		if (mStorage.getUserInformation() == null) {
			new UserInformationTask(getActivity(), null,
					getString(R.string.fetching_user_info)).execute();
		} else {
			updateViews();
		}

		return rootView;
	}

	private void updateViews() {
		UserInformation userInformation = mStorage.getUserInformation();

		String studentGroup = userInformation.getStudentGroup() == null ? "Keine Angabe"
				: userInformation.getStudentGroup();
		String studentNumber = userInformation.getStudentNumber() == null ? "Keine Angabe"
				: userInformation.getStudentNumber();

		mTextUsername.setText(userInformation.getName());
		mTextStudentGroup.setText(studentGroup);
		mTextStudentNumber.setText(studentNumber);

		String study = "Keine Angabe";

		if (!studentGroup.equals("Keine Angabe")) {
			if (studentGroup.contains("WINF")) {
				study = "Wirtschaftsinformatik";
			} else if (studentGroup.contains("WING")) {
				study = "Wirtschaftsingenieurwesen";
			} else {
				study = "Betriebswirtschaft";
			}
		}

		mTextStudy.setText(study);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private OnClickListener onClickStudentNumber = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ClipboardManager clipboard = (ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Matrikelnummer",
						mTextStudentNumber.getText());
				clipboard.setPrimaryClip(clip);
			} else {
				android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(mTextStudentNumber.getText());
			}
			Toast.makeText(getActivity(), R.string.student_number_copied,
					Toast.LENGTH_SHORT).show();
		}
	};

	private class UserInformationTask extends
			ProgressTask<Void, Void, UserInformation> {
		public UserInformationTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected UserInformation doInBackground(Void... params) {
			try {
				return JsoupUserInformationService.getInstance()
						.getUserInformation();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(UserInformation result) {
			super.onPostExecute(result);

			if (result != null) {
				mStorage.setUserInformation(result);
				updateViews();
			}
		}
	}
}
