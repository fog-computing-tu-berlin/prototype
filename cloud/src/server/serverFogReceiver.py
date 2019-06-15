import zmq
import zmq.asyncio
from handler.sensorSubmitter import SensorSubmitter


class ServerFogReceiver:
    __context = zmq.asyncio.Context()

    def __init__(self, sensor_submitter: SensorSubmitter) -> None:
        super().__init__()
        self.__sensor_submitter = sensor_submitter

    async def recv_and_process(self):
        socket = self.__context.socket(zmq.REP)
        socket.bind("tcp://*:5558")
        while True:
            message = await socket.recv()
            message = str(message)
            print("Received request: ", message)
            await self.__sensor_submitter.publish(message)
            await socket.send(bytes('0', 'UTF-8'))