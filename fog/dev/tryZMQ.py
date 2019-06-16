import zmq
from random import randint
import time

num_runs = 1000
url_client = "tcp://localhost:5555"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)

timestamp = randint(1560698424, 1660698424)
sensor = 1
while num_runs > 0:
    timestamp += randint(300, 30000)
    humidity = randint(1000, 9500)
    temperature = randint(1500, 3500) 
    uv = randint(1, 5)
    socket.send_string("{},0HF1{},1EKx{},2xkb{},{},2762".format(sensor, humidity, temperature, uv, timestamp))
    # socket.send_string("myplant,0HF14737,1EKx2862,2xkb1,1560348074409,2762")
    print(timestamp)
    response = socket.recv_string()
    print(response)
    num_runs = num_runs - 1
    time.sleep(0.300)
