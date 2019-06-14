import serverEdgeReceiver
import asyncio
import sys
from cloudUploadHandler import CloudUploaderHandler
from controlMessageHandler import ControlMessageHandler
from messageProcessor import MessageProcessor
from serverEdgeController import ServerEdgeController
from controlSubmitterHolder import ControlSubmitterHolder
from config import Config


def main():
    loop = asyncio.get_event_loop()
    config = Config(loop)

    server_edge_controller = ServerEdgeController()
    control_submitter_holder = ControlSubmitterHolder(server_edge_controller, config)
    control_message_handler = ControlMessageHandler(control_submitter_holder)
    loop.create_task(control_message_handler.process_loop())

    # TODO params
    cloud_message_handler = CloudUploaderHandler('tcp://mycoolserver:12345')
    loop.create_task(cloud_message_handler.process_loop())

    message_processor = MessageProcessor(cloud_message_handler, control_message_handler)
    loop.create_task(serverEdgeReceiver.recv_and_process(message_processor))
    loop.run_forever()

    try:
        sys.exit()
    except KeyboardInterrupt:
        # TODO DOES NOT WORK
        print("Keyboard interrupt")
        # Handle shutdown gracefully by waiting for all tasks to be cancelled
        tasks = asyncio.gather(*asyncio.Task.all_tasks(loop=loop), loop=loop, return_exceptions=True)
        tasks.add_done_callback(lambda t: loop.stop())
        tasks.cancel()

        while not tasks.done() and not loop.is_closed():
            loop.run_forever()
    except:
        print("Some Other exception")
    finally:
        loop.close()


if __name__ == "__main__":
    main()
