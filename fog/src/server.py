import serverEdgeReceiver
import asyncio
from cloudUploadHandler import CloudUploaderHandler
from controlMessageHandler import ControlMessageHandler
from messageProcessor import MessageProcessor

def main():
    loop = asyncio.get_event_loop()

    # TODO params
    cloud_message_handler = CloudUploaderHandler('tcp://mycoolserver:12345')
    control_message_handler = ControlMessageHandler()
    loop.create_task(control_message_handler.process_loop())
    loop.create_task(cloud_message_handler.process_loop())

    message_processor = MessageProcessor(cloud_message_handler, control_message_handler)
    loop.create_task(serverEdgeReceiver.recv_and_process(message_processor))

    loop.run_forever()
    loop.close()


if __name__ == "__main__":
    main()
