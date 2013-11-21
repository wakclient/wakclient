package de.wak_sh.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.backend.adapters.NavigationDrawerAdapter;
import de.wak_sh.client.backend.adapters.NavigationDrawerItem;
import de.wak_sh.client.backend.service.DataService;
import de.wak_sh.client.fragments.BenutzerinfoFragment;
import de.wak_sh.client.fragments.DateiablageFragment;
import de.wak_sh.client.fragments.NachrichtenFragment;
import de.wak_sh.client.fragments.NotenuebersichtFragment;

public class MainActivity extends SherlockFragmentActivity {
	public static final String ACTION_LOGOUT = "de.wak_sh.client.ACTION_LOGOUT";
	private ListView mDrawerList;
	private List<NavigationDrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private static File fileToUpload;

	private final OnItemClickListener mDrawerClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position, null);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initSharedPreferences();

		Intent i = getIntent();
		if (i != null) {
			String action = i.getAction();
			if (action != null && Intent.ACTION_SEND.equals(action)) {
				Uri uri = i.getParcelableExtra(Intent.EXTRA_STREAM);
				fileToUpload = new File(uri.getPath());
			}
		}

		if (!DataService.getInstance().isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		setContentView(R.layout.activity_main);

		mDrawerItems = new ArrayList<NavigationDrawerItem>();
		mDrawerItems.add(new NavigationDrawerItem(R.string.benutzerinfo,
				R.drawable.ic_menu_home, new BenutzerinfoFragment()));
		mDrawerItems
				.add(new NavigationDrawerItem(R.string.nachrichten,
						android.R.drawable.sym_action_email,
						new NachrichtenFragment()));
		mDrawerItems.add(new NavigationDrawerItem(R.string.notenuebersicht,
				R.drawable.ic_menu_mark, new NotenuebersichtFragment()));
		mDrawerItems.add(new NavigationDrawerItem(R.string.dateiablage,
				R.drawable.ic_menu_archive, new DateiablageFragment()));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new NavigationDrawerAdapter(this, mDrawerItems));
		mDrawerList.setOnItemClickListener(mDrawerClickListener);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(R.string.app_name);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				getSupportActionBar().setTitle(getTitle());
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0, null);
		}

		if (fileToUpload != null) {
			Bundle args = new Bundle();
			args.putString("fileToUploadPath", fileToUpload.getAbsolutePath());
			selectItem(3, args);
			fileToUpload = null;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// XXX: Workaround until ABS supports ActionBarDrawerToggle
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		case R.id.action_logout:
			logout();
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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

	private void logout() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(ACTION_LOGOUT, "logout");
		startActivity(intent);
		finish();
	}

	protected void selectItem(int position, Bundle arguments) {
		Fragment fragment = mDrawerItems.get(position).getFragment();
		if (arguments != null) {
			fragment.setArguments(arguments);
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);

		setTitle(mDrawerItems.get(position).getTitleId());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void initSharedPreferences() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!preferences.contains(SettingsActivity.PREF_STORAGE_LOCATION)) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(SettingsActivity.PREF_STORAGE_LOCATION,
					Environment.getExternalStorageDirectory() + "/Download/");
			editor.commit();
		}
	}
}
