package de.wak_sh.client.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.backend.model.UserInformation;

public class BenutzerinfoFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_benutzerinfo,
				container, false);

		UserInformation userInformation = DataService.getInstance()
				.getUserInformation();

		TextView benutzername = (TextView) rootView
				.findViewById(R.id.text_benutzername);
		TextView studiengang = (TextView) rootView
				.findViewById(R.id.text_studiengang);
		TextView studiengruppe = (TextView) rootView
				.findViewById(R.id.text_studiengruppe);
		TextView matrikelnummer = (TextView) rootView
				.findViewById(R.id.text_matrikelnummer);

		benutzername.setText(userInformation.getBenutzername());
		studiengang.setText(userInformation.getStudiengang());
		studiengruppe.setText(userInformation.getStudiengruppe());
		matrikelnummer.setText(userInformation.getMatrikelnummer());

		return rootView;
	}

}
