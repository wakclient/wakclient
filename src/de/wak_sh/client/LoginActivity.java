package de.wak_sh.client;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import de.wak_sh.client.backend.DataService;
import de.wak_sh.client.backend.ProgressDialogTask;

public class LoginActivity extends Activity {
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Intent intent = getIntent();
		if (intent.getExtras() != null
				&& intent.getExtras().getString(MainActivity.ACTION_LOGOUT) != null) {
			new UserLogoutTask().execute((Void) null);
		} else {
			mEmail = preferences.getString("email", null);
			mPassword = preferences.getString("password", null);
		}

		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		if (mEmail != null && mPassword != null) {
			attemptLogin();
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmail = mEmail + "@berufsakademie-sh.de";
			mEmailView.setText(mEmail);
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Kick off a background task to perform the user login attempt.
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Represents an asynchronous login task used to authenticate the user.
	 */
	public class UserLoginTask extends ProgressDialogTask<Void, Boolean> {
		public UserLoginTask() {
			super(LoginActivity.this,
					getString(R.string.login_progress_signing_in));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DataService dataService = DataService.getInstance();

			try {
				return dataService.login(mEmail, mPassword);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);
			mAuthTask = null;

			if (success) {
				SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
				preferences.edit().putString("email", mEmail)
						.putString("password", mPassword).commit();
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mAuthTask = null;
		}
	}

	public class UserLogoutTask extends ProgressDialogTask<Void, Void> {
		public UserLogoutTask() {
			super(LoginActivity.this,
					getString(R.string.login_progress_signing_out));
		}

		@Override
		protected Void doInBackground(Void... params) {
			DataService dataService = DataService.getInstance();

			SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
			preferences.edit().clear().commit();

			try {
				dataService.logout();
			} catch (IOException e) {
			}
			return null;
		}
	}
}
