package min3d.sampleProject1;

import java.util.LinkedList;
import java.util.Random;

import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.objectPrimitives.Box;
import min3d.objectPrimitives.Rectangle;
import min3d.objectPrimitives.Sphere;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Color4;
import min3d.vos.Light;
import min3d.vos.Number3d;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GameModel extends RendererActivity implements SensorEventListener {
	private Sphere[] spheres;
	private Object3dContainer[] fruits;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Number3d mAccVals;
		
	private final float FILTERING_FACTOR = .3f;
	private GameModel model;
	
	private int lives = 3;
	private int index = 0;
	private int score = 0;
	Display display;
	int screenWidth;
	int screenHeight;
	Random random = new Random();
	
	Object3dContainer appleModel;
	Object3dContainer waterModel;
	Object3dContainer coconutModel;
	Object3dContainer bananaModel; 
	Object3dContainer bombModel; 

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       // ImageView view = (ImageView) findViewById(R.id.imageView);
        //view.setOnTouchListener(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccVals = new Number3d();
        display = getWindowManager().getDefaultDisplay(); 
    	screenWidth = display.getWidth();
    	screenHeight = display.getHeight();
	}
	
	@Override
	public void initScene() {
		
		
		
		model = this;
		Light light = new Light();
    	scene.lights().add(light);
    	scene.camera().position.x = 0;
    	scene.camera().position.y = 0;
    	scene.camera().position.z = 10;
    	
    	
		Bitmap b = Utils.makeBitmapFromResourceId(R.drawable.barong);
		Shared.textureManager().addTextureId(b, "barong", false);
		b.recycle();
		
		b = Utils.makeBitmapFromResourceId(R.drawable.wood);
		Shared.textureManager().addTextureId(b, "wood", false);
		b.recycle();
	
		
		spheres = new Sphere[5];
		fruits = new Object3dContainer[5];
		
		//Creates Parsers for all of the fruits
		IParser parserFruit = Parser.createParser(Parser.Type.OBJ,
	   	getResources(), "min3d.sampleProject1:raw/apple_obj", true);
		parserFruit.parse();
		appleModel = parserFruit.getParsedObject();
		
		parserFruit = Parser.createParser(Parser.Type.OBJ,
	   	getResources(), "min3d.sampleProject1:raw/watermelon_obj", true);
		parserFruit.parse();
		waterModel = parserFruit.getParsedObject();
		
		parserFruit = Parser.createParser(Parser.Type.OBJ,
	  	getResources(), "min3d.sampleProject1:raw/coconut_obj", true);
		parserFruit.parse();
		coconutModel = parserFruit.getParsedObject();
		
		parserFruit = Parser.createParser(Parser.Type.OBJ,
	   	getResources(), "min3d.sampleProject1:raw/banana1_obj", true);
		parserFruit.parse();
		bananaModel = parserFruit.getParsedObject();

		
		for(int i=0; i<5; i++)
		{
			addFruit(spheres,fruits,i);
		}
   		
   		Color4 planeColor = new Color4(255, 255, 255, 255);
		Rectangle east = new Rectangle(40, 12, 2, 2, planeColor);
		Rectangle west = new Rectangle(40, 12, 2, 2, planeColor);
		Rectangle up = new Rectangle(40, 12, 2, 2, planeColor);
		Rectangle down = new Rectangle(40, 12, 2, 2, planeColor);
   		
		east.position().x = -6;
		east.rotation().y = -90;
		east.position().z = -20;
		east.lightingEnabled(false);
		east.textures().addById("wood");
		
		west.position().x = 6;
		west.rotation().y = 90;
		west.position().z = -20;
		west.lightingEnabled(false);
		west.textures().addById("wood");
		
		up.rotation().x = -90;
		up.rotation().z = 90;
		up.position().y = 6;
		up.position().z = -20;
		up.lightingEnabled(false);
		up.textures().addById("wood");
		
		down.rotation().x = 90;
		down.rotation().z = 90;
		down.position().y = -6;
		down.position().z = -20;
		down.lightingEnabled(false);
		down.textures().addById("wood");
		
   		scene.addChild(east);
   		scene.addChild(west);
   		scene.addChild(up);
   		scene.addChild(down);
   		
   		scene.fogColor(new Color4(0, 0, 0, 255) );
   		scene.fogNear(10);
   		scene.fogFar(40);
   		scene.fogEnabled(true);


		Rectangle rectangle = new Rectangle(4, 12, 2, 2, planeColor);
   		
   		rectangle.position().x = 0;
   		rectangle.position().y =   0;
   		rectangle.rotation().y = 180;
   		rectangle.position().z = 0;
   		rectangle.lightingEnabled(false);
   		rectangle.textures().addById("wood");
   		
   		//scene.addChild(rectangle);  \\
   		
		//mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

	}
	
	@Override 
	public void updateScene() 
	{
		for(int i=0; i<5; i++)
		{
			Sphere sphere = spheres[i];
			sphere.position().z += .25;
			sphere.rotation().x++;
			sphere.rotation().y++;
			
			Object3dContainer fruit = fruits[i];
			fruit.position().z += .25;
			fruit.rotation().x++;
			fruit.rotation().y++;
			
			//if(sphere.position().z)
			if (sphere.position().z > -sphere.getRadius()*2)
			{
				sphere.texturesEnabled(false);
			}
			if (sphere.position().z > scene.camera().position.z)
			{
				sphere.texturesEnabled(true);
				sphere.textures().addById("barong");
				scene.removeChild(spheres[i]);
				scene.removeChild(fruits[i]);
				
				addFruit(spheres,fruits,i);
				//lives --;
			}
			if(lives == 0)
			{
				Intent x=new Intent(this, GameOver.class);
				startActivity(x);
			}
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		
		// low-pass filter to make the movement more stable
		mAccVals.x = (float) (-event.values[1] * FILTERING_FACTOR + mAccVals.x * (1.0 - FILTERING_FACTOR));
		mAccVals.y = (float) (event.values[0] * FILTERING_FACTOR + mAccVals.y * (1.0 - FILTERING_FACTOR));
		
		scene.camera().position.x = mAccVals.x * .2f;
        scene.camera().position.y = mAccVals.y * .2f;
        
        scene.camera().target.x = -scene.camera().position.x;
        scene.camera().target.y = -scene.camera().position.y;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized(model){
		int action = event.getAction();
		
		
		
		if (action == MotionEvent.ACTION_DOWN) {
	    	  //Log.d("Touched", "Tapped");
	    	  
	    	  double touchedX=event.getX();
		      double touchedY=event.getY();
		      //double deviceWidth
		      
		      //Log.d("Positions","" + touchedX + "," + touchedY );
		
		     Color4 planeColor = new Color4(255, 255, 255, 255);
		      Box box = new Box(1.3f,1.3f,1.3f);

		      box.textures().addById("barong");
		      
		     box.isVisible(false);
		   		
		   		
		   		//gets the approximate x location
		   		int importantLength= Math.round(1280/11);
		   		//int importantRange=(int) Math.round((touchedX*11)/1280);
		   		int importantValue= (int) Math.round(touchedX/importantLength);
		   		
		   		//go through pX
		   		/*
		   		for(int i=0; i<pX.size(); i++)
		   		{
		   			if(pX.get(i)==importantValue);
		   			{
		   				//Log.d("WOOT"," the value:"+importantValue);
		   				//box.position().x = importantValue-5;
		   				//box.position().x= (touchedX/128)-5;
		   			}
		   		}
		   		*/
		   		box.position().x= (float) ((touchedX/(screenWidth/10))-5);
		   		//rectangle.position().y =   9;
		   		//box.rotation().y = 180;
		   		box.position().y= (float)((-touchedY/(screenWidth/10))+2.5);
		   				   		
		   		box.position().z = 0;
		   		box.lightingEnabled(false);
		   		//box.textures().addById("wood");
		   		
		   		scene.addChild(box);

		   		
		   		//Checks for collision on creation
		   		for(int x = 0; x < spheres.length; x++)
		   		{
		   			//Log.d("Position sphere", ""+ spheres[x].position().x );
		   			collided(spheres[x], fruits[x] ,box);
		   		}
		   		
		   		
	           return true;
	           }
		return true;
		}
	}
	public void collided(Sphere s, Object3dContainer f, Box b)
	{
		Vertice NW = new Vertice((b.position().x -0.5f),(b.position().y +0.5f),(b.position().z -2f));
		Vertice NE = new Vertice((b.position().x +0.5f),(b.position().y +0.5f),(b.position().z -2f));
		Vertice SW = new Vertice((b.position().x -0.5f),(b.position().y -0.5f),(b.position().z -2f));
		Vertice SE = new Vertice((b.position().x +0.5f),(b.position().y -0.5f),(b.position().z -2f));
		Vertice center = new Vertice((b.position().x),(b.position().y),(b.position().z));

		Vertice[] vertices = {center};
			
		boolean collided=false;
		for(int i=0;i<vertices.length && !collided;i++)
		{
			if(s.position().x + s.getRadius() >= vertices[i].x && s.position().x - s.getRadius() <= vertices[i].x)
			{
				if(s.position().y + s.getRadius() >= vertices[i].y && s.position().y - s.getRadius() <= vertices[i].y)
				{
					if(s.position().z + s.getRadius()*2 >= vertices[i].z && s.position().z - s.getRadius()*2 <= vertices[i].z)
					{
						collided = true;
					}
				}
			}
		}
		//Remove each object from their list of objects this will eventually be changed
		if(collided)
		{
			//The Game is Over  
			if(f == bombModel)
			{
				lives = 0;
			}
			//Log.d("X position", ""+ b.position().x + " " + s.position().x+ " " + s.getRadius());
			scene.removeChild(b);
			scene.removeChild(f);
			scene.removeChild(s);
		}
	}
	public void addFruit(Sphere[] spheres, Object3dContainer[] fruits, int i)
	{
		Sphere sphere = new Sphere(2,15,15);
		sphere.position().x = (float) (-4 + ( Math.random() * 8));
		sphere.position().y = (float) (-3 + ( Math.random() * 6));
		sphere.position().z = (float) (-25 + ( Math.random() * 7));

		sphere.vertexColorsEnabled(false);
		//sphere.textures().addById("barong");
		//sphere.vertexColorsEnabled(false);
		sphere.isVisible(false);
		spheres[i] = sphere;

   		int randFruit = random.nextInt(4);   		   		
   		
   		Object3dContainer FruitModel;
   		
   		if(randFruit == 0)
   		{
   			FruitModel = appleModel.clone();  		
   		}
   		else if(randFruit == 1)
   		{
   			FruitModel = waterModel.clone();  		
   		}
   		else if(randFruit == 2)
   		{
   			FruitModel = coconutModel.clone();  		
   		}
   		else
   		{
   			FruitModel = bananaModel.clone();  
   		}
		   			   			
		  FruitModel.scale().x = FruitModel.scale().y = FruitModel.scale().z = .4f;
		  FruitModel.position().x = sphere.position().x;
		  FruitModel.position().y = sphere.position().y;
		  FruitModel.position().z = sphere.position().z;
		
		  fruits[i] = FruitModel;
		  
	   	  scene.addChild(sphere);
		  scene.addChild(FruitModel);
	}
}