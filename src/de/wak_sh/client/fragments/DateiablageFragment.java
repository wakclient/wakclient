package de.wak_sh.client.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.ProgressDialogTask;
import de.wak_sh.client.backend.adapters.FileItemArrayAdapter;
import de.wak_sh.client.backend.model.FileItem;
import de.wak_sh.client.backend.service.FileService;

public class DateiablageFragment extends Fragment {

	private FileItemArrayAdapter adapter;
	private List<FileItem> items = new ArrayList<FileItem>();
	private String path;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);

		adapter = new FileItemArrayAdapter(getActivity(), items);
		ListView listView = (ListView) rootView;
		listView.setOnItemClickListener(clickListener);
		listView.setAdapter(adapter);

		if (getArguments() != null) {
			path = getArguments().getString("path");
		}

		if (items.isEmpty()) {
			new FileTask(getActivity()).execute();
		}

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Bundle bundle = new Bundle();
			bundle.putString("path", items.get(position).path);

			DateiablageFragment fragment = new DateiablageFragment();
			fragment.setArguments(bundle);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();
		}
	};

	private class FileTask extends ProgressDialogTask<Void, Void> {

		private Activity activity;

		public FileTask(Activity activity) {
			super(activity, "Lade Dateisystem...");
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			FileService service = FileService.getInstance();
			List<FileItem> list = null;

			try {
				if (path == null) {
					list = service.getMountpoints();
				} else {
					list = service.getFileItems(path);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			items.clear();
			items.addAll(list);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});

			return null;
		}
	}

}
