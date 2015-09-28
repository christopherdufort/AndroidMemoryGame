package com.android.imagematchapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Assignment 1: Image Matcher Game App
 * MainActivity is the primary activity of the app, where all the gameplay and scoring is happening.
 *  
 * A simple image match game where users are expected to select the duplicate images.
 * This app has 9 images randomly displayed one of which is a duplicate.
 * Multiple counters(4) and proper use of LifeCycle methods to keep track of progress that is stored temporarily and persistently.
 * Scramble button which randomizes images.
 * Second activity launched when pressing about button.
 * Zero button resets session counters and stores long term counts persistently.
 * 
 * This Assignment makes use of: 
 * UI layouts, widgets (Layouts, Vies, Text, Buttons, Images)
 * SharedPreferences (Long term storage of counters)
 * Activity lifecycle (properly maintain persistence across temporary sessions)
 * Drawable resources 
 * String resources (all strings are stored in res/values/strings.xml no constants used)
 * Localisation (completely localized to English United states and French Canada)
 * Dialogues and Toasts
 * 
 * @author Daniel Vasile Badila
 * @author Christopher Dufort
 * @author Zheng Hua Zhu
 * @version 1.0.0-Release
 * @LastModified 2015-09-28
 * 
 */
public class MainActivity extends Activity {
	
	//Private field variables used to keep track behind the scenes.
	private int matchCtr = 0, missCtr = 0, totalMatchCtr = 0, totalMissCtr = 0;
	private int buttonId , duplicateId, index = 0;
	
