package de.wak_sh.client.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.FileDownloader;
import de.wak_sh.client.fragments.backend.AdapterFileLinks;
import de.wak_sh.client.fragments.backend.AdapterMountpoints;
import de.wak_sh.client.model.FileLink;
import de.wak_sh.client.model.Mountpoint;
import de.wak_sh.client.service.JsoupFileService;

public class FragmentFileStorage extends WakFragment implements
		OnItemClickListener {

	private List<FileLink> mFileLinks = new ArrayList<FileLink>();
	private List<Mountpoint> mMountpoints = new ArrayList<Mountpoint>();
	private AdapterFileLinks mAdapterFileLinks;
	private AdapterMountpoints mAdapterMountpoints;
	private FileLink mFileLink;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_file_storage,
				container, false);

		mFileLink = (FileLink) getArguments().getSerializable("fileLink");

		mAdapterFileLinks = new AdapterFileLinks(getActivity(), mFileLinks);
		mAdapterMountpoints = new AdapterMountpoints(getActivity(),
				mMountpoints);

		ListView listView = (ListView) rootView.findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		listView.setAdapter(mFileLink == null ? mAdapterMountpoints
				: mAdapterFileLinks);

		if (mAdapterMountpoints.isEmpty() && mAdapterFileLinks.isEmpty()) {
			new FileStorageTask(listView).execute();
		}

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileLink fileLink = null;

		if (mFileLink == null) {
			Mountpoint mountpoint = mAdapterMountpoints.getItem(position);
			fileLink = new FileLink("", "", "", mountpoint, false);
		} else {
			fileLink = mAdapterFileLinks.getItem(position);
		}

		if (fileLink.isFile()) {
			new FileDownloader(getActivity()).download(fileLink);
		} else {
			Fragment fragment = new FragmentFileStorage();

			Bundle bundle = new Bundle();
			bundle.putString("title", getString(R.string.file_storage));
			bundle.putInt("iconRes", R.drawable.ic_launcher);
			bundle.putSerializable("fileLink", fileLink);

			fragment.setArguments(bundle);

			FragmentManager manager = getFragmentManager();

			FragmentTransaction transaction = manager.beginTransaction();
			transaction.addToBackStack(null);
			transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
			transaction.commit();
		}
	}

	private class FileStorageTask extends
			AsyncTask<Void, Void, ArrayAdapter<?>> {

		private ProgressDialog mProgressDialog;
		private ListView mListView;

		public FileStorageTask(ListView listView) {
			this.mListView = listView;
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage(getString(R.string.fetching_filesystem));
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

		@Override
		protected ArrayAdapter<?> doInBackground(Void... params) {
			try {
				ArrayAdapter<?> adapter = null;

				if (mFileLink == null) {
					mMountpoints = JsoupFileService.getInstance()
							.getMountpoints();
					mAdapterMountpoints = new AdapterMountpoints(getActivity(),
							mMountpoints);
					adapter = mAdapterMountpoints;
				} else {
					mFileLinks = JsoupFileService.getInstance().getFileLinks(
							mFileLink);
					mAdapterFileLinks = new AdapterFileLinks(getActivity(),
							mFileLinks);
					adapter = mAdapterFileLinks;
				}

				return adapter;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayAdapter<?> result) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			if (result != null) {
				mListView.setAdapter(result);
			}
		}
	}
}
