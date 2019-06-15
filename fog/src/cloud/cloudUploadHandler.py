import zmq
import zmq.asyncio
import pickle

from core.messageCache import MessageCache
from core.reportMessage import ReportMessage


class CloudUploaderHandler(MessageCache):
    __socket = None

    def __init__(self, server_url: str) -> None:
        super().__init__(None)

        self.__socket = self._context.socket(zmq.REQ)
        self.__socket.connect(server_url)

    async def publish(self, message: bytes) -> None:
        return await super().publish(bytes(message))

    async def process_message(self, message: bytes) -> None:
        await self.upload_to_cloud(pickle.loads(message))

    async def upload_to_cloud(self, message: ReportMessage):
        print("Would have uploaded: " + message.to_json())
        # await asyncio.sleep(5)
        #await self.__socket.send_string(message.toJSON())
        ## Just await, but ignore the return value
        #await self.__socket.recv_string()
