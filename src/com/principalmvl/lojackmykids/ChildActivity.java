package com.principalmvl.lojackmykids;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class ChildActivity extends Activity {
	private Boolean password_set=false;
	private Boolean device_is_child=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child);
		
		Intent intename = getIntent();
		// Get the Values passed in the intent. Looking for two flags: DEVICE_IS_CHILD and Password_SET
		password_set = intename.getBooleanExtra(getString(R.string.password_set),false);
		device_is_child = intename.getBooleanExtra(getString(R.string.device_is_child), false);
		
		// Handle the Login Button Click
					Button ChildLoginButton = (Button) findViewById(R.id.child_loginBtn);
								
					ChildLoginButton.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							Intent intObj = new Intent(ChildActivity.this, LoginActivity.class);
							
							intObj.putExtra(getString(R.string.password_set), password_set);
							intObj.putExtra(getString(R.string.device_is_child),
									device_is_child);
							intObj.putExtra(getString(R.string.segue_from_child),
									true);						
							startActivity(intObj);						
						}						
					});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.child, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
