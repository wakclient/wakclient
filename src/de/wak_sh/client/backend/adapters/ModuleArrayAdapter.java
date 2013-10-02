package de.wak_sh.client.backend.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.model.Module;

public class ModuleArrayAdapter extends ArrayAdapter<Module> {
	private LayoutInflater mInflater;

	public ModuleArrayAdapter(Context context, List<Module> objects) {
		super(context, R.layout.noten_list_item, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.noten_list_item, parent, false);
		} else {
			view = convertView;
		}

		Module module = getItem(position);
		TextView moduleName = (TextView) view.findViewById(R.id.txt_modul);
		TextView credits = (TextView) view.findViewById(R.id.txt_credits);
		TextView[] gradeViews = new TextView[3];
		gradeViews[0] = (TextView) view.findViewById(R.id.txt_note1);
		gradeViews[1] = (TextView) view.findViewById(R.id.txt_note2);
		gradeViews[2] = (TextView) view.findViewById(R.id.txt_note3);

		moduleName.setText(module.getName());
		credits.setText(Integer.toString(module.getCredits()));

		float[] grades = module.getGrades();
		for (int i = 0; i < grades.length; i++) {
			if (grades[i] == 0f) {
				gradeViews[i].setText("-");
			} else {
				gradeViews[i].setText(String.format("%.1f", grades[i]));
			}
		}

		return view;
	}
}
