import zmq

num_runs = 5
url_client = "tcp://18.185.92.86:5557"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
socket.setsockopt(zmq.RCVTIMEO, 50000)
socket.setsockopt(zmq.REQ_CORRELATE, 1)
socket.setsockopt(zmq.REQ_RELAXED, 1)
while num_runs > 0:
    socket.send_string("MyEdgeDevice")
    print("Message sent")
    try:
        response = socket.recv_string()
        print(response)
    except:
        pass
    num_runs = num_runs - 1