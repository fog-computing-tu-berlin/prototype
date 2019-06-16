import asyncio
import traceback

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
        socket = self.__setup_socket()

        while True:
            try:
                message = await socket.recv_string()

                if self.__config.IS_DEBUG_LOGGING:
                    print("Received request: ", message)

                reply = await self.__message_processor.process_message(message)
                await socket.send_string(reply)
            except KeyboardInterrupt:
                socket.close()
                break
            except zmq.ZMQError:
                await asyncio.sleep(60)
            except Exception as e:
                socket.close()
                traceback.print_exc()
                break

    # noinspection PyUnresolvedReferences
    def __setup_socket(self) -> zmq.asyncio.Socket:
        socket = self.context.socket(zmq.REP)
        socket.bind('tcp://*:' + str(self.__config.EDGE_RECEIVER_LISTEN_PORT))
        socket.setsockopt(zmq.SNDTIMEO, 100)
        socket.setsockopt(zmq.SNDHWM, self.__config.EDGE_RECEIVER_MAX_QUEUE_LENGTH)
        socket.setsockopt(zmq.RCVHWM, self.__config.EDGE_RECEIVER_MAX_QUEUE_LENGTH)

        return socket
