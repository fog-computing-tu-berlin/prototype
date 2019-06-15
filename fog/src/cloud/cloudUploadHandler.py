import pickle

import zmq
import zmq.asyncio

from core.config import Config
from core.messageCache import MessageCache
from core.reportMessage import ReportMessage


class CloudUploaderHandler(MessageCache):
    __socket = None

    def __init__(self, config: Config) -> None:
        super().__init__(None)

        # noinspection PyUnresolvedReferences
        self.__socket = self._context.socket(zmq.REQ)
        self.__socket.connect(config.get_cloud_upload_url())
        self.__debug_logging = config.is_debug_logging()

    async def publish(self, message: bytes) -> None:
        return await super().publish(bytes(message))

    async def process_message(self, message: bytes) -> None:
        await self.upload_to_cloud(pickle.loads(message))

    async def upload_to_cloud(self, message: ReportMessage):
        await self.__socket.send_string(message.to_json())
        # Just await, but ignore the return value
        await self.__socket.recv_string()

        if self.__debug_logging:
            print("Uploaded: " + message.to_json())
