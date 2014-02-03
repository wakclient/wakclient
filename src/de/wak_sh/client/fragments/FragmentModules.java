package de.wak_sh.client.fragments;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.DataStorage;
import de.wak_sh.client.backend.ProgressTask;
import de.wak_sh.client.fragments.backend.AdapterModules;
import de.wak_sh.client.model.Module;
import de.wak_sh.client.service.JsoupModuleService;

public class FragmentModules extends WakFragment {

	private DataStorage mStorage = DataStorage.getInstance();

	private ExpandableListView mListView;
	private TextView mTextAverage;
	private TextView mTextCredits;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_modules, container,
				false);

		mListView = (ExpandableListView) rootView
				.findViewById(android.R.id.list);
		mTextAverage = (TextView) rootView.findViewById(R.id.textView_average);
		mTextCredits = (TextView) rootView.findViewById(R.id.textView_credits);

		if (mStorage.getModules().isEmpty()) {
			new ModulesTask(getActivity(), null, "Hole Noten...").execute();
		} else {
			updateViews();
		}

		return rootView;
	}

	private void updateViews() {
		mListView.setAdapter(new AdapterModules(getActivity(), mStorage
				.getModules()));

		int credits = 0;
		float average = 0f;
		for (Module module : mStorage.getModules()) {
			credits += module.getCredits();
			average += module.getCredits() * module.getRelevantGrade();
		}

		mTextCredits.setText("" + credits);
		mTextAverage.setText("" + (average / credits));
	}

	private class ModulesTask extends ProgressTask<Void, Void, List<Module>> {

		public ModulesTask(Context context, String title, String message) {
			super(context, title, message);
		}

		@Override
		protected List<Module> doInBackground(Void... params) {
			try {
				return JsoupModuleService.getInstance().getModules();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Module> result) {
			super.onPostExecute(result);

			if (result != null) {
				mStorage.setModules(result);
				updateViews();
			}
		}

	}

}
