from asyncio import sleep
from server.serverEdgeController import ServerEdgeController
from datetime import datetime, timedelta
from core.config import Config


class ControlSubmitter:
    last_message_update: datetime
    __current_message: str
    __is_ticker_running: bool = False

    def __init__(self, server_edge_controller: ServerEdgeController, config_instance: Config, edge_id: str) -> None:
        super().__init__()
        self.__server_edge_controller = server_edge_controller
        self.__edge_id = edge_id
        self.__async_loop = config_instance.get_async_loop()
        self.__update_message_timeout = config_instance.get_control_message_ticker_update_timeout()
        self.__tick_rate = config_instance.get_control_message_tick_rate()

    async def update_message(self, new_message: str):
        self.last_message_update = datetime.now()

        if self.__tick_rate is not None and self.__tick_rate > 0:
            self.__current_message = new_message

            if not self.__is_ticker_running:
                self.__async_loop.create_task(self.__ticker(self.__tick_rate))
        else:
            await self.__send_message(new_message)

    async def __ticker(self, tick_rate: int):
        self.__is_ticker_running = True

        while self.last_message_update + timedelta(seconds=self.__update_message_timeout) > datetime.now():
            await self.__send_message(self.__current_message)
            await sleep(1 / tick_rate)

        self.__is_ticker_running = False

    async def __send_message(self, message: str):
        await self.__server_edge_controller.publish_for_edge(self.__edge_id, message)
