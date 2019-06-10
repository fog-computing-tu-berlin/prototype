##To run that:
Add following arguments: ip name button_id tuple_of_sensors [dev mode]

tuple_of_sensors (sensor_type,sensor_id)

example
192.168.99.100 myplant but (t,abc) (h,cde) (u,dgh) 

[optional DEV for development (no sensor connection)]
192.168.99.100 myplant but (t,abc) (h,cde) (u,dgh) DEV


##Messageformat:

The client sends a message to the server.
Format (without brackets):
[name,sensordata...,first timestamp,last timestamp]

name = name of the flower setup example: "my plant"

sensordata... = for example "1abc34,0cde845,2dgh155"
That stands for 3 sensors. The first value defines the sensor type
0 = humidity
1 = temperature
2 = uv
the 3 characters are the uid of the sensor
the numbers until the comma are the value
These value is in the same format as the thinkerforges ones (can be negative)
You have to divide humidity by 10, temperature by 100 and uv by 250 to get the real value.

timestamp = the data is accumulated this timestamp presents the timestamp of the first measurement
last timestamp = this is not a real timestamp, just the difference to the first one. To get the last timestamp you have to add this value to the first timestamp

The client receives messages from the server:
The format ist also quite short:

It only contains one value:
0 to 5

the value is calculated:
needs water = 1
needs no water = 0
needs less uv 0
no uv change necessary = 1
more uv = 2

the value is calculated water * 3 + uv
for example it needs water and more uv = 3 * 1 + 2 = 5