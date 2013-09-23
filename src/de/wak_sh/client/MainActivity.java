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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.fragments.BenutzerinfoFragment;
import de.wak_sh.client.fragments.NachrichtenFragment;
import de.wak_sh.client.fragments.NotenFragment;

public class MainActivity extends SherlockFragmentActivity {
	public static final String ACTION_LOGOUT = "de.wak_sh.client.ACTION_LOGOUT";
	private ListView mDrawerList;
	private List<String> mTitles;
	private List<Fragment> mFragments;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private OnItemClickListener mDrawerClickListener = new OnItemClickListener() {
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

		mTitles = new ArrayList<String>();
		mTitles.add(getString(R.string.benutzerinfo));
		mTitles.add(getString(R.string.nachrichten));
		mTitles.add(getString(R.string.notenuebersicht));

		mFragments = new ArrayList<Fragment>();
		mFragments.add(new BenutzerinfoFragment());
		mFragments.add(new NachrichtenFragment());
		mFragments.add(new NotenFragment());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mTitles));
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
		Fragment fragment = mFragments.get(position);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);

		setTitle(mTitles.get(position));
		mDrawerLayout.closeDrawer(mDrawerList);
	}

}
