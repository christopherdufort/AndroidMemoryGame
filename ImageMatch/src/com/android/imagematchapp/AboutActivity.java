package com.android.imagematchapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Assignment 1: Image Matcher Game App
 * About Activity used to display author names and brief description on how to play.
 * 
 * @author Daniel Vasile Badila
 * @author Christopher Dufort
 * @author Zheng Hua Zhu
 * @version 1.0.0-Release
 * @LastModified 2015-09-28
 */
public class AboutActivity extends Activity {

	/**
	 * Auto Generated method.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	/**
	 * Auto Generated method.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}
	
	/**
	 * Auto Generated method.
	 */
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
	
	/**
	 * This onClickListen is called to finish the activity and return to MainActivity.
	 * This method is called when return button in view is pressed.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
    public void onClickReturn(View view){
    	finish();
    }  
}