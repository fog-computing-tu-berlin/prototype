import zmq
import zmq.asyncio
from handler.edgeIDGenerator import EdgeIDGenerator
from core.config import Config


class ServerIDReceiver:
    __context = zmq.asyncio.Context()

    def __init__(self, config: Config, edge_id_generator: EdgeIDGenerator) -> None:
        super().__init__()
        self.__config = config
        self.__edge_id_generator = edge_id_generator

    async def recv_and_process(self):
        # noinspection PyUnresolvedReferences
        socket = self.__setup_socket()

        while True:
            message = await socket.recv()
            message = str(message, 'UTF-8')

            if self.__config.IS_DEBUG_LOGGING:
                print('Received request: ', message)

            reply = await self.__edge_id_generator.create_new_id(message)
            await socket.send(bytes(reply, 'UTF-8'))

    # noinspection PyUnresolvedReferences
    def __setup_socket(self) -> zmq.asyncio.Socket:
        socket = self.__context.socket(zmq.REP)
        socket.setsockopt(zmq.SNDHWM, self.__config.ID_RECEIVER_MAX_QUEUE_LENGTH)
        socket.setsockopt(zmq.RCVHWM, self.__config.ID_RECEIVER_MAX_QUEUE_LENGTH)
        socket.bind('tcp://*:' + str(self.__config.ID_FOG_RECEIVER_LISTEN_PORT))

        return socket