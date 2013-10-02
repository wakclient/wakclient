package de.wak_sh.client.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.adapters.ModuleArrayAdapter;
import de.wak_sh.client.backend.model.Module;

public class SemesterFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_semester, container,
				false);

		@SuppressWarnings("unchecked")
		ArrayList<Module> grades = (ArrayList<Module>) getArguments()
				.getSerializable("grades");

		ListView list = (ListView) rootView.findViewById(android.R.id.list);
		list.setAdapter(new ModuleArrayAdapter(getActivity(), grades));

		return rootView;
	}
}
