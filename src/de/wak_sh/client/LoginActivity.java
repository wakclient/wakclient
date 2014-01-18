package de.wak_sh.client;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import de.wak_sh.client.backend.DataStorage;
import de.wak_sh.client.model.LoginResult;
import de.wak_sh.client.service.JsoupDataService;

public class LoginActivity extends SherlockActivity {

	private JsoupDataService mDataService;
	private LoginTask mLoginTask;

	private String mEmail;
	private String mPassword;

	private EditText mEditTextEmail;
	private EditText mEditTextPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mDataService = JsoupDataService.getInstance();

		mEditTextEmail = (EditText) findViewById(R.id.editText_email);
		mEditTextPassword = (EditText) findViewById(R.id.editText_password);
		Button buttonSignIn = (Button) findViewById(R.id.button_sign_in);

		if (getIntent().getExtras() != null
				&& getIntent().getExtras().getBoolean("logout")) {
			new LogoutTask().execute();
		} else {
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			mEmail = prefs.getString("email", null);
			mPassword = prefs.getString("password", null);
		}

		mEditTextEmail.setText(mEmail);
		mEditTextPassword.setText(mPassword);

		if (mEmail != null && mPassword != null) {
			attemptLogin();
		}

		mEditTextPassword
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == R.id.action_login
								|| actionId == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		buttonSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
	}

	private void attemptLogin() {
		if (mLoginTask != null) {
			return;
		}

		mEditTextEmail.setError(null);
		mEditTextPassword.setError(null);

		mEmail = mEditTextEmail.getText().toString();
		mPassword = mEditTextPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (mEmail.length() == 0) {
			mEditTextEmail.setError(getString(R.string.missing_username));
			focusView = mEditTextEmail;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmail = mEmail + "@berufsakademie-sh.de";
			mEditTextEmail.setText(mEmail);
		}

		if (mPassword.length() == 0) {
			mEditTextPassword.setError(getString(R.string.missing_password));
			focusView = mEditTextPassword;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginTask = new LoginTask();
			mLoginTask.execute();
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, LoginResult>
			implements OnCancelListener {

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(LoginActivity.this);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(this);
			mProgressDialog
					.setMessage(getString(R.string.login_progress_signing_in));
			mProgressDialog.show();
		}

		@Override
		protected LoginResult doInBackground(Void... arg0) {
			try {
				return mDataService.login(mEmail, mPassword);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(LoginResult result) {
			mLoginTask = null;

			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			if (result.isLoggedIn()) {
				SharedPreferences prefs = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("email", mEmail);
				editor.putString("password", mPassword);
				editor.commit();

				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(LoginActivity.this, result.getErrorMessage(),
						Toast.LENGTH_LONG).show();
				mEditTextPassword.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mLoginTask = null;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			mLoginTask.cancel(true);
		}

	}

	private class LogoutTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(LoginActivity.this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog
					.setMessage(getString(R.string.login_progress_signing_out));
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				JsoupDataService.getInstance().logout();
				SharedPreferences prefs = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.clear();
				editor.commit();

				DataStorage.getInstance().clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

	}
}
