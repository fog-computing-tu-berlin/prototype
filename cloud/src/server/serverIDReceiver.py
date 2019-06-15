import zmq
import zmq.asyncio
from handler.edgeIDGenerator import EdgeIDGenerator
from core.config import Config


class ServerIDReceiver:
    context = zmq.asyncio.Context()

    def __init__(self, config: Config, edge_id_generator: EdgeIDGenerator) -> None:
        super().__init__()
        self.__config = config
        self.__edge_id_generator = edge_id_generator

    async def recv_and_process(self):
        # noinspection PyUnresolvedReferences
        socket = self.context.socket(zmq.REP)
        socket.bind('tcp://*:' + str(self.__config.id_fog_receiver_listen_port()))
        while True:
            #  Wait for next request from client
            message = await socket.recv()
            message = str(message, 'UTF-8')

            if self.__config.is_debug_logging():
                print('Received request: ', message)

            reply = await self.__edge_id_generator.create_new_id(message)
            await socket.send(bytes(reply, 'UTF-8'))
