package de.wak_sh.client.backend;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.wak_sh.client.R;
import de.wak_sh.client.model.Recipient;

public class SelectionDialogRecipient extends SherlockDialogFragment {

	private OnRecipientSelectedListener listener;

	public interface OnRecipientSelectedListener {
		public void onRecipientSelected(DialogFragment dialog,
				Recipient recipient);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnRecipientSelectedListener) activity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final List<Recipient> list = (List<Recipient>) getArguments()
				.getSerializable("list");
		String[] items = new String[list.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = list.get(i).getName() + " - "
					+ list.get(i).getLocation();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select_recipient);
		builder.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Recipient recipient = list.get(which);
				listener.onRecipientSelected(SelectionDialogRecipient.this,
						recipient);
			}
		});
		return builder.create();
	}

}
