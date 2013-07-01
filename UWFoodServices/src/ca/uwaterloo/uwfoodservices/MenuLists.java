package ca.uwaterloo.uwfoodservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.content.Intent;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MenuLists extends SlidingMenus implements ActionBar.TabListener{
	

	// Network Verification
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
    
    ViewPager vp;
    String restaurant_selection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_lists);
		
		Intent intent = getIntent();
		restaurant_selection = intent.getStringExtra("Restaurant Name");
		
		Log.d("Restaurant Selected", restaurant_selection);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.setTitle(restaurant_selection);
		actionBar.setDisplayUseLogoEnabled(false);

		vp = (ViewPager) findViewById(R.id.pager);
		vp.setAdapter(new MenuAdapter(getSupportFragmentManager()));

		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					break;
				default:
					getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
					break;
				}
			}

		});
		
		vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		for (int i = 0; i < 7; i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(MenuAdapter.days[i])
					.setTabListener(this));
		}

		vp.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
		/*try {
			dwt = new DownloadWebpageTask().execute().get();
			//Log.d(dwt.get(2).get(0), "result3");
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
		// Register BroadcastReceiver to track connection changes.
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		this.registerReceiver(receiver, filter);
	}
	
	// Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("connection_type_preference", "Both Wi-Fi and Data");

        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page instead of the menu content.
        if (refreshDisplay) {
            loadPage();
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();

    	IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        
        updateConnectedFlags();
        Log.d(refreshDisplay + "", "network refresh resume");
        if (refreshDisplay) {
            loadPage();
        }
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();

    	IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        
        updateConnectedFlags();
        Log.d(refreshDisplay + "", "network refresh restart");
        if (refreshDisplay) {
            loadPage();
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
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
    private void loadPage() {
    	Log.d(sPref, "network");
    	Log.d(BOTH, "network");
    	Log.d(wifiConnected + "", "network");
    	Log.d(mobileConnected + "", "network");
        if (((sPref.equals(BOTH)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
        	try {
				dwt = new DownloadWebpageTask().execute().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            showErrorPage();
        }
    }
    
    // Displays an error if the app is unable to load content.
    private void showErrorPage() {
        setContentView(R.layout.activity_menu_lists);

        // The specified network connection is not available. Displays error message.
        // Show: "Unable to load content. Check your network connection."
    }
    
    // Populates the activity's options menu.
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    */
    // Handles the user's menu selection.
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.settings) {
			Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		} else if (itemId == R.id.settingstest) {
			Intent settingsActivityTest = new Intent(getBaseContext(), SettingsActivityTest.class);
			startActivity(settingsActivityTest);
			return true;
		} else if (itemId == R.id.refresh) {
			loadPage();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    */
	
	private static String url = "http://api.uwaterloo.ca/public/v2/foodservices/2013/26/menu.json?key=98bbbd30b3e4f621d9cb544a790086d6";
	private static final String TAG_META = "meta";
	private static final String TAG_MESSAGE = "message";
	
	private static final String TAG_DATA = "data";
	private static final String TAG_OUTLETS = "outlets";
	//private static final String TAG_BONAPPETIT = "BonAppetit";
	private static final String TAG_MENU = "menu";
	
	private static final String[] TAG_DAYS = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	
	private static final String TAG_MEALS = "meals";
	private static final String TAG_LUNCH = "lunch";
	private static final String TAG_DINNER = "dinner";
	
	private static final String TAG_PRODUCT_NAME = "product_name";
	private static final String TAG_PRODUCT_ID = "product_id";
	private static final String TAG_DIET_TYPE = "diet_type";
	private static final String TAG_RESULT = "result";
	
	//public final static String ITEM_TITLE = "title";
	//public final static String ITEM_CAPTION = "caption";

	static ArrayList<ArrayList<ArrayList<String>>> dwt;
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, ArrayList<ArrayList<ArrayList<String>>>> {
		
		public ArrayList<ArrayList<ArrayList<String>>> ParseDay() throws IOException, JSONException {
			
			// menuResult data structure:
			// 
			//             | mondayLunch      
			//             | mondayDinner     | Item #1      | product_name
			//             | tuesdayLunch ----| Item #2 -----| product_id
			//             | tuesdayDinner    | Item #3      | diet_type
			// menuResult -| wednesdayLunch
			//             | wednesdayDinner
			//             |     .
			//             |     .
			//             |     .
			
			ArrayList<ArrayList<ArrayList<String>>> menuResult = new ArrayList<ArrayList<ArrayList<String>>>();
			ArrayList<ArrayList<String>> mondayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> mondayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> tuesdayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> tuesdayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> wednesdayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> wednesdayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> thursdayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> thursdayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> fridayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> fridayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> saturdayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> saturdayDinner = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> sundayLunch = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> sundayDinner = new ArrayList<ArrayList<String>>();
			
			menuResult.add(mondayLunch);
			menuResult.add(mondayDinner);
			menuResult.add(tuesdayLunch);
			menuResult.add(tuesdayDinner);
			menuResult.add(wednesdayLunch);
			menuResult.add(wednesdayDinner);
			menuResult.add(thursdayLunch);
			menuResult.add(thursdayDinner);
			menuResult.add(fridayLunch);
			menuResult.add(fridayDinner);
			menuResult.add(saturdayLunch);
			menuResult.add(saturdayDinner);
			menuResult.add(sundayLunch);
			menuResult.add(sundayDinner);
			
			String TAG_RESTAURANT = "BonAppetit";
			
			// Creating JSON Parser instance
			JSONParser jParser = new JSONParser();
					
			// Getting JSON string from URL
			JSONObject json = jParser.getJSONFromUrl(url);
			
			JSONObject meta = json.getJSONObject(TAG_META);
			
			//JSONObject message = meta.getJSONObject(TAG_MESSAGE);
			Log.d(meta.getString("message"), "message");
			
			JSONObject data = json.getJSONObject(TAG_DATA);
			JSONArray outlets = data.getJSONArray(TAG_OUTLETS);
			
			JSONObject Bon_Appetit = outlets.getJSONObject(1);
			JSONArray menu = Bon_Appetit.getJSONArray(TAG_MENU);
			
			JSONObject day;
			JSONObject meals;
			JSONArray lunch;
			JSONArray dinner;
			String product_name;
			String product_id;
			String diet_type;
			
			for (int i = 0; i < menuResult.size(); i++) {
				menuResult.get(i).add(new ArrayList<String>());
				menuResult.get(i).get(0).add("");
			}
			
			String weekDay;
			int position;
			
			for (int i = 0; i < menu.length(); i ++) {
				
				position = i;
				
				day = menu.getJSONObject(i);
				meals = day.getJSONObject(TAG_MEALS);
				weekDay = day.getString("day");
				
				if (weekDay == "Monday") { position = 0;}
				if (weekDay == "Tuesday") { position = 1;}
				if (weekDay == "Wednesday") { position = 2;}
				if (weekDay == "Thursday") { position = 3;}
				if (weekDay == "Friday") { position = 4;}
				if (weekDay == "Saturday") { position = 5;}
				if (weekDay == "Sunday") { position = 6;}
				
				// Lunch
				if (meals.getJSONArray(TAG_LUNCH).length() > 0) {
					lunch = meals.getJSONArray(TAG_LUNCH);
					for (int j = 0; j < lunch.length(); j ++) {
						product_name = lunch.getJSONObject(j).getString(TAG_PRODUCT_NAME);
						product_id = lunch.getJSONObject(j).getString(TAG_PRODUCT_ID);
						diet_type = lunch.getJSONObject(j).getString(TAG_DIET_TYPE);
						
						menuResult.get(2*position).add(new ArrayList<String>());
						menuResult.get(2*position).get(j).clear();
						menuResult.get(2*position).get(j).add(product_name);
						menuResult.get(2*position).get(j).add(product_id);
						menuResult.get(2*position).get(j).add(diet_type);
					}
				}
				// Dinner
				if (meals.getJSONArray(TAG_DINNER).length() > 0) {
					dinner = meals.getJSONArray(TAG_DINNER);
					for (int j = 0; j < dinner.length(); j ++) {
						product_name = dinner.getJSONObject(j).getString(TAG_PRODUCT_NAME);
						product_id = dinner.getJSONObject(j).getString(TAG_PRODUCT_ID);
						diet_type = dinner.getJSONObject(j).getString(TAG_DIET_TYPE);
					
						menuResult.get(2*position + 1).add(new ArrayList<String>());
						menuResult.get(2*position + 1).get(j).clear();
						menuResult.get(2*position + 1).get(j).add(product_name);
						menuResult.get(2*position + 1).get(j).add(product_id);
						menuResult.get(2*position + 1).get(j).add(diet_type);
					}
				}
			}
			return menuResult;
		}
		
		@Override
        protected ArrayList<ArrayList<ArrayList<String>>> doInBackground(String... params) {
            try {
            	Log.d(Integer.toString(params.length), "length");
                return ParseDay();
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<ArrayList<ArrayList<String>>> result) {
        	Log.d(result.get(2).get(0).get(0), "result2");
       }
	}
	
	public static class MenuAdapter extends FragmentPagerAdapter {

		private ArrayList<MenuFragment> mFragments;

		public final static String[] days = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

		public MenuAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<MenuFragment>();
			for (int i = 0; i < days.length; i++)
				mFragments.add(new MenuFragment());
		}

		@Override
		public int getCount() {
			return days.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new MenuFragment();
			Bundle args = new Bundle();
			args.putInt(MenuFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction arg1) {
		vp.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
	}
	
	public static class MenuFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public String day;

		public static final int HDR_POS1 = 0;
	    public static int HDR_POS2;
	    
	    List<String> LIST = new ArrayList<String>();

	    private static final Integer LIST_HEADER = 0;
	    private static final Integer LIST_ITEM = 1;
		
		public MenuFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_restaurant_menu,
					container, false);
			
			ListView listView = (ListView) rootView.findViewById(R.id.list_menu);
			listView.setAdapter(new MenuListAdapter(getActivity()));
			
			LIST.clear();
			LIST.add("LUNCH");
			
			int positionLunch = getArguments().getInt(ARG_SECTION_NUMBER)*2;
			int positionDinner = getArguments().getInt(ARG_SECTION_NUMBER)*2 + 1;
			
			if (dwt.get(positionLunch).get(0).get(0) != "") {
				Log.d(dwt.get(positionLunch) + "", "size");
				Log.d(dwt.get(positionLunch).get(0).get(0) + "", "size");
				Log.d(dwt.get(positionLunch).get(1).get(0) + "", "size");
				Log.d(dwt.get(positionLunch).get(2).get(0) + "", "size");
				for (int i = 0; i < dwt.get(positionLunch).size() - 1; i ++) {
					Log.d(i+"", "size i");
					LIST.add(dwt.get(positionLunch).get(i).get(0));
					Log.d("yes", "size");
				}
			} else {
				LIST.add("There is nothing on the menu");
			}
			
			HDR_POS2 = LIST.size();
			LIST.add("DINNER");
			
			if (dwt.get(positionDinner).get(0).get(0) != "") {
				for (int i = 0; i < dwt.get(positionDinner).size() - 1; i ++) {
					LIST.add(dwt.get(positionDinner).get(i).get(0));
				}
			} else {
				LIST.add("There is nothing on the menu");
			}
			
			/*TextView resultText = (TextView) rootView.findViewById(R.id.section_label);
			int positionLunch = getArguments().getInt(ARG_SECTION_NUMBER)*2;
			int positionDinner = getArguments().getInt(ARG_SECTION_NUMBER)*2 + 1;
			String textLunch = "Lunch: \n";
			String textDinner = "Dinner: \n";
			if (dwt.get(positionLunch).get(0).get(0) != "") {
				for (int i = 0; i < dwt.get(positionLunch).size(); i ++) {
					for (int j = 0; j < dwt.get(positionLunch).get(i).size(); j ++) {
						textLunch += dwt.get(positionLunch).get(i).get(j) + " ";
					}
					textLunch += "\n";
				}
			} else {
				textLunch += "There is nothing on the menu. \n";
			}
			if (dwt.get(positionDinner).get(0).get(0) != "") {
				for (int i = 0; i < dwt.get(positionDinner).size(); i ++) {
					for (int j = 0; j < dwt.get(positionDinner).get(i).size(); j ++) {
						textDinner += dwt.get(positionDinner).get(i).get(j) + " ";
					}
					textDinner += "\n";
				}
			} else {
				textDinner += "There is nothing on the menu. \n";
			}
			resultText.setText(textLunch + textDinner);
			*/
			/* if (dwt.get((getArguments().getInt(ARG_SECTION_NUMBER)-1) * 2).get(0) != "") {
				String resultString = dwt.get((getArguments().getInt(ARG_SECTION_NUMBER)-1) * 2).get(0) + "\n" + 
						dwt.get((getArguments().getInt(ARG_SECTION_NUMBER)-1) * 2 + 1).get(0);
				resultText.setText(resultString);
			} else {
				resultText.setText("There is nothing on the menu.");
			}
			*/
			
			return rootView;
		}
		
		@Override
		public void onResume() {
			//Log.d(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER) - 1),"result");
			super.onResume();
		}
		
		private class MenuListAdapter extends BaseAdapter {
	        public MenuListAdapter(Context context) {
	            mContext = context;
	        }

	        @Override
	        public int getCount() {
	            return LIST.size();
	        }

	        @Override
	        public boolean areAllItemsEnabled() {
	            return true;
	        }

	        @Override
	        public boolean isEnabled(int position) {
	            return true;
	        }

	        @Override
	        public Object getItem(int position) {
	            return position;
	        }

	        @Override
	        public long getItemId(int position) {
	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	            String headerText = getHeader(position);
	            if(headerText != null) {

	                View item = convertView;
	                if(convertView == null || convertView.getTag() == LIST_ITEM) {

	                    item = LayoutInflater.from(mContext).inflate(
	                            R.layout.lv_header_layout, parent, false);
	                    item.setTag(LIST_HEADER);

	                }

	                TextView headerTextView = (TextView)item.findViewById(R.id.lv_list_hdr);
	                headerTextView.setText(headerText);
	                return item;
	            }

	            View item = convertView;
	            if(convertView == null || convertView.getTag() == LIST_HEADER) {
	                item = LayoutInflater.from(mContext).inflate(
	                        R.layout.lv_layout, parent, false);
	                item.setTag(LIST_ITEM);
	            }

	            TextView header = (TextView)item.findViewById(R.id.lv_item_header);
	            header.setText(LIST.get(position % LIST.size()));

	            //Set last divider in a sublist invisible
	            View divider = item.findViewById(R.id.item_separator);
	            if(position == HDR_POS2 -1) {
	                divider.setVisibility(View.INVISIBLE);
	            }


	            return item;
	        }

	        private String getHeader(int position) {

	            if(position == HDR_POS1  || position == HDR_POS2) {
	                return LIST.get(position);
	            }

	            return null;
	        }

	        private final Context mContext;
	    }
	}
	
	public static class ListViewFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";
		private ListView listView;
		private Context context;
		
		public void onAttach(Activity activity){
	        super.onAttach(activity);
	        context = getActivity();
	      }

		 @Override
		    public void onActivityCreated(Bundle savedInstanceState) {
		     super.onActivityCreated(savedInstanceState);
		     init();
		    }
		 
		 public void init() {
		     listView.setAdapter(new ImageAdapter(context, -1));
		   }
		 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = null;
			  try {
			        view = inflater.inflate(
							R.layout.activity_restaurant_menu_list, container, false);
			        listView = (ListView) view.findViewById(R.id.list_restaurant);
			    } catch (InflateException e) {}
			 return view;
		}
	}

	@Override
	public void onTabSelected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(com.actionbarsherlock.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}

