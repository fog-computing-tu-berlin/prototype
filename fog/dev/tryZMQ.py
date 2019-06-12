import zmq

url_client = "tcp://localhost:5555"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
socket.send_string("myplant,0HF14737,1EKx2862,2xkb1,1560348074409,2762")
print("Message sent")
response = socket.recv_string()
print(response)

