package org.games.ukodus;

import org.games.ukodus.About;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class Ukodus extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Init click listeners for buttons
        View continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);
        View newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(this);
        View aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.settings:
    		startActivity(new Intent(this, Prefs.class));
    		return true;
    	//more items go here
    	}
    	return false;	
    }
    
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.continue_button:
			//Continue Game
			break;
		case R.id.new_button:
			//Start New Game
			openNewGameDialog();
			break;
		case R.id.about_button:
    		Intent i = new Intent(this, About.class);
    		startActivity(i);
    		break;
		case R.id.exit_button:
			//Exit Game
			finish();
			break;
			
    	}
    }
	
	private static final String TAG = "Ukodus";
	
	private void openNewGameDialog(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.new_game_title)
		.setItems(R.array.difficulty, 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i)
			{
				startGame(i);
			}
		}).show();
	}
	
	private void startGame(int i){
			Log.d(TAG, "clicked on " + i);
			//START GAME
			Intent intent = new Intent(Ukodus.this , Game.class);
			intent.putExtra(Game.KEY_DIFFICULTY, i);
			startActivity(intent);
	}
}