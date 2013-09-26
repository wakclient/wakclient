package de.wak_sh.client;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.backend.NavigationDrawerAdapter;
import de.wak_sh.client.backend.NavigationDrawerItem;
import de.wak_sh.client.fragments.BenutzerinfoFragment;
import de.wak_sh.client.fragments.DateiablageFragment;
import de.wak_sh.client.fragments.NachrichtenFragment;
import de.wak_sh.client.fragments.NotenFragment;

public class MainActivity extends SherlockFragmentActivity {
	public static final String ACTION_LOGOUT = "de.wak_sh.client.ACTION_LOGOUT";
	private ListView mDrawerList;
	private List<NavigationDrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private final OnItemClickListener mDrawerClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
				R.drawable.ic_menu_mark, new NotenFragment()));
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
			selectItem(0);
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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(ACTION_LOGOUT, "logout");
		startActivity(intent);
		finish();
	}

	protected void selectItem(int position) {
		Fragment fragment = mDrawerItems.get(position).getFragment();

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);

		setTitle(mDrawerItems.get(position).getTitleId());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

}
