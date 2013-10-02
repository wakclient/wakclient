package de.wak_sh.client.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.adapters.ModulePagerAdapter;
import de.wak_sh.client.backend.service.DataService;
import de.wak_sh.client.backend.service.ModuleService;

public class NotenuebersichtFragment extends Fragment {
	private ModuleService moduleService;
	private ModulePagerAdapter adapter;
	private ViewPager pager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notenuebersicht,
				container, false);

		if (moduleService == null) {
			new GradesTask(getActivity()).execute();
		}

		adapter = new ModulePagerAdapter(getFragmentManager());
		pager = (ViewPager) rootView.findViewById(R.id.module_pager);
		pager.setAdapter(adapter);

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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			int semesters = moduleService.countSemesters();
			adapter.setModuleService(moduleService);
			adapter.notifyDataSetChanged();
			pager.setCurrentItem(semesters - 1);
		}
	}
}
