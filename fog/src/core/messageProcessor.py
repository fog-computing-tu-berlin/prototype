from core.reportMessage import ReportMessage
from cloud.cloudUploadHandler import CloudUploaderHandler
from control.controlMessageHandler import ControlMessageHandler

import pickle


class MessageProcessor:
    cloud_upload_handler: CloudUploaderHandler
    control_message_handler: ControlMessageHandler

    def __init__(self, cloud_upload_handler: CloudUploaderHandler, control_message_handler: ControlMessageHandler) -> None:
        super().__init__()
        self.cloud_upload_handler = cloud_upload_handler
        self.control_message_handler = control_message_handler

    async def process_message(self, message: str) -> str:
        try:
            parsed_message = ReportMessage(message)
        except ValueError:
            return "Error parsing Message"

        serialized = pickle.dumps(parsed_message)

        await self.control_message_handler.publish(serialized)
        await self.cloud_upload_handler.publish(serialized)

        # 1 -> Confirm the received message
        return str(1)
