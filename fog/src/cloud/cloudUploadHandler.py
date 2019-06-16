import asyncio
import json
from datetime import datetime, timedelta

import zmq
import zmq.asyncio

from core.config import Config
from core.messageCache import MessageCache
from core.reportMessage import ReportMessage


class CloudUploaderHandler(MessageCache):
    __socket = None

    def __init__(self, config: Config) -> None:
        super().__init__(config.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH)

        self.__socket = self.__setup_socket(config)
        self.__debug_logging = config.IS_DEBUG_LOGGING
        self.__config = config
        self.__last_timeout = datetime.now()
        self.__insert_batch = []
        self.__insert_loop: asyncio.Task = asyncio.get_event_loop().create_task(self.insert_loop())

    # noinspection PyUnresolvedReferences
    def __setup_socket(self, config):
        socket = self._context.socket(zmq.REQ)
        socket.connect(config.CLOUD_UPLOAD_URL)
        socket.setsockopt(zmq.RCVTIMEO, config.CLOUD_SUBMIT_TIMEOUT)
        socket.setsockopt(zmq.SNDTIMEO, config.CLOUD_SUBMIT_TIMEOUT)
        socket.setsockopt(zmq.REQ_CORRELATE, 1)
        socket.setsockopt(zmq.REQ_RELAXED, 1)

        return socket

    async def publish(self, message: ReportMessage) -> None:
        await self._add_to_cache(bytes(message.to_json(), 'UTF-8'))

    async def process_message(self, message: bytes) -> None:
        self.__insert_batch.append(json.loads(str(message, 'UTF-8')))
        # Ensure the insert Loop is restart on errors
        if self.__insert_loop.done():
            self.__insert_loop = asyncio.get_event_loop().create_task(self.insert_loop())

    async def insert_loop(self):
        while True:
            await asyncio.sleep(2)
            if len(self.__insert_batch) > 0:
                insert_batch = self.__insert_batch.copy()
                self.__insert_batch = []
                self.__last_timeout = datetime.now()
                await self.__socket.send_string(json.dumps(insert_batch))

                try:
                    # Just await, but ignore the return value
                    await self.__socket.recv_string()
                    if self.__debug_logging:
                        print("Uploaded " + str(len(insert_batch)) + ' reports')
                except zmq.error.Again:
                    # Same issue as in serverEdgeIDRelay.py
                    print('Cloud Upload issue. Retrying..')
                    if self.__last_timeout + timedelta(
                            milliseconds=self.__config.CLOUD_SUBMIT_TIMEOUT) > datetime.now():
                        print('Upstream Cloud Uploader Socket broken. Recreating...')
                        self.__socket.close(linger=500)
                        self.__socket = self.__setup_socket(self.__config)
                        print('Recreate successful')

                    self.__last_timeout = datetime.now()
                    for d in insert_batch:
                        await self._add_to_cache(bytes(json.dumps(d), 'UTF-8'))
