package de.wak_sh.client.fragments.backend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.model.FileLink;

public class AdapterFileLinks extends ArrayAdapter<FileLink> {

	private static class ViewHolder {
		public ImageView imageIcon;
		public TextView textName;
		public TextView textDate;
	}

	public AdapterFileLinks(Context context, List<FileLink> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_file_link,
					parent, false);

			ViewHolder holder = new ViewHolder();
			holder.imageIcon = (ImageView) convertView
					.findViewById(R.id.imageView_icon);
			holder.textName = (TextView) convertView
					.findViewById(R.id.textView_name);
			holder.textDate = (TextView) convertView
					.findViewById(R.id.textView_date);

			convertView.setTag(holder);
		}

		FileLink fileLink = getItem(position);

		ViewHolder holder = (ViewHolder) convertView.getTag();

		holder.textName.setText(fileLink.getName());
		holder.textDate.setText(fileLink.getDate());

		if (!fileLink.isFile()) {
			holder.imageIcon.setImageResource(R.drawable.ic_folder);
		} else {
			int resId = R.drawable.ic_file;

			if (fileLink.getName().contains(".pdf")) {
				resId = R.drawable.ic_pdf;
			} else if (fileLink.getName().contains(".xls")
					|| fileLink.getName().contains(".xlsx")) {
				resId = R.drawable.ic_excel;
			} else if (fileLink.getName().contains(".doc")
					|| fileLink.getName().contains(".docx")) {
				resId = R.drawable.ic_word;
			} else if (fileLink.getName().contains(".ppt")
					|| fileLink.getName().contains(".pptx")) {
				resId = R.drawable.ic_ppt;
			} else if (fileLink.getName().contains(".txt")) {
				resId = R.drawable.ic_txt;
			} else if (fileLink.getName().contains(".rar")
					|| fileLink.getName().contains(".zip")) {
				resId = R.drawable.ic_rar;
			}

			holder.imageIcon.setImageResource(resId);
		}

		return convertView;
	}
}
