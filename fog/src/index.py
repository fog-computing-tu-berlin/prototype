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
    signal.signal(signal.SIGTERM, signal.SIG_DFL)
    loop = asyncio.get_event_loop()

    config = Config()

    server_edge_controller = ServerEdgeController(config)
    control_submitter_holder = ControlSubmitterHolder(server_edge_controller, config)
    control_message_handler = ControlMessageHandler(control_submitter_holder, config)

    cloud_message_handler = CloudUploaderHandler(config)

    server_edge_id = ServerIDReceiver(config)
    server_edge_id_loop = loop.create_task(server_edge_id.recv_and_process())

    message_processor = MessageProcessor(cloud_message_handler, control_message_handler)
    server_edge_receiver = ServerEdgeReceiver(config, message_processor)
    server_edge_receiver_loop = loop.create_task(server_edge_receiver.recv_and_process())

    while True:
        try:
            loop.run_until_complete(asyncio.sleep(2))
            if server_edge_id_loop.done():
                server_edge_id_loop = loop.create_task(server_edge_id.recv_and_process())
            if server_edge_receiver_loop.done():
                server_edge_receiver_loop = loop.create_task(server_edge_receiver.recv_and_process())
        except KeyboardInterrupt:
            break
        except:
            pass

    loop.close()

if __name__ == "__main__":
    main()
