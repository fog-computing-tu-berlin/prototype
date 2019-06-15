import zmq
import zmq.asyncio

from core.config import Config


class ServerEdgeController:
    _context: zmq.asyncio.Context = zmq.asyncio.Context()
    __pub_socket = None

    def __init__(self, config: Config) -> None:
        super().__init__()
        # noinspection PyUnresolvedReferences
        self.__pub_socket = self._context.socket(zmq.PUB)
        self.__pub_socket.bind('tcp://*:' + str(config.EDGE_CONTROLLER_PORT))

    async def publish_for_edge(self, edge_id: str,  message: str) -> None:
        topic_message = edge_id + " " + message
        await self.__pub_socket.send(bytes(topic_message, 'UTF-8'))
