package com.principalmvl.lojackmykids;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {

	/*
	 * Set the password dialog to identify it. This has to be unique
	 */

	private EditText password1, password2;
	private TextView error;
	private SharedPreferences sharedPref;
	private String PREFS_NAME = "LJKIDSPrefs";
	private Boolean password_set, device_is_child;
	private static final int PASSWORD_DIALOG_ID = 4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);

		Intent intename = getIntent();
		// Get the Values passed in the intent. Looking for two flags:
		// DEVICE_IS_CHILD and Password_SET
		password_set = (Boolean) intename
				.getSerializableExtra(getString(R.string.password_set));
		device_is_child = (Boolean) intename
				.getSerializableExtra(getString(R.string.device_is_child));

		if (password_set) {
			if (!device_is_child) {
				Intent intObj = new Intent(this, MainActivity.class);
				/*
				 * Set the password_set flag..need to pass Is child Flag
				 */
				intObj.putExtra(getString(R.string.password_set), password_set);

				// Start MainActivity
				startActivity(intObj);
			} else {
				// TODO: Create Child Intent and Activity
			}
		} else {

/*			password1 = (EditText) findViewById(R.id.EditText_Pwd1);
			password2 = (EditText) findViewById(R.id.EditText_Pwd2);
			error = (TextView) findViewById(R.id.TextView_PwdProblem);*/
		}
	}

	private void setPassword(String password) {
		password_set = true;
		sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = sharedPref.edit();
		prefsEditor.putBoolean(getString(R.string.password_set), password_set);
		prefsEditor.putString(getString(R.string.password), password1.getText()
				.toString());
		prefsEditor.commit();

		password_set = sharedPref.getBoolean(getString(R.string.password_set),
				false);
		/*
		 * Log.i(MainActivity.DEBUGTAG, "Value of Password: " +
		 * mPasswordView.getText().toString()); Log.i(MainActivity.DEBUGTAG,
		 * "Value of Password_set: " + password_set);
		 * Log.i(MainActivity.DEBUGTAG, "Value of Saved Password: " + password);
		 */

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case PASSWORD_DIALOG_ID:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.activity_password,
					(ViewGroup) findViewById(R.id.root));
			password1 = (EditText) findViewById(R.id.EditText_Pwd1);
			password2 = (EditText) findViewById(R.id.EditText_Pwd2);
			error = (TextView) findViewById(R.id.TextView_PwdProblem);

			password2.addTextChangedListener(new TextWatcher() {

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
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
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							removeDialog(PASSWORD_DIALOG_ID);
						}
					});

			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String strPassword1 = password1.getText()
									.toString();
							String strPassword2 = password2.getText()
									.toString();
							if (strPassword1.equals(strPassword2)) {
								Toast.makeText(PasswordActivity.this,
										"Matching passwords=" + strPassword2,
										Toast.LENGTH_SHORT).show();
								setPassword(strPassword1);
							}
							removeDialog(PASSWORD_DIALOG_ID);
						}
					});
			

			builder.setTitle("Enter Password");
			builder.setView(layout);
			AlertDialog passwordDialog = builder.create();
			return passwordDialog;
		}
		;

		return super.onCreateDialog(id);
	}

}
