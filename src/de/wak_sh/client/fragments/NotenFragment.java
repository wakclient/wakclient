package de.wak_sh.client.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.service.DataService;
import de.wak_sh.client.backend.service.ModuleService;

public class NotenFragment extends Fragment {
	private ModuleService moduleService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notenuebersicht, container,
				false);

		if (moduleService == null) {
			new GradesTask(getActivity()).execute();
		}

		return rootView;
	}

	private class GradesTask extends ProgressDialogTask<Void, Void> {
		public GradesTask(Context context) {
			super(context, context.getString(R.string.fetching_grades));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DataService dataService = DataService.getInstance();
			moduleService = new ModuleService(dataService);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				moduleService.fetchModules();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
