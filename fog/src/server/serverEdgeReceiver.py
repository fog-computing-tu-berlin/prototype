import asyncio

import zmq
import zmq.asyncio

from core.config import Config
from core.messageProcessor import MessageProcessor


class ServerEdgeReceiver:

    context = zmq.asyncio.Context()

    def __init__(self, config: Config, message_processor: MessageProcessor) -> None:
        super().__init__()
        self.__config = config
        self.__message_processor = message_processor

    async def recv_and_process(self) -> None:
        socket = self.__setup_socker()

        while True:
            try:
                message = await socket.recv_string()

                if self.__config.is_debug_logging():
                    print("Received request: ", message)

                reply = await self.__message_processor.process_message(message)
                await socket.send_string(reply)
            except KeyboardInterrupt:
                break
            except zmq.ZMQError:
                await asyncio.sleep(60)

    # noinspection PyUnresolvedReferences
    def __setup_socker(self) -> zmq.asyncio.Socket:
        socket = self.context.socket(zmq.REP)
        socket.bind('tcp://*:' + str(self.__config.get_edge_receiver_listen_port()))
        socket.setsockopt(zmq.SNDTIMEO, 100)
        socket.setsockopt(zmq.SNDHWM, self.__config.get_edge_receiver_max_queue_length())
        socket.setsockopt(zmq.RCVHWM, self.__config.get_edge_receiver_max_queue_length())

        return socket
