package ca.uwaterloo.uwfoodservices;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import ca.uwaterloo.uwfoodservicesutility.ParseMenuData;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class SplashScreen extends Activity {
	
	static ParseLocationData locationParser;
	static ParseMenuData menuParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Register BroadcastReceiver to track connection changes.
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		this.registerReceiver(receiver, filter);
		
		// Gets the user's network preference settings
        Log.d("yes" + "", "network");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        Log.d("yes4" + "", "network");
        sPref = sharedPrefs.getString("connection_type_preference", "Both Wi-Fi and Data");
        
        Log.d("yes5" + "", "network");
        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page instead of the menu content.
        /*
        if (refreshDisplay) {
            loadPage();
        }*/
		
		setContentView(R.layout.activity_splash_screen);
		
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
	
    @Override
    public void onStart() {
        super.onStart();
    }

	private static class LocationList extends AsyncTask<String, Void, JSONObject[]> {


		@Override
		protected JSONObject[] doInBackground(String... urls) {
			Log.d("Url", urls[0]);
			JSONParser json_parse = new JSONParser();
			JSONObject[] jsonObjectArray = new JSONObject[2];
			jsonObjectArray[0] = json_parse.getJSONFromUrl(urls[0]);
			jsonObjectArray[1] = json_parse.getJSONFromUrl(urls[1]);
			return jsonObjectArray;
		}
		
		@Override
        protected void onPostExecute(JSONObject[] jObjArray) {
			if(jObjArray != null){
				menuParser.Parse(jObjArray[0]);	
				locationParser.Parse(jObjArray[1]);	
			}
			else{
				Log.d("Object is null", "Null");
			}
			
       }
		
	}
	
	// Date Handling
	//======================================================
	public static String formattedDate;
    static int weekDay;
    static Calendar calendar;
    static SimpleDateFormat simpleDateFormat;
    
    public String getDatedMenuUrl() {
    	// Date handling
		calendar = Calendar.getInstance();
		Log.d(calendar.getTime() + "", "current time");
		
		simpleDateFormat = new SimpleDateFormat("MMMMMMMMM dd");
		formattedDate = simpleDateFormat.format(calendar.getTime());
		
		String weekInYear = (new SimpleDateFormat("w")).format(calendar.getTime());

		Log.d(formattedDate + "", "current time - formmated");
		
		weekDay = 0;
		if (calendar.getTime().toString().split(" ")[0].equals("Mon")) { weekDay = 0; }
		if (calendar.getTime().toString().split(" ")[0].equals("Tue")) { weekDay = 1; }
		if (calendar.getTime().toString().split(" ")[0].equals("Wed")) { weekDay = 2; }
		if (calendar.getTime().toString().split(" ")[0].equals("Thu")) { weekDay = 3; }
		if (calendar.getTime().toString().split(" ")[0].equals("Fri")) { weekDay = 4; }
		if (calendar.getTime().toString().split(" ")[0].equals("Sat")) { weekDay = 5; }
		if (calendar.getTime().toString().split(" ")[0].equals("Sun")) { weekDay = 6; }
		
		Log.d(Integer.parseInt(weekInYear) + "", "current time - weekInYear");
		
		return "http://api.uwaterloo.ca/public/v2/foodservices/2013/" + Integer.parseInt(weekInYear) + "/menu.json?key=98bbbd30b3e4f621d9cb544a790086d6";
    }
	
	// Network Verification
	//======================================================
	public static final String WIFI = "Wi-Fi Only";
    public static final String BOTH = "Both Wi-Fi and Data";
	
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static String sPref = null;
    
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    
    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Checks the user prefs and the network connection. Based on the result, decides
            // whether to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                refreshDisplay = true;
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile), sets refreshDisplay to true.
            } else if (BOTH.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else {
                refreshDisplay = false;
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null) {
        	Log.d(activeInfo.isConnected() + "", "network connected");
        } else {
        	Log.d(null + "", "network connected");
        }
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }
    
    // Uses AsyncTask subclass to download the JSON data. This avoids UI lock up. 
    // To prevent network operations from causing a delay that results in a poor 
    // user experience, always perform network operations on a separate thread from the UI.
    private void loadData() {
    	Log.d(sPref, "network");
    	Log.d(BOTH, "network");
    	Log.d(wifiConnected + "", "network");
    	Log.d(mobileConnected + "", "network");
        if (((sPref.equals(BOTH)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
        	// Load Data
        	Log.d("yes3" + "", "network");
        	menuParser = new ParseMenuData();
    		locationParser = new ParseLocationData(this);
    		
    		String urlLocations = "http://api.uwaterloo.ca/public/v1/?key=4aa5eb25c8cc979600724104ccfb70ea&service=FoodServices&output=json";
    		String urlMenu = getDatedMenuUrl();
    		
    		Log.d(urlMenu + "", "menuurl");
    		
    		Intent intent = new Intent(this, MainScreen.class);
    		new LocationList().execute(urlMenu, urlLocations);
    		startActivity(intent);
    		
        } else {
        	Log.d("yes" + "", "network");
            showErrorPage();
            Log.d("yes1" + "", "network");
        }
    }
    
    // Displays an error if the app is unable to load content.
    private void showErrorPage() {
        //setContentView(R.layout.activity_menu_lists);
    	Log.d("yes2" + "", "network");
        // The specified network connection is not available. Displays error message.
        // Show: "Unable to load content. Check your network connection."
    }
}
