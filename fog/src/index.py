import asyncio
import signal
from cloud.cloudUploadHandler import CloudUploaderHandler
from control.controlMessageHandler import ControlMessageHandler
from core.messageProcessor import MessageProcessor
from server.serverEdgeController import ServerEdgeController
from server import serverEdgeReceiver
from control.controlSubmitterHolder import ControlSubmitterHolder
from core.config import Config


def main():
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    loop = asyncio.get_event_loop()

    config = Config(loop)

    server_edge_controller = ServerEdgeController()
    control_submitter_holder = ControlSubmitterHolder(server_edge_controller, config)
    control_message_handler = ControlMessageHandler(control_submitter_holder)
    loop.create_task(control_message_handler.process_loop())

    # TODO params
    cloud_message_handler = CloudUploaderHandler('tcp://mycoolserver:5558')
    loop.create_task(cloud_message_handler.process_loop())

    message_processor = MessageProcessor(cloud_message_handler, control_message_handler)
    loop.create_task(serverEdgeReceiver.recv_and_process(message_processor))
    loop.run_forever()

if __name__ == "__main__":
    main()
