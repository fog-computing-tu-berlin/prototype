from core.config import Config
from core.messageCache import MessageCache
from core.databaseConnector import DatabaseConnector
import pickle
import json
import asyncio


class SensorSubmitter(MessageCache):

    def __init__(self, config: Config, database_connector: DatabaseConnector) -> None:
        super().__init__(config.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH)
        self.database_connector = database_connector
        self.__config = config
        self.__insert_batch = []
        self.__insert_loop: asyncio.Task = asyncio.get_event_loop().create_task(self.insert_loop())

    async def publish(self, message: str) -> None:
        return await super().publish(pickle.dumps(message))

    async def process_message(self, message: bytes) -> None:
        message_deserialized = pickle.loads(message)
        if self.__config.USE_DATABASE_BULK_INSERT and (len(self.__insert_batch)) < self.__config.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH:
            self.__insert_batch.append(message_deserialized)
            # Ensure the insert Loop is restart on errors
            if self.__insert_loop.done():
                self.__insert_loop = asyncio.get_event_loop().create_task(self.insert_loop())
        else:
            await self.create_in_database(message_deserialized)

    async def insert_loop(self):
        while True:
            await asyncio.sleep(2)
            if len(self.__insert_batch) > 0:
                insert_batch = self.__insert_batch.copy()
                self.__insert_batch = []
                data = []
                while len(insert_batch) > 0:
                    data.append(json.loads(insert_batch.pop(), encoding='UTF-8'))
                try:
                    await self.database_connector.create_new_sensor_entry(json.dumps(data))
                except:
                    # Only at least once; Not exactly once
                    print('Database bulk insert issue. Retrying one by one..')
                    while len(data) > 0:
                        try:
                            await self.create_in_database(json.dumps(data.pop()))
                        except:
                            pass

    async def create_in_database(self, message: str):
        try:
            return await self.database_connector.create_new_sensor_entry(message)
        except:
            # Only at least once; Not exactly once
            print('Database issue. Retrying..')
            await self.publish(pickle.dumps(message))
