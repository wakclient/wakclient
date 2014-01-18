package de.wak_sh.client.fragments;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;

public class WakFragment extends SherlockFragment {
	public static final String TAG = "wakFragment";

	protected String mTitle;
	protected int mIconRes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTitle = getArguments().getString("title");
		mIconRes = getArguments().getInt("iconRes");
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public int getIconRes() {
		return mIconRes;
	}

	public void setIconRes(int iconRes) {
		this.mIconRes = iconRes;
	}
}
