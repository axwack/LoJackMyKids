package com.principalmvl.lojackmykids.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.principalmvl.lojackmykids.MainActivity;
import com.principalmvl.lojackmykids.R;
import com.principalmvl.lojackmykids.Interfaces.OnAlertDialogClickListener;

public class NewPasswordDialog extends DialogFragment {

	private static final int PASSWORD_DIALOG_ID = 4;
	private TextView password1, password2;
	private TextView error;
	private OnAlertDialogClickListener mListener;

	public static NewPasswordDialog newInstance(int title) {
        NewPasswordDialog frag = new NewPasswordDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {

		mListener = (OnAlertDialogClickListener) getActivity();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Pass null as the parent view because its going in the dialog layout
		//builder.setView(layout);

		int PASSWORD_DIALOG_ID = getArguments().getInt("PASSWORD_DIALOG_ID");

		// switch (savedInstance.getInt("PASSWORD_DIALOG_KEY")) {

		// case PASSWORD_DIALOG_ID:

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mListener.onNegativeClick(dialog, whichButton);

					}
				});

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String strPassword1 = password1.getText().toString();
						String strPassword2 = password2.getText().toString();
						if (strPassword1.equals(strPassword2)) {
							Log.i(MainActivity.DEBUGTAG, "Matching passwords="
									+ strPassword2);
							// TODO: Return the password the caller..this is
							// the call back.
							// setPassword(strPassword1);
						}
						mListener.onPositiveClick(dialog, whichButton);
					}
				});

		password2.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String strPass1 = password1.getText().toString();
				String strPass2 = password2.getText().toString();
				if (strPass1.equals(strPass2)) {
					error.setText(R.string.settings_pwd_equal);
				} else {
					error.setText(R.string.settings_pwd_not_equal);
				}
			}
		});
		// }
		return builder.create();
	}

}
