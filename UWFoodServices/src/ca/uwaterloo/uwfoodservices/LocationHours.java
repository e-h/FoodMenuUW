package ca.uwaterloo.uwfoodservices;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ca.uwaterloo.uwfoodservices.MenuLists.MenuFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class LocationHours extends SlidingMenus implements ActionBar.TabListener{

	ViewPager vp;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_hours);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
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
		
		for (int i = 0; i < 2; i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(MenuAdapter.location_tabs[i])
					.setTabListener(this));
		}

		vp.setCurrentItem(0);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		vp.setCurrentItem(tab.getPosition());
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
	
	public static class MenuAdapter extends FragmentPagerAdapter {

		private ArrayList<MenuFragment> mFragments;

		public final static String[] location_tabs = new String[] {"ListView", "MapView"};

		public MenuAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<MenuFragment>();
			for (int i = 0; i < location_tabs.length; i++)
				mFragments.add(new MenuFragment());
		}

		@Override
		public int getCount() {
			return location_tabs.length;
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0){
				Fragment fragment = new ListViewFragment();
				Bundle args = new Bundle();
				args.putInt(MenuFragment.ARG_SECTION_NUMBER, position);
				fragment.setArguments(args);
				return fragment;		
			}
			else{
				Fragment fragment = new MyMapFragment();
				Bundle args = new Bundle();
				args.putInt(MenuFragment.ARG_SECTION_NUMBER, position);
				fragment.setArguments(args);
				return fragment;
			}
			
		}

	}
	
public static class MyMapFragment extends Fragment {
		
		public static final String ARG_SECTION_NUMBER = "section_number";
		GoogleMap myMap;
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = null;
			  try {
			        view = inflater.inflate(
							R.layout.fragment_map, container, false);
			          SupportMapFragment mySupportMapFragment 
			           = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
			             myMap = mySupportMapFragment.getMap();   
			             
			             if(myMap != null){
			            	 myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			             }
			             
			    } catch (InflateException e) {}
			 return view;
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

}