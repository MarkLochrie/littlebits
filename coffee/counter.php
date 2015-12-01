
<?php
$access_token = 'YOUR_TOKEN';
$cloudBit_ID = 'YOUR_DEVICE_ID';

$postdata = file_get_contents("php://input");
$jsonObj = json_decode($postdata);
$type = $jsonObj->type;
$data = $jsonObj->payload->percent;

if ($data > 1){
	updateCoffee();
}

function updateCoffee(){
	global $api;
	global $access_token;
	global $cloudBit_ID;
	
	$connection = new conn();
	$conn = $connection->connect();
	$date = date_create("now", timezone_open("Europe/London"));
	$date = date_format($date,'Y-m-d H:i:s');
	$c = 1;
	
	//add the count to the db
	if ($conn->query("INSERT INTO tbl_coffees (count,timestamp) VALUES ('".$c."','".$date."')") === TRUE) {
		//send a signal to the littlebit
		$data = array("percent" => 100, "duration_ms" => 5000);                                                                    
		$data_string = json_encode($data);
		$curl = curl_init();
		// Set some options - we are passing in a useragent too here
		curl_setopt_array($curl, array(
			CURLOPT_RETURNTRANSFER => 1,
			CURLOPT_HEADER => true,
			CURLINFO_HEADER_OUT => true,
			CURLOPT_URL => "http://api-http.littlebitscloud.cc/devices/".$cloudBit_ID."/output",
			CURLOPT_HTTPHEADER => array                                                                                
				('Authorization: Bearer '.$access_token,
				'Content-Type: application/json'),               
			CURLOPT_POST => 1,		
			CURLOPT_POSTFIELDS=> $data_string 
			));
			// Send the request & save response to $resp
			$resp = curl_exec($curl);
			$headers = curl_getinfo($curl, CURLINFO_HEADER_OUT);
			// Close request to clear up some resources
			curl_close($curl);
	
		header("Status: 200");
	}
}
?>
