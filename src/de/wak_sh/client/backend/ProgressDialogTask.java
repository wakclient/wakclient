package de.wak_sh.client.backend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressDialogTask<Params, Result> extends
		AsyncTask<Params, Void, Result> {
	private ProgressDialog progressDialog;
	private Context context;
	private String text;

	public ProgressDialogTask(Context context, String text) {
		this.context = context;
		this.text = text;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(text);
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
