package min3d.sampleProject1;


import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;


/**
 * Main menu activity
 * 
 * @author Lee
 */
public class SplashActivity extends ListActivity  
{
	private final int CONTEXTID_VIEWFILE = 0;
	private final int CONTEXTID_CANCEL = 1;
	
	private String _baseSourcePath = "http://code.google.com/p/min3d/source/browse/trunk/sampleProjects/min3dSampleProject1/src/min3d/sampleProject1/"; 
	
	class ItemVo
	{
		public String filename;
		public Class<?> cls;
		public String label;

		public ItemVo(String $label, Class<?> $class, String $filename)
		{
			label = $label;
			cls = $class;
			filename = $filename;
		}
	}	
	
	private ItemVo[] _items = {
			new ItemVo("Fog Example", GameModel.class, "ExampleFog.java"),
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	String[] strings = new String[_items.length];
    	for (int i = 0; i < _items.length; i++) {
    		strings[i] = _items[i].label;
    	}
    	
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
	    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings));
	    
	    TextView tv = (TextView) this.findViewById(R.id.splashTitle);
	    Linkify.addLinks(tv, 0x07);
	    Music.create(this, R.raw.chinatown);
        Music.setLooping(this, R.raw.chinatown);
        Music.start(this);
	    
	    registerForContextMenu(getListView());	    
	    
	    // TEST ONLY:
    	// this.startActivity( new Intent(this, ExampleTransparentGlSurface.class ) );
    }
    @Override
    protected void onPause()
    {
    	super.onPause();
    	Music.stop(this);
    }
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	Music.stop(this);
    }
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
    	this.startActivity( new Intent(this, _items[position].cls ) );
    }
    
    //
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);

        int i = 0;
        menu.add(0, 0, i++, "project home");
        menu.add(0, 1, i++, "author blog");

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	Intent i;
    	
        switch (item.getItemId()) 
        {
            case 0:
            	i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse( this.getResources().getString(R.string.projectUrl) ));
            	startActivity(i);                
            	return true;
                
            case 1:
            	i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse( this.getResources().getString(R.string.myBlogUrl) ));
            	startActivity(i);                
            	return true;
        }
        return false;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXTID_VIEWFILE, 0, "View source on Google Code");
		menu.add(0, CONTEXTID_CANCEL, 0, "Cancel");
    }

    @Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) 
		{
			case CONTEXTID_VIEWFILE:
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	String url = _baseSourcePath + _items[ (int)info.id ].filename;
            	i.setData(Uri.parse(url));
            	startActivity(i);                
				return true;
			case CONTEXTID_CANCEL:
				// do nothing
				return true;
				
			default:
				return super.onContextItemSelected(item);
		}
	}    
}
