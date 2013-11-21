package de.wak_sh.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.wak_sh.client.R;

public class NachrichtSchreibenFragment extends SherlockFragment {
	private MultiAutoCompleteTextView to;
	private TextView subject;
	private TextView text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_nachrichten_schreiben, container, false);

		to = (MultiAutoCompleteTextView) rootView
				.findViewById(R.id.multiAutoCompleteTextView1);
		subject = (TextView) rootView.findViewById(R.id.editText1);
		text = (TextView) rootView.findViewById(R.id.editText2);

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.nachrichtenschreiben_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println(item);
		return true;
	}

}
