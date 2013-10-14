package de.wak_sh.client.fragments;

import java.util.ArrayList;
import java.util.Locale;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.adapters.ModuleArrayAdapter;
import de.wak_sh.client.backend.model.Module;

public class SemesterFragment extends ListFragment {
	private ArrayList<Module> grades;
	private float average;

	public static Fragment newInstance(ArrayList<Module> grades, float average) {
		SemesterFragment f = new SemesterFragment();

		Bundle args = new Bundle();
		args.putSerializable("grades", grades);
		args.putFloat("average", average);
		f.setArguments(args);

		return f;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		grades = (ArrayList<Module>) getArguments().getSerializable("grades");
		average = getArguments().getFloat("average");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_semester, container,
				false);

		TextView textAverage = (TextView) rootView
				.findViewById(R.id.txt_durchschnitt);
		textAverage
				.setText(String.format(Locale.getDefault(), "%.2f", average));

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

		moduleName.setTypeface(moduleName.getTypeface(), Typeface.BOLD);
		credits.setTypeface(credits.getTypeface(), Typeface.BOLD);
		grade1.setTypeface(grade1.getTypeface(), Typeface.BOLD);
		grade2.setTypeface(grade2.getTypeface(), Typeface.BOLD);
		grade3.setTypeface(grade3.getTypeface(), Typeface.BOLD);

		list.addHeaderView(header);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ModuleArrayAdapter(getActivity(), grades));
	}

}
