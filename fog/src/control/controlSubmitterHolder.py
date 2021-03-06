from asyncio import sleep, get_event_loop, Task
from datetime import timedelta, datetime

from control.controlSubmitter import ControlSubmitter
from core.config import Config
from server.serverEdgeController import ServerEdgeController


class ControlSubmitterHolder:
    control_submitters: dict = {}

    def __init__(self, server_edge_controller: ServerEdgeController, config_instance: Config) -> None:
        super().__init__()
        self.server_edge_controller = server_edge_controller
        self.config_instance = config_instance
        self.__cache_cleanup_task = self.__init_cache_cleanup()

    async def create_or_update_last_report_message(self, edge_id: str, message) -> None:
        control_submitter = self.control_submitters.get(edge_id)
        if control_submitter is None:
            control_submitter = ControlSubmitter(self.server_edge_controller, self.config_instance, edge_id)
            self.control_submitters[edge_id] = control_submitter

        await control_submitter.update_message(message)

        if self.__cache_cleanup_task.done():
            self.__cache_cleanup_task = self.__init_cache_cleanup()

    def __init_cache_cleanup(self) -> Task:
        return get_event_loop().create_task(self.__cache_cleanup_loop())

    async def __cache_cleanup_loop(self) -> None:
        while True:
            await sleep(30)
            control_submitters = self.control_submitters.copy().items()
            for edge_id, control_submitter in control_submitters:
                if control_submitter.last_message_update + timedelta(milliseconds=self.config_instance.CONTROL_MESSAGE_TICKER_UPDATE_TIMEOUT) < datetime.now():
                    del self.control_submitters[edge_id]
