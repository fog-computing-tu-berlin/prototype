import zmq

url_client = "tcp://localhost:5558"

context = zmq.Context()
print("Connecting to server...")
socket = context.socket(zmq.REQ)
socket.connect(url_client)
socket.send_string('{    "edge_id": "myplant",    "stopped_at": 1560348077171,    "humidity": 47.37,    "humidity_sensor_id": "0HF",    "started_at": 1560348074409,    "temperature": 28.62,    "temperature_sensor_id": "1EK",    "uv": 0.004,    "uv_sensor_id": "2xk"}')
print("Message sent")
response = socket.recv_string()
print(response)

