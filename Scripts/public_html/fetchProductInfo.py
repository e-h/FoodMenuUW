import json, urllib2, urllib

url = 'http://localhost/getProductIDs.php';
response = urllib2.urlopen(url);
data = json.load(response)['products'][0]
for item in data:
	url = 'http://api.uwaterloo.ca/public/v2/foodservices/product/' + item + '.json?key=4aa5eb25c8cc979600724104ccfb70ea';
	returnValue = json.load(urllib2.urlopen(url));
	info = returnValue['data'];
	params = {'diet_type': info['diet_type'],
			'name' : info['product_name'],
			'pid' : info['product_id'],
			'ingredients' : info['ingredients'],
			'servingsize' : info['serving_size'],
			'calories' : info['calories'],
			'totalfat' : info['total_fat_g'],
			'totalfatpercent' : info['total_fat_percent'],
			'fatsaturated' : info['fat_saturated_g'],
			'fatsaturatedpercent' : info['fat_saturated_percent'],
			'cholesterol' : info['cholesterol_mg'],
			'sodium' : info['sodium_mg'],
			'sodiumpercent' : info['sodium_percent'],
			'carbo' : info['carbo_g'],
			'carbopercent' : info['carbo_percent'],
			'protein' : info['protein_g'],
			'vitamina' : info['vitamin_a_percent'],
			'vitaminc' : info['vitamin_c_percent'],
			'calcium' : info['calcium_percent'],
			'iron' : info['iron_percent']};
	parameters = urllib.urlencode(params);
	request = urllib2.Request('http://localhost/insertProductInfo.php', parameters);
	response = urllib2.urlopen(request);
	print response.read();
	
