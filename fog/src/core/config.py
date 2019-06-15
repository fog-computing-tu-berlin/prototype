import os
from asyncio import AbstractEventLoop


class Config:
    def __init__(self, async_loop: AbstractEventLoop) -> None:
        super().__init__()
        self.ASYNC_LOOP = async_loop
        self.IS_DEBUG_LOGGING = bool(os.environ.get('IS_DEBUG_LOGGING', True))
        self.CONTROL_MESSAGE_TICK_RATE = int(os.environ.get('CONTROL_MESSAGE_TICK_RATE', 4))
        self.CONTROL_MESSAGE_TICKER_UPDATE_TIMEOUT = int(os.environ.get('CONTROL_MESSAGE_TICKER_UPDATE_TIMEOUT', 300000))
        self.EDGE_RECEIVER_LISTEN_PORT = int(os.environ.get('EDGE_RECEIVER_LISTEN_PORT', 5555))
        self.EDGE_CONTROLLER_PORT = int(os.environ.get('EDGE_CONTROLLER_PORT', 5556))
        self.EDGE_ID_GENERATOR_LISTEN_PORT = int(os.environ.get('EDGE_ID_GENERATOR_LISTEN_PORT', 5557))
        self.CLOUD_UPLOAD_URL = os.environ.get('CLOUD_UPLOAD_URL', 'tcp://localhost:5558')
        self.EDGE_ID_UPSTREAM_URL = os.environ.get('EDGE_ID_UPSTREAM_URL', 'tcp://localhost:5559')
        self.EDGE_RECEIVER_MAX_QUEUE_LENGTH = int(os.environ.get('EDGE_RECEIVER_MAX_QUEUE_LENGTH', 10000))
        self.EDGE_ID_RELAY_MAX_QUEUE_LENGTH = int(os.environ.get('EDGE_ID_RELAY_MAX_QUEUE_LENGTH', 10000))
        self.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH = int(os.environ.get('INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH', 100000))
        self.CLOUD_SUBMIT_TIMEOUT = int(os.environ.get('CLOUD_SUBMIT_TIMEOUT', 60000))
        self.CLOUD_ID_RELAY_CLOUD_TIMEOUT = int(os.environ.get('CLOUD_ID_RELAY_CLOUD_TIMEOUT', 5000))
