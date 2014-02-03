package de.wak_sh.client.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

		if (mStorage.getUserInformation() == null) {
			new UserInformationTask(getActivity(), null,
					"Hole Benutzerinformationen...").execute();
		} else {
			updateViews();
		}

		return rootView;
	}

	private void updateViews() {
		UserInformation userInformation = mStorage.getUserInformation();
		mTextUsername.setText(userInformation.getName());
		mTextStudentGroup.setText(userInformation.getStudentGroup());
		mTextStudentNumber.setText(userInformation.getStudentNumber());

		String study = "";

		if (userInformation.getStudentGroup().contains("WINF")) {
			study = "Wirtschaftsinformatik";
		} else if (userInformation.getStudentGroup().contains("WING")) {
			study = "Wirtschaftsingenieurwesen";
		} else {
			study = "Betriebswirtschaft";
		}

		mTextStudy.setText(study);
	}

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