	//An array that holds the 8 different images used in this game.
	private int[] allImageIds ={R.drawable.img01, R.drawable.img02, R.drawable.img03, R.drawable.img04, R.drawable.img05, R.drawable.img06, R.drawable.img07, R.drawable.img08};
	//An array that holds the 9 button id's
	private int[] allButtonIds = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};
	//An arraylist that is used to mix and add shuffled images.
	private ArrayList<Integer> mixedImageIds ;
	//An array keeping track of which buttons have been pressed.
	private boolean[] enabledButtons = new boolean[9];
	
	//Game state, has the first duplicate been found.
	private boolean firstDuplicateHit;
	
	//Winning alert dialog message.
	private AlertDialog.Builder builder;
	
	//Reusable variables for object manipulation and casting.
	private ImageButton imageButton;	
	private TextView textView;
	private Button scrambleButton;
	
	//Long term storage in shared preferences.
	private SharedPreferences prefs;
	
	//This app supports background music and sound effects.
	private MediaPlayer background, soundMatch, soundMiss, soundWin;
	
	/**
	 * This onClickListener method is called when a button in the view is pressed.
	 * This method will retrieve the selected image button from the view and if enabled will do the following:
	 * This method is responsible for disabling buttons when clicked, incrementing counters, and updating arrays.
	 * Clicking on a duplicate image will result in a change to the view.
	 * Clicking on a second duplicate image will result in game win, and all other buttons being disabled.
	 * This method will call update display counter, and will start sound effects.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
	public void handleClick(View view){
		//Cast selected image into a Image button to work with.
		ImageButton clickedButton = (ImageButton)view;
		
		//Retrieve the exact position of the selected button in the array of all buttons.
		for(int i =0 ; i< allButtonIds.length; i++)
		{
			if(allButtonIds[i] == view.getId())
				index = i;
		}		
		//Retrieve the customly set tag value(id associated with current image)
		buttonId = Integer.parseInt(String.valueOf(view.getTag()));

		//If the button is not enabled skip the following clock.
		if (clickedButton.isEnabled())
		{
			//Disable button and update arry of buttons.
			clickedButton.setEnabled(false);
			enabledButtons[index]=false;
			
			//If the selected button is a duplicate and the first duplicate has not been found.
			if (buttonId == duplicateId && !firstDuplicateHit)
			{
				soundMatch.start();
				matchCtr++;
				totalMatchCtr++;
				Toast.makeText(this, R.string.matchMessage, Toast.LENGTH_SHORT).show(); //No constants
				clickedButton.setImageResource(R.drawable.img_duplicate); //Update image to displayed duplicate.
				firstDuplicateHit = true; //state variable
				mixedImageIds.set(index, R.drawable.img_duplicate); //Update image in array of images. 
			}
			//If the selected button is a duplicate and the first duplicate has already been found.
			else if (buttonId == duplicateId && firstDuplicateHit)
			{
				soundWin.start();
				matchCtr++;
				totalMatchCtr++;;
				launchWinDialogue();
				
				//Loop through all buttons disabling them as the game has been won.
				for (int i =0; i <= allImageIds.length ; i++) 
				{
					imageButton = (ImageButton) findViewById(allButtonIds[i]);
					imageButton.setEnabled(false);
					enabledButtons[i]=false;
				}
				scrambleButton = (Button) findViewById(R.id.scramble);
				//Set text color to red to hint at the user to restart game.
				scrambleButton.setTextColor(Color.RED);
			}
			//If selected button is not a duplicate
			else
			{
				soundMiss.start();
				missCtr++;
				totalMissCtr++;
				Toast.makeText(this, R.string.missMessage, Toast.LENGTH_SHORT).show(); //No constants
			}
			//Always update counters (if button was enabled)
			updateDisplayCounters();
		}
	}// end of handleClick()
	
	/**
	 * Custom event handle for scramble button being pressed.
	 * This function called the private local scramble method.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
	public void handleScramble(View view) {
		scramble();
	}// end of handleScramble()
	
	/**
	 * This method is called when the about button is pressed in the main activity.
	 * launchAboutActivity is responsible for launching the new about activity using an intent.
	 * This activity will display how to play and authors of the image match application.
	 * Primary purpose is for players to learn how to play and read about game/creators.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
	public void launchAboutActivity(View view){
		Intent launchAboutActivity = new Intent(getApplicationContext(),AboutActivity.class);
		startActivity(launchAboutActivity);
	} //end of launchAboutActivity()	
	/**
	 * Overridden onCreate method called at the beginning of the activity life cycle.
	 * Responsible for setting the content view from the layout.
	 * Persistent values stored in shared preferences are retrieved upon starting of app.
	 * If a bundle exists retrieve all temporary state information from it and update fields.
	 * Otherwise this method will call the scramble method to initate a new game.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Always call super to retrieve persistence from view hierarchy state
		super.onCreate(savedInstanceState);
		//Set the view.
		setContentView(R.layout.activity_main);
		
		
		// Restore preferences, no editor needed since we're only reading values.
	    prefs = getPreferences(MODE_PRIVATE); //From local app storage.
	    totalMissCtr= prefs.getInt("TOTAL_MISS", 0);
	    totalMatchCtr = prefs.getInt("TOTAL_MATCH", 0);
	    
	    // Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) 
	    {
	    	//Retrieve counters from bundle.
		    matchCtr = savedInstanceState.getInt("MATCH", 0);
		    missCtr = savedInstanceState.getInt("MISS", 0);
		    
		    //Retrieve status of buttons and image locations from bundle.
		    mixedImageIds = savedInstanceState.getIntegerArrayList("IMAGE_LOCATIONS");
		    enabledButtons = savedInstanceState.getBooleanArray("ENABLED_BUTTONS");
		    
		    //Retrieve duplicate id and whether or not it has been hit yet game state from bundle.
		    firstDuplicateHit = savedInstanceState.getBoolean("FIRST_DUPLICATE_HIT");
		    duplicateId = savedInstanceState.getInt("DUPLICATE_ID", 0);
		    
		    //Loop through all buttons
		    for (int i = 0; i < allButtonIds.length; i++)
		    {
		    	imageButton = (ImageButton) findViewById(allButtonIds[i]);
		    	imageButton.setImageResource(mixedImageIds.get(i)); //Set image as old state specified from bundle
		    	imageButton.setTag(mixedImageIds.get(i)); //Set custom tag as image id int as old state specified from bundle
		    	if(!enabledButtons[i]){ //if the button is not enabled  in bundle disable in view.
		    		imageButton.setEnabled(false);
		    	}
		    }
	    }
	    else{ //No state is retrieved from bundle scramble as new game.
	    	scramble();
	    }
	    //Always update display of counters in view.
	    updateDisplayCounters();
	} //end of onCreate
	
	/**
	 * Auto created method for option menu on task bar.
	 * This method and its associated menu bar is not used by this application in its current state.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}// end of onCreateOptionsMenu
	
	/**
	 * Auto created method for option item selected menu on task bar.
	 * This method and its associated menu bar is not used by this application in its current state.
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
	}// end of onOptionsItemSelected
	
	/**
	 * Overridden onPause method used to release resources, and store persistent data during lifecycle of application.
	 * Note that it is important to save persistent data in onPause() instead of onSaveInstanceState(Bundle) because the latter is not part of the lifecycle callbacks, so will not be called in every situation.
	 * This method will store the lifetime counters in the shared preferences for the application.
	 */
	@Override
	public void onPause(){
		//Always call super.
		super.onPause();
		
		// editor used to modify shared preferences mode private = exclusive to this activity.
		prefs = getPreferences(MODE_PRIVATE); 
		SharedPreferences.Editor editor = prefs.edit();
		
		//Store life time counters.
		editor.putInt("TOTAL_MISS", totalMissCtr);
		editor.putInt("TOTAL_MATCH", totalMatchCtr);
		
		//Commit changes to shared preferences
		editor.commit();
		
		//Release media player resources.
		disableMusic();
	} //end of onPause()
	
	/**
	 * Overridden onResume method used to gather resources and call media player methods.
	 * Called during life cycle right before activity is on foreground.
	 * Called enableMusic to acquire media player resources.
	 */
	@Override
	public void onResume(){
		//Always call super.
		super.onResume();
		//Enable use of music resources.
		enableMusic();
	} //End of onResume()
	
	/**
	 * Overridden onSaveInstanceState method used to store temporary state in bundle.
	 * This method is called when application orientation changes or when killed by OS to reclaim resources.
	 * This method saves the state of the game by storing counters, and button position / status in bundle.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		// Always call the superclass so it can save the view hierarchy state.
	    super.onSaveInstanceState(savedInstanceState);
	    
	    //Store counters.
	    savedInstanceState.putInt("MATCH", matchCtr);
	    savedInstanceState.putInt("MISS", missCtr);
	    
	    //Store location of all images currently
	    savedInstanceState.putIntegerArrayList("IMAGE_LOCATIONS", mixedImageIds);
	    
	    //Store which buttons have been enabled/ disabled.
	    savedInstanceState.putBooleanArray("ENABLED_BUTTONS", enabledButtons);
	    
	    //Has the first duplicate been hit or not state of winning.
	    savedInstanceState.putBoolean("FIRST_DUPLICATE_HIT", firstDuplicateHit);
	    
	    //Randomly selected duplicate for the current game.
	    savedInstanceState.putInt("DUPLICATE_ID", duplicateId);
	} //end of onSaveInstanceState()
	
	/**
	 * This method is called when the rotate button is pressed in the main activity.
	 * rotateOrientation is responsible for rotating the screen orientation manually.
	 * Orientation can be changed via the device's gyroscope or this method.
	 * Primary purpose is for Patricia to be able to test landscape view of application.
	 * 
	 * TODO: This method will disable the automatic orientation sensor.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
	public void rotateOrientation(View view) {
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}  //end of rotateScreen()

	/**
	 * This onClick listener is called when the Zero button is clicked in the main activity.
	 * This method will reset the visible counter for this current matches and misses to zero.
	 * This method will also store the lifetime counters in the shared preferences.
	 * 
	 * @param view
	 * 			The current view retrieved from the current running activity.
	 */
	public void zeroAndSave(View view){
		// Reset fields
		matchCtr = 0;
		missCtr = 0;
		
		// Editor used to modify shared preferences mode private = exclusive to this activity.
		prefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		  
		//Store life time counters.
		editor.putInt("TOTAL_MISS", totalMissCtr);
		editor.putInt("TOTAL_MATCH", totalMatchCtr);
		
		// Commit changed to shared preferences
		editor.commit();
		
		// Call update method to refresh the view to represent changes to variables. 
		updateDisplayCounters();
	}//end of zeroAndSave()

	/**
	 * This method is called in order to stop,reset,release and null out all media player assets.
	 * Expensive media resources are released as soon as possible to reduce change of application being reaped by OS.
	 * 3 sound effects, and 1 background music are released during onPause()
	 */
	private void disableMusic(){
		if (background != null) {
			if (background.isPlaying())
				background.stop();
			background.reset();
			background.release();
			background = null;
		}
		if (soundMiss != null) {
			if (soundMiss.isPlaying())
				soundMiss.stop();
			soundMiss.reset();
			soundMiss.release();
			soundMiss = null;
		}
		if (soundMatch != null) {
			if (soundMatch.isPlaying())
				soundMatch.stop();
			soundMatch.reset();
			soundMatch.release();
			soundMatch = null;
		}
		if (soundWin != null) {
			if (soundWin.isPlaying())
				soundWin.stop();
			soundWin.reset();
			soundWin.release();
			soundWin = null;
		}
	}// end of disableMusic
	
	/**
	 * This method is called to acquire resources for Playing of raw media files.
	 * This method will create all sound effect used by this application.
	 * This method will also comment the background music.
	 * Called from onResume.
	 * 
	 * FIXME "Should have subtitle controller already set error" - where do I set that?, ignore error.
	 */
	private void enableMusic(){
		
		//Create and start looping background music
		background = MediaPlayer.create(this, R.raw.background);
		background.setLooping(true);
		background.start();
		
		//Create sound effects
		soundMatch = MediaPlayer.create(this, R.raw.sound_match);
		soundMiss = MediaPlayer.create(this, R.raw.sound_miss);
		soundWin = MediaPlayer.create(this, R.raw.sound_win);
	} //end of enableMusic()
	
	/**
	 * Alert Dialog launcher is called in order to build and show the winning alert dialog message.
	 * All strings are localized to be displayed in any language, no constants.
	 * Nested anonymous function used to create onClick listener for ok button of dialog.
	 */
	private void launchWinDialogue() {
		//Build up alert Dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.winTitle)); //TODO can this be reduced in size?
		builder.setMessage(getResources().getString(R.string.winMessage)); //TODO can this be reduced in size?
		builder.setPositiveButton(getResources().getString(R.string.winButton), new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); //Dismiss the dialog window when ok button is clicked	
			}
		});
		// Create the AlertDialog object and display it.
		AlertDialog dialog = builder.create();
		dialog.show();
	} //end of launchWinDialogue()
	
	/**
	 * Scramble method used to randomly select duplicate image and assign random images to buttons.
	 * Makes used of Collections.Shuffle method (do not reinvent the wheel)
	 * Updates mixedImagesIds array to keep a stored list of all current image positions including duplicates.
	 */
	private void scramble() {
		//Set to size of 9 (8 images + 1 duplicate)
		mixedImageIds = new ArrayList<Integer>(9);
		
		scrambleButton = (Button) findViewById(R.id.scramble);
		//Return color of text to normal
		scrambleButton.setTextColor(Color.BLACK);
		
		//Fill arraylist with images from resources.
		for(Integer imageId : allImageIds)
			mixedImageIds.add(imageId);
		
		//Randomly select one image from the list to be the duplicate
		Random r = new Random();
		duplicateId = mixedImageIds.get(r.nextInt(8));
		
		//Add another copy of duplicate image to list(full length of 9)
		mixedImageIds.add(duplicateId);
		
		//Randomly shuffle contents of list.
		Collections.shuffle(mixedImageIds);
		
		//Loop through all buttons
		for (int i = 0; i < 9; i++)
		{
			ImageButton btn = (ImageButton)findViewById(allButtonIds[i]); 
			btn.setEnabled(true);
			enabledButtons[i] = true; //Update array
			btn.setTag(mixedImageIds.get(i)); //Set custom tag (image id used for quick int comparison)
			btn.setImageResource(mixedImageIds.get(i));	//update image of button with value from list.
		}
		//Reset state variable.
		firstDuplicateHit = false;
	} //end of scramble()
	
	/**
	 * private update method used to refresh the view and update the value of all counters.
	 * Only called when a change of state is made to field backing variables.
	 */
	private void updateDisplayCounters() {
		textView = (TextView) findViewById(R.id.matchesCtr);
		textView.setText(String.valueOf("" + matchCtr));
		
		textView = (TextView) findViewById(R.id.missesCtr);
		textView.setText(String.valueOf("" + missCtr));

		textView = (TextView) findViewById(R.id.totalMatchesCtr);
		textView.setText(String.valueOf(totalMatchCtr));

		textView = (TextView) findViewById(R.id.totalMissesCtr);
		textView.setText(String.valueOf(totalMissCtr));	
	} // end of updateDisplayCounters();	
} //end of MainActivity()
