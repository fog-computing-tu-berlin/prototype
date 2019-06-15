from core.config import Config
from core.messageCache import MessageCache
from core.databaseConnector import DatabaseConnector
import pickle


class SensorSubmitter(MessageCache):

    def __init__(self, config: Config, database_connector: DatabaseConnector) -> None:
        super().__init__(config.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH)
        self.database_connector = database_connector

    async def publish(self, message: str) -> None:
        return await super().publish(pickle.dumps(message))

    async def process_message(self, message: bytes) -> None:
        await self.create_in_database(pickle.loads(message))

    async def create_in_database(self, message: str):
        return await self.database_connector.create_new_sensor_entry(message)
