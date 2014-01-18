package de.wak_sh.client;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.backend.AdapterDrawerItems;
import de.wak_sh.client.fragments.FragmentEmails;
import de.wak_sh.client.fragments.FragmentFileStorage;
import de.wak_sh.client.fragments.FragmentModules;
import de.wak_sh.client.fragments.FragmentUserInformation;
import de.wak_sh.client.fragments.WakFragment;
import de.wak_sh.client.model.DrawerItem;
import de.wak_sh.client.service.JsoupDataService;

public class MainActivity extends SherlockFragmentActivity {

	private JsoupDataService mDataService;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDataService = JsoupDataService.getInstance();

		if (!mDataService.isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) findViewById(android.R.id.list);
		mDrawerToggle = new DrawerLayoutActionBarToggle();

		List<DrawerItem> drawerItems = new ArrayList<DrawerItem>();
		drawerItems.add(new DrawerItem(R.drawable.ic_menu_home,
				getString(R.string.user_information)));
		drawerItems.add(new DrawerItem(android.R.drawable.sym_action_email,
				getString(R.string.emails)));
		drawerItems.add(new DrawerItem(R.drawable.ic_menu_archive,
				getString(R.string.file_storage)));
		drawerItems.add(new DrawerItem(R.drawable.ic_menu_mark,
				getString(R.string.overview_grades)));

		mDrawerListView.setAdapter(new AdapterDrawerItems(MainActivity.this,
				drawerItems));
		mDrawerListView.setOnItemClickListener(new DrawerLayoutClickListener());
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		Bundle bundle = new Bundle();
		bundle.putString("title", getString(R.string.user_information));
		bundle.putInt("iconRes", R.drawable.ic_menu_home);

		Fragment fragment = new FragmentUserInformation();
		fragment.setArguments(bundle);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragment, WakFragment.TAG);
		ft.commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.user_information);
		getSupportActionBar().setIcon(R.drawable.ic_menu_home);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
				mDrawerLayout.closeDrawer(mDrawerListView);
			} else {
				mDrawerLayout.openDrawer(mDrawerListView);
			}
			return true;
		case R.id.action_logout:
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra("logout", true);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		return false;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	private class DrawerLayoutActionBarToggle extends ActionBarDrawerToggle {

		public DrawerLayoutActionBarToggle() {
			super(MainActivity.this, mDrawerLayout, R.drawable.ic_drawer,
					R.string.drawer_open, R.string.drawer_close);
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			getSupportActionBar().setIcon(R.drawable.ic_launcher);
			getSupportActionBar().setTitle(R.string.app_name);
			supportInvalidateOptionsMenu();
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			WakFragment fragment = (WakFragment) getSupportFragmentManager()
					.findFragmentByTag(WakFragment.TAG);
			getSupportActionBar().setTitle(fragment.getTitle());
			getSupportActionBar().setIcon(fragment.getIconRes());
			supportInvalidateOptionsMenu();
		}
	}

	private class DrawerLayoutClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}

		private void selectItem(int position) {
			Fragment fragment = null;
			Bundle bundle = new Bundle();

			switch (position) {
			case 0:
				fragment = new FragmentUserInformation();
				bundle.putString("title", getString(R.string.user_information));
				bundle.putInt("iconRes", R.drawable.ic_menu_home);
				break;
			case 1:
				fragment = new FragmentEmails();
				bundle.putString("title", getString(R.string.emails));
				bundle.putInt("iconRes", android.R.drawable.sym_action_email);
				break;
			case 2:
				fragment = new FragmentFileStorage();
				bundle.putString("title", getString(R.string.file_storage));
				bundle.putInt("iconRes", R.drawable.ic_menu_archive);
				break;
			case 3:
				fragment = new FragmentModules();
				bundle.putString("title", getString(R.string.overview_grades));
				bundle.putInt("iconRes", R.drawable.ic_menu_mark);
				break;
			}

			fragment.setArguments(bundle);

			FragmentManager manager = getSupportFragmentManager();
			manager.popBackStack();

			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.content_frame, fragment, WakFragment.TAG);
			transaction.commit();

			mDrawerListView.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerListView);
		}
	}

}
