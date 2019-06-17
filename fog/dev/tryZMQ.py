from random import randint

import zmq

num_runs = 1000
url_client = "tcp://localhost:5555"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)

sensor = 2
while num_runs > 0:
    timestamp = 1560790096
    timestamp = timestamp * 1000
    timestamp_end = randint(300, 30000)
    humidity = randint(1000, 9500)
    temperature = randint(1500, 3500) 
    uv = randint(1, 5)
    message = "{},{},{},0;HF1;{},1;EKx;{},2;xkb;{}".format(sensor, timestamp, timestamp_end, humidity, temperature, uv)
    socket.send_string(message)
    # socket.send_string("myplant,0HF14737,1EKx2862,2xkb1,1560348074409,2762")
    print(timestamp)
    response = socket.recv_string()
    print(response)
    num_runs = num_runs - 1
