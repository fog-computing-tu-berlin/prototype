import zmq
import zmq.asyncio

from core.config import Config


class ServerIDReceiver:
    context = zmq.asyncio.Context()

    def __init__(self, config: Config) -> None:
        super().__init__()
        self.__config = config

    async def recv_and_process(self):
        # noinspection PyUnresolvedReferences
        socket_req = self.context.socket(zmq.REQ)
        socket_req.connect(self.__config.get_edge_id_upstream_url())

        # noinspection PyUnresolvedReferences
        socket_rep = self.context.socket(zmq.REP)
        socket_rep.bind('tcp://*:' + str(self.__config.get_edge_id_generator_listen_port()))
        while True:
            message = await socket_rep.recv()

            if self.__config.is_debug_logging():
                print("EdgeIDRelay received request: ", message)

            await socket_req.send(message)
            edge_id = await socket_req.recv()

            await socket_rep.send(edge_id)
