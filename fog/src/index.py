import asyncio
import signal

from cloud.cloudUploadHandler import CloudUploaderHandler
from control.controlMessageHandler import ControlMessageHandler
from control.controlSubmitterHolder import ControlSubmitterHolder
from core.config import Config
from core.messageProcessor import MessageProcessor
from server.serverEdgeController import ServerEdgeController
from server.serverEdgeIDRelay import ServerIDReceiver
from server.serverEdgeReceiver import ServerEdgeReceiver


def main():
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    loop = asyncio.get_event_loop()

    config = Config(loop)

    server_edge_controller = ServerEdgeController()
    control_submitter_holder = ControlSubmitterHolder(server_edge_controller, config)
    control_message_handler = ControlMessageHandler(control_submitter_holder, config)
    loop.create_task(control_message_handler.process_loop())

    cloud_message_handler = CloudUploaderHandler(config)
    loop.create_task(cloud_message_handler.process_loop())

    server_edge_id = ServerIDReceiver(config)
    loop.create_task(server_edge_id.recv_and_process())

    message_processor = MessageProcessor(cloud_message_handler, control_message_handler)
    server_edge_receiver = ServerEdgeReceiver(config, message_processor)
    loop.create_task(server_edge_receiver.recv_and_process())

    loop.run_forever()


if __name__ == "__main__":
    main()
