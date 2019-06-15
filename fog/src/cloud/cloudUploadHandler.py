import pickle
from datetime import datetime, timedelta

import zmq
import zmq.asyncio

from core.config import Config
from core.messageCache import MessageCache


class CloudUploaderHandler(MessageCache):
    __socket = None

    def __init__(self, config: Config) -> None:
        super().__init__(config.get_internal_message_cache_max_queue_length())

        self.__socket = self.__setup_socket(config)
        self.__debug_logging = config.is_debug_logging()
        self.__config = config
        self.__last_timeout = datetime.now()

    # noinspection PyUnresolvedReferences
    def __setup_socket(self, config):
        socket = self._context.socket(zmq.REQ)
        socket.connect(config.get_cloud_upload_url())
        socket.setsockopt(zmq.RCVTIMEO, config.get_cloud_submit_timeout())
        socket.setsockopt(zmq.SNDTIMEO, config.get_cloud_submit_timeout())
        socket.setsockopt(zmq.REQ_CORRELATE, 1)
        socket.setsockopt(zmq.REQ_RELAXED, 1)

        return socket

    async def process_message(self, message: bytes) -> None:
        await self.upload_to_cloud(message)

    async def upload_to_cloud(self, message_raw: bytes):
        message = pickle.loads(message_raw)
        self.__last_timeout = datetime.now()
        await self.__socket.send_string(message.to_json())
        # Just await, but ignore the return value

        try:
            await self.__socket.recv_string()
            if self.__debug_logging:
                print("Uploaded: " + message.to_json())
        except zmq.error.Again:
            # Same issue as in serverEdgeIDRelay.py
            print('Cloud Upload issue. Retrying..')
            if self.__last_timeout + timedelta(milliseconds=self.__config.get_cloud_submit_timeout()) > datetime.now():
                print('Upstream Cloud Uploader Socket broken. Recreating...')
                self.__socket.close(linger=500)
                self.__socket = self.__setup_socket()
                print('Recreate successful')

            self.__last_timeout = datetime.now()
            # This only at least once; Not exactly once
            await self.publish(message_raw)
