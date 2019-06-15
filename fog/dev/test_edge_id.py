import zmq

num_runs = 3
url_client = "tcp://localhost:5557"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
while num_runs > 0:
    socket.send_string("MyEdgeDevice")
    print("Message sent")
    response = socket.recv_string()
    print(response)
    num_runs = num_runs - 1