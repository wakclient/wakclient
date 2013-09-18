package de.wak_sh.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.viewpagerindicator.TabPageIndicator;

import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.fragments.NachrichtenFragment;
import de.wak_sh.client.fragments.NotenFragment;

public class MainActivity extends FragmentActivity {
	public static final String ACTION_LOGOUT = "de.wak_sh.client.ACTION_LOGOUT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!DataService.getInstance().isLoggedIn()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

		setContentView(R.layout.activity_main);

		SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(
				getSupportFragmentManager(), this);
		pagerAdapter.addFragment(new NachrichtenFragment());
		pagerAdapter.addFragment(new NotenFragment());

		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.pager_indicator);
		indicator.setViewPager(viewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
}
