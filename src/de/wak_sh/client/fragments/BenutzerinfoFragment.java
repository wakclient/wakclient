package de.wak_sh.client.fragments;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
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

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		new UserInfoTask(activity).execute();
	}

	private class UserInfoTask extends ProgressDialogTask<Void, Void> {
		public UserInfoTask(Context context) {
			super(context, context.getString(R.string.fetching_user_info));
		}

		UserInformation userInformation;

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

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					benutzername.setText(userInformation.getBenutzername());
					studiengang.setText(userInformation.getStudiengang());
					studiengruppe.setText(userInformation.getStudiengruppe());
					matrikelnummer.setText(userInformation.getMatrikelnummer());
				}
			});

			return null;

		}

	}
}
