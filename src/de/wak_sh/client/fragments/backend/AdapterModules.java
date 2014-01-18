package de.wak_sh.client.fragments.backend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.Module;

public class AdapterModules extends BaseExpandableListAdapter {

	private List<Group> mGroups;
	private LayoutInflater mInflater;

	private static class ViewHolder {
		public TextView textModule;
		public TextView textCredits;
		public TextView textGrade1;
		public TextView textGrade2;
		public TextView textGrade3;
	}

	private class Group {
		public int title;
		public List<Module> modules;

		public Group(int title, List<Module> modules) {
			this.title = title;
			this.modules = modules;
		}
	}

	public AdapterModules(Context context, List<Module> modules) {
		mGroups = new ArrayList<AdapterModules.Group>();
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		List<Module> list1 = new ArrayList<Module>();
		List<Module> list2 = new ArrayList<Module>();
		List<Module> list3 = new ArrayList<Module>();
		List<Module> list4 = new ArrayList<Module>();
		List<Module> list5 = new ArrayList<Module>();
		List<Module> list6 = new ArrayList<Module>();

		for (Module module : modules) {
			switch (module.getSemester()) {
			case 1:
				list1.add(module);
				break;
			case 2:
				list2.add(module);
				break;
			case 3:
				list3.add(module);
				break;
			case 4:
				list4.add(module);
				break;
			case 5:
				list5.add(module);
				break;
			case 6:
				list6.add(module);
				break;
			}
		}

		if (!list1.isEmpty()) {
			mGroups.add(new Group(R.string.semester1, list1));
		}

		if (!list2.isEmpty()) {
			mGroups.add(new Group(R.string.semester2, list2));
		}

		if (!list3.isEmpty()) {
			mGroups.add(new Group(R.string.semester3, list3));
		}

		if (!list4.isEmpty()) {
			mGroups.add(new Group(R.string.semester4, list4));
		}

		if (!list5.isEmpty()) {
			mGroups.add(new Group(R.string.semester5, list5));
		}

		if (!list6.isEmpty()) {
			mGroups.add(new Group(R.string.semester6, list6));
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).modules.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_module, parent,
					false);
			ViewHolder holder = new ViewHolder();

			holder.textModule = (TextView) convertView
					.findViewById(R.id.textView_module);
			holder.textCredits = (TextView) convertView
					.findViewById(R.id.textView_credits);
			holder.textGrade1 = (TextView) convertView
					.findViewById(R.id.textView_grade1);
			holder.textGrade2 = (TextView) convertView
					.findViewById(R.id.textView_grade2);
			holder.textGrade3 = (TextView) convertView
					.findViewById(R.id.textView_grade3);

			convertView.setTag(holder);
		}

		Module module = mGroups.get(groupPosition).modules.get(childPosition);
		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.textModule.setText(module.getName());
		holder.textCredits.setText("" + module.getCredits());
		holder.textGrade1.setText(""
				+ (module.getGrades()[0] == 0 ? "-" : module.getGrades()[0]));
		holder.textGrade2.setText(""
				+ (module.getGrades()[1] == 0 ? "-" : module.getGrades()[1]));
		holder.textGrade3.setText(""
				+ (module.getGrades()[2] == 0 ? "-" : module.getGrades()[2]));

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroups.get(groupPosition).modules.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_group, parent,
					false);
		}

		Group group = mGroups.get(groupPosition);
		TextView textGroup = (TextView) convertView
				.findViewById(R.id.text_group);
		textGroup.setText(group.title);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
