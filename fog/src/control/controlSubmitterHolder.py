from control.controlSubmitter import ControlSubmitter
from core.config import Config
from server.serverEdgeController import ServerEdgeController
from asyncio import sleep
from datetime import timedelta, datetime


class ControlSubmitterHolder:
    control_submitters: dict = {}

    def __init__(self, server_edge_controller: ServerEdgeController, config_instance: Config) -> None:
        super().__init__()
        self.server_edge_controller = server_edge_controller
        self.config_instance = config_instance
        self.__init_cache_cleanup()

    async def create_or_update_last_report_message(self, edge_id: str, message):
        control_submitter = self.control_submitters.get(edge_id)
        if control_submitter is None:
            control_submitter = ControlSubmitter(self.server_edge_controller, self.config_instance, edge_id)
            self.control_submitters[edge_id] = control_submitter

        await control_submitter.update_message(message)

    def __init_cache_cleanup(self):
        self.config_instance.get_async_loop().create_task(self.__cache_cleanup_loop())

    async def __cache_cleanup_loop(self):
        while True:
            await sleep(1)
            control_submitters = self.control_submitters.copy().items()
            for edge_id, control_submitter in control_submitters:
                if control_submitter.last_message_update + timedelta(seconds=self.config_instance.get_control_message_ticker_update_timeout()) < datetime.now():
                    del self.control_submitters[edge_id]
