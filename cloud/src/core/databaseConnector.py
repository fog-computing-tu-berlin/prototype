from core.config import Config
import json


class DatabaseConnector:
    def __init__(self, config: Config) -> None:
        super().__init__()
        self.__url = config.DATABASE_REST_URL
        self.__http_client_session = config.HTTP_CLIENT_SESSION
        self.__debug_logging = config.IS_DEBUG_LOGGING

    async def create_new_edge_id(self, plant: str) -> str:
        data = json.dumps({'plant': plant})
        url = self.__url + 'edge_id'
        headers = {'Accept': 'application/json', 'Content-type': 'application/json', 'Prefer': 'return=representation'}
        async with self.__http_client_session.post(url=url, data=data, headers=headers) as resp:
            if resp.status != 201:
                return "Could not create edge_id"
            resp_content = await resp.json()
            return str(resp_content[0]['id'])

    async def create_new_sensor_entry(self, data: str) -> str:
        url = self.__url + 'sensor'
        headers = {'Accept': 'application/json', 'Content-type': 'application/json'}
        async with self.__http_client_session.post(url=url, data=data, headers=headers) as resp:
            if resp.status != 201:
                if self.__debug_logging:
                    print('Database insert error. Non 201 Code. Ignoring message')
                return '-1'
            if self.__debug_logging:
                print('Database insert successful')
            return '0'
