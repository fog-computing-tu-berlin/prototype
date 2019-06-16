import zmq

num_runs = 1000
url_client = "tcp://localhost:5555"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
while num_runs > 0:
    socket.send_string("1,0HF14737,1EKx2862,2xkb1,1560348074409,2762")
    # socket.send_string("myplant,0HF14737,1EKx2862,2xkb1,1560348074409,2762")
    print("Message sent")
    response = socket.recv_string()
    print(response)
    num_runs = num_runs - 1