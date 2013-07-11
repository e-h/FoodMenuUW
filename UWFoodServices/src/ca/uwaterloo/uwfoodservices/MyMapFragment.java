package ca.uwaterloo.uwfoodservices;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MyMapFragment extends Fragment implements FragmentCommunicator{
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private GoogleMap myMap = null;
	static final LatLng UW = new LatLng(43.4722, -80.5472);
	private Context context;
	
	CameraPosition camera = new CameraPosition(UW, 14, 0, 0);
	int restaurant;
	
	public MyMapFragment(){
	}
	
	@Override
	 public void onAttach(Activity activity){
	  super.onAttach(activity);
	  context = getActivity();
	  ((LocationHours)context).fragmentCommunicator = this;
	}
	
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


	@Override
	public void passDataToFragment(int position) {
	// Broken by changes to RestaurantLocationHolder. 
		/*
		String restaurant;
		
		if(position == -1){
			
			for(int i = 0; i < new RestarauntLocationHolder().restaurant_list.length; i++){	
				restaurant = new RestarauntLocationHolder().restaurant_list[i];
				Marker restaurant_location = myMap.addMarker(new MarkerOptions()
		        .position(new Coordinates().points.get(i))
		        .title(restaurant)
		        .snippet("Info Comes Here"));			
			}
			
		}
		
		else if(position == -2){
			myMap.clear();
		}
			
		else{
<<<<<<< HEAD
			
			myMap.clear();
=======
	
>>>>>>> Added show all restaurants and clear all for the map
			restaurant = new RestarauntLocationHolder().restaurant_list[position];
			Log.d("Restaurant Clicked", restaurant);
			Marker restaurant_location = myMap.addMarker(new MarkerOptions()
			.position(new Coordinates().points.get(position))
			.title(restaurant)
			.snippet("Info Comes Here"));
<<<<<<< HEAD

		String restaurant = new RestarauntLocationHolder().restaurant_list[position];
		Log.d("Restaurant Clicked", restaurant);
		Marker restaurant_location = myMap.addMarker(new MarkerOptions()
        .position(UW)
        .title(restaurant)
        .snippet("Info Comes Here"));
		restaurant_location.showInfoWindow();
=======
		
			restaurant_location.showInfoWindow();
		}
>>>>>>> Added show all restaurants and clear all for the map
=======
		
		String restaurant;
		
		if(position == -1){
			
			for(int i = 0; i < new RestarauntLocationHolder().restaurant_list.length; i++){	
				restaurant = new RestarauntLocationHolder().restaurant_list[i];
				Marker restaurant_location = myMap.addMarker(new MarkerOptions()
		        .position(new Coordinates().points.get(i))
		        .title(restaurant)
		        .snippet("Info Comes Here"));			
			}
			
		}
		
		else if(position == -2){
			myMap.clear();
		}
			
		else{
	
			myMap.clear();
			restaurant = new RestarauntLocationHolder().restaurant_list[position];
			Log.d("Restaurant Clicked", restaurant);
			Marker restaurant_location = myMap.addMarker(new MarkerOptions()
			.position(new Coordinates().points.get(position))
			.title(restaurant)
			.snippet("Info Comes Here"));
		
			restaurant_location.showInfoWindow();
		}
>>>>>>> 48b7e019bed4e982e414004e5225e28cc0382132
		
			restaurant_location.showInfoWindow();
		}
		*/
	}

}
