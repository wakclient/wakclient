package de.wak_sh.client;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.viewpagerindicator.TabPageIndicator;

import de.wak_sh.client.fragments.NachrichtenFragment;
import de.wak_sh.client.fragments.NotenFragment;

public class MainActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

}
