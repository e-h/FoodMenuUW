<?php

$response = array();

if(isset($_POST['restaurant']) && isset($_POST['day']) && isset($_POST['meal']) && isset($_POST['item'])){

	$restaurant = mysql_real_escape_string($_POST['restaurant']);
	$day = mysql_real_escape_string($_POST['day']);
	$meal = mysql_real_escape_string($_POST['meal']);
	$pid = mysql_real_escape_string($_POST['item']);

	require_once __DIR__ . '/db_connect.php';

	$db = new DB_CONNECT();

	if(isset($_POST['product'])){
		$product = mysql_real_escape_string($_POST['product']);
		$result = mysql_query("INSERT INTO restaurantMenu(restaurant,day,meal,item,product) VALUES('$restaurant', '$day','$meal', '$pid', 			'$product')");
	}
	else{
		$result = mysql_query("INSERT INTO restaurantMenu(restaurant,day,meal,item) VALUES('$restaurant', '$day','$meal', '$pid')");
	}
	
	if($result){
		$response['success'] = 1;
		$response['message'] = "Created Row";
	
		echo json_encode($response);
		return json_encode($response);
	
	}
	else{
		$response["success"] = 0;
		$response["message"] = "Error inserting.";
		echo json_encode($response);
		return json_encode($response);
	}
}
else{
	$response["success"] = 0;
    	$response["message"] = "Missing parameters";
 
    	echo json_encode($response);
	return json_encode($response);
}




?>
