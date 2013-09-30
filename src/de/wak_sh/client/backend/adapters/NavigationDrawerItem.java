package de.wak_sh.client.backend.adapters;

import android.support.v4.app.Fragment;

public class NavigationDrawerItem {
	private int titleId;
	private int iconId;
	private Fragment fragment;

	public NavigationDrawerItem(int titleId, int iconId, Fragment fragment) {
		super();
		this.titleId = titleId;
		this.iconId = iconId;
		this.fragment = fragment;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

}
