import json, urllib2, urllib

data = json.load(urllib2.urlopen('http://api.uwaterloo.ca/public/v1/?key=4aa5eb25c8cc979600724104ccfb70ea&service=FoodServices&output=json'));

timing = "";
result = data['response']['data']['result']
for location in result:
	timing = "";
	for timings in location['Hours']['result']:
		timing += timings + "\n";
		params = {'id': location['ID'], 'name': location['Name'], 'location': location['Location'],'timings': timing}
	parameters = urllib.urlencode(params);
	request = urllib2.Request('http://localhost/create.php', parameters);
	response = urllib2.urlopen(request);	
  	print response.read();
	
