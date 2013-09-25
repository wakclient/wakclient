package de.wak_sh.client.fragments;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.model.UserInformation;
import de.wak_sh.client.backend.service.UserInformationService;

public class BenutzerinfoFragment extends Fragment {
	protected UserInformation userInformation;

	private TextView benutzername;
	private TextView studiengang;
	private TextView studiengruppe;
	private TextView matrikelnummer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_benutzerinfo,
				container, false);

		benutzername = (TextView) rootView.findViewById(R.id.text_benutzername);
		studiengang = (TextView) rootView.findViewById(R.id.text_studiengang);
		studiengruppe = (TextView) rootView
				.findViewById(R.id.text_studiengruppe);
		matrikelnummer = (TextView) rootView
				.findViewById(R.id.text_matrikelnummer);

		if (userInformation != null) {
			populateUi();
		}

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (userInformation == null) {
			new UserInfoTask(activity).execute();
		}
	}

	protected void populateUi() {
		benutzername.setText(userInformation.getBenutzername());
		studiengang.setText(userInformation.getStudiengang());
		studiengruppe.setText(userInformation.getStudiengruppe());
		matrikelnummer.setText(userInformation.getMatrikelnummer());
	}

	private class UserInfoTask extends ProgressDialogTask<Void, Void> {
		private Activity activity;

		public UserInfoTask(Activity activity) {
			super(activity, activity.getString(R.string.fetching_user_info));
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				userInformation = new UserInformationService()
						.getUserInformation();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					populateUi();
				}
			});

			return null;

		}

	}
}
