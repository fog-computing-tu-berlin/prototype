from asyncio import AbstractEventLoop
from aiohttp import ClientSession


class Config:
    def __init__(self, async_loop: AbstractEventLoop) -> None:
        super().__init__()
        self.__async_loop = async_loop
        # TODO Close the session somehow
        self.__http_client_session = ClientSession()

    def get_async_loop(self) -> AbstractEventLoop:
        return self.__async_loop

    def get_database_rest_url(self) -> str:
        return 'http://localhost:8080/'

    def get_http_client_session(self) -> ClientSession:
        return self.__http_client_session

    def is_debug_logging(self) -> bool:
        return True

    def get_fog_receiver_listen_port(self) -> int:
        return 5558

    def id_fog_receiver_listen_port(self) -> int:
        return 5559
