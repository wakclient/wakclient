package de.wak_sh.client.fragments;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
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

	private OnClickListener onClickMatrikelnummer = new OnClickListener() {
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				ClipboardManager clipboard = (ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Matrikelnummer",
						matrikelnummer.getText());
				clipboard.setPrimaryClip(clip);
			} else {
				android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(matrikelnummer.getText());
			}
			Toast.makeText(getActivity(), R.string.matrikelnummer_kopiert,
					Toast.LENGTH_SHORT).show();
		}
	};

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
		matrikelnummer.setOnClickListener(onClickMatrikelnummer);

		new UserInfoTask(getActivity()).execute();

		return rootView;
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
