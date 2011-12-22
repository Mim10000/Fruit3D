package min3d.sampleProject1;

import java.util.ArrayList;
import java.util.Random;



import min3d.Shared;
import min3d.Utils;
import min3d.core.Object3d;
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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameModel extends RendererActivity implements SensorEventListener {

	private ArrayList<Sphere> spheres;
	private ArrayList<Object3dContainer> fruits;

	private Object3dContainer[] livesContainer = new Object3dContainer[3];
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Number3d mAccVals;

	private final float FILTERING_FACTOR = .3f;
	private GameModel model;

	private int lives = 3;
	private int index = 0;

	Display display;
	int screenWidth;
	int screenHeight;
	Random random = new Random();

	Object3dContainer appleModel;
	Object3dContainer waterModel;
	Object3dContainer coconutModel;
	Object3dContainer bananaModel;
	Object3dContainer bombModel;

	private int score = 0;
	private int combo = 0;

	View ll;
	TextView scoreView;
	Object3dContainer gunModel;
	
	SoundManager sManager;
		
	//Bitmap[] numbers = new Bitmap[10];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		sManager = new SoundManager();
		sManager.initSounds(this);
		sManager.addSound(1,R.raw.gunfire);  
		sManager.addSound(2,R.raw.explosion); 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// ImageView view = (ImageView) findViewById(R.id.imageView);
		// view.setOnTouchListener(this);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAccVals = new Number3d();
		display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();

		ll = new LinearLayout(this);

		scoreView = new TextView(ll.getContext());
		scoreView.setText(score+"");
		scoreView.setTextColor(Color.BLUE);
		scoreView.setTextSize(60);
		
		((ViewGroup) ll).addView(scoreView);
		((LinearLayout) ll).setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.RIGHT | Gravity.TOP);
		this.addContentView(ll, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		ll.setVisibility(LinearLayout.VISIBLE);
		ll.setFocusable(true);
		
		


	/*	numbers[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.zero);
		numbers[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.one);
		numbers[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.two);
		numbers[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.three);
		numbers[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.four);
		numbers[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.five);
		numbers[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.six);
		numbers[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.seven);
		numbers[8] = BitmapFactory.decodeResource(getResources(),
				R.drawable.eight);
		numbers[9] = BitmapFactory.decodeResource(getResources(),
				R.drawable.nine); */

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

		spheres = new ArrayList<Sphere>();
		fruits = new ArrayList<Object3dContainer>();

		// Creates Parser for all of the ObjectModels 
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				getResources(), "min3d.sampleProject1:raw/apple_obj", true);
		parser.parse();
		appleModel = parser.getParsedObject();

		parser = Parser.createParser(Parser.Type.OBJ, getResources(),
				"min3d.sampleProject1:raw/watermelon_obj", true);
		parser.parse();
		waterModel = parser.getParsedObject();

		parser = Parser.createParser(Parser.Type.OBJ, getResources(),
				"min3d.sampleProject1:raw/coconut_obj", true);
		parser.parse();
		coconutModel = parser.getParsedObject();

		parser = Parser.createParser(Parser.Type.OBJ, getResources(),
				"min3d.sampleProject1:raw/banana1_obj", true);
		parser.parse();
		bananaModel = parser.getParsedObject();

		parser = Parser.createParser(Parser.Type.OBJ, getResources(),
				"min3d.sampleProject1:raw/bomb_obj", true);
		parser.parse();
		bombModel = parser.getParsedObject();
		
		parser = Parser.createParser(Parser.Type.OBJ, getResources(),
				"min3d.sampleProject1:raw/gun_obj", true);
		parser.parse();
		gunModel = parser.getParsedObject();

		// Starts the game with 4 Fruits
		for (int i = 0; i < 4; i++) {
			addFruit();
		}

		
		//Initializes the guns as the life bar and puts them in them in the top left
		for(int i=0; i<livesContainer.length; i++)
		{
			livesContainer[i] = gunModel.clone();
			livesContainer[i].scale().x = livesContainer[i].scale().y = livesContainer[i].scale().z = .05f;
			
			//NEEDS TO BE PUT INTO SCREEN COORDINATES FOR SCALING
			livesContainer[i].position().x = (-4.5f)+(i*1);
			livesContainer[i].position().y = (2.5f);
			
			livesContainer[i].position().z = (0);
			scene.addChild(livesContainer[i]);
		}
		
		Color4 planeColor = new Color4(255, 255, 255, 255);
		Rectangle east = new Rectangle(40, 12, 2, 2, planeColor);
		Rectangle west = new Rectangle(40, 12, 2, 2, planeColor);
		Rectangle up = new Rectangle(40, 12, 2, 2 , planeColor);
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

		scene.fogColor(new Color4(0, 0, 0, 255));
		scene.fogNear(10);
		scene.fogFar(40);
		scene.fogEnabled(true);

		Rectangle rectangle = new Rectangle(4, 12, 2, 2, planeColor);

		rectangle.position().x = 0;
		rectangle.position().y = 0;
		rectangle.rotation().y = 180;
		rectangle.position().z = 0;
		rectangle.lightingEnabled(false);
		rectangle.textures().addById("wood");

		// scene.addChild(rectangle); 

		// mSensorManager.registerListener((SensorEventListener) this,
		// mAccelerometer, SensorManager.SENSOR_DELAY_UI);

	}

	@Override
	public void updateScene() {
		for (int i = 0; i < spheres.size(); i++) {
			Sphere sphere = (Sphere) spheres.get(i);
			sphere.position().z += .25;

			Object3dContainer fruit = (Object3dContainer) fruits.get(i);
			fruit.position().z += .25;
			fruit.rotation().x++;
			fruit.rotation().y++;

			// if(sphere.position().z)
			/*
			 * if (sphere.position().z > -sphere.getRadius()*2) {
			 * sphere.texturesEnabled(false); }
			 */
			
			//When the fruit goes past the camera removes it from the Arraylist, adds a new fruit to the list and decrements lives
			if (sphere.position().z > scene.camera().position.z) {
				// sphere.texturesEnabled(true);
				scene.removeChild((Object3d) spheres.get(i));
				scene.removeChild((Object3d) fruits.get(i));
				spheres.remove(i);
				fruits.remove(i);

				// Restocks the fruit supply to full each round
				for (int x = 0; x < 4; x++) {
					if (spheres.size() < 4) {
						addFruit();
					}
				}
				//Only decrements lives when the fruit is not a bomb
				if (!fruits.get(i).isBomb) {
					lives--;
					// Resets the combo meter
					combo = 0;
										
					if (lives == 2)
					{	
						scene.removeChild(livesContainer[2]);
					}	
					if (lives == 1)
					{	
						scene.removeChild(livesContainer[1]);
					}	
				}
			}
			if (lives <= 0) 
			{
				this.finish();
				Intent x = new Intent(this, GameOver.class);
				startActivity(x);
			}
		}
		//Rotates Lives
		for(int i=0; i<livesContainer.length;i++)
		{
			livesContainer[i].rotation().x += 1f;
			livesContainer[i].rotation().y += 1f;
			livesContainer[i].rotation().z += 1f;
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
			mAccVals.x = (float) (-event.values[1] * FILTERING_FACTOR + mAccVals.x
					* (1.0 - FILTERING_FACTOR));
		mAccVals.y = (float) (event.values[0] * FILTERING_FACTOR + mAccVals.y
				* (1.0 - FILTERING_FACTOR));

		scene.camera().position.x = mAccVals.x * .2f;
		scene.camera().position.y = mAccVals.y * .2f;

		scene.camera().target.x = -scene.camera().position.x;
		scene.camera().target.y = -scene.camera().position.y;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (model) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_DOWN) {
				// Log.d("Touched", "Tapped");

				double touchedX = event.getX();
				double touchedY = event.getY();
				// double deviceWidth

				// gets the approximate x location
				//int importantLength = Math.round(1280 / 11);
				// int importantRange=(int) Math.round((touchedX*11)/1280);
				//int importantValue = (int) Math.round(touchedX
				// importantLength);

				// go through pX
				/*
				 * for(int i=0; i<pX.size(); i++) {
				 * if(pX.get(i)==importantValue); {
				 * //Log.d("WOOT"," the value:"+importantValue);
				 * //box.position().x = importantValue-5; //box.position().x=
				 * (touchedX/128)-5; } }
				 */
				Vertice ray = new Vertice(
						(float) ((touchedX / (screenWidth / 10)) - 5),
						(float) ((-touchedY / (screenWidth / 10)) + 2.5), 0);

				// Checks for collision on creation
				//for (int x = 0; x < spheres.size(); x++) {
					// Log.d("Position sphere", ""+ spheres[x].position().x );
					collided(spheres, fruits, ray);
				//}

				return true;
			}
			return false;
		}
	}

	/**
	 * Takes as parameters the ArrayList of fruits and the ray that was generated when the user touches a place on the screne.  Uses ray to sphere collision
	 * 
	 */
	public void collided(ArrayList<Sphere> spheres,
			ArrayList<Object3dContainer> fruits, Vertice ray) {
		boolean collided = false;

		//Log.d("X", "X: " + ray.x);
		//Log.d("Y", "Y: " + ray.y);
		
		float x1 = 0;
		float y1  = 0;
		
		
		for (int i = 0; i < spheres.size() && collided == false; i++) {
			//Log.d("camera position", "" + scene.camera().position.z);
			//Camera position is 10
			
			//Deals with the accuracy of ray
			x1 = ray.x*((spheres.get(i).position().z + scene.camera().position.z) / scene.camera().position.z);
			y1 = ray.y*((spheres.get(i).position().z + scene.camera().position.z) / scene.camera().position.z);
						
			float radius2 = (float) Math.pow(spheres.get(i).getRadius(),2);
			float distance = ((float)(Math.pow((x1-spheres.get(i).position().x),2)) + ((float) Math.pow((y1-spheres.get(i).position().y),2)));
			
			if(distance <= radius2)
			{
				sManager.playSound(1);  
				collided = true;
			}
			// Remove each object from their list of objects this will
			// eventually be changed
			if (collided) {
				// The Game is Over + Bomb Noise
				if (fruits.get(i).isBomb) 
				{
					sManager.playSound(2);
					lives = 0;
				}
				// Log.d("X position", ""+ b.position().x + " " +
				// s.position().x+ " " + s.getRadius());
				scene.removeChild((Object3d) fruits.get(i));
				scene.removeChild((Object3d) spheres.get(i));
				spheres.remove(i);
				fruits.remove(i);
				addFruit();
				collided = false;

				combo += 100;
				score += combo;
				
				scoreView.setText(score+"");
								
			}
		}
	}
	/**
	 *   Adds a fruit to the scene with a sphere around it that will act like a hitbox
	 */
	public void addFruit() {
		
		//Randomly generates the placement of where the sphere will be added to the scene
		Sphere sphere = new Sphere(2, 15, 15);
		sphere.position().x = (float) (-4 + (Math.random() * 8));
		sphere.position().y = (float) (-3 + (Math.random() * 6));
		sphere.position().z = (float) (-25 + (Math.random() * 7));

		sphere.vertexColorsEnabled(false);
		// sphere.vertexColorsEnabled(false);
		sphere.isVisible(false);

		int randFruit = random.nextInt(5);

		Object3dContainer FruitModel;

		//Randomly generates which type of fruit will be added to the scene
		if (randFruit == 0) {
			FruitModel = appleModel.clone();
		} else if (randFruit == 1) {
			FruitModel = waterModel.clone();
		} else if (randFruit == 2) {
			FruitModel = coconutModel.clone();
		} else if (randFruit == 3) {
			FruitModel = bombModel.clone();
			FruitModel.isBomb = true;
		} else {
			FruitModel = bananaModel.clone();
		}

		//Matches the placement of the fruit with the sphere that was added to spheres
		FruitModel.scale().x = FruitModel.scale().y = FruitModel.scale().z = .4f;
		FruitModel.position().x = sphere.position().x;
		FruitModel.position().y = sphere.position().y;
		FruitModel.position().z = sphere.position().z;

		spheres.add(sphere);
		fruits.add(FruitModel);

		scene.addChild(sphere);
		scene.addChild(FruitModel);
		
	}
}