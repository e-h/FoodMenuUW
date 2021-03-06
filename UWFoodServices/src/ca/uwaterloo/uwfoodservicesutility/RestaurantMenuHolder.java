package ca.uwaterloo.uwfoodservicesutility;

import java.util.ArrayList;

import android.app.Application;
import android.util.Log;

public class RestaurantMenuHolder extends Application{

    private static RestaurantMenuHolder mInstance = null;
    private static ArrayList<RestaurantMenuObject> restaurantMenu;

    public synchronized static RestaurantMenuHolder getInstance(ArrayList<RestaurantMenuObject> restaurantMenu){
        if(mInstance == null){
            mInstance = new RestaurantMenuHolder(restaurantMenu);
        }
        return mInstance;
    }

    public static RestaurantMenuHolder getInstance(){
        if(mInstance == null){
            mInstance = new RestaurantMenuHolder(restaurantMenu);
        }
        return mInstance;
    }

    private RestaurantMenuHolder(ArrayList<RestaurantMenuObject> restaurantMenu){
        RestaurantMenuHolder.restaurantMenu = restaurantMenu;
    }

    public int getCount(){
        return restaurantMenu.size();
    }

    public ArrayList<RestaurantMenuObject> getRestaurantMenu() {
        return restaurantMenu;
    }

}
