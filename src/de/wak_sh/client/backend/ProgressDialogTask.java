package de.wak_sh.client.backend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressDialogTask<Params, Result> extends
		AsyncTask<Params, Void, Result> {
	private ProgressDialog progressDialog;
	private Context context;
	private String title;

	public ProgressDialogTask(Context context, String title) {
		this.context = context;
		this.title = title;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.show();
	}

	@Override
	protected void onPostExecute(Result result) {
		dismissProgressDialog();
	}

	@Override
	protected void onCancelled() {
		dismissProgressDialog();
	}

	private void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}
