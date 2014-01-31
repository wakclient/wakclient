package de.wak_sh.client.backend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class ProgressTask<V, P, R> extends AsyncTask<V, P, R> {

	private ProgressDialog mProgressDialog;

	private Context mContext;
	private String mTitle;
	private String mMessage;

	public ProgressTask(Context context, String title, String message) {
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mMessage);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);

		if (mTitle != null) {
			mProgressDialog.setTitle(mTitle);
		}

		mProgressDialog.show();
	}

	@Override
	protected void onPostExecute(R result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

}