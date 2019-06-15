from core.databaseConnector import DatabaseConnector


class EdgeIDGenerator:

    def __init__(self, database_connector: DatabaseConnector) -> None:
        super().__init__()
        self.database_connector = database_connector

    async def create_new_id(self, plant: str) -> str:
        return await self.database_connector.create_new_edge_id(plant)
