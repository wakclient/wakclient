package de.wak_sh.client.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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

		View header = inflater.inflate(R.layout.noten_list_item, null);

		TextView moduleName = (TextView) header.findViewById(R.id.txt_modul);
		TextView credits = (TextView) header.findViewById(R.id.txt_credits);
		TextView grade1 = (TextView) header.findViewById(R.id.txt_note1);
		TextView grade2 = (TextView) header.findViewById(R.id.txt_note2);
		TextView grade3 = (TextView) header.findViewById(R.id.txt_note3);

		moduleName.setText(R.string.module);
		credits.setText(R.string.credits);
		grade1.setText(R.string.grade1);
		grade2.setText(R.string.grade2);
		grade3.setText(R.string.grade3);

		list.addHeaderView(header);
		list.setAdapter(new ModuleArrayAdapter(getActivity(), grades));

		return rootView;
	}
}
