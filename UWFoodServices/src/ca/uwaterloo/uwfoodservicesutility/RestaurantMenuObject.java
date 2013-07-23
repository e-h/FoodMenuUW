package ca.uwaterloo.uwfoodservicesutility;

public class RestaurantMenuObject {
	
	int id;
	String restaurant_name;
	String location;
	Integer image;
	DailyMenu[] menu;
	
	public RestaurantMenuObject(int id, String restaurant_name, String location, Integer image, DailyMenu[] menu){
		this.id = id;
		this.restaurant_name = restaurant_name;
		this.location = location;
		this.image = image;
		this.menu = menu;
	}
	
	public int getID(){
		return id;
	}
	
	public String getRestaurant(){
		return restaurant_name;
	}
	
	public String getLocation(){
		return location;
	}
	
	public Integer getImage(){
		return image;
	}
	
	public DailyMenu[] getMenu(){
		return menu;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setRestaurant(String restaurant_name){
		this.restaurant_name = restaurant_name;
	}

	public void setLocation(String location){
		this.location = location;
	}
	
	public void setTimings(Integer image){
		this.image = image;
	}
	
	public void setMenu(DailyMenu[] menu){
		this.menu = menu;
	}

}
