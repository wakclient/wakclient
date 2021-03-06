package de.wak_sh.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import com.google.analytics.tracking.android.EasyTracker;

import de.wak_sh.client.backend.AdapterDrawerItems;
import de.wak_sh.client.fragments.FragmentMessages;
import de.wak_sh.client.fragments.FragmentFileStorage;
import de.wak_sh.client.fragments.FragmentModules;
import de.wak_sh.client.fragments.FragmentUserInformation;
import de.wak_sh.client.fragments.WakFragment;
import de.wak_sh.client.model.DrawerItem;
import de.wak_sh.client.service.JsoupDataService;
import eu.ngls.articus.apprater.AppRater;

public class MainActivity extends SherlockFragmentActivity {

	private JsoupDataService mDataService;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

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

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (!prefs.contains(SettingsActivity.PREF_STORAGE_LOCATION)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(SettingsActivity.PREF_STORAGE_LOCATION,
					Environment.getExternalStorageDirectory() + File.separator
							+ "Download");
			editor.commit();
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) findViewById(android.R.id.list);
		mDrawerToggle = new DrawerLayoutActionBarToggle();

		List<DrawerItem> drawerItems = new ArrayList<DrawerItem>();
		drawerItems.add(new DrawerItem(R.drawable.ic_menu_home,
				getString(R.string.user_information)));
		drawerItems.add(new DrawerItem(android.R.drawable.sym_action_email,
				getString(R.string.messages)));
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

		AppRater.Configuration.title = getString(R.string.app_name);
		AppRater.rateApp(MainActivity.this);
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
			finish();
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

	@Override
	public void onBackPressed() {
		if (!getSupportFragmentManager().popBackStackImmediate()) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(R.string.app_close_text)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}

							}).setNegativeButton(android.R.string.no, null)
					.show();
		}
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
				fragment = new FragmentMessages();
				bundle.putString("title", getString(R.string.messages));
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
