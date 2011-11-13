package min3d.sampleProject1;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameController implements OnTouchListener{
	GameModel model;
	final float delta=10;
	
	float firstX;
	float firstY;
	int state=0;
	float updateX;
	float updateY;
	float distance;
	float previous=0f;
	
	public GameController(GameModel model){
		this.model=model;
	}


		public boolean onTouch(View v, MotionEvent event) {
			synchronized(model){
				 Log.d("Touched", "Tapped");
			int action = event.getAction();
		      updateX=event.getX();
		      updateY=event.getY();
		       
			
			if (action == MotionEvent.ACTION_DOWN) {
		    	  Log.d("Touched", "Tapped");
		           firstX = event.getRawX();
		           firstY = event.getRawY();
		           return true;
		       }else  if (action == MotionEvent.ACTION_MOVE){
		    	   if(Math.abs(updateX-firstX)>delta ){
		    		  
		    		   distance = firstX - event.getX();
		    		  //previous=distance;
		    			  }
		    			  
		    		  }
		    		  
		    	  }
		           return true;
		           }
}
