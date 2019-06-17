import os
from aiohttp import ClientSession


class Config:
    def __init__(self) -> None:
        super().__init__()
        # TODO Close the session somehow
        self.HTTP_CLIENT_SESSION = ClientSession()
        self.IS_DEBUG_LOGGING = os.environ.get('IS_DEBUG_LOGGING', 'True').lower() in ('yes', 'true')
        self.DATABASE_REST_URL = str(os.environ.get('DATABASE_REST_URL', 'http://localhost:8080/'))
        self.FOG_RECEIVER_LISTEN_PORT = int(os.environ.get('FOG_RECEIVER_LISTEN_PORT', 5558))
        self.ID_FOG_RECEIVER_LISTEN_PORT = int(os.environ.get('ID_FOG_RECEIVER_LISTEN_PORT', 5559))
        self.FOG_RECEIVER_MAX_QUEUE_LENGTH = int(os.environ.get('FOG_RECEIVER_MAX_QUEUE_LENGTH', 10000))
        self.ID_RECEIVER_MAX_QUEUE_LENGTH = int(os.environ.get('ID_RECEIVER_MAX_QUEUE_LENGTH', 10000))
        self.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH = int(os.environ.get('INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH', 100000))
