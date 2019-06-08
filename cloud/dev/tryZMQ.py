import zmq

url_client = "tcp://localhost:5555"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
socket.send_string("Hello")
print("Message sent")
response = socket.recv_string()
print(response)

