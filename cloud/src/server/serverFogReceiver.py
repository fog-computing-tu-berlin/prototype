import zmq
import zmq.asyncio
from core.config import Config
from handler.sensorSubmitter import SensorSubmitter


class ServerFogReceiver:
    __context = zmq.asyncio.Context()

    def __init__(self, config: Config, sensor_submitter: SensorSubmitter) -> None:
        super().__init__()
        self.__config = config
        self.__sensor_submitter = sensor_submitter

    async def recv_and_process(self):
        socket = self.__setup_socket()

        while True:
            message = await socket.recv()
            message = str(message, 'UTF-8')

            if self.__config.IS_DEBUG_LOGGING:
                print('Received request: ', message)

            await self.__sensor_submitter.publish(message)
            await socket.send(bytes('0', 'UTF-8'))

    # noinspection PyUnresolvedReferences
    def __setup_socket(self) -> zmq.asyncio.Socket:
        socket = self.__context.socket(zmq.REP)
        socket.setsockopt(zmq.SNDHWM, self.__config.FOG_RECEIVER_MAX_QUEUE_LENGTH)
        socket.setsockopt(zmq.RCVHWM, self.__config.FOG_RECEIVER_MAX_QUEUE_LENGTH)
        socket.bind('tcp://*:' + str(self.__config.FOG_RECEIVER_LISTEN_PORT))

        return socket
