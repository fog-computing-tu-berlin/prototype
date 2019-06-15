from core.config import Config
import json


class DatabaseConnector:
    def __init__(self, config: Config) -> None:
        super().__init__()
        self.__url = config.get_database_rest_url()
        self.__http_client_session = config.get_http_client_session()

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
        url = self.__url + '/sensor'
        headers = {'Accept': 'application/json', 'Content-type': 'application/json'}
        async with self.__http_client_session.post(url=url, data=data, headers=headers) as resp:
            if resp.status != 201:
                return '-1'
            return '0'
