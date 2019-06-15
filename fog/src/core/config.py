from asyncio import AbstractEventLoop


class Config:
    def __init__(self, async_loop: AbstractEventLoop) -> None:
        super().__init__()
        self.__async_loop = async_loop

    # This is not really a config item, but allows easy passing around of the async_loop
    def get_async_loop(self) -> AbstractEventLoop:
        return self.__async_loop

    def is_debug_logging(self) -> bool:
        return True

    def get_control_message_tick_rate(self) -> int:
        # 0 For only publish on request
        # Otherwise value in Hz
        return 4

    def get_control_message_ticker_update_timeout(self) -> int:
        return 300000

    def get_edge_receiver_listen_port(self) -> int:
        return 5555

    def get_edge_controller_port(self) -> int:
        return 5556

    def get_edge_id_generator_listen_port(self) -> int:
        return 5557

    def get_cloud_upload_url(self):
        return 'tcp://localhost:5558'

    def get_edge_id_upstream_url(self) -> str:
        return 'tcp://localhost:5559'

    def get_edge_receiver_max_queue_length(self):
        return 10000

    def get_edge_id_relay_max_queue_length(self):
        return 10000

    def get_internal_message_cache_max_queue_length(self):
        return 100000

    def get_cloud_submit_timeout(self):
        return 60000

    def get_cloud_id_relay_cloud_timeout(self):
        return 5000
