package min3d.sampleProject1;

import min3d.core.RendererActivity;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import android.app.Activity;
import android.view.View.OnClickListener;

public class GameOver extends RendererActivity implements OnClickListener
{
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //GameCall.mPaused=false;
		
        Music.stop(this);

        setContentView(R.layout.gameover);
  
        //View settingsButton = findViewById(R.id.settings_button);
        //settingsButton.setOnClickListener(this);
        View mContinueButton = findViewById(R.id.continueButton);
        mContinueButton.setOnClickListener(this);
        View mQuitButton = findViewById(R.id.quitButton);
        mQuitButton.setOnClickListener(this);  
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public void onClick(View v)
	{
		switch (v.getId())
		{
		//Continues from the last end point
			case R.id.continueButton:
				this.finish();
		    	Music.stop(this);
				Intent x=new Intent(this, GameModel.class);
				startActivity(x);

				break;
		//Quits the game		
			case R.id.quitButton:
				//finish();
			onBackPressed();
		        break;
		}
	}
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    }
    @Override
    protected void onPause()
    {
    	super.onPause();
    }
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	//Music.stop(this);
    }	
    protected void onBackPressed()
    {
    	this.finish();
    	Intent mainmenu = new Intent(this, SplashActivity.class);
		startActivity(mainmenu);
    	return;
    }
}