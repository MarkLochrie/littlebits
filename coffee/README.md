
# littleBits coffee counter 

Curl the following:

url: <br />
https://api-http.littlebitscloud.cc/subscriptions
<br />
headers: <br />
Authorization: Bearer YOUR_TOKEN
<br />
payload: <br />
{<br />
  publisher_events: [<br />
      "amplitude:delta:ignite"<br />
  ],<br />
  publisher_id: "YOUR_DEVICE_ID",<br />
  subscriber_id: "URL_WHERE_TO_POST"<br />
}<br />
